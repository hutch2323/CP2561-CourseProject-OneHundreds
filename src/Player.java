import java.io.Serializable;
import java.util.List;
import java.util.LinkedList;

public class Player implements Serializable {
    private String name;
    private LinkedList<Card> hand = new LinkedList<>();

    public Player(String name, LinkedList<Card> hand){
        this.setName(name);
        this.setHand(hand);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedList<Card> getHand() {
        return hand;
    }

    public void setHand(LinkedList<Card> hand) {
        this.hand = hand;
    }

    public void PrintHand(){
        System.out.println(this.getName() + "'s Hand\n");
        for (Card card : this.hand) {
            System.out.println("Value: " + card.getValue());
            System.out.println("Status: " + card.getStatus() + "\n");
        }
    }
}
