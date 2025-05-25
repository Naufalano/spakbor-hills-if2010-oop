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
    private static List<String> fish = List.of(
        "Bullhead", "Carp", "Chub", "Largemouth Bass", "Rainbow Trout",
        "Sturgeon", "Midnight Carp", "Flounder", "Halibut", "Octopus", "Pufferfish",
        "Sardine", "Super Cucumber", "Catfish", "Salmon", "Angler", "Crimsonfish", "Glacierfish", "Legend"
        );
    
    public NPC(String name, List<String> love, List<String> like, List<String> hate) {
        this.name = name;
        relationshipStatus = "Single";
        lovedItems = love;
        likedItems = like;
        hatedItems = hate;
        engaged = -1;
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
        return getAffection() == MAX_HEART && getStatus().equals("Single");
    }

    public void propose() {
        if(proposeCheck()){
            setStatus("Fiance");
            setEngaged(0);
        } else {
            if(getAffection() < MAX_HEART){
                System.out.println("Unable to propose yet!");
            } else {
                System.out.println("\"What do you mean I will be your fiance? I already am you i-idiot!\" >///<");
            }
        }
    }

    public boolean marriageCheck() {
        return getStatus().equals("Fiance") && getEngaged() > 0;
    }

    public void marry() {
        if(marriageCheck() && getEngaged() == 1){
            setStatus("Spouse");
        } else {
            if(!getStatus().equals("Fiance")){
                System.out.println("Unable to marry yet!");
            } else {
                System.out.println("\"I-I'm not ready yet. Let's m-marry tomorrow okay?\"");
            }
        }
    }

    public void giftCheck(Item item) {
        if(lovedItems.contains(item.getName()) && !getName().equals("Emily")){
            setAffection(25);
        } else if(getName().equals("Emily") && item instanceof Seeds){
            setAffection(25);
        } else if(likedItems.contains(item.getName())){
            setAffection(20);
        } else if(hatedItems.contains(item.getName()) && !getName().equals("MayorTadi") && !getName().equals("Perry")){
            setAffection(-25);
        } else if(getName().equals("MayorTadi")){
            setAffection(-25);
        } else if(getName().equals("Perry") && item instanceof Fish){
                setAffection(-25);
        } else {
            setAffection(0);
        }
    }

    public void giftCheck(String item) {
        if(lovedItems.contains(item) && !getName().equals("Emily")){
            setAffection(25);
        } else if(getName().equals("Emily") && item.contains("Seeds")){
            setAffection(25);
        } else if(likedItems.contains(item)){
            setAffection(20);
        } else if(hatedItems.contains(item) && !getName().equals("MayorTadi") && !getName().equals("Perry")){
            setAffection(-25);
        } else if(getName().equals("MayorTadi")){
            setAffection(-25);
        } else if(getName().equals("Perry") && item.equals(itemFinder(item, NPC.fish))){
                setAffection(-25);
        } else {
            setAffection(0);
        }
    }

    public String itemFinder(String name, List<String> list){
        for(String a : list){
            if(a.equals(name)){
                return a;
            }
        }
        return null;
    }

    public void chat() throws InterruptedException {
        System.out.print("Chatting with " + getName() + " .");
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
