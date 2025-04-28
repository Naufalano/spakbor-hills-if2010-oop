import java.util.ArrayList;
import java.util.List;

public abstract class NPC {
    private static final int MAX_HEART = 150;
    private int heartPoints = 0;
    protected List<String> lovedItems = new ArrayList<>();
    protected List<String> likedItems = new ArrayList<>();
    protected List<String> hatedItems = new ArrayList<>();
    private String relationshipStatus;
    
    public NPC(List<String> love, List<String> like, List<String> hate) {
        relationshipStatus = "Single";
        lovedItems = love;
        likedItems = like;
        hatedItems = hate;
    }
    // Contoh cara inisiasi:
    // new Buku(new ArrayList<>(List.of("Tere Liye", "Andrea Hirata", "Dee Lestari")));

    public int getAffection() {
        return heartPoints;
    }

    public void setAffection(int point) {
        this.heartPoints += point;
    }

    public String getStatus() {
        return relationshipStatus;
    }

    public void setStatus(String stat) {
        relationshipStatus = stat;
    }

    public void proposeCheck() {
        if(getAffection() == MAX_HEART){
            setStatus("Fiance");
        }
    }

    public void marriageCheck() {
        if(getStatus().equals("Fiance")){
            setStatus("Spouse");
        }
    }

    public static void chatDelay() throws InterruptedException {
        Thread.sleep(1000);
    }
}