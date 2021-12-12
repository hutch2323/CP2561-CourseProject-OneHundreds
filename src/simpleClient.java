import java.net.*;
import java.io.*;
import java.util.*;

public class simpleClient {
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
                if (fromServer.equals("Good")) {
                    LinkedList<Card> hand = new LinkedList<>();
                    player = new Player(userInput, hand);
                    break;
                }
                System.out.println("Server: " + fromServer);

                userInput = stdIn.readLine();
                if (userInput != null) {
                    System.out.println("Client: " + userInput);
                    out.println(userInput);
                }
            }

            Card card;
            while (true) {
                if ((card = (Card)objectInput.readObject()) != null) {
                    LinkedList<Card> playerHand = player.getHand();
                    playerHand.add(card);
                    System.out.println("Dealt card: " + card.getValue() + " " + card.getStatus());
                } else {

                }
            }

            //System.out.println("We left the loop");

        } catch (ClassNotFoundException classNotFoundException) {
            classNotFoundException.printStackTrace();
        } catch (UnknownHostException e) {
            System.err.println("Unable to find host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Unable to retrieve I/O for connection to " + hostName);
            System.exit(1);
        }
    }
}
