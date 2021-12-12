import java.io.*;
import java.net.Socket;

public class ServerClientSocket implements Runnable{
    String socketName;
    Socket socket;
    PrintWriter out;
    BufferedReader in;
    OutputStream outputStream;
    ObjectOutputStream objectOutput;
    InputStream inputStream;
    ObjectInputStream objectInput;
    OneHundredsGame game;

    public ServerClientSocket(Socket socket, OneHundredsGame game) throws IOException {
        this.socket = socket;
        this.game = game;
        socketSetup();
    }

    public void socketSetup() throws IOException {
        this.out = new PrintWriter(this.socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.outputStream = this.socket.getOutputStream();
        this.objectOutput = new ObjectOutputStream(this.outputStream);
        this.inputStream = this.socket.getInputStream();
        this.objectInput = new ObjectInputStream(this.inputStream);
    }

    synchronized void getPlayerName() throws IOException {
        String inputLine, outputLine;
        System.out.println(Thread.currentThread().getName());
        outputLine = "Welcome to One Hundreds! Please enter your name:";
        this.out.println(outputLine);
        String name = "";
        while ((inputLine = this.in.readLine()) != null) {
            if (inputLine.matches("[a-zA-z]{4,10}")) {
                name = inputLine;
                outputLine = "Good";
                this.out.println(outputLine);
                game.players.add(name);
                this.socketName = name;
                System.out.println(name + " has been added to the game");
                break;
            } else {
                outputLine = "Sorry. Name must be between 4-10 characters. Try again. Please enter your name: ";
                this.out.println(outputLine);
            }
        }
    }

    synchronized void getCard() throws InterruptedException, IOException {
        this.objectOutput.writeObject(game.dealCard(this.socketName));
    }

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
                    for (int i = 0; i < 25; i++) {
                        if (this.socketName.equals(game.players.get(game.currentPlayer))) {
                            System.out.println(i);
                            System.out.println(this.socketName);
                            this.getCard();
                        } else {
                            try {
                                Thread.sleep(500);
                                i--;
                            } catch (InterruptedException e) {
                                System.out.println("Thread has been interrupted");
                            }
                        }
                    }
                    break;
                } else {
                    Thread.sleep(1000);
                }
            }

//            if (game.gameState.equals("deal")){
////                while(game.cardDeck.CardsRemaining(game.deck) > 0){
//                    this.objectOutput.writeObject(game.dealCard(this.socketName));
////                }
//        }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
