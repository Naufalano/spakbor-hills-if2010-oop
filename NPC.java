import java.util.ArrayList;
import java.util.List;

public class NPC {
    private static final int MAX_HEART = 150;
    private String name;
    private int heartPoints = 0;
    protected List<String> lovedItems = new ArrayList<>();
    protected List<String> likedItems = new ArrayList<>();
    protected List<String> hatedItems = new ArrayList<>();
    private String relationshipStatus;
    private int engaged; // Tambah variabel menyatakan proposal di hari tersebut. Jika true, setiap NPC dengan status fiance akan menambah engaged sebanyak 1 untuk validasi marrigae 
    
    public NPC(String name, List<String> love, List<String> like, List<String> hate) {
        this.name = name;
        relationshipStatus = "Single";
        lovedItems = love;
        likedItems = like;
        hatedItems = hate;
        engaged = 0;
    }
    // Contoh cara inisiasi:
    // new Buku(new ArrayList<>(List.of("Tere Liye", "Andrea Hirata", "Dee Lestari")));

    public void showStats() {
        System.out.println(name);
        System.out.println(heartPoints);
        System.out.println(relationshipStatus);
        for(String item : lovedItems){
            System.out.print(item + " ");
        }
        System.out.println("");
        for(String item : likedItems){
            System.out.print(item + " ");
        }
        System.out.println("");
        for(String item : hatedItems){
            System.out.print(item + " ");
        }
        System.out.println("");
    }

    public String getName() {
        return this.name;
    }

    public int getAffection() {
        return heartPoints;
    }

    public void setAffection(int point) {
        this.heartPoints += point;
        if(heartPoints > MAX_HEART){
            heartPoints = MAX_HEART;
        }
    }

    public String getStatus() {
        return relationshipStatus;
    }

    public void setStatus(String stat) {
        relationshipStatus = stat;
    }

    public int getEngaged() {
        return this.engaged;
    }

    public void setEngaged(int num) {
        engaged = num;
    }

    public boolean proposeCheck() {
        return getAffection() == MAX_HEART;
    }

    public void propose() {
        if(proposeCheck()){
            setStatus("Fiance");
        } else {
            System.out.println("Unable to propose  yet!");
        }
    }

    public boolean marriageCheck() {
        return getStatus().equals("Fiance");
    }

    public void marry() {
        if(marriageCheck() && getEngaged() >= 1){
            setStatus("Spouse");
        } else {
            System.out.println("Unable to marry yet!");
        }
    }

    public void giftCheck(String name) {
        if(lovedItems.contains(name)){
            setAffection(25);
        } else if(likedItems.contains(name)){
            setAffection(20);
        } else if(hatedItems.contains(name)){
            setAffection(-25);
        } else {
            setAffection(0);
        }
    }

    public void chat() throws InterruptedException {
        System.out.print("Chatting with " + getName() + ".");
        for(int i = 0; i < 3; i++){
            NPC.chatDelay();
            System.out.print(".");
        }
        System.out.println();
        setAffection(10);
    }

    public static void chatDelay() throws InterruptedException {
        Thread.sleep(2000);
    }
}
