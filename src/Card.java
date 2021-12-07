import java.io.Serializable;

public class Card implements Serializable {
    private int value;
    private String status;

    public Card(int value, String status){
        this.setValue(value);
        this.setStatus(status);
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
