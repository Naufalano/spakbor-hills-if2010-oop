import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

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
    private static Timer gameTimer;
    private static volatile boolean timeDisplayEnabled = false;
    private static GameState currentGameState = GameState.MAIN_MENU;
    private static boolean milestoneReachedGold = false;
    private static boolean milestoneReachedMarriage = false;
    private static final int GOLD_MILESTONE_TARGET = 17209;

    public static void main(String[] args) throws IOException{
        System.out.println("Memulai Spakbor Hills RPG...");

        while (currentGameState != GameState.EXITING) {
            switch (currentGameState) {
                case MAIN_MENU:
                    mainMenuLoop();
                    break;
                case IN_GAME:
                    if (player == null || farm == null) {
                        currentGameState = GameState.MAIN_MENU;
                    } else {
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

    private static void mainMenuLoop() {
        System.out.println("\n=========================");
        System.out.println("    SPAKBOR HILLS RPG");
        System.out.println("=========================");
        System.out.println("        MENU UTAMA");
        System.out.println("-------------------------");
        System.out.println("1. New Game");
        System.out.println("2. Help");
        System.out.println("3. Credits");
        System.out.println("4. Exit");
        System.out.println("-------------------------");
        System.out.print("Pilih opsi (1-4): ");

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
                displayHelpScreen();
                break;
            case "3":
                displayCredits();
                break;
            case "4":
                currentGameState = GameState.EXITING;
                break;
            default:
                System.out.println("Opsi tidak valid. Silakan coba lagi.");
                break;
        }
    }

    private static void initializeNewGame() {
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
        if (coal != null) playerInventory.addItem(coal, 10);

        Seeds parsnipSeeds = SeedDataRegistry.getSeedByName("Parsnip Seeds");
        if (parsnipSeeds != null) playerInventory.addItem(parsnipSeeds, 15);

        System.out.println("\nGame baru dimulai untuk " + player.getName() + " di " + farm.getName() + "!");
    }

    private static void startGameTimer() {
        if (gameTimer != null) { gameTimer.cancel(); gameTimer.purge(); }
        gameTimer = new Timer(true);
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (currentGameState != GameState.IN_GAME || farm == null || farm.getTimeController() == null) {
                    return;
                }
                farm.getTimeController().updateTime();
                Time currentTime = farm.getTimeController().getGameTime();
                if (currentTime.getHour() == 2 && currentTime.getMinute() >= 0 && !farm.isSleepingScheduled()) {
                    System.out.println("\nSudah jam 2:00 PAGI! Waktunya tidur otomatis.");
                    farm.scheduleAutomaticSleep();
                }
                if (timeDisplayEnabled) {
                    System.out.println("[Jam Real-time: " + farm.getFormattedTime() + "]");
                }
            }
        }, 0, 1000);
    }

    private static void stopGameTimer() {
        if (gameTimer != null) {
            gameTimer.cancel();
            gameTimer.purge();
            gameTimer = null;
        }
    }

    private static void inGameLoop() throws IOException{
        System.out.println("\n--- Selamat datang di " + player.getCurrentLocationName() + "! ---");
        displayFullStatus();

        boolean inGamePlaying = true;
        while (inGamePlaying && currentGameState == GameState.IN_GAME) {
            if (farm.isAutomaticSleepScheduled()) {
                System.out.println("\nMemproses tidur otomatis karena sudah larut malam...");
                player.performAction(new SleepingAction(true), farm); 
                farm.clearAutomaticSleepSchedule();
                farm.loadMap("Farm", null);
                displayFullStatus();
            }

            checkAndDisplayMilestones();

            System.out.println("\nAksi Dalam Game (Ketik 'help' untuk perintah, 'mainmenu' untuk kembali):");
            System.out.print(player.getCurrentLocationName() + " > ");

            if (!scanner.hasNextLine()) { currentGameState = GameState.EXITING; break; }
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.isEmpty()) continue;
            
            String[] parts = input.split("\\s+");
            String command = parts[0];
            Action actionToPerform = null;

            try {
                if (command.equals("mainmenu")) {
                    System.out.println("Kembali ke Menu Utama...");
                    stopGameTimer();
                    player = null; farm = null; // Bersihkan state game saat ini
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
                            } catch (IllegalArgumentException e) { System.out.println("Arah atau langkah tidak valid. Contoh: move up 1"); }
                        } else if (parts.length == 2) {
                            try {
                                Direction dir = Direction.valueOf(parts[1].toUpperCase());
                                int steps = 1;
                                actionToPerform = new MovingAction(dir, steps);
                            } catch (IllegalArgumentException e) { System.out.println("Arah tidak valid. Contoh: move up"); }
                        } else { System.out.println("Format: move [up|down|left|right] [langkah]"); }
                        break;
                    case "interact":
                        handleInteractCommand();
                        break;
                    case "till": actionToPerform = new TillingAction(); break;
                    case "plant":
                        if (parts.length >= 2) {
                            String seedName = combineParts(parts, 1);
                            Item seedItem = player.getInventory().getItemByName(seedName);
                            if (seedItem instanceof Seeds) { actionToPerform = new PlantingAction((Seeds) seedItem); }
                            else { System.out.println("'" + seedName + "' bukan benih valid atau tidak ada di inventaris."); }
                        } else { System.out.println("Format: plant [nama_benih]");}
                        break;
                    case "recover": // Atau "pulihkanlahan"
                        actionToPerform = new RecoverLandAction();
                        break;
                    case "water": actionToPerform = new WateringAction(); break;
                    case "harvest": actionToPerform = new HarvestingAction(); break;
                    case "eat":
                        if (parts.length >= 2) {
                            String foodName = combineParts(parts, 1);
                            Item foodItem = player.getInventory().getItemByName(foodName);
                            if (foodItem instanceof EdibleItem) { actionToPerform = new EatingAction(foodItem); }
                            else { System.out.println("'" + foodName + "' tidak bisa dimakan atau tidak ada di inventaris."); }
                        } else { System.out.println("Format: eat [nama_makanan]");}
                        break;
                    case "sleep": actionToPerform = new SleepingAction(); break;
                    case "inventory": displayInventory(); break;
                    case "sell":
                         if (parts.length >= 2) {
                            String itemName = combineParts(parts, 1);
                            Item itemToSell = player.getInventory().getItemByName(itemName);
                            if (itemToSell != null) { actionToPerform = new SellingAction(itemToSell); }
                            else { System.out.println("Item '" + itemName + "' tidak ditemukan di inventaris.");}
                        } else { System.out.println("Format: sell [nama_item]");}
                        break;
                    case "playerinfo": displayPlayerStatusDetailed(); break;
                    case "stats": displayPlayerStatistics(); break;
                    case "farmstatus": displayFarmStatus(); break;
                    case "map": if (farm.getCurrentMap() != null) farm.getCurrentMap().display(player); break;
                    case "nextday": farm.nextDay(); displayFullStatus(); break;
                    case "toggletime": timeDisplayEnabled = !timeDisplayEnabled; System.out.println("Tampilan jam periodik " + (timeDisplayEnabled ? "diaktifkan." : "dinonaktifkan.")); break;
                    case "help": displayInGameHelp(); break;
                    case "quitgame": currentGameState = GameState.EXITING; inGamePlaying = false; break;
                    default: System.out.println("Perintah tidak dikenal. Ketik 'help' untuk opsi."); break;
                }

                if (actionToPerform != null) {
                    boolean actionSuccess = player.performAction(actionToPerform, farm);
                    if (actionSuccess) {
                        GameMap mapBeforeAction = farm.getCurrentMap();
                        String locBeforeAction = player.getCurrentLocationName();

                        if (actionToPerform instanceof SleepingAction) {
                            displayFullStatus();
                        } else {
                            displayPlayerStatus();
                        }

                        if (!locBeforeAction.equals(player.getCurrentLocationName()) || farm.getCurrentMap() != mapBeforeAction) {
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

    private static void checkAndDisplayMilestones() {
        if (player == null || farm == null) return;

        boolean showStats = false;

        if (!milestoneReachedGold && player.getGold() >= GOLD_MILESTONE_TARGET) {
            milestoneReachedGold = true;
            showStats = true;
            System.out.println("\n======================================================");
            System.out.println("SELAMAT! Anda telah mencapai milestone kekayaan!");
            System.out.println("Total emas Anda: " + player.getGold() + "g");
            System.out.println("======================================================");
        }

        if (!milestoneReachedMarriage && player.isMarried()) {
            milestoneReachedMarriage = true;
            showStats = true;
            System.out.println("\n======================================================");
            System.out.println("SELAMAT! Anda telah menikah!");
            System.out.println("Pasangan Anda: " + (player.getPartner().isEmpty() ? "N/A" : player.getPartner().get(0).getName()));
            System.out.println("======================================================");
        }

        if (showStats) {
            displayEndGameStatistics();
            System.out.println("\nPermainan berlanjut... Anda bisa terus bermain dan mencapai hal lain!");
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
            totalSeasonsPassed = 1; // Jika masih di musim pertama tapi sudah ada hari berlalu
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
                // System.out.println("  Frekuensi Kunjungan: " + stats.getVisitFrequency());
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

    private static void handleInteractCommand() {
        GameMap currentMap = farm.getCurrentMap();
        if (currentMap == null) { System.out.println("Tidak bisa berinteraksi: peta saat ini tidak diketahui."); return; }

        if (player.getCurrentLocationName().equals("Player's House")) {
            String adjacentObjectInHouse = InteractionHelper.getAdjacentInteractableObject(player, currentMap);
            if (PlayerHouseMap.BED_ID.equals(adjacentObjectInHouse)) {
                System.out.println("Beranjak tidur...");
                player.performAction(new SleepingAction(), farm);
                displayFullStatus(); return;
            } else if (PlayerHouseMap.STOVE_ID.equals(adjacentObjectInHouse)) {
                System.out.println("Memasak...");
                handleCookInteraction(); return;
            } else if (PlayerHouseMap.DOOR_TO_FARM_ID.equals(adjacentObjectInHouse)) {
                farm.loadMap("Farm", "Player's House");
                displayFullStatus(); return;
            } 
        } else if (player.getCurrentLocationName().equals("Farm")) {
            String adjToHouseEntrance = InteractionHelper.getAdjacentInteractableObject(player, currentMap);
            if (FarmMap.HOUSE_ENTRANCE_EXTERIOR_ID.equals(adjToHouseEntrance)) {
                farm.loadMap("Player's House", "Farm");
                displayFullStatus(); return;
            }
        } else if (player.getCurrentLocationName().equals("Town")) {
            String adjacentObjectId = InteractionHelper.getAdjacentInteractableObject(player, currentMap);
            if (TownMap.STORE_ENTRANCE_ID.equals(adjacentObjectId)) {
                farm.loadMap("Store", "Town");
                displayFullStatus(); return;
            } else if (TownMap.DASCO_HOUSE_ENTRANCE_ID.equals(adjacentObjectId)) {
                farm.loadMap("Dasco's Lair", "Town");
                displayFullStatus(); return;
            } else if (TownMap.PERRY_HOUSE_ENTRANCE_ID.equals(adjacentObjectId)) {
                farm.loadMap("Perry's Place", "Town");
                displayFullStatus(); return;
            } else if (TownMap.ABIGAIL_HOUSE_ENTRANCE_ID.equals(adjacentObjectId)) {
                farm.loadMap("Abigail's Room", "Town");
                displayFullStatus(); return;
            } else if (TownMap.CAROLINE_HOUSE_ENTRANCE_ID.equals(adjacentObjectId)) {
                farm.loadMap("Caroline's Home", "Town");
                displayFullStatus(); return;
            } else if (TownMap.MAYOR_HOUSE_ENTRANCE_ID.equals(adjacentObjectId)) {
                farm.loadMap("Mayor's Manor", "Town");
                displayFullStatus(); return;
            } else if (TownMap.TOWN_EXIT_TO_FARM_ID.equals(adjacentObjectId)) {
                farm.loadMap("Farm", "Town");
                displayFullStatus(); return;
            }
        } else if (player.getCurrentLocationName().equals("Store")) {
            String adjacentObjectId = InteractionHelper.getAdjacentInteractableObject(player, currentMap);
            if (StoreMap.DOOR_ID.equals(adjacentObjectId)) {
                farm.loadMap("Town", "Store");
                displayFullStatus(); return;
            }
        } else if (GenericInteriorMap.DOOR_ID.equals(InteractionHelper.getAdjacentInteractableObject(player, currentMap)) && farm.getCurrentMap() instanceof GenericInteriorMap) {
            farm.loadMap("Town", player.getCurrentLocationName());
            displayFullStatus(); return;
        }

        String adjacentObjectId = InteractionHelper.getAdjacentInteractableObject(player, currentMap);
        if (adjacentObjectId != null) {
            if (FarmMap.SHIPPING_BIN_ID.equals(adjacentObjectId) && player.getCurrentLocationName().equals("Farm")) {
                System.out.println("Berinteraksi dengan Kotak Pengiriman.");
                System.out.print("Masukkan nama item untuk dijual (atau 'batal'): ");
                String itemToSellName = scanner.nextLine().trim();
                if (itemToSellName.equalsIgnoreCase("batal")) return;
                Item itemToSell = player.getInventory().getItemByName(itemToSellName);
                if (itemToSell != null) { player.performAction(new SellingAction(itemToSell), farm); }
                else { System.out.println("Item '" + itemToSellName + "' tidak ditemukan di inventaris."); }
            }
            else if ((FarmMap.POND_ID.equals(adjacentObjectId) && player.getCurrentLocationName().equals("Farm")) ||
                     (ForestMap.RIVER_WATER_ID.equals(adjacentObjectId) && player.getCurrentLocationName().equals("Forest Zone")) ||
                     (MountainMap.LAKE_WATER_ID.equals(adjacentObjectId) && player.getCurrentLocationName().equals("Mountain Area")) ||
                     (CoastalMap.OCEAN_WATER_ID.equals(adjacentObjectId) && player.getCurrentLocationName().equals("Coastal Region"))) {
                System.out.println("Berinteraksi dengan perairan untuk memancing...");
                player.performAction(new FishingAction(), farm);
            }
            else if (TownMap.STORE_ENTRANCE_ID.equals(adjacentObjectId) && player.getCurrentLocationName().equals("Town")) {
                System.out.println("Memasuki Toko...");
                farm.loadMap("Store", "Town"); displayFullStatus();
            }
            else if (StoreMap.DOOR_ID.equals(adjacentObjectId) && player.getCurrentLocationName().equals("Store")) {
                System.out.println("Keluar dari Toko...");
                farm.loadMap("Town", "Store"); displayFullStatus();
            }
            else if (adjacentObjectId.startsWith("DASCO_DOOR") && player.getCurrentLocationName().equals("Town")) { farm.loadMap("Dasco's Lair", "Town"); displayFullStatus(); }
            else if (adjacentObjectId.startsWith("PERRY_DOOR") && player.getCurrentLocationName().equals("Town")) { farm.loadMap("Perry's Place", "Town"); displayFullStatus(); }
            else if (adjacentObjectId.startsWith("CAROLINE_DOOR") && player.getCurrentLocationName().equals("Town")) { farm.loadMap("Caroline's Home", "Town"); displayFullStatus(); }
            else if (adjacentObjectId.startsWith("MAYOR_DOOR") && player.getCurrentLocationName().equals("Town")) { farm.loadMap("Mayor's Manor", "Town"); displayFullStatus(); }
            else if (adjacentObjectId.startsWith("ABIGAIL_DOOR") && player.getCurrentLocationName().equals("Town")) { farm.loadMap("Abigail's Room", "Town"); displayFullStatus(); }
            else if (GenericInteriorMap.DOOR_ID.equals(adjacentObjectId) && currentMap instanceof GenericInteriorMap) {
                 System.out.println("Keluar dari rumah NPC...");
                 farm.loadMap("Town", player.getCurrentLocationName()); displayFullStatus();
            }
            else if (TownMap.TOWN_EXIT_TO_FARM_ID.equals(adjacentObjectId) && player.getCurrentLocationName().equals("Town")) {
                System.out.println("Kembali ke Kebun...");
                farm.loadMap("Farm", "Town"); displayFullStatus();
            }
            else {
                System.out.println("Anda bersebelahan dengan '" + adjacentObjectId + "', tapi interaksi spesifik belum didefinisikan.");
            }
        } else {
            System.out.println("Tidak ada yang spesifik untuk diinteraksikan di sini.");
        }
    }

    private static void handleCookInteraction() {
        System.out.println("\n--- Memasak ---");
        List<Recipe> availableRecipes = RecipeDataRegistry.getAvailableRecipesForPlayer(player);

        if (availableRecipes.isEmpty()) {
            System.out.println("Anda belum memiliki resep yang bisa dimasak atau tidak memenuhi syarat resep lain.");
            return;
        }

        System.out.println("Resep yang Tersedia:");
        for (int i = 0; i < availableRecipes.size(); i++) {
            Recipe r = availableRecipes.get(i);
            System.out.println((i + 1) + ". " + r.getCookedItemName());
            // System.out.print("   Bahan: ");
            // r.getIngredients().forEach((name, qty) -> System.out.print(name + " x" + qty + ", "));
            // System.out.println();
        }
        System.out.println("0. Batal");
        System.out.print("Pilih resep untuk dimasak (nomor): ");

        String choiceStr = scanner.nextLine().trim();
        try {
            int choice = Integer.parseInt(choiceStr);
            if (choice == 0) {
                System.out.println("Membatalkan memasak.");
                return;
            }
            if (choice > 0 && choice <= availableRecipes.size()) {
                Recipe selectedRecipe = availableRecipes.get(choice - 1);
                System.out.println("Mencoba memasak: " + selectedRecipe.getCookedItemName());
                player.performAction(new CookingAction(selectedRecipe), farm);
            } else {
                System.out.println("Pilihan resep tidak valid.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Input tidak valid, masukkan nomor.");
        }
    }

    private static void displayHelpScreen() {
        System.out.println("\n--- Bantuan Game ---");
        System.out.println("Selamat datang di Spakbor Hills, game simulasi kebun dan kehidupan!");
        System.out.println("Tujuanmu adalah mengelola kebun, menanam tanaman, berinteraksi dengan penduduk,");
        System.out.println("dan membangun kehidupan yang makmur.");
        System.out.println("\nCara Bermain (Dalam Game):");
        System.out.println("- Gunakan perintah seperti 'move', 'till', 'plant', 'water', 'harvest' untuk mengelola kebunmu.");
        System.out.println("- 'interact' dengan objek seperti rumahmu (untuk tidur/memasak), kotak pengiriman (untuk menjual), atau perairan (untuk memancing).");
        System.out.println("- Kelola energi dan waktumu. Tidur memulihkan energi.");
        System.out.println("- Jelajahi area berbeda dengan bergerak ke tepi kebunmu.");
        System.out.println("- Ketik 'help' di dalam game untuk daftar perintah aksi spesifik.");
        System.out.println("-----------------");
    }
    private static void displayCredits() {
        System.out.println("\n--- Credits ---");
        System.out.println("Game Concept & Design: Team 11 of Section 01");
        System.out.println("Lead Programmer: Dr. Asep Spakbor");
        System.out.println("Susah banget integrasi apalagi GUI T_T");
        System.out.println("Terima kasih telah bermain!");
        System.out.println("---------------");
    }
    private static void displayInGameHelp() {
        System.out.println("\n--- Perintah Dalam Game ---");
        System.out.println("  move [up|down|left|right] [langkah] - Gerakkan pemain.");
        System.out.println("  interact             - Berinteraksi dengan objek sekitar atau struktur yang dimasuki.");
        System.out.println("  till                 - Mencangkul petak saat ini (butuh Cangkul).");
        System.out.println("  plant [nama_benih]   - Menanam benih di tanah yang dicangkul (butuh benih).");
        System.out.println("  recover              - Mengembalikan tanah dicangkul menjadi normal atau mencabut benih.");
        System.out.println("  water                - Menyiram petak yang ditanami (butuh Penyiram Air).");
        System.out.println("  harvest              - Memanen tanaman dewasa.");
        System.out.println("  eat [nama_makanan]   - Mengonsumsi item makanan dari inventaris.");
        System.out.println("  inventory            - Tampilkan inventaris.");
        System.out.println("  sell [nama_item]     - (Manual) Masukkan item ke kotak pengiriman (harus di sebelahnya).");
        System.out.println("  playerinfo           - Lihat atribut detail pemain.");
        System.out.println("  stats                - Lihat statistik pemain (placeholder).");
        System.out.println("  farmstatus           - Tampilkan status waktu, tanggal, dan cuaca saat ini.");
        System.out.println("  map                  - Tampilkan peta saat ini.");
        System.out.println("  nextday              - Lanjut ke hari berikutnya (juga dilakukan dengan tidur).");
        System.out.println("  toggletime           - Aktifkan/nonaktifkan tampilan jam periodik di konsol.");
        System.out.println("  mainmenu             - Kembali ke Menu Utama (sesi game saat ini akan berakhir).");
        System.out.println("  quitgame             - Keluar dari aplikasi.");
        System.out.println("------------------------");
    }
    private static void displayPlayerStatusDetailed() {
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
        if (farm != null && farm.getTimeController() != null) System.out.println("Waktu: " + farm.getFormattedTime()); else System.out.println("Waktu: N/A");
        System.out.println("--------------");
    }
    private static void displayInventory() {
        if (player == null || player.getInventory() == null) { System.out.println("Tidak ada game aktif atau inventaris."); return; }
        System.out.println("\n--- Inventaris ---");
        Map<Item, Integer> items = player.getInventory().getInventoryMap(); // Pastikan getInventoryMap() ada
        if (items == null || items.isEmpty()) { System.out.println("Inventaris kosong."); }
        else { for (Map.Entry<Item, Integer> entry : items.entrySet()) { System.out.println("- " + entry.getKey().getName() + ": " + entry.getValue()); } }
        System.out.println("-----------------");
    }
    private static void displayFarmStatus() { 
        if (farm == null || player == null) { System.out.println("Tidak ada game aktif."); return; }
        System.out.println("\n----- Status Kebun & Dunia -----");
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
