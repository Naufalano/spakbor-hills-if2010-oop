package com.spakbor.main;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import com.spakbor.action.Action;
import com.spakbor.action.Blackjack;
import com.spakbor.action.ChatAction;
import com.spakbor.action.CookingAction;
import com.spakbor.action.EatingAction;
import com.spakbor.action.FishingAction;
import com.spakbor.action.GiftAction;
import com.spakbor.action.HarvestingAction;
import com.spakbor.action.LearnRecipeAction;
import com.spakbor.action.MarryAction;
import com.spakbor.action.MovingAction;
import com.spakbor.action.PlantingAction;
import com.spakbor.action.ProposeAction;
import com.spakbor.action.RecoverLandAction;
import com.spakbor.action.SellingAction;
import com.spakbor.action.SleepingAction;
import com.spakbor.action.TillingAction;
import com.spakbor.action.WateringAction;
import com.spakbor.cls.core.Farm;
import com.spakbor.cls.core.Inventory;
import com.spakbor.cls.core.NPC;
import com.spakbor.cls.core.NPCInteractionStats;
import com.spakbor.cls.core.OngoingCooking;
import com.spakbor.cls.core.Player;
import com.spakbor.cls.core.Recipe;
import com.spakbor.cls.core.ShippingBin;
import com.spakbor.cls.items.EdibleItem;
import com.spakbor.cls.items.Equipment;
import com.spakbor.cls.items.Food;
import com.spakbor.cls.items.Item;
import com.spakbor.cls.items.Misc;
import com.spakbor.cls.items.RecipeItem;
import com.spakbor.cls.items.Seeds;
import com.spakbor.cls.world.CoastalMap;
import com.spakbor.cls.world.FarmMap;
import com.spakbor.cls.world.ForestMap;
import com.spakbor.cls.world.GameMap;
import com.spakbor.cls.world.MountainMap;
import com.spakbor.cls.world.PlayerHouseMap;
import com.spakbor.cls.world.Tile;
import com.spakbor.data.FoodDataRegistry;
import com.spakbor.data.MiscDataRegistry;
import com.spakbor.data.NPCFactory;
import com.spakbor.data.RecipeDataRegistry;
import com.spakbor.data.SaveLoadManager;
import com.spakbor.data.SeedDataRegistry;
import com.spakbor.enums.Direction;
import com.spakbor.enums.FishRarity;
import com.spakbor.system.Time;
import com.spakbor.utils.InteractionHelper;

enum GameState {
    MAIN_MENU,
    IN_GAME,
    EXITING
}

public class GameDriver {
    private static Player player;
    private static Farm farm;
    private static NPCFactory npcFactory;
    private static Scanner scanner = new Scanner(System.in);
    private static Thread gameTimeThread;
    private static volatile boolean gameIsPlaying = true;
    private static GameState currentGameState = GameState.MAIN_MENU;
    private static boolean milestoneReachedGold = false;
    private static boolean milestoneReachedMarriage = false;
    private static final int GOLD_MILESTONE_TARGET = 17209;
    private static boolean savedSession = false;

    public static void main(String[] args) throws IOException, InterruptedException{
        while (currentGameState != GameState.EXITING) {
            switch (currentGameState) {
                case MAIN_MENU:
                    clearConsole();
                    mainMenuLoop();
                    break;
                case IN_GAME:
                    if (player == null || farm == null) {
                        currentGameState = GameState.MAIN_MENU;
                    } else {
                        clearConsole();
                        inGameLoop();
                    }
                    break;
                default:
                    currentGameState = GameState.EXITING;
                    break;
            }
        }
        System.out.println("Terima kasih telah bermain Spakbor Hills!");
        scanner.close();
        stopGameTimer();
    }

    private static void mainMenuLoop() throws InterruptedException {
        displayMainMenu();
        System.out.print("Pilih opsi (1-5): ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                initializeNewGame();
                if (player != null && farm != null) {
                    startGameTimer();
                    currentGameState = GameState.IN_GAME;
                } else {
                    System.out.println("Gagal memulai game baru. Silakan coba lagi.");
                }
                break;
            case "2":
                System.out.print("Masukkan nama file save untuk dimuat (misal: bas.json): ");
                String loadFilename = scanner.nextLine().trim();
                if (loadFilename.isEmpty()) {
                    System.out.println("Nama file tidak boleh kosong. Load dibatalkan.");
                    break;
                }
                try {
                    SaveLoadManager.GameSaveData loadedData = SaveLoadManager.loadGame(loadFilename);
                    if (loadedData != null) {
                        farm = loadedData.farm;
                        player = farm.getPlayer();
                        npcFactory = farm.getNpcFactory();
                        if (farm != null) {
                            farm.performFullPostLoadObjectConversion(SaveLoadManager.getGsonInstance());
                        }
                        currentGameState = GameState.IN_GAME;
                        startGameTimer();
                        System.out.println("Game berhasil dimuat dari '" + loadFilename + "'!");
                    } else {
                        System.out.println("Tidak ada data game yang ditemukan di file '" + loadFilename + "'.");
                    }
                } catch (IOException e) {
                    System.err.println("Gagal memuat game dari '" + loadFilename + "': " + e.getMessage());
                }
                break;
            case "3":
                displayHelpScreen();
                break;
            case "4":
                displayCredits();
                break;
            case "5":
                clearConsole();
                currentGameState = GameState.EXITING;
                break;
        }
    }

    private static void initializeNewGame() {
        clearConsole();
        System.out.println("\n--- Memulai Game Baru ---");
        System.out.print("Masukkan Nama Pemain: ");
        String playerName = scanner.nextLine();
        if (playerName.isEmpty()) playerName = "Asep";

        System.out.print("Masukkan Gender Pemain: ");
        String playerGender = scanner.nextLine();
        if (playerGender.isEmpty()) playerGender = "Attack Helicopter";

        System.out.print("Masukkan Nama Farm: ");
        String farmName = scanner.nextLine();
        if (farmName.isEmpty()) farmName = playerName + "'s Farm";

        player = new Player(playerName, playerGender, farmName);
        npcFactory = new NPCFactory();
        farm = new Farm(farmName, player, npcFactory);

        Inventory playerInventory = player.getInventory();

        playerInventory.addItem(new Equipment("Hoe"), 1);
        playerInventory.addItem(new Equipment("Watering Can"), 1);
        playerInventory.addItem(new Equipment("Fishing Rod"), 1);
        playerInventory.addItem(new Equipment("Pickaxe"),1);
        
        Misc coal = MiscDataRegistry.getMiscItemByName("Coal");
        if (coal != null) playerInventory.addItem(coal, 5);

        Seeds parsnipSeeds = SeedDataRegistry.getSeedByName("Parsnip Seeds");
        if (parsnipSeeds != null) playerInventory.addItem(parsnipSeeds, 15);

        System.out.println("\nGame baru dimulai untuk " + player.getName() + " di " + farm.getName() + "!");
    }
    
