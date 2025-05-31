package com.spakbor.cls.core;

import com.spakbor.cls.items.*;
import com.spakbor.data.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class Inventory implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<Item, Integer> inventory = new HashMap<>();

    public Inventory playerInv() {
        return this;
    }

    public void addItem(Item item, int amt) {
        if (inventory.containsKey(item)) {
            if (item instanceof Equipment) {
                return;
            }
            int curAmt = inventory.get(item);
            inventory.put(item, curAmt + amt);
            return;
        }
        inventory.put(item, amt);
    }

    public void useItem(Item item, int amt) {
        if (!inventory.containsKey(item)) {
            System.out.println("Item tidak ada di inventory!");
            return;
        }
        int curAmt = inventory.get(item);
        if (curAmt > amt) {
            inventory.put(item, curAmt - amt);
        } else if (curAmt == amt) {
            inventory.remove(item);
        } else {
            System.out.println("Jumlah item tidak cukup!");
        }
    }

    public void useEquipment(Item item) {
        if (item instanceof Equipment) {
            item.use();
        }
    }

    public boolean hasItem(Item item) {
        return inventory.containsKey(item);
    }

    public int getItemQuantity(Item item) {
        return inventory.getOrDefault(item, 0);
    }

    public Map<Item, Integer> getInventoryMap() {
        return new HashMap<>(this.inventory);
    }

    public Item getItemByName(String name) {
        for (Item item : this.inventory.keySet()) {
            if (item.getName().equalsIgnoreCase(name) && this.inventory.get(item) > 0) {
                return item;
            }
        }
        return null;
    }

    // ----- SERIALIZATION HELPERS FOR GSON -----
    
    /**
     * InventoryEntry yang menyimpan Item object utuh untuk serialisasi JSON
     * Ini akan menggunakan RuntimeTypeAdapterFactory untuk handle polymorphism
     */
    public static class InventoryEntry implements Serializable {
        public Item item;
        public Integer quantity;
        
        public InventoryEntry() {}  // Default constructor for Gson
        
        public InventoryEntry(Item item, Integer quantity) {
            this.item = item;
            this.quantity = quantity;
        }
    }

    /**
     * Convert inventory Map to List of InventoryEntry for JSON serialization
     * Ini menyimpan full Item objects, bukan just strings
     */
    public List<InventoryEntry> toSerializableList() {
        List<InventoryEntry> list = new ArrayList<>();
        for (Map.Entry<Item, Integer> entry : inventory.entrySet()) {
            list.add(new InventoryEntry(entry.getKey(), entry.getValue()));
        }
        return list;
    }

    /**
     * Load inventory from List of InventoryEntry after JSON deserialization
     * Item objects sudah di-deserialize oleh RuntimeTypeAdapterFactory
     */
    public void loadFromSerializableList(List<InventoryEntry> list) {
        inventory.clear();
        if (list != null) {
            for (InventoryEntry entry : list) {
                if (entry.item != null && entry.quantity != null && entry.quantity > 0) {
                    inventory.put(entry.item, entry.quantity);
                }
            }
        }
    }
    
    /**
     * Legacy InventoryEntry yang menggunakan strings - keep untuk backwards compatibility
     */
    public static class LegacyInventoryEntry implements Serializable {
        public String itemType;
        public String itemName;
        public int quantity;

        public LegacyInventoryEntry() {}
        
        public LegacyInventoryEntry(String itemType, String itemName, int quantity) {
            this.itemType = itemType;
            this.itemName = itemName;
            this.quantity = quantity;
        }
    }

    /**
     * Convert to legacy format - useful for debugging atau migration
     */
    public List<LegacyInventoryEntry> toLegacySerializableList() {
        List<LegacyInventoryEntry> list = new ArrayList<>();
        for (Map.Entry<Item, Integer> entry : inventory.entrySet()) {
            Item item = entry.getKey();
            int qty = entry.getValue();
            list.add(new LegacyInventoryEntry(item.getClass().getSimpleName(), item.getName(), qty));
        }
        return list;
    }

    /**
     * Load from legacy format - untuk backwards compatibility
     */
    public void loadFromLegacySerializableList(List<LegacyInventoryEntry> list) {
        inventory.clear();
        for (LegacyInventoryEntry entry : list) {
            Item item = null;
            switch (entry.itemType) {
                case "Seeds":
                    item = SeedDataRegistry.getSeedByName(entry.itemName);
                    break;
                case "Equipment":
                    item = new Equipment(entry.itemName);
                    break;
                case "Food":
                    item = FoodDataRegistry.getFoodByName(entry.itemName);
                    break;
                case "Misc":
                    item = MiscDataRegistry.getMiscItemByName(entry.itemName);
                    break;
                case "RecipeItem":
                    Recipe recipe = RecipeDataRegistry.getRecipeByCookedItemName(entry.itemName);
                    if (recipe != null) {
                        item = new RecipeItem(recipe.getCookedItemName(), 0, recipe.getRecipeId());
                    }
                    break;
            }
            if (item != null) {
                inventory.put(item, entry.quantity);
            } else {
                System.err.println("Item not found while loading inventory: " + entry.itemType + " " + entry.itemName);
            }
        }
    }
}