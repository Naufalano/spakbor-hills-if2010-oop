import java.util.ArrayList;
import java.util.List;

public class Emily extends NPC {
    private String name;

    public Emily() {
        super(
            new ArrayList<>(), 
            new ArrayList<>(List.of("Catfish", "Salmon", "Sardine")), 
            new ArrayList<>(List.of("Coal", "Wood")));
        this.name = "Emily";
    }

    public void giftCheck(String item) {
        if(item.equals("Seed")){
            setAffection(25);
            return;
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
