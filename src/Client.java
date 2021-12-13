import java.net.*;
import java.io.*;
import java.util.*;

/** Represents a Client.
 * @author Marcus Hutchings
 * @version 1.0
 */

public class Client {
    public static void main(String[] args) throws IOException {
        /** Instance variables used in the client class
         * hostName - the host name of the server to connect with
         * portNumber - the port number used to connect with the server
         * player - Player object held by the client class
         * */
        String hostName = "localhost";
        int portNumber = 8020;
        Player player = null;

        /** Try with resources to initiate all client sockets */
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
            // initial conversation
            /** Initial communication between client/server. Will wait for "Ready" from server before breaking loop */
            String userInput = "", fromServer;
            while ((fromServer = in.readLine()) != null) {
                if (fromServer.contains("Ready")) {
                    break;
                }
                System.out.println("Server: " + fromServer);
            }

            // get player's name
            /** Communication between client/server. This is used to prompt and ensure that the client's name is successful
             * Client's Player object will also be instantiated here.
             * */
            while ((fromServer = in.readLine()) != null) {
                if (fromServer.equals("Good")) {
                    LinkedList<Card> hand = new LinkedList<>();
                    player = new Player(userInput, hand);
                    System.out.println(in.readLine());
                    break;
                }

                if (fromServer.contains("Waiting")){
                    System.out.println("Server: " + fromServer);
                    continue;
                }
                System.out.println("Server: " + fromServer);

                userInput = stdIn.readLine();
                if (userInput != null) {
                    System.out.println("Client: " + userInput);
                    out.println(userInput);
                }
            }

            // get hand of cards from server (deal)
            /** Communication between the client and server to deal 25 cards to the client's player object's hand */
            Card card = null;
            while (player.getHand().size() < 25) {
                try {
                    card = (Card) objectInput.readObject();
                    player.getHand().add(card);
                    //                    System.out.println("Dealt card (#" + player.getHand().size() +"): " + card.getValue() + " " + card.getStatus());
                } catch (Exception e) {
                    continue;
                }
            }

            // display player's hand to specific client
            /** Loop used to display the player's hand to the client */
            String playerHand = "";
            int count = 0;
            for (Card currentCard : player.getHand()) {
                if (count != player.getHand().size() - 1) {
                    playerHand += currentCard.getValue() + " " + currentCard.getStatus() + ", ";
                } else {
                    playerHand += currentCard.getValue() + " " + currentCard.getStatus();
                }
                count++;
            }
            System.out.println("\nPlayer Hand: " + playerHand);

            /** Loop used to control communication between the client and server during the actual gameplay
             * This loop will request player cards, display the card to the client, and display results
             * for each round of play as well as the end of game results
             */
            while ((fromServer = in.readLine()) != null) {
                if (fromServer.contains("Game Over")) {
                    break;
                }
                System.out.println(fromServer);
                if (fromServer.contains("Round")) {
                    //System.out.println(player.getName() + ": " + player.getHand().get(0).getValue() + " " + player.getHand().get(0).getStatus());
                    card = player.getHand().get(0);
                    player.getHand().remove(0);
                    System.out.println("User Card: " + card.getValue() + " " + card.getStatus());
                    objectOutput.writeObject(card);
                    objectOutput.writeObject(null);
                } else if (fromServer.contains("key")) {
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