    private static void inGameLoop() throws IOException {
        clearConsole();
        System.out.println("\n--- Selamat datang di " + player.getCurrentLocationName() + "! ---");
        displayFullStatus();

        boolean inGamePlaying = true;
        while (inGamePlaying && currentGameState == GameState.IN_GAME) {
            if (player.getEnergy() == -20) {
                farm.scheduleAutomaticSleep();
            }
            if (farm.isAutomaticSleepScheduled()) {
                clearConsole();
                if (player.getCurrentLocationName().equals("Player's House")) {
                    System.out.println("Ambruk di rumah sendiri cik.");
                } else {
                    System.out.println(player.getName() + " tepar brutal. Lupa istirahat jadi digotong pulang.");
                }
                System.out.println("\nNguorok karena sudah larut malam...");
                player.performAction(new SleepingAction(true), farm);
                farm.clearAutomaticSleepSchedule();
                if (!player.getCurrentLocationName().equals("Player's House")) {
                    farm.loadMap("Farm", null);
                }
                displayFullStatus();
            }

            checkAndDisplayMilestones();

            System.out.println("\nAksi Dalam Game (Ketik 'help' untuk perintah, 'mainmenu' untuk kembali):");
            System.out.print(player.getCurrentLocationName() + " > ");

            if (!scanner.hasNextLine()) {
                currentGameState = GameState.EXITING;
                break;
            }
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.isEmpty()) continue;

            String[] parts = input.split("\\s+");
            String command = parts[0];
            Action actionToPerform = null;

            try {
                if (command.equals("mainmenu")) {
                    if (!GameDriver.savedSession) {
                        System.out.println("Sesi belum disimpan. Kembali ke menu? [Y/N]:");
                        String ans = scanner.nextLine();
                        if (ans.equalsIgnoreCase("y")) {
                            clearConsole();
                            System.out.println("Kembali ke Menu Utama...");
                            stopGameTimer();
                            player = null;
                            farm = null;
                            currentGameState = GameState.MAIN_MENU;
                            inGamePlaying = false;
                            continue;
                        } else {
                            break;
                        }
                    }
                    clearConsole();
                    System.out.println("Kembali ke Menu Utama...");
                    stopGameTimer();
                    player = null;
                    farm = null;
                    currentGameState = GameState.MAIN_MENU;
                    inGamePlaying = false;
                    continue;
                }

                switch (command) {
                    case "move":
                        if (parts.length == 3) {
                            try {
                                Direction dir = Direction.valueOf(parts[1].toUpperCase());
                                int steps = Integer.parseInt(parts[2]);
                                actionToPerform = new MovingAction(dir, steps);
                                GameDriver.savedSession = false;
                            } catch (IllegalArgumentException e) {
                                System.out.println("Arah atau langkah tidak valid. Contoh: move up 1");
                            }
                        } else if (parts.length == 2) {
                            try {
                                Direction dir = Direction.valueOf(parts[1].toUpperCase());
                                int steps = 1;
                                actionToPerform = new MovingAction(dir, steps);
                                GameDriver.savedSession = false;
                            } catch (IllegalArgumentException e) {
                                System.out.println("Arah tidak valid. Contoh: move up");
                            }
                        } else {
                            System.out.println("Format: move [up|down|left|right] [langkah]");
                        }
                        break;

                    case "interact":
                        handleInteractCommand();
                        break;

                    case "till":
                        actionToPerform = new TillingAction();
                        GameDriver.savedSession = false;
                        break;

                    case "plant":
                        if (parts.length >= 2) {
                            String seedName = combineParts(parts, 1);
                            Item seedItem = player.getInventory().getItemByName(seedName);
                            if (seedItem instanceof Seeds) {
                                actionToPerform = new PlantingAction((Seeds) seedItem);
                                GameDriver.savedSession = false;
                            } else {
                                System.out.println("'" + seedName + "' bukan benih valid atau tidak ada di inventaris.");
                            }
                        } else {
                            System.out.println("Format: plant [nama_benih]");
                        }
                        break;

                    case "recover":
                        actionToPerform = new RecoverLandAction();
                        GameDriver.savedSession = false;
                        break;

                    case "water":
                        actionToPerform = new WateringAction();
                        GameDriver.savedSession = false;
                        break;

                    case "harvest":
                        actionToPerform = new HarvestingAction();
                        GameDriver.savedSession = false;
                        break;

                    case "eat":
                        if (parts.length >= 2) {
                            String foodName = combineParts(parts, 1);
                            Item foodItem = player.getInventory().getItemByName(foodName);
                            if (foodItem instanceof EdibleItem) {
                                actionToPerform = new EatingAction(foodItem);
                                GameDriver.savedSession = false;
                            } else {
                                System.out.println("'" + foodName + "' tidak bisa dimakan atau tidak ada di inventaris.");
                            }
                        } else {
                            System.out.println("Format: eat [nama_makanan]");
                        }
                        break;

                    case "learn":
                        if (parts.length >= 2) {
                            String recipeItemName = combineParts(parts, 1);
                            Item itemInInventory = player.getInventory().getItemByName(recipeItemName);
                            if (itemInInventory instanceof RecipeItem) {
                                actionToPerform = new LearnRecipeAction((RecipeItem) itemInInventory);
                                GameDriver.savedSession = false;
                            } else {
                                System.out.println("'" + recipeItemName + "' bukan item resep yang bisa dipelajari atau tidak ada di inventory.");
                            }
                        } else {
                            System.out.println("Format: learn [nama_item_resep_dari_inventory]");
                        }
                        break;

                    case "sleep":
                        actionToPerform = new SleepingAction();
                        GameDriver.savedSession = false;
                        break;
                        
                    case "inv":
                        displayInventory();
                        break;
                        
                    case "equip":
                        if (parts.length >= 2) {
                            String itemName = combineParts(parts, 1);
                            Item itemToEquip = player.getInventory().getItemByName(itemName);
                            if (itemToEquip != null) {
                                if (itemToEquip instanceof Equipment) {
                                    clearConsole();
                                    player.holdItem(itemToEquip);
                                    displayFullStatus();
                                    GameDriver.savedSession = false;
                                } else {
                                    System.out.println(itemToEquip.getName() + " bukan peralatan yang bisa dipegang untuk aksi ini.");
                                }
                            } else {
                                System.out.println("Item '" + itemName + "' tidak ditemukan di inventaris.");
                            }
                        } else {
                            System.out.println("Format: equip [nama_peralatan]");
                        }
                        break;
                        
                    case "unequip":
                        player.unequipItem();
                        GameDriver.savedSession = false;
                        break;
                        
                    case "viewbin":
                        displayShippingBinContents();
                        break;
                        
                    case "info":
                        displayPlayerStatusDetailed();
                        break;
                        
                    case "stats":
                        displayPlayerStatistics();
                        break;
                        
                    case "map":
                        clearConsole();
                        displayFullStatus();
                        break;
                        
                    case "help":
                        displayInGameHelp();
                        break;
                        
                    case "save":
                        farm.getTimeController().pauseGameTime();
                        if (player != null && farm != null) {
                            System.out.print("Masukkan nama file save (misal: savegame.json): ");
                            String filename = scanner.nextLine().trim();
                            if (filename.isEmpty()) {
                                System.out.println("Nama file tidak boleh kosong. Simpan dibatalkan.");
                                break;
                            }
                            try {
                                SaveLoadManager.saveGame(player, farm, filename);
                                GameDriver.savedSession = true;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Belum ada game untuk disimpan.");
                        }
                        farm.getTimeController().resumeGameTime();
                        break;
                        
                    case "quit":
                        if (!GameDriver.savedSession) {
                            System.out.print("Permainan tidak disimpan. Yakin ingin keluar? [Y/N]: ");
                            String ans = scanner.nextLine();
                            if (ans.equalsIgnoreCase("y")) {
                                clearConsole();
                                currentGameState = GameState.EXITING;
                                inGamePlaying = false;
                                break;
                            } else {
                                break;
                            }
                        }
                        clearConsole();
                        currentGameState = GameState.EXITING;
                        inGamePlaying = false;
                        break;

                    case "nd":
                        clearConsole();
                        farm.nextDay();
                        displayFullStatus();
                        break;

                    default:
                        System.out.println("Perintah tidak dikenal. Ketik 'help' untuk opsi.");
                        break;
                }

                if (actionToPerform != null) {
                    boolean actionSuccess = player.performAction(actionToPerform, farm);
                    if (actionSuccess) {
                        GameMap mapBeforeAction = farm.getCurrentMap();
                        String locBeforeAction = player.getCurrentLocationName();

                        clearConsole();
                        displayFullStatus();

                        if (!locBeforeAction.equals(player.getCurrentLocationName()) || farm.getCurrentMap() != mapBeforeAction) {
                            clearConsole();
                            System.out.println("\n--- Selamat datang di " + player.getCurrentLocationName() + "! ---");
                            displayFullStatus();
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Terjadi kesalahan tak terduga dalam game loop: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void clearConsole() {
        try {
            String operatingSystem = System.getProperty("os.name"); // Dapatkan nama OS

            if (operatingSystem.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // Untuk Unix-like (Linux, macOS)
                System.out.print("\033[H\033[2J");
                System.out.flush();
                // Alternatif lain untuk Unix: Runtime.getRuntime().exec("clear");
                // Namun, \033[H\033[2J lebih portabel untuk terminal yang mendukung ANSI
            }
        } catch (IOException | InterruptedException ex) {
            // Jika gagal, cetak beberapa baris baru sebagai fallback sederhana
            // System.err.println("Gagal membersihkan konsol: " + ex.getMessage());
            for (int i = 0; i < 25; ++i) System.out.println(); // Fallback: cetak banyak baris baru
        }
    }

    private static void startGameTimer() {
        if (gameTimeThread != null && gameTimeThread.isAlive()) {
            return;
        }

        gameIsPlaying = true;
        gameTimeThread = new Thread(() -> {
            System.out.println("Game Time Thread dimulai.");
            long lastTickTime = System.currentTimeMillis();

            while (gameIsPlaying && currentGameState == GameState.IN_GAME) {
                try {
                    Thread.sleep(1000);

                    if (!gameIsPlaying || currentGameState != GameState.IN_GAME || farm == null || farm.getTimeController() == null) {
                        continue;
                    }

                    farm.getTimeController().updateTime();
                    farm.updateCookingProgress();

                    Time currentTime = farm.getTimeController().getGameTime();

                    if (currentTime.getHour() == 2 && currentTime.getMinute() >= 0 && !farm.isSleepingScheduled()) {
                        System.out.println("\nSudah jam 2:00 PAGI! Waktunya turu cik. (Tekan enter)");
                        farm.scheduleAutomaticSleep();
                    }

                } catch (InterruptedException e) {
                    // System.out.println("Game Time Thread diinterupsi.");
                    Thread.currentThread().interrupt();
                    gameIsPlaying = false;
                } catch (Exception e) {
                    System.err.println("Kesalahan di Game Time Thread: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            // System.out.println("Game Time Thread berhenti.");
        });

        gameTimeThread.setDaemon(true);
        gameTimeThread.setName("GameTimeUpdater");
        gameTimeThread.start();
    }

    private static void stopGameTimer() {
        gameIsPlaying = false;
        if (gameTimeThread != null && gameTimeThread.isAlive()) {
            try {
                gameTimeThread.interrupt();
                gameTimeThread.join(1500);
            } catch (InterruptedException e) {
                System.err.println("Interupsi saat menunggu Game Time Thread berhenti.");
                Thread.currentThread().interrupt();
            }
        }
        gameTimeThread = null;
    }


    private static void checkAndDisplayMilestones() {
        if (player == null || farm == null) return;

        boolean showStats = false;

        if (!milestoneReachedGold && player.getGold() >= GOLD_MILESTONE_TARGET) {
            clearConsole();
            milestoneReachedGold = true;
            showStats = true;
            System.out.println("\n======================================================");
            System.out.println("SELAMAT! Kamu telah mencapai milestone kekayaan!");
            System.out.println("Total emasmu: " + player.getGold() + "g");
            System.out.println("======================================================");
        }

        if (!milestoneReachedMarriage && player.isMarried()) {
            clearConsole();
            milestoneReachedMarriage = true;
            showStats = true;
            System.out.println("\n======================================================");
            System.out.println("SELAMAT! Kamu telah menikah!");
            System.out.println("Pasanganmu: " + (player.getPartner().isEmpty() ? "N/A" : player.getPartner().get(0).getName()));
            System.out.println("======================================================");
        }

        if (showStats) {
            displayEndGameStatistics();
            System.out.println("\nPermainan berlanjut... Ayo terus bermain dan mencapai hal lain!");
            System.out.println("------------------------------------------------------");
        }
    }

    private static void displayEndGameStatistics() {
        if (player == null || farm == null || npcFactory == null) {
            System.out.println("Data game tidak lengkap untuk menampilkan statistik.");
            return;
        }

        System.out.println("\n========= STATISTIK PENCAPAIAN SPAKBOR HILLS =========");

        System.out.println("\n--- Keuangan ---");
        System.out.println("Total Pendapatan: " + player.getTotalGoldEarned() + "g");
        System.out.println("Total Pengeluaran: " + player.getTotalGoldSpent() + "g");
        System.out.println("Saldo Emas Saat Ini: " + player.getGold() + "g");

        int totalSeasonsPassed = farm.getSeasonController().getTotalSeasonsPassed();
        if (totalSeasonsPassed == 0 && farm.getSeasonController().getTotalDaysPassed() > 0) {
            totalSeasonsPassed = 1;
        }

        if (totalSeasonsPassed > 0) {
            System.out.printf("Rata-rata Pendapatan per Musim: %.2fg%n", (double) player.getTotalGoldEarned() / totalSeasonsPassed);
            System.out.printf("Rata-rata Pengeluaran per Musim: %.2fg%n", (double) player.getTotalGoldSpent() / totalSeasonsPassed);
        } else {
            System.out.println("Rata-rata Pendapatan per Musim: N/A (belum satu musim penuh)");
            System.out.println("Rata-rata Pengeluaran per Musim: N/A (belum satu musim penuh)");
        }

        System.out.println("\n--- Durasi Permainan ---");
        System.out.println("Total Hari Bermain: " + farm.getSeasonController().getTotalDaysPassed() + " hari");
        System.out.println("Musim Saat Ini: " + farm.getCurrentSeason() + ", Hari ke-" + farm.getCurrentDayInSeason());

        System.out.println("\n--- Status Hubungan NPC ---");
        List<NPC> allNpcs = npcFactory.getAllNPCs(); // Asumsi NPCFactory punya metode ini
        if (allNpcs.isEmpty()) {
            System.out.println("Tidak ada data NPC.");
        } else {
            for (NPC npc : allNpcs) {
                NPCInteractionStats stats = player.getNpcStats(npc.getName());
                stats.setCurrentRelationshipStatus(npc.getStatus());
                System.out.println("- " + npc.getName() + ":");
                System.out.println("  Status Hubungan: " + stats.getCurrentRelationshipStatus() + " (" + npc.getAffection() + " hati)");
                System.out.println("  Frekuensi Chat: " + stats.getChatFrequency());
                System.out.println("  Frekuensi Hadiah: " + stats.getGiftFrequency());
                System.out.println("  Frekuensi Kunjungan: " + stats.getVisitFrequency());
            }
        }

        System.out.println("\n--- Hasil Panen ---");
        Map<String, Integer> cropsHarvested = player.getCropsHarvestedCount();
        if (cropsHarvested.isEmpty()) {
            System.out.println("Belum ada tanaman yang dipanen.");
        } else {
            cropsHarvested.forEach((cropName, count) -> 
                System.out.println("- " + cropName + ": " + count + " buah")
            );
        }

        System.out.println("\n--- Hasil Memancing ---");
        System.out.println("Total Ikan Ditangkap: " + player.getTotalFishCaught());
        Map<FishRarity, Integer> fishByRarity = player.getFishCaughtByRarity();
        System.out.println("  Rincian berdasarkan Kelangkaan:");
        if (fishByRarity.isEmpty() && player.getTotalFishCaught() == 0) {
             System.out.println("  Belum ada ikan yang ditangkap.");
        } else {
            for (FishRarity rarity : FishRarity.values()) {
                 System.out.println("  - " + rarity.toString() + ": " + fishByRarity.getOrDefault(rarity, 0) + " ekor");
            }
        }
        System.out.println("======================================================");
    }

    private static void handleInteractCommand() throws InterruptedException {
        GameMap currentMap = farm.getCurrentMap();
        if (currentMap == null) { System.out.println("Tidak bisa berinteraksi: peta saat ini tidak diketahui."); return; }

        NPC adjacentNpc = getAdjacentNPC(player, currentMap);
        if (adjacentNpc != null) {
            handleNPCInteractionSubMenu(adjacentNpc);
            return;
        }

        if (player.getCurrentLocationName().equals("Player's House")) {
            String adjacentObjectInHouse = InteractionHelper.getAdjacentInteractableObject(player, currentMap);
            if (PlayerHouseMap.BED_ID.equals(adjacentObjectInHouse)) {
                System.out.println("Beranjak tidur...");
                player.performAction(new SleepingAction(), farm);
                GameDriver.savedSession = false;
                displayFullStatus(); return;
            } else if (PlayerHouseMap.STOVE_ID.equals(adjacentObjectInHouse)) {
                System.out.println("Memasak...");
                farm.getTimeController().pauseGameTime();
                handleStoveInteraction();
                farm.getTimeController().resumeGameTime();
                return;
            } 
        }

        String adjacentObjectId = InteractionHelper.getAdjacentInteractableObject(player, currentMap);
        if (adjacentObjectId != null) {
            if (FarmMap.SHIPPING_BIN_ID.equals(adjacentObjectId) && player.getCurrentLocationName().equals("Farm")) {
                farm.getTimeController().pauseGameTime();

                System.out.println("Anda bisa melihat isi bin dengan 'viewbin' atau menjual item.");
                System.out.print("Masukkan nama item untuk dijual (atau ketik 'lihat' untuk isi bin, 'batal' untuk keluar): ");
                String itemToSellName = scanner.nextLine().trim();

                if (itemToSellName.equalsIgnoreCase("batal")) {
                    farm.getTimeController().resumeGameTime();
                    return;
                }
                if (itemToSellName.equalsIgnoreCase("lihat")) {
                    displayShippingBinContents();
                    farm.getTimeController().resumeGameTime();
                    return;
                }

                Item itemToSell = player.getInventory().getItemByName(itemToSellName);
                if (itemToSell != null) {
                    int availableQty = player.getInventory().getItemQuantity(itemToSell);
                    System.out.println("Anda memiliki " + availableQty + " " + itemToSell.getName() + ".");
                    System.out.print("Berapa banyak yang ingin dijual? (1-" + availableQty + ", atau 'semua'): ");
                    String qtyInput = scanner.nextLine().trim().toLowerCase();
                    int quantityToSell = 0;

                    if (qtyInput.equals("semua")) {
                        quantityToSell = availableQty;
                    } else {
                        try {
                            quantityToSell = Integer.parseInt(qtyInput);
                        } catch (NumberFormatException e) {
                            System.out.println("Input jumlah tidak valid.");
                            farm.getTimeController().resumeGameTime();
                            return;
                        }
                    }

                    if (quantityToSell > 0 && quantityToSell <= availableQty) {
                        player.performAction(new SellingAction(itemToSell, quantityToSell), farm);
                        GameDriver.savedSession = false;
                    } else if (quantityToSell > availableQty) {
                        System.out.println("Tidak memiliki " + itemToSell.getName() + " untuk dijual sebanyak itu.");
                    } else {
                        System.out.println("Jumlah yang dimasukkan tidak valid.");
                    }
                } else {
                    System.out.println("Item '" + itemToSellName + "' tidak ditemukan di inventory.");
                }

                farm.getTimeController().resumeGameTime();
                return;
            }
            else if ((FarmMap.POND_ID.equals(adjacentObjectId) && player.getCurrentLocationName().equals("Farm")) ||
                     (ForestMap.RIVER_WATER_ID.equals(adjacentObjectId) && player.getCurrentLocationName().equals("Forest Zone")) ||
                     (MountainMap.LAKE_WATER_ID.equals(adjacentObjectId) && player.getCurrentLocationName().equals("Mountain Area")) ||
                     (CoastalMap.OCEAN_WATER_ID.equals(adjacentObjectId) && player.getCurrentLocationName().equals("Coastal Region"))) {
                System.out.println("Berinteraksi dengan perairan untuk memancing...");
                farm.getTimeController().pauseGameTime();
                player.performAction(new FishingAction(), farm);
                farm.getTimeController().resumeGameTime();
                GameDriver.savedSession = false;
            } else {
                System.out.println("Anda bersebelahan dengan '" + adjacentObjectId + "', tapi interaksi spesifik belum didefinisikan.");
            }
        } else {
            System.out.println("Tidak ada yang spesifik untuk interaksi di sini.");
        }
    }

    private static void handleStoveInteraction() {
        OngoingCooking currentTask = farm.getCurrentCookingTaskInfo();

        if (currentTask != null && !currentTask.isClaimed()) {
            if (currentTask.isReadyToClaim()) {
                System.out.println(currentTask.getCookedItemName() + " sudah matang!");
                System.out.print("Apakah kamu ingin mengambilnya? (y/n) > ");
                String claimChoice = scanner.nextLine().trim().toLowerCase();
                if (claimChoice.equals("y")) {
                    Food claimedFood = farm.claimCookedFood(player);
                    GameDriver.savedSession = false;
                    if (claimedFood != null) {
                        System.out.print("Ingin memasak sesuatu yang baru? (y/n) > ");
                        if (scanner.nextLine().trim().toLowerCase().equals("y")) {
                            handleStartCookingProcess();
                        }
                    } else {
                        System.out.println("Gagal mengambil makanan.");
                    }
                } else {
                    System.out.println(currentTask.getCookedItemName() + " tetap di Stove.");
                }
            } else {
                System.out.println(currentTask.getCookedItemName() + " sedang dimasak. Periksa lagi nanti.");
            }
        } else {
            System.out.println("Stove tidak sedang digunakan.");
            handleStartCookingProcess();
        }
    }

    private static void handleStartCookingProcess() {
        System.out.println("\n--- Mulai Memasak Baru ---");
        List<Recipe> availableRecipes = RecipeDataRegistry.getAvailableRecipesForPlayer(player);

        if (availableRecipes == null || availableRecipes.isEmpty()) {
            System.out.println("Belum mengetahui resep apapun atau tidak bisa membuat resep saat ini.");
            return;
        }

        System.out.println("Resep yang Tersedia untuk Dimasak:");
        for (int i = 0; i < availableRecipes.size(); i++) {
            Recipe r = availableRecipes.get(i);
            System.out.print((i + 1) + ". " + r.getCookedItemName() + " (Bahan: ");
            boolean firstIngredient = true;
            for(Map.Entry<String, Integer> ingredient : r.getIngredients().entrySet()){
                if(!firstIngredient) System.out.print(", ");
                System.out.print(ingredient.getKey() + " x" + ingredient.getValue());
                firstIngredient = false;
            }
            System.out.println(")");
        }
        System.out.println("0. Batal");
        System.out.print("Pilih resep untuk dimasak (nomor): ");

        String choiceStr = scanner.nextLine().trim();
        try {
            int choice = Integer.parseInt(choiceStr);
            if (choice == 0) { System.out.println("Membatalkan memasak."); return; }
            if (choice > 0 && choice <= availableRecipes.size()) {
                Recipe selectedRecipe = availableRecipes.get(choice - 1);
                player.performAction(new CookingAction(selectedRecipe), farm);
                GameDriver.savedSession = false;
            } else { System.out.println("Pilihan resep tidak valid."); }
        } catch (NumberFormatException e) { System.out.println("Input tidak valid, masukkan nomor."); }
    }

    private static NPC getAdjacentNPC(Player player, GameMap map) {
        int px = player.getX();
        int py = player.getY();
        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

        for (int[] dir : directions) {
            Tile adjacentTile = map.getTileAtPosition(px + dir[0], py + dir[1]);
            if (adjacentTile != null && adjacentTile.isOccupied() && adjacentTile.getObjectOnTile() instanceof NPC) {
                return (NPC) adjacentTile.getObjectOnTile();
            }
        }
        return null;
    }

    private static void handleNPCInteractionSubMenu(NPC npc) throws InterruptedException{
        System.out.println("\nBerinteraksi dengan " + npc.getName() + " (Suka: " + npc.getAffection() + ", Status: " + npc.getStatus() + ")");
        System.out.println("Pilih aksi:");
        System.out.println("1. Chat");
        System.out.println("2. Gift");
        System.out.println("3. Propose");
        System.out.println("4. Marry");
        if (npc.getName().equalsIgnoreCase("Emily") && player.getCurrentLocationName().equals("Store")) {
            System.out.println("5. Shop");
        }
        if (npc.getName().equalsIgnoreCase("Dasco")) {
            System.out.println("5. Gamble");
        }
        System.out.println("0. Kembali");
        System.out.print("Pilihan Anda > ");
        String choice = scanner.nextLine().trim();

        Action npcAction = null;
        switch (choice) {
            case "1":
                npcAction = new ChatAction(npc);
                GameDriver.savedSession = false;
                break;
            case "2": 
                System.out.print("Masukkan nama item untuk diberikan: ");
                String itemName = scanner.nextLine().trim();
                Item itemToGift = player.getInventory().getItemByName(itemName);
                if (itemToGift != null) {
                    npcAction = new GiftAction(npc, itemToGift, 1);
                    GameDriver.savedSession = false;
                } else {
                    System.out.println("Item '" + itemName + "' tidak ditemukan di inventory.");
                }
                break;
            case "3":
                npcAction = new ProposeAction(npc);
                GameDriver.savedSession = false;
                break;
            case "4": 
                npcAction = new MarryAction(npc);
                GameDriver.savedSession = false;
                break;
            case "5":
                if (npc.getName().equalsIgnoreCase("Emily") && player.getCurrentLocationName().equals("Store")) {
                    handleShopInteraction(npc); 
                } else if (npc.getName().equalsIgnoreCase("Dasco")) {
                    clearConsole();
                    handleGambling();
                } else {
                    System.out.println("Opsi tidak valid.");
                }
                break;
            case "0":
                System.out.println("Kembali...");
                return;
            default:
                System.out.println("Pilihan tidak valid.");
                break;
        }

        if (npcAction != null) {
            player.performAction(npcAction, farm);
        }
    }

    private static void handleShopInteraction(NPC shopkeeper) {
        System.out.println("\n--- Selamat Datang di Toko " + shopkeeper.getName() + "! ---");
        boolean shopping = true;
        while (shopping) {
            System.out.println("\nKategori Barang (Emasmu: " + player.getGold() + "g):");
            System.out.println("1. Seeds");
            System.out.println("2. Food"); 
            System.out.println("3. Misc (Lain-lain & Resep)");
            System.out.println("0. Keluar Toko");
            System.out.print("Pilih kategori > ");
            String categoryChoice = scanner.nextLine().trim();

            List<? extends Item> itemsForSale = new ArrayList<>();
            String categoryName = "";

            switch (categoryChoice) {
                case "1":
                    itemsForSale = SeedDataRegistry.getSeedsForSeason(farm.getCurrentSeason());
                    categoryName = "Benih";
                    break;
                case "2":
                    itemsForSale = FoodDataRegistry.getPurchasableFood();
                    categoryName = "Makanan Jadi";
                    break;
                case "3":
                    List<Misc> purchasableMisc = MiscDataRegistry.getPurchasableMiscItems();
                    
                    itemsForSale = purchasableMisc;
                    categoryName = "Lain-lain & Resep";
                    break;
                case "0":
                    shopping = false;
                    System.out.println("Terima kasih telah berbelanja!");
                    continue;
                default:
                    System.out.println("Kategori tidak valid.");
                    continue;
            }

            if (itemsForSale.isEmpty()) {
                System.out.println("Tidak ada barang yang tersedia di kategori '" + categoryName + "' saat ini.");
                continue;
            }

            System.out.println("\nBarang Tersedia di Kategori '" + categoryName + "':");
            for (int i = 0; i < itemsForSale.size(); i++) {
                Item item = itemsForSale.get(i);
                int buyPrice = 0; 
                if (item instanceof Seeds) buyPrice = ((Seeds)item).getBuyPrice();
                else if (item instanceof Food) buyPrice = ((Food)item).getBuyPrice();
                else if (item instanceof Misc) buyPrice = ((Misc)item).getBuyPrice();

                System.out.println((i + 1) + ". " + item.getName() + " - " + buyPrice + "g");
            }
            System.out.println("0. Kembali ke Kategori");
            System.out.print("Pilih barang untuk dibeli (nomor) > ");
            String itemChoiceStr = scanner.nextLine().trim();
            try {
                int itemIndex = Integer.parseInt(itemChoiceStr) - 1;
                if (itemChoiceStr.equals("0")) continue;

                if (itemIndex >= 0 && itemIndex < itemsForSale.size()) {
                    Item selectedItemToBuy = itemsForSale.get(itemIndex);
                    int itemPrice = 0;
                    if (selectedItemToBuy instanceof Seeds) itemPrice = ((Seeds)selectedItemToBuy).getBuyPrice();
                    else if (selectedItemToBuy instanceof Food) itemPrice = ((Food)selectedItemToBuy).getBuyPrice();
                    else if (selectedItemToBuy instanceof Misc) itemPrice = ((Misc)selectedItemToBuy).getBuyPrice();

                    System.out.print("Berapa banyak " + selectedItemToBuy.getName() + " yang ingin dibeli? > ");
                    String quantityStr = scanner.nextLine().trim();
                    int quantity = Integer.parseInt(quantityStr);

                    if (quantity > 0) {
                        int totalCost = itemPrice * quantity;
                        if (player.getGold() >= totalCost) {
                            player.spendGold(totalCost);
                            player.obtainItem(selectedItemToBuy, quantity);
                            GameDriver.savedSession = false;
                            System.out.println("Beli " + quantity + " " + selectedItemToBuy.getName() + " seharga " + totalCost + "g.");
                            System.out.println("Sisa gold: " + player.getGold() + "g.");
                        } else {
                            System.out.println("Gold tidak cukup untuk membeli " + quantity + " " + selectedItemToBuy.getName() + ".");
                        }
                    } else {
                        System.out.println("Jumlah tidak valid.");
                    }
                } else {
                    System.out.println("Pilihan barang tidak valid.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Input tidak valid.");
            }
        }
    }

    private static void handleGambling() throws InterruptedException{
        System.out.println("Wilkommen in Dascos Verbotenem Versteck!");
        boolean gambling = true;
        while (gambling) {
            System.out.println();
            System.out.println("Pick your nestapa:");
            System.out.println("1. Selot");
            System.out.println("2. Jack Hitam");
            System.out.println("3. Cek Saldo");
            System.out.println("0. Tobat");
            System.out.print("Obviously Considered Choice > ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    farm.getTimeController().pauseGameTime();
                    slotMinigame();
                    farm.getTimeController().resumeGameTime();
                    GameDriver.savedSession = false;
                    continue;
                case "2":
                    clearConsole();
                    farm.getTimeController().pauseGameTime();
                    Blackjack session = new Blackjack(player);
                    session.playRound();
                    farm.getTimeController().resumeGameTime();
                    GameDriver.savedSession = false;
                    continue;
                case "3":
                    clearConsole();
                    System.out.println("Saldo: " + player.getGold() + "g | Mending tobat sebelum rungkat.");
                    continue;
                case "0":
                    gambling = false;
                    clearConsole();
                    System.out.println("Akhirnya sadar dan tobat.");
                    continue;
                default:
                    System.out.println("Pilihan tidak bijak.");
                    continue;
            }
        }
    }

    private static void slotMinigame() throws InterruptedException {
        clearConsole();
        String[] reels = new String[3];
        int[] ctr = new int[4];
        Random random = new Random();
        System.out.println();
        System.out.println("\nPLACE YOUR BET!");
        System.out.print("Uang yang ingin dihamburkan: ");
        String moneyGambled = scanner.nextLine().trim();
        int money = Integer.parseInt(moneyGambled);
        if (player.getGold() >= money) {
            player.spendGold(money);
        } else {
            System.out.println("Kalo miskin jangan judi bleug. Kerja sono!");
            return;
        }
        
        System.out.println("\nThe Result is...");
        for (int i = 0; i < 3; i++) {
            if (random.nextDouble() < 0.25) {
                reels[i] = "hati"; ctr[0]++;
            } else if (random.nextDouble() < 0.5) {
                reels[i] = "buah"; ctr[1]++;
            } else if (random.nextDouble() < 0.75) {
                reels[i] = "lonceng"; ctr[2]++;
            } else {
                reels[i] = "7"; ctr[3]++;
            }
            
            Thread.sleep(1000);
            System.out.println(reels[i]);
        }

        System.out.println();
        if (ctr[1] == 2) {
            System.out.println("Selamat! Anda win 2x lipat uang anda!");
            player.setGold(player.getGold() + (money * 2));
        } else if (ctr[1] == 3) {
            System.out.println("Selamat! Anda win 3x lipat uang anda!");
            player.setGold(player.getGold() + (money * 3));
        } else if (ctr[2] == 3 || ctr[0] == 3) {
            System.out.println("Beuh win 5x uang anda! Bakal rungkad Dasco mah.");
            player.setGold(player.getGold() + (money * 5));
        } else if (ctr[3] == 3) {
            System.out.println("GACORRR KINGG! Menang 10x lipat Dasco rungkad parah!");
            player.setGold(player.getGold() + (money * 10));
        } else {
            System.out.println("Yah kalah. Kata aing mending tobat sih. Mending kerja halal.");
        }
        System.out.println();
    }

    private static void displayMainMenu() {
        clearConsole();
        System.out.println("\n=========================");
        System.out.println("    SPAKBOR HILLS RPG");
        System.out.println("=========================");
        System.out.println("        MENU UTAMA");
        System.out.println("-------------------------");
        System.out.println("1. New Game");
        System.out.println("2. Load Game");
        System.out.println("3. Help");
        System.out.println("4. Credits");
        System.out.println("5. Exit");
        System.out.println("-------------------------");
    }

    private static void displayHelpScreen() {
        clearConsole();
        System.out.println("\n--- Bantuan Game ---");
        System.out.println("Selamat datang di Spakbor Hills, game simulasi bertani dan kehidupan!");
        System.out.println("Ayo mengelola kebun, menanam tanaman, mancing keribu- maksudku ikan, berinteraksi dengan penduduk,");
        System.out.println("dan membangun kehidupan yang makmur.");
        System.out.println("\nCara Bermain:");
        System.out.println("- Gunakan perintah seperti 'move', 'till', 'plant', 'water', 'harvest' untuk mengelola kebunmu! Jangan lupa equip item yang sesuai.");
        System.out.println("- 'interact' dengan objek dalam rumahmu untuk tidur dan memasak, Shipping Bin untuk menjual, atau perairan untuk memancing!");
        System.out.println("- Kelola energi dan waktumu. Tidur memulihkan energi!");
        System.out.println("- Jelajahi area berbeda dengan bergerak ke pojok atas peta.");
        System.out.println("- Ketik 'help' di dalam game untuk daftar perintah aksi spesifik.");
        System.out.println("-----------------");
        
    }

    private static void displayShippingBinContents() {
        clearConsole();
        if (farm == null || farm.getShippingBin() == null) {
            System.out.println("Tidak ada game aktif atau Shipping Bin tidak tersedia.");
            return;
        }
        ShippingBin bin = farm.getShippingBin();
        List<Item> itemsInBin = bin.getItems(); 

        System.out.println("\n--- Isi Bin ---");
        if (itemsInBin.isEmpty()) {
            System.out.println("Shipping Bin kosong.");
        } else {
            Map<String, Integer> itemCounts = new HashMap<>();
            for (Item item : itemsInBin) {
                itemCounts.put(item.getName(), itemCounts.getOrDefault(item.getName(), 0) + 1);
            }
            itemCounts.forEach((name, count) -> System.out.println("- " + name + ": " + count));
        }
        System.out.println("Kapasitas: " + bin.getCurrentSize() + "/" + (bin.getMaxCapacity() > 0 ? bin.getMaxCapacity() : "Tak Terbatas"));
        System.out.println("---------------------------");
    }

    private static void displayCredits() throws InterruptedException {
        clearConsole();
        System.out.println("\n--- Credits ---");
        System.out.println("Game Concept & Design: Team 11 of Section 01");
        Thread.sleep(1000);
        System.out.println("Lead Programmer: Dr. Asep Spakbor");
        Thread.sleep(1000);
        System.out.println("Susah banget integrasi apalagi GUI T_T");
        Thread.sleep(1000);
        System.out.println("HE DOPE JOKE COW WE!");
        Thread.sleep(1000);
        System.out.println("Wi kod de kod, not onle prom de kod. Wi ret de kod, not nowing hau tu komben.");
        Thread.sleep(1000);
        System.out.println("Send help pls bug everywhere. My glock looks kinda tempting lately.");
        Thread.sleep(750);
        System.out.println("Terima kasih telah bermain!");
        System.out.println("---------------");
        Thread.sleep(3000);
    }

    private static void displayInGameHelp() {
        clearConsole();
        System.out.println("\n--- Perintah Dalam Game ---");
        System.out.println("  move [up|down|left|right] [langkah (opsional)] - Gerakkan pemain.");
        System.out.println("  interact                  - Berinteraksi dengan objek sekitar atau struktur yang dimasuki.");
        System.out.println("  till                      - Mencangkul petak saat ini.");
        System.out.println("  plant [nama_benih]        - Menanam benih di tanah yang dicangkul.");
        System.out.println("  recover                   - Mengembalikan tanah dicangkul menjadi normal atau mencabut benih.");
        System.out.println("  water                     - Menyiram petak yang ditanami.");
        System.out.println("  harvest                   - Memanen tanaman dewasa.");
        System.out.println("  eat [nama_makanan]        - Mengonsumsi item makanan dari inventory.");
        System.out.println("  learn [nama_resep_item]   - Mempelajari resep dari item di inventory.");
        System.out.println("  inv                       - Tampilkan inventory.");
        System.out.println("  equip [nama_alat]         - Memegang alat dari inventory.");
        System.out.println("  unequip                   - Melepas alat yang dipegang.");
        System.out.println("  viewbin                   - Lihat isi Shipping Bin.");
        System.out.println("  info                      - Lihat atribut detail pemain.");
        System.out.println("  stats                     - Lihat statistik pemain.");
        System.out.println("  map                       - Tampilkan peta dan waktu saat ini.");
        System.out.println("  sleep                     - Lanjut ke hari berikutnya.");
        System.out.println("  save                      - Simpan sesi saat ini.");
        System.out.println("  mainmenu                  - Kembali ke Menu Utama (sesi game saat ini akan berakhir).");
        System.out.println("  quit                      - Keluar dari aplikasi.");
        System.out.println("------------------------");
    }

    private static void displayPlayerStatusDetailed() {
        clearConsole();
        if (player == null) { System.out.println("Tidak ada game aktif untuk menampilkan info pemain."); return; }
        System.out.println("\n--- Informasi Pemain ---");
        System.out.println("1. Nama    : " + player.getName());
        System.out.println("2. Gender  : " + player.getGender());
        System.out.println("3. Energi  : " + player.getEnergy() + "/" + Player.MAX_ENERGY);
        System.out.print("4. Pasangan: ");
        if (player.getPartner() == null || player.getPartner().isEmpty()) { System.out.println("Belum ada");
        } else { player.getPartner().forEach(p -> System.out.print(p.getName() + " ")); System.out.println();}
        System.out.println("5. Emas    : " + player.getGold() + "g");
        System.out.println("--------------------------");
    }

    private static void displayPlayerStatistics() {
        clearConsole();
        if (player == null) {
            System.out.println("Tidak ada game aktif untuk menampilkan statistik.");
            return;
        }
        System.out.println("\nMenampilkan Statistik Pemain Saat Ini...");
        displayEndGameStatistics();
    }

    private static String combineParts(String[] parts, int startIndex) { 
        StringBuilder sb = new StringBuilder();
        for (int i = startIndex; i < parts.length; i++) { sb.append(parts[i]); if (i < parts.length - 1) sb.append(" ");}
        return sb.toString();
    }
    
    private static void displayPlayerStatus() {
        if (player == null) return;
        System.out.println("\n--- Status ---");
        System.out.println("Pemain: " + player.getName() + " | Lokasi: " + player.getCurrentLocationName() + " (" + player.getX() + "," + player.getY() + ")");
        System.out.println("Energi: " + player.getEnergy() + "/" + Player.MAX_ENERGY + " | Emas: " + player.getGold() + "g");
        if (player.getHeldItem() != null) {
            System.out.println("Memegang: " + player.getHeldItem().getName());
        } else {
            System.out.println("Memegang: Tidak ada");
        }
        System.out.println("--------------");
    }

    private static void displayInventory() {
        clearConsole();
        if (player == null || player.getInventory() == null) { System.out.println("Tidak ada game aktif atau inventory."); return; }
        System.out.println("\n--- Inventory ---");
        Map<Item, Integer> items = player.getInventory().getInventoryMap(); // Pastikan getInventoryMap() ada
        if (items == null || items.isEmpty()) { System.out.println("Inventory kosong."); }
        else { for (Map.Entry<Item, Integer> entry : items.entrySet()) { System.out.println("- " + entry.getKey().getName() + ": " + entry.getValue()); } }
        System.out.println("-----------------");
    }

    private static void displayFarmStatus() { 
        if (farm == null || player == null) { System.out.println("Tidak ada game aktif."); return; }
        System.out.println("\n----- Status Farm & Dunia -----");
        System.out.println("Lokasi Saat Ini: " + player.getCurrentLocationName());
        if (farm.getSeasonController() != null && farm.getTimeController() != null && farm.getWeatherController() != null) {
            System.out.println("Tanggal: " + farm.getCurrentSeason().toString() + ", Hari ke-" + farm.getCurrentDayInSeason());
            System.out.println("Waktu: " + farm.getFormattedTime());
            System.out.println("Cuaca: " + farm.getCurrentWeather().toString());
        } else { System.out.println("Info waktu/musim/cuaca tidak tersedia.");}
        System.out.println("-----------------------------");
    }

    private static void displayFullStatus() {
        displayPlayerStatus();
        displayFarmStatus();
        if(farm != null && farm.getCurrentMap() != null && player != null) farm.getCurrentMap().display(player);
    }
}
