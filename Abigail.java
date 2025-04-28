import java.util.ArrayList;
import java.util.List;

public class Abigail extends NPC {
    private String name;

    public Abigail() {
        super(
            new ArrayList<>(List.of("Blueberry", "Melon", "Grape", "Pumpkin", "Cranberry")), 
            new ArrayList<>(List.of("Baguette", "Pumpkin Pie", "Wine")),  
            new ArrayList<>(List.of("Hot Pepper", "Cauliflower", "Parsnip", "Wheat")));
        this.name = "Abigail";
    }

    public void giftCheck(String item) {
        for(String brg : lovedItems){
            if(item.equals(brg)){
                setAffection(25);
                return;
            }
        }
        for(String brg : likedItems){
            if(item.equals(brg)){
                setAffection(20);
                return;
            }
        }
        for(String brg : hatedItems){
            if(item.equals(brg)){
                setAffection(-25);
                return;
            }
        }
    }

    public void chat() throws InterruptedException {
        System.out.print("Chatting with " + name + ".");
        for(int i = 0; i < 4; i++){
            chatDelay();
            System.out.print(".");
        }
        System.out.println();
        System.out.println();
        System.out.println("Chatting is fun!");
        setAffection(10);
    }
}
