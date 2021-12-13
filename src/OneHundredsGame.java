import java.lang.reflect.Array;
import java.util.*;

/** Represents a game of OneHundreds
 * @author Marcus Hutchings
 * @version 1.0
 */
public class OneHundredsGame {
    /**Instance variables:
     * players - ArrayList used to store all the names of the players in the game
     * scores - Hashmap used to store the stores of all players
     * cardDeck - CardDeck object used to perform operations from the CardDeck class on the deck ArrayList
     * deck - ArrayList used to store the deck of cards used for the game
     * winners - ArrayList used to store the end of game winners. List required in the event of mulitple winners (tie)
     * gameState - String variable used to track the current state of the game. This is used by the threads of the ServerClientSocket class
     * currentPlayer - This integer variable is used to store the current player (0-3)
     * currentRound - This integer is used to store the current round of the One Hundreds game
     * cardsPlayedInRound - This ArrayList is used to store all the cards played by the different players/clients in a round
     * wildCardsInRound - This is a List used to store any wildcards that might be played in a round
     * resultsOutput - This array list is used for passing bulk string communication back to the ServerClientSocket class
     * */
    ArrayList<String> players = new ArrayList<>();
    Map<String, Integer> scores = new HashMap<>();
    CardDeck cardDeck = new CardDeck();
    ArrayList<Card> deck;
    ArrayList<String> winners = new ArrayList<>();
    String gameState = "pregame";
    int currentPlayer = 0;
    int currentRound = 1;

    ArrayList<Card> cardsPlayedInRound = new ArrayList<>();
    List<Card> wildCardsInRound = new ArrayList<>();
    ArrayList<String> resultsOutput = new ArrayList<>();

    /** Default constructor of OneHundredsGame */
    public OneHundredsGame() {

    }

    /** Deals a card to a specific player, depending on which player's turn it is to receive a card
     * @param playerName Name of the player requesting a card
     * @return card Card object that is dealt to the player
     */
    public Card dealCard(String playerName) throws InterruptedException {
        while(true) {
            Card card = null;
            if (cardDeck.CardsRemaining(deck) > 0) {
//                if (players.get(currentPlayer).equals(playerName)) {
                System.out.println("hello " + Thread.currentThread().getName());
                card = deck.get(0);
                System.out.println(players.get(currentPlayer) + " was dealt " + deck.get(0).getValue() + deck.get(0).getStatus());
                deck.remove(0);
                if (currentPlayer == 3) {
                    currentPlayer = 0;
                } else {
                    currentPlayer++;
                }
                return card;
            }
        }

    }

    /** This adds a card (played by a client) to the cardsPlayedInRound list
     * @param cardPlayed This represents the card that was played by the client/player
     */
    public void playRound(Card cardPlayed) {
        cardsPlayedInRound.add(cardPlayed);

        if (cardPlayed.getStatus().equals("w")) {
            wildCardsInRound.add(cardPlayed);
        }

        if (currentPlayer == 3) {
            currentPlayer = 0;
            currentRound++;
        } else {
            currentPlayer++;
        }
    }


