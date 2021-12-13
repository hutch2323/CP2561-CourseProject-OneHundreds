import java.io.Serializable;

/** Represents a playing card in the game One Hundreds
 * @author Marcus Hutchings
 * @version 1.0
 */
public class Card implements Serializable {
    /** Instance variables:
     * value - the integer value of the card (between 1-100)
     * status - the status of a card (either n - normal or w - wildcard)
     * */
    private int value;
    private String status;

    /** Constructor for the Card class. Initiates the value and status of a card
     * @param value - the integer value of the card
     * @param status - status of a card
     * */
    public Card(int value, String status){
        this.setValue(value);
        this.setStatus(status);
    }

    /** Getter method to grab the value of a Card object
     * @return value - the integer value of the card
     * */
    public int getValue() {
        return this.value;
    }

    /** Setter method to set the value of a Card object
     * @param value - value of the card to set
     * */
    public void setValue(int value) {
        this.value = value;
    }

    /** Getter method to grab the value of a Card object
     * @return status - status of a card
     * */
    public String getStatus() {
        return this.status;
    }

    /** Setter method to set the status of a Card object
     * @param status - status to set to the card
     * */
    public void setStatus(String status) {
        this.status = status;
    }
}
