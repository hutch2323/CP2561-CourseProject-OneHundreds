import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/** Represents a Client connection to the Server
 * @author Marcus Hutchings
 * @version 1.0
 */
public class ServerClientSocket implements Runnable{
    /**Instance variables:
     * socketName - this is a name assigned to the socket. For simplicity, you should assign the player name to it
     * socket - this is the socket object passed into the constructor. It represents the resultant socket of the .accept() (using server socket)
     * out - this is the PrintWriter object used to communicate with the client
     * in - this is the BufferedReader used to read in any communication from the client
     * outputStream - the OutputStream object used to create the ObjectOutputStream
     * objectOutput - ObjectOutputStream used to send objects to the client
     * inputStream - the InputStream object used to create the ObjectInputStream
     * objectInput - ObjectInputStream used to read objects in from the client
     * game - This will hold reference to the OneHundredsGame object created in Server.java
     * */
    String socketName;
    Socket socket;
    PrintWriter out;
    BufferedReader in;
    OutputStream outputStream;
    ObjectOutputStream objectOutput;
    InputStream inputStream;
    ObjectInputStream objectInput;
    OneHundredsGame game;

    /** Creates an employee with the specified name.
     * @param socket The server-side client socket that was instantiated in Server.java
     * @param game Reference to a game object used to keep track of all game related activities. All threads reference same game
     */
    public ServerClientSocket(Socket socket, OneHundredsGame game) throws IOException {
        this.socket = socket;
        this.game = game;
        socketSetup();
    }

    /** Sets up the input/output streams for the socket */
    public void socketSetup() throws IOException {
        this.out = new PrintWriter(this.socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.outputStream = this.socket.getOutputStream();
        this.objectOutput = new ObjectOutputStream(this.outputStream);
        this.inputStream = this.socket.getInputStream();
        this.objectInput = new ObjectInputStream(this.inputStream);
    }

    /** Synchronized function used to get the player's name. Will also validate name before accepting
     * This will also updates some of the instance variables of the OneHundredsGame object
     * */
    synchronized void getPlayerName() throws IOException {
        String inputLine, outputLine;
        System.out.println(Thread.currentThread().getName());
        outputLine = "Welcome to One Hundreds! Please enter your name:";
        this.out.println(outputLine);
        String name = "";
        while ((inputLine = this.in.readLine()) != null) {
            if (inputLine.matches("[a-zA-Z]{4,10}")) {
                name = inputLine;
                this.out.println("Waiting on opponents...");
                outputLine = "Good";
                this.out.println(outputLine);
                game.players.add(name);
                game.scores.put(name, 0);
                this.socketName = name;
                System.out.println(name + " has been added to the game");
                break;
            } else {
                outputLine = "Sorry. Name must be between 4-10 characters. Try again. Please enter your name: ";
                this.out.println(outputLine);
            }
        }
    }

    /** Synchronized method that will get a card from the game object's deck and pass it to the client/player */
    synchronized void getCard() throws InterruptedException, IOException {
        this.objectOutput.writeObject(game.dealCard(this.socketName));
    }

    /** Synchronized method that handles a round of One Hundreds. It will receive a card from each client and play it
     * in the game. The game will then determine which player won the round
     * */
    synchronized void playRound() throws IOException, ClassNotFoundException {
        System.out.println("Thread for " + this.socketName + " in playRound(): " + Thread.currentThread().getName());
        String inputLine, outputLine;
        if (game.currentRound == 1) {
            outputLine = "Press Enter key to begin!";
        } else {
            outputLine = "Press Enter key to continue!";
        }
        this.out.println(outputLine);
//        println(outputLine);
        while (this.in.readLine() != null) {
//                System.out.println("Round #" + roundNumber);
            outputLine = "Round #" + game.currentRound;
            this.out.println(outputLine);
            Card cardToPlay = (Card) this.objectInput.readObject();
//            this.out.println(game.players.get(game.currentPlayer) + ": " + cardToPlay.getValue() + cardToPlay.getStatus());
            game.playRound(cardToPlay);
            if(!this.socketName.equals(game.players.get(game.players.size()-1))){
                this.out.println("Waiting on opponents...");
            }
            break;
        }
    }

    /** Synchronized method that displays the results for a round of One Hundreds to each client */
    synchronized void displayRoundResults(){
        ArrayList<String> results;
        while (true){
            if (this.socketName.equals(game.players.get(game.currentPlayer))) {
                results = game.displayRoundResults();
                break;
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("Thread has been interrupted");
                }
            }
        }

        for (String resultsLine : results){
            this.out.println(resultsLine);
        }
    }

