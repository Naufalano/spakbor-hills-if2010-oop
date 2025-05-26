import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
                    System.out.println("\nSudah jam 2:00 PAGI! Waktunya tidur otomatis. (Tekan enter)");
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
                    case "learn":
                        if (parts.length >= 2) {
                            String recipeItemName = combineParts(parts, 1);
                            Item itemInInventory = player.getInventory().getItemByName(recipeItemName);
                            if (itemInInventory instanceof RecipeItem) {
                                actionToPerform = new LearnRecipeAction((RecipeItem) itemInInventory);
                            } else {
                                System.out.println("'" + recipeItemName + "' bukan item resep yang bisa dipelajari atau tidak ada di inventory.");
                            }
                        } else {
                            System.out.println("Format: learn [nama_item_resep_dari_inventory]");
                        }
                        break;
                    case "sleep": actionToPerform = new SleepingAction(); break;
                    case "inv": displayInventory(); break;
                    case "equip":
                        if (parts.length >= 2) {
                            String itemName = combineParts(parts, 1);
                            Item itemToEquip = player.getInventory().getItemByName(itemName);
                            if (itemToEquip != null) {
                                if (itemToEquip instanceof Equipment) { 
                                    player.holdItem(itemToEquip);
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
                        break;
                    case "sell":
                         if (parts.length == 2) {
                            String itemName = combineParts(parts, 1);
                            Item itemToSell = player.getInventory().getItemByName(itemName);
                            if (itemToSell != null) { actionToPerform = new SellingAction(itemToSell, 1); }
                            else { System.out.println("Item '" + itemName + "' tidak ditemukan di inventaris.");}
                        } else if (parts.length > 2 ) {
                            String itemName = combineParts(parts, 1, parts.length - 2);
                            Item itemToSell = player.getInventory().getItemByName(itemName);
                            String itemAmt = combineParts(parts, parts.length - 1);
                            int amt = Integer.parseInt(itemAmt);
                            if (itemToSell != null) { actionToPerform = new SellingAction(itemToSell, amt); }
                            else { System.out.println("Item '" + itemName + "' tidak ditemukan di inventaris.");}
                        } else { System.out.println("Format: sell [nama_item]");}
                        break;
                    case "viewbin":
                        displayShippingBinContents();
                        break;
                    case "info": displayPlayerStatusDetailed(); break;
                    case "stats": displayPlayerStatistics(); break;
                    case "status": displayFarmStatus(); break;
                    case "map": if (farm.getCurrentMap() != null) farm.getCurrentMap().display(player); break;
                    case "toggletime": timeDisplayEnabled = !timeDisplayEnabled; System.out.println("Tampilan jam periodik " + (timeDisplayEnabled ? "diaktifkan." : "dinonaktifkan.")); break;
                    case "help": displayInGameHelp(); break;
                    case "quit": currentGameState = GameState.EXITING; inGamePlaying = false; break;
                    case "nd": farm.nextDay(); displayFullStatus(); break;
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

        NPC adjacentNpc = getAdjacentNPC(player, currentMap);
        if (adjacentNpc != null) {
            handleNPCInteractionSubMenu(adjacentNpc);
            return; // Interaksi NPC selesai
        }

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
                int amt = scanner.nextInt();
                if (itemToSellName.equalsIgnoreCase("batal")) return;
                Item itemToSell = player.getInventory().getItemByName(itemToSellName);
                if (itemToSell != null) { player.performAction(new SellingAction(itemToSell, amt), farm); }
                else { System.out.println("Item '" + itemToSellName + "' tidak ditemukan di inventaris."); }
            }
            else if ((FarmMap.POND_ID.equals(adjacentObjectId) && player.getCurrentLocationName().equals("Farm")) ||
                     (ForestMap.RIVER_WATER_ID.equals(adjacentObjectId) && player.getCurrentLocationName().equals("Forest Zone")) ||
                     (MountainMap.LAKE_WATER_ID.equals(adjacentObjectId) && player.getCurrentLocationName().equals("Mountain Area")) ||
                     (CoastalMap.OCEAN_WATER_ID.equals(adjacentObjectId) && player.getCurrentLocationName().equals("Coastal Region"))) {
                System.out.println("Berinteraksi dengan perairan untuk memancing...");
                player.performAction(new FishingAction(), farm);
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

        if (availableRecipes == null || availableRecipes.isEmpty()) {
            System.out.println("Anda belum mengetahui resep apapun yang bisa dimasak dengan bahan yang ada, atau belum memenuhi syarat unlock.");
            return;
        }
        System.out.println("Resep yang Dapat Anda Masak Saat Ini:");
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
                System.out.println("Mencoba memasak: " + selectedRecipe.getCookedItemName());
                player.performAction(new CookingAction(selectedRecipe), farm);
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

    private static void handleNPCInteractionSubMenu(NPC npc) {
        System.out.println("\nBerinteraksi dengan " + npc.getName() + " (Suka: " + npc.getAffection() + ", Status: " + npc.getStatus() + ")");
        System.out.println("Pilih aksi:");
        System.out.println("1. Chat");
        System.out.println("2. Gift");
        System.out.println("3. Propose");
        System.out.println("4. Marry");
        if (npc.getName().equalsIgnoreCase("Emily") && player.getCurrentLocationName().equals("Store")) {
            System.out.println("5. Shop");
        }
        System.out.println("0. Kembali");
        System.out.print("Pilihan Anda > ");
        String choice = scanner.nextLine().trim();

        Action npcAction = null;
        switch (choice) {
            case "1":
                npcAction = new ChatAction(npc);
                break;
            case "2": 
                System.out.print("Masukkan nama item untuk diberikan: ");
                String itemName = scanner.nextLine().trim();
                Item itemToGift = player.getInventory().getItemByName(itemName);
                if (itemToGift != null) {
                    npcAction = new GiftAction(npc, itemToGift, 1);
                } else {
                    System.out.println("Item '" + itemName + "' tidak ditemukan di inventaris.");
                }
                break;
            case "3":
                npcAction = new ProposeAction(npc);
                break;
            case "4": 
                npcAction = new MarryAction(npc);
                break;
            case "5":
                if (npc.getName().equalsIgnoreCase("Emily") && player.getCurrentLocationName().equals("Store")) {
                    handleShopInteraction(npc); 
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
            System.out.println("\nKategori Barang (Emas Anda: " + player.getGold() + "g):");
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
                    // Filter RecipeItem dari Misc
                    // List<RecipeItem> purchasableRecipeItems = purchasableMisc.stream()
                    // .filter(item -> item instanceof RecipeItem)
                    // .map(item -> (RecipeItem) item)
                    // .filter(rItem -> !player.hasLearnedRecipe(rItem.getRecipeIdToUnlock()))
                    // .collect(Collectors.toList());
                    // itemsForSale = purchasableRecipeItems;
                    
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
                // else if (item instanceof Equipment) buyPrice = ((Equipment)item).getBuyPrice();

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
                            System.out.println("Beli " + quantity + " " + selectedItemToBuy.getName() + " seharga " + totalCost + "g.");
                            System.out.println("Sisa gold: " + player.getGold() + "g.");

                            if (selectedItemToBuy instanceof RecipeItem) {
                                System.out.println("Karena beli resep, langsung sikat!");
                                player.learnRecipe(((RecipeItem) selectedItemToBuy).getRecipeIdToUnlock());
                            }

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
    private static void displayShippingBinContents() {
        if (farm == null || farm.getShippingBin() == null) {
            System.out.println("Tidak ada game aktif atau kotak pengiriman tidak tersedia.");
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
    private static void displayCredits() {
        System.out.println("\n--- Credits ---");
        System.out.println("Game Concept & Design: Team 11 of Section 01");
        System.out.println("Lead Programmer: Dr. Asep Spakbor");
        System.out.println("Susah banget integrasi apalagi GUI T_T");
        System.out.println("HE DOPE JOKE COW WE!");
        System.out.println("Wi kod de kod, not onle prom de kod. Wi ret de kod, not nowing hau to komben.");
        System.out.println("Send help pls bug everywhere. My glock looks kinda tempting lately.");
        System.out.println("Terima kasih telah bermain!");
        System.out.println("---------------");
    }
    private static void displayInGameHelp() {
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
        System.out.println("  status                    - Tampilkan status waktu, tanggal, dan cuaca saat ini.");
        System.out.println("  map                       - Tampilkan peta saat ini.");
        System.out.println("  sleep                     - Lanjut ke hari berikutnya.");
        System.out.println("  toggletime                - Aktifkan/nonaktifkan tampilan jam periodik di konsol.");
        System.out.println("  mainmenu                  - Kembali ke Menu Utama (sesi game saat ini akan berakhir).");
        System.out.println("  quit                      - Keluar dari aplikasi.");
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
    private static String combineParts(String[] parts, int startIdx, int endIdx) { 
        StringBuilder sb = new StringBuilder();
        for (int i = startIdx; i < endIdx; i++) { sb.append(parts[i]); if (i < endIdx - 1) sb.append(" ");}
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
        if (farm != null && farm.getTimeController() != null) System.out.println("Waktu: " + farm.getFormattedTime()); else System.out.println("Waktu: N/A");
        System.out.println("--------------");
    }
    private static void displayInventory() {
        if (player == null || player.getInventory() == null) { System.out.println("Tidak ada game aktif atau inventory."); return; }
        System.out.println("\n--- Inventaris ---");
        Map<Item, Integer> items = player.getInventory().getInventoryMap(); // Pastikan getInventoryMap() ada
        if (items == null || items.isEmpty()) { System.out.println("Inventory kosong."); }
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
