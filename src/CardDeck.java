import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ListIterator;
import java.util.Collections;

/** Represents operations to be performed on a deck of cards
 * @author Marcus Hutchings
 * @version 1.0
 */
public class CardDeck {

    /** Method used to generate a deck of cards between 1-100. It will also insert 4 random wild cards
     * @return deck An ArrayList of Card objects that form a deck
     * */
    public ArrayList<Card> GenerateDeck(){
        ArrayList<Card> deck = new ArrayList<>();
        for(int i = 1; i <= 100; i++){
            Card card = new Card(i, "n");
            deck.add(card);
        }

        Random rand = new Random();
        ArrayList<Integer> randomIntegers = new ArrayList();

        for(int i = 0; i < 4; i++){
            int randomInt = rand.nextInt(100);
            // in order to prevent duplicate wildcards
            if(!randomIntegers.contains(randomInt)){
                randomIntegers.add(randomInt);
            } else {
                i--;
            }
        }

        for(int randomInteger : randomIntegers){
            //this.deck.get(randomInteger).setStatus("w");
            deck.get(randomInteger).setStatus("w");
        }
        return deck;
    }

    /** Method used to retrieve any remaining cards in the deck after the game has ended
     * @param deck A collection of Card objects stored in an ArrayList
     * @return cardsRemaining A List of strings that represent the cards remaining in the deck
     * */
    public ArrayList<String> PrintDeck(ArrayList<Card> deck){
        ListIterator<Card> iterator = deck.listIterator();
        ArrayList<String> cardsRemaining = new ArrayList<>();
        iterator.forEachRemaining((card) -> {
           //System.out.println(card.getValue() + " " + card.getStatus());
            cardsRemaining.add(card.getValue() + " " + card.getStatus());
        });
        return cardsRemaining;
    }

    /** Method used to randomize/shuffle the deck of cards to take them out of their current order
     * @param deck A collection of Card objects stored in an ArrayList
     * @param numberOfShuffles The number of times the deck should be shuffled
     * @return deck An ArrayList of Card objects (a deck) that has been shuffled
     * */
    public ArrayList<Card> ShuffleDeck(ArrayList<Card> deck, int numberOfShuffles){
        for(int i = 0; i < numberOfShuffles; i++){
            Collections.shuffle(deck);
        }
        return deck;
    }

    /** Method used return the size of the deck (ArrayList)
     * @param deck A collection of Card objects stored in an ArrayList
     * @return deck An ArrayList of Card objects (a deck) that has been shuffled
     * */
    public int CardsRemaining(ArrayList<Card> deck){
        return deck.size();
    }
}
