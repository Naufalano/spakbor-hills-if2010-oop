package com.spakbor.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.spakbor.cls.core.Farm;
import com.spakbor.cls.core.Inventory;
import com.spakbor.cls.core.Player;
import com.spakbor.cls.items.*;
import com.spakbor.utils.RuntimeTypeAdapterFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Enhanced SaveLoadManager dengan better error handling dan backwards compatibility
 */
public class SaveLoadManager {
    private static final String SAVE_FOLDER = "saves";

    // Registrasi RuntimeTypeAdapterFactory untuk polymorphic serialization Item dan turunannya
    private static final RuntimeTypeAdapterFactory<Item> itemAdapterFactory = RuntimeTypeAdapterFactory
            .of(Item.class, "type")
            .registerSubtype(Equipment.class, "Equipment")
            .registerSubtype(Seeds.class, "Seeds")
            .registerSubtype(Food.class, "Food")
            .registerSubtype(Misc.class, "Misc")
            .registerSubtype(RecipeItem.class, "RecipeItem")
            .registerSubtype(Crop.class, "Crop")
            .registerSubtype(Fish.class, "Fish");

    // Gson instance dengan adapter factory ini
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(itemAdapterFactory)
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .serializeNulls() // Handle null values properly
            .create();

    public static void saveGame(Player player, Farm farm, String filename) throws IOException {
        try {
            GameSaveData saveData = new GameSaveData(player, farm);
            String json = gson.toJson(saveData);

            File dir = new File(SAVE_FOLDER);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            try (FileWriter writer = new FileWriter(new File(dir, filename))) {
                writer.write(json);
            }
            
            System.out.println("Game berhasil disimpan ke " + filename);
        } catch (Exception e) {
            System.err.println("Error saat menyimpan game: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Gagal menyimpan game", e);
        }
    }

    public static GameSaveData loadGame(String filename) throws IOException {
        File file = new File(SAVE_FOLDER, filename);
        if (!file.exists()) {
            throw new FileNotFoundException("Save file tidak ditemukan: " + filename);
        }

        try (FileReader reader = new FileReader(file)) {
            System.out.println("Loading game from: " + filename);
            
            GameSaveData saveData = gson.fromJson(reader, GameSaveData.class);
            
            if (saveData == null) {
                throw new IOException("Save file kosong atau corrupt");
            }
            
            // Initialize player inventory jika null
            if (saveData.player != null) {
                if (saveData.player.getInventory() == null) {
                    saveData.player.setInventory(new Inventory());
                }
                
                // Load inventory dari serialized entries
                if (saveData.playerInventoryEntries != null) {
                    System.out.println("Loading " + saveData.playerInventoryEntries.size() + " inventory items...");
                    saveData.player.getInventory().loadFromSerializableList(saveData.playerInventoryEntries);
                }
            }
            
            System.out.println("Game berhasil dimuat dari " + filename);
            return saveData;
            
        } catch (JsonSyntaxException e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            System.err.println("Save file mungkin corrupt atau format lama.");
            
            // Try to handle legacy format or provide better error info
            if (e.getMessage().contains("Not a JSON Object") && e.getMessage().contains("@")) {
                System.err.println("Detected old save format. Please delete the save file and create a new game.");
                throw new IOException("Save file menggunakan format lama yang tidak compatible. Silakan hapus file save dan buat game baru.", e);
            }
            
            throw new IOException("Gagal parsing save file: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Error loading game: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Gagal memuat game", e);
        }
    }

    // Utility methods
    public static boolean saveFileExists(String filename) {
        File file = new File(SAVE_FOLDER, filename);
        return file.exists() && file.isFile();
    }

    public static boolean deleteSaveFile(String filename) {
        File file = new File(SAVE_FOLDER, filename);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                System.out.println("Save file " + filename + " berhasil dihapus.");
            }
            return deleted;
        }
        return false;
    }

    public static String[] listSaveFiles() {
        File dir = new File(SAVE_FOLDER);
        if (!dir.exists() || !dir.isDirectory()) {
            return new String[0];
        }
        
        return dir.list((dir1, name) -> name.toLowerCase().endsWith(".json"));
    }

    /**
     * Wrapper class untuk save data
     */
    public static class GameSaveData {
        public Player player;
        public Farm farm;
        public List<Inventory.InventoryEntry> playerInventoryEntries;
        public String saveVersion = "2.0"; // Version untuk future compatibility

        public GameSaveData(Player player, Farm farm) {
            this.player = player;
            this.farm = farm;
            if (player != null && player.getInventory() != null) {
                this.playerInventoryEntries = player.getInventory().toSerializableList();
            }
        }

        // Constructor kosong untuk Gson
        public GameSaveData() {}
    }
}