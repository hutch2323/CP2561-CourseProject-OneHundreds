import java.net.ServerSocket;
import java.net.*;
import java.util.*;
import java.io.*;

public class Server2 {

    public static void main(String[] args) throws Exception {
        int portNumber = 8020;
        try (var listener = new ServerSocket(portNumber)) {
            System.out.println("Server is Running...");
            ArrayList<ServerClientSocket2> sockets = new ArrayList<>();
            int count = 0;
            while (true) {
                Socket socket = listener.accept();
                sockets.add(new ServerClientSocket2(socket));
                if (count >= 3){
                    for (ServerClientSocket2 client : sockets){
                        client.out.println("Ready");
                    }
                    break;
                }
                count++;
            }
                OneHundredsGame2 game = new OneHundredsGame2(sockets);
                Thread thread1 = new Thread(game);
                Thread thread2 = new Thread(game);
                Thread thread3 = new Thread(game);
                Thread thread4 = new Thread(game);

                thread1.setName("thread1");
                thread2.setName("thread2");
                thread3.setName("thread3");
                thread4.setName("thread4");

                thread1.start();
                thread2.start();
                thread3.start();
                thread4.start();
//            // create ExecutorService to manage threads
//            ExecutorService executorService = Executors.newCachedThreadPool();
//
//            // start the three PrintTasks
//            executorService.execute(thread1); // start task1
//            executorService.execute(thread2); // start task2
//            executorService.execute(thread3); // start task3
//            executorService.execute(thread4); //
//            // shut down ExecutorService--it decides when to shut down threads
//            executorService.shutdown();

        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
