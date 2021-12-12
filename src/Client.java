import java.net.*;
import java.io.*;
import java.util.*;

public class Client {
    public static void main(String[] args) throws IOException {
        String hostName = "localhost";
        int portNumber = 8020;
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
            String userInput = "", fromServer;
            while ((fromServer = in.readLine()) != null) {
                System.out.println("Server: " + fromServer);
                if (fromServer.contains("Ready")) {
                    break;
                }
            }

            while ((fromServer = in.readLine()) != null) {
                System.out.println("Server: " + fromServer);
                if (fromServer.equals("Good")) {
                    LinkedList<Card> hand = new LinkedList<>();
                    player = new Player(userInput, hand);
                    break;
                }

                userInput = stdIn.readLine();
                if (userInput != null) {
                    System.out.println("Client: " + userInput);
                    out.println(userInput);
                }
            }

            Card card = null;
            while (player.getHand().size() < 25) {
                try{
                    card = (Card)objectInput.readObject();
                    player.getHand().add(card);
                    System.out.println("Dealt card (#" + player.getHand().size() +"): " + card.getValue() + " " + card.getStatus());
                } catch (Exception e){
                    continue;
                }
            }

            String playerHand = "";
            int count = 0;
            for (Card currentCard : player.getHand()){
                if (count != player.getHand().size() - 1){
                    playerHand += currentCard.getValue() + " " + currentCard.getStatus() + ", ";
                } else {
                    playerHand += currentCard.getValue() + " " + currentCard.getStatus();
                }
                count++;
            }
            System.out.println("\nPlayer Hand: " + playerHand);

            while ((fromServer = in.readLine()) != null){
                System.out.println(fromServer);
                if (fromServer.contains("Round")){
                    card = player.getHand().get(0);
                    player.getHand().remove(0);
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
        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.err.println("Unable to retrieve I/O for connection to " + hostName);
            System.exit(1);
        }
    }
}
