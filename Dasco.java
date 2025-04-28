import java.util.ArrayList;
import java.util.List;

public class Dasco extends NPC {
    private String name;

    public Dasco() {
        super(
            new ArrayList<>(List.of("The Legends of Spakbor", "Cooked Pig's Head", "Wine", "Fugu", "Spakbor Salad")), 
            new ArrayList<>(List.of("Fish Sandwich", "Fish Stew", "Baguette", "Fish n Chips")), 
            new ArrayList<>(List.of("Legend", "Grape", "Cauliflower", "Wheat", "Pufferfish", "Salmon")));
        this.name = "Dasco";
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
