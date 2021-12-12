//import java.io.*;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.util.*;
//import java.util.concurrent.Executors;
//
//public class TicTacToeServer {
//
//    public static void main(String[] args) throws Exception {
//        try (var listener = new ServerSocket(58901)) {
//            System.out.println("Tic Tac Toe Server is Running...");
//            var pool = Executors.newFixedThreadPool(200);
//            while (true) {
//                pool.execute(game.new Player(listener.accept(), 'X'));
//                pool.execute(game.new Player(listener.accept(), 'O'));
//            }
//        }
//    }
//}
//
//
//    }

