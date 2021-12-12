import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class Client2 {
    public static void main(String[] args) throws IOException {
        String hostName = "localhost";
        int portNumber = 8010;
        Player player = null;

        try (
                Socket clientSocket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

                OutputStream outputStream = clientSocket.getOutputStream();
                ObjectOutputStream objectOutput = new ObjectOutputStream(outputStream);
                InputStream inputStream = clientSocket.getInputStream();
                ObjectInputStream objectInput = new ObjectInputStream(inputStream);
        ) {
            String userInput, fromServer;
            while ((fromServer = in.readLine()) != null) {
                if (fromServer.equals("Good")) {
                    break;
                }
                System.out.println("Server: " + fromServer);

                userInput = stdIn.readLine();
                if (userInput != null) {
                    System.out.println("Client: " + userInput);
                    out.println(userInput);
                }
            }

            while ((player = (Player)objectInput.readObject()) != null){
                System.out.println("Player: " + player.getName());
                Card card = null;
                while ((card = (Card)objectInput.readObject()) != null) {
                    LinkedList<Card> playerHand = player.getHand();
                    playerHand.add(card);
//                    System.out.println("Dealt card: " + card.getValue() + " " + card.getStatus());
                }
                break;
            }

            String playerHand = "";
            int count = 0;
            for (Card card : player.getHand()){
                if (count != player.getHand().size() - 1){
                    playerHand += card.getValue() + " " + card.getStatus() + ", ";
                } else {
                    playerHand += card.getValue() + " " + card.getStatus();
                }
                count++;
            }
            System.out.println("\nPlayer Hand: " + playerHand);

            while ((fromServer = in.readLine()) != null){
                System.out.println(fromServer);
                if (fromServer.contains("Round")){
                    //System.out.println(player.getName() + ": " + player.getHand().get(0).getValue() + " " + player.getHand().get(0).getStatus());
                    Card card = player.getHand().get(0);
                    player.getHand().remove(0);
//                    System.out.println("User Card: " + card.getValue() + " " + card.getStatus());
                    objectOutput.writeObject(card);
                    objectOutput.writeObject(null);
                } else if (fromServer.contains("any key")){
                    userInput = stdIn.readLine();
                    out.println(userInput);
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Unable to find host " + hostName);
            System.exit(1);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Unable to retrieve I/O for connection to " + hostName);
            System.exit(1);
        }
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
    }
}
