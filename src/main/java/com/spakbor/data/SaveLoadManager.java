package com.spakbor.data;

import com.google.gson.Gson;
import com.spakbor.cls.core.Player;
import com.spakbor.cls.core.Farm;
import com.spakbor.system.SeasonController;
import com.spakbor.system.TimeController;

import java.io.*;

public class SaveLoadManager {
    private static final String SAVE_FOLDER = "saves";
    private static final Gson gson = new Gson();

    public static void saveGame(Player player, Farm farm, String filename) throws IOException {
        GameSaveData saveData = new GameSaveData(player, farm);
        String json = gson.toJson(saveData);

        File dir = new File(SAVE_FOLDER);
        if (!dir.exists()) dir.mkdirs();

        try (FileWriter writer = new FileWriter(new File(dir, filename))) {
            writer.write(json);
        }
    }

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
        // Anda bisa tambahkan SeasonController, TimeController, NPCFactory, dll sesuai kebutuhan

        public GameSaveData(Player player, Farm farm) {
            this.player = player;
            this.farm = farm;
        }

        // Constructor kosong untuk Gson
        public GameSaveData() {}
    }
}
