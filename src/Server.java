import java.net.ServerSocket;
import java.net.*;
import java.util.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static void main(String[] args) throws Exception {
        int portNumber = 8020;
        OneHundredsGame game = new OneHundredsGame();
        ExecutorService executorService = Executors.newCachedThreadPool();
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
                if(count == 3){
                    break;
                }
                count++;
            }
            game.deck = game.cardDeck.GenerateDeck();
            game.cardDeck.ShuffleDeck(game.deck, 5);
            for(ServerClientSocket clientSocket : sockets){
                clientSocket.out.println("Game about to begin");
                clientSocket.out.println("Ready");
                System.out.println(clientSocket);
                executorService.execute(clientSocket);
            }
            executorService.shutdown();
        }

        catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}