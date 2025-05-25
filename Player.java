import java.util.ArrayList;
import java.util.HashMap; // Untuk statistik
import java.util.List;
import java.util.Map;

public class Player {
    public static final int MAX_ENERGY = 100;

    private String name;
    private String gender;
    private int energy;
    private String farmName;
    private List<NPC> partner;
    private int gold;
    private Inventory inventory;
    // private ShippingBin shippingBin; // ShippingBin sekarang dikelola oleh Farm

    // Atribut Lokasi
    private int x;
    private int y;
    private String currentLocationName;

    // Atribut Statistik untuk End Game
    private long totalGoldEarned;
    private long totalGoldSpent;
    private Map<String, Integer> cropsHarvestedCount; // Key: Nama Tanaman, Value: Jumlah
    private int totalFishCaught;
    private Map<FishRarity, Integer> fishCaughtByRarity;
    private Map<String, NPCInteractionStats> npcInteractionStats; // Key: Nama NPC

    public Player(String name, String gender, String farmName) {
        this.name = name;
        this.gender = gender;
        this.energy = MAX_ENERGY;
        this.farmName = farmName;
        this.currentLocationName = farmName; // Mulai di kebun
        this.gold = 500; // Emas awal
        this.inventory = new Inventory();
        this.inventory.playerInv(); // Inisialisasi inventaris awal
        // this.shippingBin = new ShippingBin(); // Dihapus, dikelola oleh Farm

        this.partner = new ArrayList<>();
        this.x = 0; // Akan diatur oleh Farm saat memuat peta
        this.y = 0;

        // Inisialisasi statistik
        this.totalGoldEarned = 0; // Emas awal tidak dihitung sebagai pendapatan
        this.totalGoldSpent = 0;
        this.cropsHarvestedCount = new HashMap<>();
        this.totalFishCaught = 0;
        this.fishCaughtByRarity = new HashMap<>();
        for (FishRarity rarity : FishRarity.values()) {
            this.fishCaughtByRarity.put(rarity, 0);
        }
        this.npcInteractionStats = new HashMap<>();
        // NPCInteractionStats akan diinisialisasi saat NPCFactory dibuat atau saat interaksi pertama
    }

    public boolean performAction(Action action, Farm farm) {
        if (action.validate(this, farm)) {
            action.execute(this, farm);
            return true;
        }
        return false;
    }

    // Metode untuk memperbarui statistik
    public void addGold(int amount) {
        if (amount > 0) {
            this.gold += amount;
            this.totalGoldEarned += amount; // Catat sebagai pendapatan
        }
    }

    public void spendGold(int amount) {
        if (amount > 0 && this.gold >= amount) {
            this.gold -= amount;
            this.totalGoldSpent += amount; // Catat sebagai pengeluaran
            return; // Berhasil
        } else if (amount > 0) {
            System.out.println("Emas tidak cukup untuk pengeluaran ini.");
        }
        // return false; // Jika ingin menandakan kegagalan
    }
    
    // Overload setGold untuk memastikan sinkronisasi jika gold diubah langsung
    public void setGold(int gold) {
        int diff = gold - this.gold;
        if (diff > 0) { // Jika gold bertambah
            this.totalGoldEarned += diff;
        } else if (diff < 0) { // Jika gold berkurang (dianggap pengeluaran)
            this.totalGoldSpent += Math.abs(diff);
        }
        this.gold = Math.max(0, gold);
    }


    public void recordCropHarvested(String cropName, int amount) {
        this.cropsHarvestedCount.put(cropName, this.cropsHarvestedCount.getOrDefault(cropName, 0) + amount);
    }

    public void recordFishCaught(FishRarity rarity) {
        this.totalFishCaught++;
        this.fishCaughtByRarity.put(rarity, this.fishCaughtByRarity.getOrDefault(rarity, 0) + 1);
    }

    public NPCInteractionStats getNpcStats(String npcName) {
        // Buat entri statistik jika belum ada
        return this.npcInteractionStats.computeIfAbsent(npcName, k -> new NPCInteractionStats(npcName));
    }
    
    public void recordChatWithNPC(String npcName) {
        getNpcStats(npcName).incrementChatFrequency();
    }

    public void recordGiftToNPC(String npcName) {
        getNpcStats(npcName).incrementGiftFrequency();
    }
    
    public void updateNpcRelationshipStatus(String npcName, String status) {
        getNpcStats(npcName).setCurrentRelationshipStatus(status);
    }
    
    public boolean isMarried() {
        if (partner == null || partner.isEmpty()) {
            return false;
        }
        // Asumsikan menikah jika ada partner dan statusnya "Spouse"
        // Atau, jika hanya ada satu partner, cek statusnya.
        for (NPC p : partner) {
            if ("Spouse".equalsIgnoreCase(p.getStatus())) { // Gunakan NPC.getStatus()
                return true;
            }
        }
        return false;
    }


    // Getter untuk statistik
    public long getTotalGoldEarned() { return totalGoldEarned; }
    public long getTotalGoldSpent() { return totalGoldSpent; }
    public Map<String, Integer> getCropsHarvestedCount() { return cropsHarvestedCount; }
    public int getTotalFishCaught() { return totalFishCaught; }
    public Map<FishRarity, Integer> getFishCaughtByRarity() { return fishCaughtByRarity; }
    public Map<String, NPCInteractionStats> getAllNpcInteractionStats() { return npcInteractionStats; }


    // Getter & Setter yang sudah ada
    public String getName() { return name; }
    public String getGender() { return gender; }
    public int getEnergy() { return energy; }
    public void setEnergy(int energy) { this.energy = Math.max(0, Math.min(MAX_ENERGY, energy)); }
    public String getFarmName() { return farmName; }
    public List<NPC> getPartner() { return partner; }
    public void addPartner(NPC npc) { if (!this.partner.contains(npc)) this.partner.add(npc); }
    public int getGold() { return gold; }
    // public void setGold(int gold) { this.gold = Math.max(0, gold); } // Diganti dengan versi yang melacak
    public Inventory getInventory() { return inventory; }
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public void setLocation(int x, int y) { this.x = x; this.y = y; }
    public String getCurrentLocationName() { return currentLocationName; }
    public void setCurrentLocationName(String currentLocationName) { this.currentLocationName = currentLocationName; }
    public void setName(String name) { this.name = name; }
    public void setGender(String gender) { this.gender = gender; }
    public void setFarmName(String farmName) { this.farmName = farmName; }
    public void setPartner(List<NPC> partner) { this.partner = partner; }
    public void setInventory(Inventory inventory) { this.inventory = inventory; }
}
