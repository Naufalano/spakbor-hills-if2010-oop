import java.util.ArrayList;
import java.util.List;

public class Caroline extends NPC {
    private String name;

    public Caroline() {
        super(
            new ArrayList<>(List.of("Firewood", "Coal")), 
            new ArrayList<>(List.of("Potato", "Wheat")), 
            new ArrayList<>(List.of("Hot Pepper")));
        this.name = "Caroline";
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
