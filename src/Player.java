import java.io.Serializable;
import java.util.List;
import java.util.LinkedList;

/** Represents a player in the game One Hundreds
 * @author Marcus Hutchings
 * @version 1.0
 */
public class Player implements Serializable {
    /** Instance variables
     * name - the player name
     * hand - LinkedList that holds all the card objects that are dealt to the player
     * */
    private String name;
    private LinkedList<Card> hand = new LinkedList<>();

    /** Player class constructor
     * @param name - the name of the player being initialized
     * @param hand - empty hand list used initialize
     * */
    public Player(String name, LinkedList<Card> hand){
        this.setName(name);
        this.setHand(hand);
    }

    /** Getter method for the name variable
     * @return name - name of the player
     * */
    public String getName() {
        return name;
    }

    /** Setter for name variable
     * @param name - value of the name to be set
     * */
    public void setName(String name) {
        this.name = name;
    }

    /** Getter method for the hand list
     * @return hand - LinkedList containing all player objects belonging to the player
     * */
    public LinkedList<Card> getHand() {
        return hand;
    }

    /** Setter for had LinkedList
     * @param hand - value of the name to be set
     * */
    public void setHand(LinkedList<Card> hand) {
        this.hand = hand;
    }

    /** Method used to print the player's hand to the console */
    public void PrintHand(){
        System.out.println(this.getName() + "'s Hand\n");
        for (Card card : this.hand) {
            System.out.println("Value: " + card.getValue());
            System.out.println("Status: " + card.getStatus() + "\n");
        }
    }

}
