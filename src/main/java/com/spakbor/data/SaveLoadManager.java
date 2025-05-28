package com.spakbor.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spakbor.cls.core.*;
import com.spakbor.cls.items.*;
import com.spakbor.utils.RuntimeTypeAdapterFactory;

import java.io.*;

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
        // Tambahkan subclass Item lain jika ada

    // Gson instance dengan adapter factory ini
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(itemAdapterFactory)
            .setPrettyPrinting()
            .create();

    // Menyimpan game ke file JSON
    public static void saveGame(Player player, Farm farm, String filename) throws IOException {
        GameSaveData saveData = new GameSaveData(player, farm);
        String json = gson.toJson(saveData);

        File dir = new File(SAVE_FOLDER);
        if (!dir.exists()) dir.mkdirs();

        try (FileWriter writer = new FileWriter(new File(dir, filename))) {
            writer.write(json);
        }
    }

    // Memuat game dari file JSON
    public static GameSaveData loadGame(String filename) throws IOException {
        File file = new File(SAVE_FOLDER, filename);
        if (!file.exists()) throw new FileNotFoundException("Save file tidak ditemukan.");

        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, GameSaveData.class);
        }
    }

    // Class pembantu untuk serialisasi (bisa Anda perluas sesuai kebutuhan)
    public static class GameSaveData {
        public Player player;
        public Farm farm;

        public GameSaveData(Player player, Farm farm) {
            this.player = player;
            this.farm = farm;
        }

        // Constructor kosong untuk Gson
        public GameSaveData() {}
    }
}