    /** Synchronized method that displays the results for the entire game of One Hundreds to the client */
    synchronized void displayGameResults(){
        ArrayList<String> results;
        while (true){
            if (this.socketName.equals(game.players.get(game.currentPlayer))) {
                System.out.println(this.socketName);
                results = game.displayGameResults();
                break;
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("Thread has been interrupted");
                }
            }
        }

        for (String resultsLine : results){
            this.out.println(resultsLine);
        }
    }

    /** Synchronized method that asks client/player if they would like to play again. Method currently not working */
    synchronized void playAgain() throws IOException {
        System.out.println("Attempted to ask "+ this.socketName +" if they wanted to keep playing");
        this.out.println("game over");
        this.out.println("Continue? (y/n): ");
        System.out.println("After Questions");
        if (game.currentPlayer == 3){
            game.currentPlayer = 0;
        } else {
            game.currentPlayer++;
        }
    }

    /** The run method of the class. It will handle all multithread requests as each thread proceeds through the method.
     * This will make use of game variables to ensure that each threads are in control when they need to be.
     * */
    @Override
    public void run() {
        try {
            if (game.gameState.equals("pregame")) {
                getPlayerName();
                if (game.players.size() == 4) {
                    game.gameState = "deal";
                }
                String playerString = "";
                for (String player : game.players){
                    playerString += player + ", ";
                }
                System.out.println("Players: " + playerString);
            }

            System.out.println("Thread for " + this.socketName + " running: " + Thread.currentThread().getName());
            while(true) {
                if (game.gameState.equals("deal")) {
                    this.out.println("Dealing cards...");
                    for (int i = 0; i < 25; i++) {
                        if (this.socketName.equals(game.players.get(game.currentPlayer))) {
                            System.out.println(i);
                            System.out.println(this.socketName);
                            this.getCard();
                        } else {
                            try {
                                Thread.sleep(50);
                                i--;
                            } catch (InterruptedException e) {
                                System.out.println("Thread has been interrupted");
                            }
                        }
                    }
                    if(!this.socketName.equals(game.players.get(0))){
                        this.out.println("Waiting on opponents...");
                    }
                    break;
                } else {
                    Thread.sleep(500);
                }
            }

            if (game.currentPlayer == 0){
                game.gameState = "game";
                System.out.println("Game State: " + game.gameState);
            }

            System.out.println("Thread for " + this.socketName + " running: " + Thread.currentThread().getName());
            while(true) {
                if (game.gameState.equals("game")) {
                    for (int i = 0; i < 25; i++) {
                        if (this.socketName.equals(game.players.get(game.currentPlayer))) {
//                            System.out.println(i);
//                            System.out.println(this.socketName);
                            int round = game.currentRound;
                            this.playRound();
                            while(round == game.currentRound){
                                Thread.sleep(500);
                            }

                            if (round < game.currentRound) {
                                System.out.println(this.socketName + " is calling displayRoundResults()");
                                this.displayRoundResults();
                                if(!this.socketName.equals(game.players.get(0)) && (!(round == 25))){
                                    this.out.println("Waiting on opponents...");
                                }
                            }
//                            if (count == 3){
//                                this.displayRoundResults();
//                            } else {
//                                count++;
//                            }
                        } else {
                            try {
                                Thread.sleep(250);
                                i--;
                            } catch (InterruptedException e) {
                                System.out.println("Thread has been interrupted");
                            }
                        }
                    }
                    break;
                } else {
                    Thread.sleep(500);
                }
            }

            if (game.currentPlayer == 0){
                game.gameState = "end";
                System.out.println("Game State: " + game.gameState);
            }

            while(true) {
                if (game.gameState.equals("end")) {
//                    this.out.println("Dealing cards...");
                    if (this.socketName.equals(game.players.get(game.currentPlayer))) {
                        this.displayGameResults();
                        break;
                    } else {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            System.out.println("Thread has been interrupted");
                        }
                    }
                } else {
                    Thread.sleep(500);
                }
            }
//            while (!game.gameState.equals("postGame")){
//                Thread.sleep(500);
//            }
//            while(true) {
//                if (game.gameState.equals("postGame")) {
//                    if (this.socketName.equals(game.players.get(game.currentPlayer))) {
//                        System.out.println(this.socketName + " is about to be asked to play again");
//                        this.playAgain();
//                        break;
//                    } else {
//                        try {
//                            Thread.sleep(50);
//
//                        } catch (InterruptedException e) {
//                            System.out.println("Thread has been interrupted");
//                        }
//                    }
//                } else {
//                    Thread.sleep(500);
//                }
//            }



//            while(!(game.currentPlayer == 0) ){
//                Thread.sleep(250);
//            }
//            if (game.currentPlayer == 0) {
//                socket.close();
//            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
