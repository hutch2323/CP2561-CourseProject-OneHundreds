import java.net.ServerSocket;
import java.net.*;
import java.util.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Represents a Server
 * @author Marcus Hutchings
 * @version 1.0
 */
public class Server {

    public static void main(String[] args) throws Exception {
        while(true) {
            /** Local variables/objects used for the server/ServerClientSocket threads
             * portNumber - the port number which the server listens on
             * game - This is an object of the OneHundredsGame class and will be passed into each thread to keep track of gameplay
             * executorService - service responsible for thread management
             * */
            int portNumber = 8020;
            OneHundredsGame game = new OneHundredsGame();
            ExecutorService executorService = Executors.newCachedThreadPool();

            /** Try with resources to establish connection between client/server. Each client will spawn a new thread which will
             * be added to the executor service to be processed. Each thread will also be passed the game object, which also
             * initializes and shuffles the game object's deck object.
             * */
            try (var listener = new ServerSocket(portNumber)) {
                System.out.println("Server is Running...");
                ArrayList<ServerClientSocket> sockets = new ArrayList<>();
                int count = 0;
                while (true) {
                    Socket socket = listener.accept();
                    ServerClientSocket client = new ServerClientSocket(socket, game);
                    System.out.println(socket);
                    sockets.add(client);
                    client.out.println("Waiting for other players...");
                    if (count == 3) {
                        break;
                    }
                    count++;
                }
                game.deck = game.cardDeck.GenerateDeck();
                game.cardDeck.ShuffleDeck(game.deck, 5);
                for (ServerClientSocket clientSocket : sockets) {
                    clientSocket.out.println("Game about to begin");
                    clientSocket.out.println("Ready");
                    System.out.println(clientSocket);
                    executorService.execute(clientSocket);
                }
                System.out.println("Back out to the server");
                executorService.shutdown();


            } catch (IOException e) {
                System.out.println("Exception caught when trying to listen on port "
                        + portNumber + " or listening for a connection");
                System.out.println(e.getMessage());
            }
        }
    }
}