import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ListIterator;
import java.util.Collections;

public class CardDeck {
    //private List<Card> deck = new ArrayList<>();

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

    public void PrintDeck(ArrayList<Card> deck, PrintWriter out){
        ListIterator<Card> iterator = deck.listIterator();

        iterator.forEachRemaining((card) -> {
           //System.out.println(card.getValue() + " " + card.getStatus());
            String outputLine = card.getValue() + " " + card.getStatus();
            out.println(outputLine);
        });
    }

    public ArrayList<Card> ShuffleDeck(ArrayList<Card> deck, int numberOfShuffles){
        for(int i = 0; i < numberOfShuffles; i++){
            Collections.shuffle(deck);
        }
        return deck;
    }

    public int CardsRemaining(ArrayList<Card> deck){
        return deck.size();
    }
}
