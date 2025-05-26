package cls.core;
import action.*;
import cls.items.*;
import cls.world.*;
import data.*;
import enums.*;

import java.util.ArrayList;
import java.util.HashMap; // Untuk statistik
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Player {
    public static final int MAX_ENERGY = 100;

    private String name;
    private String gender;
    private int energy;
    private String farmName;
    private List<NPC> partner;
    private int gold;
    private Inventory inventory;
    private Item heldItem;
    private Set<String> learnedRecipeIds;
    private Set<String> itemsEverObtained;

    private int x;
    private int y;
    private String currentLocationName;

    private long totalGoldEarned;
    private long totalGoldSpent;
    private Map<String, Integer> cropsHarvestedCount; 
    private int totalFishCaught;
    private Map<FishRarity, Integer> fishCaughtByRarity;
    private Map<String, NPCInteractionStats> npcInteractionStats; 

    public Player(String name, String gender, String farmName) {
        this.name = name;
        this.gender = gender;
        this.energy = MAX_ENERGY;
        this.farmName = farmName;
        this.currentLocationName = farmName; 
        this.gold = 0; 
        this.inventory = new Inventory();
        this.inventory.playerInv();
        this.heldItem = null;
        this.learnedRecipeIds = new HashSet<>();
        this.itemsEverObtained = new HashSet<>();
        initializeDefaultRecipes();

        this.partner = new ArrayList<>();
        this.x = 0; 
        this.y = 0;

        this.totalGoldEarned = 0; 
        this.totalGoldSpent = 0;
        this.cropsHarvestedCount = new HashMap<>();
        this.totalFishCaught = 0;
        this.fishCaughtByRarity = new HashMap<>();
        for (FishRarity rarity : FishRarity.values()) {
            this.fishCaughtByRarity.put(rarity, 0);
        }
        this.npcInteractionStats = new HashMap<>();
    }

    public boolean performAction(Action action, Farm farm) {
        if (action.validate(this, farm)) {
            action.execute(this, farm);
            return true;
        }
        return false;
    }

    public Item getHeldItem() {
        return heldItem;
    }

    public boolean holdItem(Item itemToHold) {
        if (itemToHold == null) {
            this.heldItem = null;
            System.out.println(getName() + " tidak lagi memegang item.");
            return true;
        }
        if (this.inventory.hasItem(itemToHold)) {
            this.heldItem = itemToHold;
            System.out.println(getName() + " sekarang memegang: " + itemToHold.getName());
            return true;
        } else {
            System.out.println("Tidak bisa memegang '" + itemToHold.getName() + "'. Item tidak ada di inventory.");
            return false;
        }
    }

    public void unequipItem() {
        if (this.heldItem != null) {
            System.out.println(getName() + " melepaskan " + this.heldItem.getName() + ".");
            this.heldItem = null;
        } else {
            System.out.println(getName() + " tidak memegang item apapun.");
        }
    }

    private void initializeDefaultRecipes() {
        List<Recipe> allRecipes = RecipeDataRegistry.getAllRecipes();
        for (Recipe recipe : allRecipes) {
            if ("Default/Bawaan".equalsIgnoreCase(recipe.getUnlockConditionDescription())) {
                this.learnedRecipeIds.add(recipe.getRecipeId());
                // System.out.println("Resep default dipelajari: " + recipe.getCookedItemName()); // Debug
            }
        }
    }

    public void learnRecipe(String recipeId) {
        if (recipeId != null && !recipeId.trim().isEmpty()) {
            if (this.learnedRecipeIds.add(recipeId)) {
                Recipe learned = RecipeDataRegistry.getRecipeById(recipeId);
                if (learned != null) {
                    System.out.println("Mempelajari resep baru: " + learned.getCookedItemName() + "!");
                } else {
                    System.out.println("Mempelajari resep dengan ID: " + recipeId + ".");
                }
            } else {
                Recipe alreadyLearned = RecipeDataRegistry.getRecipeById(recipeId);
                System.out.println("Sudah mempelajari resep untuk " + (alreadyLearned != null ? alreadyLearned.getCookedItemName() : recipeId) + ".");
            }
        }
    }

    public boolean hasLearnedRecipe(String recipeId) {
        return this.learnedRecipeIds.contains(recipeId);
    }

    public Set<String> getLearnedRecipeIds() {
        return new HashSet<>(this.learnedRecipeIds);
    }

    public void obtainItem(Item item, int quantity) {
        if (item == null || quantity <= 0) {
            System.err.println("Peringatan: Mencoba menambahkan item null atau kuantitas tidak valid ke inventaris.");
            return;
        }
        this.inventory.addItem(item, quantity);
        this.itemsEverObtained.add(item.getName().toLowerCase());
        // System.out.println("[DEBUG Player.obtainItem] Mencatat: " + item.getName()); // Debug
    }

    /**
     * Memeriksa apakah pemain pernah memiliki item dengan nama yang diberikan.
     * Pencocokan nama bersifat case-insensitive.
     * @param itemName Nama item yang akan diperiksa.
     * @return true jika pemain pernah memiliki item tersebut, false jika tidak.
     */
    public boolean hasEverHadItem(String itemName) {
        if (itemName == null || itemName.trim().isEmpty()) {
            return false;
        }
        return this.itemsEverObtained.contains(itemName.toLowerCase());
    }

    public void addGold(int amount) {
        if (amount > 0) {
            this.gold += amount;
            this.totalGoldEarned += amount; 
        }
    }

    public void spendGold(int amount) {
        if (amount > 0 && this.gold >= amount) {
            this.gold -= amount;
            this.totalGoldSpent += amount; 
            return; 
        } else if (amount > 0) {
            System.out.println("Emas tidak cukup untuk pengeluaran ini.");
        }
    }
    
    public void setGold(int gold) {
        int diff = gold - this.gold;
        if (diff > 0) { 
            this.totalGoldEarned += diff;
        } else if (diff < 0) { 
            this.totalGoldSpent += Math.abs(diff);
        }
        this.gold = Math.max(0, gold);
    }


    public void recordCropHarvested(String cropName, int amount) {
        this.cropsHarvestedCount.put(cropName, this.cropsHarvestedCount.getOrDefault(cropName, 0) + amount);
    }

    public void recordFishCaught(FishRarity rarity, String fishName) {
        this.totalFishCaught++;
        this.fishCaughtByRarity.put(rarity, this.fishCaughtByRarity.getOrDefault(rarity, 0) + 1);
        Fish fishInstance = FishDataRegistry.getFishByName(fishName); 
        if (fishInstance != null) {
            obtainItem(fishInstance, 1);
        } else {
            System.err.println("Peringatan: Ikan '" + fishName + "' tidak ditemukan di registry saat mencatat tangkapan.");
        }
    }

    public NPCInteractionStats getNpcStats(String npcName) {
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
        for (NPC p : partner) {
            if ("Spouse".equalsIgnoreCase(p.getStatus())) { 
                return true;
            }
        }
        return false;
    }

    public long getTotalGoldEarned() { return totalGoldEarned; }
    public long getTotalGoldSpent() { return totalGoldSpent; }
    public Map<String, Integer> getCropsHarvestedCount() { return cropsHarvestedCount; }
    public int getTotalFishCaught() { return totalFishCaught; }
    public Map<FishRarity, Integer> getFishCaughtByRarity() { return fishCaughtByRarity; }
    public Map<String, NPCInteractionStats> getAllNpcInteractionStats() { return npcInteractionStats; }

    public String getName() { return name; }
    public String getGender() { return gender; }
    public int getEnergy() { return energy; }
    public void setEnergy(int energy) { this.energy = Math.max(0, Math.min(MAX_ENERGY, energy)); }
    public String getFarmName() { return farmName; }
    public List<NPC> getPartner() { return partner; }
    public void addPartner(NPC npc) { if (!this.partner.contains(npc)) this.partner.add(npc); }
    public int getGold() { return gold; }
    // public void setGold(int gold) { this.gold = Math.max(0, gold); }
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