    /** Method used to retrieve the results of a singular round to the client
     * @return resultsOutput The ArrayList that holds strings of information to be sent to the client
     * */
    public ArrayList<String> displayRoundResults(){
        Card lowestValuedWildCard = null;
        resultsOutput.clear();

        if (wildCardsInRound.size() > 0) {
            lowestValuedWildCard = wildCardsInRound.get(0);
            if (wildCardsInRound.size() > 1) {
                for (Card wildCard : wildCardsInRound) {
                    if (wildCard.getValue() < lowestValuedWildCard.getValue()) {
                        lowestValuedWildCard.setValue(wildCard.getValue());
                    }
                }
            }
        }
        int winningPlayerIndex = 0;
        if (lowestValuedWildCard == null) {
            Card highestCardInRound = cardsPlayedInRound.get(0);
            for (Card card : cardsPlayedInRound) {
                if (card.getValue() > highestCardInRound.getValue()) {
                    highestCardInRound = card;
                }
            }
            winningPlayerIndex = cardsPlayedInRound.indexOf(highestCardInRound);
        } else {
            winningPlayerIndex = cardsPlayedInRound.indexOf(lowestValuedWildCard);
        }


        String winningPlayerName = players.get(winningPlayerIndex);
        if (currentPlayer == 0) {
            scores.put(winningPlayerName, scores.get(winningPlayerName) + 1);
        }
        resultsOutput.add("");
        for(int i = 0; i < 4; i++){
            resultsOutput.add(players.get(i) + ": " + cardsPlayedInRound.get(i).getValue() + " " + cardsPlayedInRound.get(i).getStatus());
        }
        resultsOutput.add("");
        resultsOutput.add(winningPlayerName + " has won the hand");
        resultsOutput.add("");
//        outputLine = winningPlayerName + " has won the hand\n";
//        out.println(outputLine);
        //System.out.println(winningPlayerName + " has won the hand\n");
        // roundNumber++;
        // empty lists for next hand/round
        if (currentPlayer == 3){
            cardsPlayedInRound.clear();
            wildCardsInRound.clear();
            currentPlayer = 0;
        } else {
            currentPlayer++;
        }

        return resultsOutput;
    }


    /** Method used to retrieve the results of the entire game to the client
     * @return resultsOutput The ArrayList that holds strings of information to be sent to the client
     * */
    public ArrayList<String> displayGameResults(){
        resultsOutput.clear();
        resultsOutput.add("=== End of Game ===");
        resultsOutput.add("Scores");
        displayScores();

        if (currentPlayer == 0) {
            determineWinners();
        }

//        resultsOutput.add("");
        if (winners.size() > 1){
            //System.out.println("\nWinners: ");
            String outputLine = "Winners: ";
//            out.println(outputLine);
            resultsOutput.add(outputLine);
            int counter = 0;
            for (String winner: winners) {
                if (counter != winners.size() - 1) {
                    //System.out.print(winner + ", ");
                    outputLine += winner + ", ";
                } else {
                    //System.out.print(winner + "\n");
                    outputLine += winner;
                }
                counter++;
            }
            resultsOutput.add(outputLine);
        } else {
            String outputLine = "Winner: " + winners.get(0);
            resultsOutput.add(outputLine);
            //System.out.println("\nWinner: " + winners.get(0));
        }

        String outputLine = "Score: " + scores.get(winners.get(0));
        resultsOutput.add(outputLine);
        //System.out.println("Score: " + scores.get(winners.get(0)));

        displayRemainingCards();

        if (currentPlayer == 3) {
            currentPlayer = 0;
            currentRound++;
            gameState = "postGame";
        } else {
            currentPlayer++;
        }
        return resultsOutput;
    }

    /** Method used to determine the winner(s) of the game */
    public void determineWinners(){
        int winningScore = Collections.max(scores.values());
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            if (entry.getValue()==winningScore) {
                winners.add(entry.getKey());
            }
        }
    }

    /** Method used to retrieve any remaining cards in the deck after the game has finished */
    public void displayRemainingCards(){
        //System.out.println("\nCards remaining in deck: ");
        String outputLine = "\nCards remaining in deck: ";
        resultsOutput.add(outputLine);
        ArrayList<String> cardsRemaining = new ArrayList<>();
        if(cardDeck.CardsRemaining(deck) > 0) {
            cardsRemaining = cardDeck.PrintDeck(deck);
            for(String card : cardsRemaining){
                resultsOutput.add(card);
            }
        } else {
            //System.out.println("None");
            outputLine = "None";
            resultsOutput.add(outputLine);
        }
    }

    /** Method used to retrieve the scores of all players in the game */
    public void displayScores(){
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            //System.out.println(entry.getKey() + " - " + entry.getValue());
            String outputLine = entry.getKey() + " - " + entry.getValue();
            resultsOutput.add(outputLine);
        }
        resultsOutput.add("");
    }
}