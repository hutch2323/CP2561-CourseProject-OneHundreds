import java.io.IOException;
import java.util.*;

public class OneHundredsGame2 implements Runnable{
    String status = "setup";
    ArrayList<ServerClientSocket2> clients;
    int currentPlayerIndex = 0;

    ArrayList<String> players = new ArrayList<>();
    Map<String, Integer> scores = new HashMap<>();
    CardDeck cardDeck = new CardDeck();
    ArrayList<Card> deck;
    int numberOfRoundsToPlay;
    ArrayList<String> winners = new ArrayList<>();

    public OneHundredsGame2(ArrayList<ServerClientSocket2> clients){
        this.clients = clients;

        this.setUp();
    }

    public void setUp(){
        deck = cardDeck.GenerateDeck();
        cardDeck.ShuffleDeck(deck, 5);

        numberOfRoundsToPlay = deck.size() / 4;
    }

    public void gameFlow() throws IOException, InterruptedException {
        if (status == "setup") {
            this.getPlayerNames();
        }

        if (status == "deal") {
            this.dealCards();
        }

        if (status == "game"){
            System.out.println("Time to play");
        }
    }

    @Override
    public void run() {
        try {
            gameFlow();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
////        dealCards(cardDeck, deck, players, objectInput, objectOutput);
//        playOneHundreds(numberOfRoundsToPlay, players, scores, in, out, objectInput, objectOutput);
//
//        //System.out.println("=== End of Game ===");
//        outputLine = "=== End of Game ===";
//        out.println(outputLine);
//
//        int winningScore = Collections.max(scores.values());
//
//        //System.out.println("Scores: ");
//        outputLine = "Scores: ";
//        out.println(outputLine);
//        ArrayList<String> winners = new ArrayList<>();
//
//        displayScores(scores, out);
//
//        determineWinners(winningScore, scores, winners);
//
//        displayWinners(scores, winners, out);
//
//        displayRemainingCards(cardDeck, deck, out);

    synchronized void getPlayerNames() throws IOException {
        String inputLine, outputLine;
        System.out.println(Thread.currentThread().getName());
        outputLine = "Welcome to One Hundreds! Please enter your name:";
        clients.get(currentPlayerIndex).out.println(outputLine);
        String name = "";
        while ((inputLine = clients.get(currentPlayerIndex).in.readLine()) != null) {
            if (inputLine.matches("[a-zA-z]{4,10}")) {
                name = inputLine;
                outputLine = "Good";
                clients.get(currentPlayerIndex).out.println(outputLine);
                players.add(name);
                System.out.println(name + " has been added to the game");
                break;
            } else {
                outputLine = "Sorry. Name must be between 4-10 characters. Try again. Please enter your name: ";
                clients.get(currentPlayerIndex).out.println(outputLine);
            }
        }
        System.out.println(currentPlayerIndex + " has entered their name: " + name);
        if (currentPlayerIndex == 3){
            currentPlayerIndex = 0;
            status = "deal";
        } else {
            currentPlayerIndex++;
        }
    }

    synchronized void dealCards() throws IOException, InterruptedException {
        if (cardDeck.CardsRemaining(deck) > 0) {
            System.out.println(Thread.currentThread().getName());
            clients.get(currentPlayerIndex).objectOutput.writeObject(deck.get(0));
            System.out.println(players.get(currentPlayerIndex) + " was dealt " + deck.get(0).getValue() + deck.get(0).getStatus());
            deck.remove(0);
            if (currentPlayerIndex == 3) {
                currentPlayerIndex = 0;
            } else {
                currentPlayerIndex++;
            }
        }
    }



//            int count = 0;
//            for (Player player : players) {
//                if (count == 0){
//                    objectOutput.writeObject(deck.get(0));
//                } else {
//                    System.out.println(count + ". Card Value: " + deck.get(0).getValue() + " " + deck.get(0).getStatus());
//                    LinkedList<Card> playerHand = player.getHand();
//                    playerHand.add(deck.get(0));
//                }
//                deck.remove(0);
//                count++;
//            }
//        }
//        objectOutput.writeObject(null);

}
