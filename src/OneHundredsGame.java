import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OneHundredsGame {
    ArrayList<String> players = new ArrayList<>();
    Map<String, Integer> scores = new HashMap<>();
    CardDeck cardDeck = new CardDeck();
    ArrayList<Card> deck;
    int numberOfRoundsToPlay;
    ArrayList<String> winners = new ArrayList<>();
    String gameState = "pregame";
    int currentPlayer = 0;

    public OneHundredsGame() {

    }

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
//                } else {
////                    Thread.currentThread().sleep(1000);
//                    System.out.println(Thread.currentThread().getName() + " is exiting.");
//                    break;
//                } else {
//                break;
//            }
            }
            return card;
        }

    }
}