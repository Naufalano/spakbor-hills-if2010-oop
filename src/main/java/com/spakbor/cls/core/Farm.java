package com.spakbor.cls.core;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.spakbor.cls.items.Food;
import com.spakbor.cls.items.Item;
import com.spakbor.cls.world.CoastalMap;
import com.spakbor.cls.world.FarmMap;
import com.spakbor.cls.world.ForestMap;
import com.spakbor.cls.world.GameMap;
import com.spakbor.cls.world.GenericInteriorMap;
import com.spakbor.cls.world.MountainMap;
import com.spakbor.cls.world.PlayerHouseMap;
import com.spakbor.cls.world.StoreMap;
import com.spakbor.cls.world.Tile;
import com.spakbor.cls.world.TownMap;
import com.spakbor.data.NPCFactory;
import com.spakbor.enums.SeasonType;
import com.spakbor.enums.TileState;
import com.spakbor.enums.WeatherType;
import com.spakbor.system.SeasonController;
import com.spakbor.system.TimeController;
import com.spakbor.system.Weather;

public class Farm implements Serializable {
    private static final long serialVersionUID = 1L; 
    private String name; 
    private Player player;
    private NPCFactory npcFactory;
    private int totalGold;
    private int croppedCrop;
    private int fishCaught;

    private TimeController timeController;
    private SeasonController seasonController;
    private Weather weatherController;
    private int days = 0;

    private Map<String, GameMap> worldMaps; 
    private GameMap currentMap;

    private House house;
    private ShippingBin shippingBin;
    private OngoingCooking currentCookingTask;

    private boolean automaticSleepScheduled = false;
    private boolean isCurrentlySleeping = false;

    /**
     * Constructor for the Farm (main game state manager).
     * @param farmNameParam The name given to the player's farm.
     * @param player The player object.
     * @param npcFactory Factory to create and manage NPCs.
     */
    public Farm(String farmNameParam, Player player, NPCFactory npcFactory) {
        totalGold = 0;
        croppedCrop = 0;
        fishCaught = 0;
        this.name = farmNameParam;
        this.player = player;
        this.npcFactory = npcFactory;

        this.timeController = new TimeController();
        this.seasonController = new SeasonController();
        this.weatherController = new Weather();

        this.worldMaps = new HashMap<>();
        FarmMap playerFarmMap = new FarmMap();
        this.worldMaps.put(playerFarmMap.getMapName(), playerFarmMap);
        this.worldMaps.put("Player's House", new PlayerHouseMap());
        this.worldMaps.put("Forest Zone", new ForestMap());
        this.worldMaps.put("Mountain Area", new MountainMap());
        this.worldMaps.put("Coastal Region", new CoastalMap());
        this.worldMaps.put("Town", new TownMap());
        this.worldMaps.put("Store", new StoreMap(npcFactory));

        String[] npcNamesForHouses = {"Dasco", "Perry", "Caroline", "MayorTadi", "Abigail"};
        String[] npcHouseMapKeys = {
            "Dasco's Lair", "Perry's Place", "Caroline's Home", "Mayor's Manor", "Abigail's Room"
        };

        for (int i = 0; i < npcNamesForHouses.length; i++) {
            NPC resident = npcFactory.getNPC(npcNamesForHouses[i]);
            if (resident != null) {
                this.worldMaps.put(npcHouseMapKeys[i], new GenericInteriorMap(npcHouseMapKeys[i], resident));
            }
        }

        this.house = new House(0, 0, 0, 0);
        this.shippingBin = new ShippingBin();
        this.currentCookingTask = null;

        loadMap(playerFarmMap.getMapName(), null); 
        int[] initialSpawnPoint = playerFarmMap.getEntryPoint(null); 
        this.player.setLocation(initialSpawnPoint[0], initialSpawnPoint[1]);
        this.player.setCurrentLocationName(playerFarmMap.getMapName());
    }

    /**
     * Loads a specified map as the current map and positions the player at its entry point.
     * @param mapName The unique name (key) of the map to load from worldMaps.
     * @param comingFromMapName The name of the map the player is transitioning from, to determine the correct entry point.
     */
    public void loadMap(String mapName, String comingFromMapName) {
        // System.out.println("[DEBUG Farm.loadMap] Memuat peta: '" + mapName + "', dari: '" + comingFromMapName + "'");
        GameMap mapToLoad = worldMaps.get(mapName);
        if (mapToLoad != null) {
            this.currentMap = mapToLoad;
            this.player.setCurrentLocationName(this.currentMap.getMapName());
            // System.out.println("[DEBUG Farm.loadMap] player.currentLocationName diatur ke: '" + this.player.getCurrentLocationName() + "'");
            // System.out.println("[DEBUG Farm.loadMap] Nama peta tujuan (currentMap.getMapName()): '" + this.currentMap.getMapName() + "'");
            int[] entryPoint = this.currentMap.getEntryPoint(comingFromMapName);
            this.player.setLocation(entryPoint[0], entryPoint[1]);
            // System.out.println("[DEBUG Farm.loadMap] Pemain ditempatkan di peta '" + this.currentMap.getMapName() + "' pada koordinat: (" + player.getX() + "," + player.getY() + ")");

            System.out.println("\nPindah ke " + this.currentMap.getMapName() + ".");
            System.out.println("Player di (" + player.getX() + "," + player.getY() + ")");
            // this.currentMap.display(this.player);
        } else {
            System.err.println("Map '" + mapName + "' ga ada di worldMaps! Player tetap di: " + (currentMap != null ? currentMap.getMapName() : "undefined map"));
        }
    }

    /**
     * Retrieves the specific FarmMap instance that represents the player's personal farm.
     * This is useful for operations that always target the player's home farm (e.g., crop growth),
     * regardless of the player's current location.
     * @return The player's FarmMap instance, or null if not found (initialization error).
     */
    public FarmMap getFarmMap() {
        GameMap playerOwnedFarm = worldMaps.get("Farm"); 
        if (playerOwnedFarm instanceof FarmMap) {
            return (FarmMap) playerOwnedFarm;
        }
        System.err.println("'Farm' tidak ditemukan.");
        return null;
    }

    /**
     * Gets the map the player is currently on.
     * @return The current GameMap instance.
     */
    public GameMap getCurrentMap() {
        return currentMap;
    }

    /**
     * Advances the game to the next day, processing daily events.
     */
    public void nextDay() {
        int earningsToday = 0;
        for (Item item : shippingBin.getItems()) {
            earningsToday += item.getSellPrice();
        }
        player.setGold(player.getGold() + earningsToday);
        totalGold += earningsToday;
        if (earningsToday > 0) {
            System.out.println("Dapet gold dari Shipping Bin: " + earningsToday + "g.");
        }
        shippingBin.getItems().clear(); 

        seasonController.nextDay();
        weatherController.nextDay(); 

        timeController.resetTime();
        updateCookingProgress();

        System.out.println("\n--- Pagiku cerahku! Matahari di Bar- ehem. ---");
        System.out.println("Musim: " + getCurrentSeason().toString() + ", Hari " + getCurrentDayInSeason());
        System.out.println("Cuaca: " + getCurrentWeather().toString());

        FarmMap playerOwnedFarm = getFarmMap();
        if (playerOwnedFarm != null) {
            updateCropGrowthOnMap(playerOwnedFarm);
        }

        if (npcFactory != null && npcFactory.getAllNPCs() != null) {
            for (NPC npc : npcFactory.getAllNPCs()) {
                if ("Fiance".equals(npc.getStatus()) && npc.getEngaged() == 0) {
                    npc.setEngaged(1);
                }
            }
        }
        
        clearAutomaticSleepSchedule();
        days++;
    }

    /**
     * Updates the growth status of all crops on the specified FarmMap.
     * @param specificFarmMap The FarmMap instance to update crops on.
     */
    private void updateCropGrowthOnMap(FarmMap specificFarmMap) {
        SeasonType currentFarmSeason = seasonController.getCurrentSeason();
        WeatherType weatherToday = weatherController.getTodayWeather();

        boolean isRaining = (weatherToday == WeatherType.RAINY);
        if (specificFarmMap.getTiles() == null) {
             return;
        }

        for (Tile tile : specificFarmMap.getTiles()) {
            if (tile.getState() == TileState.PLANTED && tile.getObjectOnTile() instanceof PlantedCrop) {
                PlantedCrop plant = (PlantedCrop) tile.getObjectOnTile();
                boolean shouldRemovePlant = plant.grow(currentFarmSeason);
                
                if (isRaining) {
                    plant.setWateredToday(true);
                } 

                if (shouldRemovePlant) {
                    tile.setObjectOnTile(null);
                    tile.setState(TileState.TILLED);
                    System.out.println("Tanaman di farm (" + tile.getX() + "," + tile.getY() + ") mati.");
                } else {
                    if (plant.isMature()) {
                        tile.setState(TileState.HARVESTABLE);
                        System.out.println("Tanaman di farm (" + tile.getX() + "," + tile.getY() + ") udah bisa panen!");
                    }
                }
            } else if (tile.getState() == TileState.HARVESTABLE && !(tile.getObjectOnTile() instanceof PlantedCrop)) {
                System.out.println("Warning: Farm tile ("+tile.getX()+","+tile.getY()+") is HARVESTABLE but has no crop data. Resetting to Tilled.");
                tile.setState(TileState.TILLED);
            }
        }
    }

    public boolean startNewCookingProcess(Recipe recipe, Player player) {
        if (isStoveBusy()) {
            System.out.println("Stove sedang digunakan untuk memasak " + currentCookingTask.getCookedItemName() + ".");
            return false;
        }
        if (recipe == null || player == null) return false;

        this.currentCookingTask = new OngoingCooking(
            recipe,
            recipe.getResultItem(),
            seasonController.getTotalDaysPassed(),
            timeController.getGameTime()
        );
        System.out.println(recipe.getCookedItemName() + " sedang dimasak! Akan siap dalam " + Recipe.COOKING_DURATION_MINUTES + " menit.");
        return true;
    }

    public void updateCookingProgress() {
        if (currentCookingTask != null && !currentCookingTask.isReadyToClaim() && !currentCookingTask.isClaimed()) {
            if (currentCookingTask.checkIsReady(seasonController.getTotalDaysPassed(), timeController.getGameTime())) {
                System.out.println(currentCookingTask.getCookedItemName() + " sudah matang dan siap diambil dari Stove!");
            }
        }
    }

    public boolean isStoveBusy() {
        return currentCookingTask != null && !currentCookingTask.isClaimed();
    }

    public Food claimCookedFood(Player player) {
        if (currentCookingTask != null && currentCookingTask.isReadyToClaim() && !currentCookingTask.isClaimed()) {
            Food cookedFood = currentCookingTask.getResultItemPrototype();
            player.obtainItem(cookedFood, 1); // Tambahkan ke inventaris pemain
            currentCookingTask.setClaimed(true); // Tandai sudah diambil
            
            System.out.println(cookedFood.getName() + " berhasil diambil dan ditambahkan ke inventaris.");
            OngoingCooking claimedTask = currentCookingTask;
            currentCookingTask = null; 
            return claimedTask.getResultItemPrototype();
        }
        // System.out.println("Tidak ada makanan yang siap diambil atau sudah diambil.");
        return null;
    }

    /**
     * Advances the in-game time by a specified number of minutes.
     * Also checks for automatic sleep if time passes 2:00 AM.
     * @param minutes The number of game minutes to advance.
     */
    public void advanceGameTime(int minutes) {
        this.timeController.getGameTime().advanceMinutes(minutes);
        updateCookingProgress();
        if (this.timeController.getGameTime().getHour() == 2 && !isSleepingScheduled()) {
            System.out.println("\nOh tidak it's turu o'clock. Jam 2 malem ngapain bro? (Tekan enter)");
            scheduleAutomaticSleep();
        }
    }

    public void performFullPostLoadObjectConversion(Gson gsonInstance) {
        if (worldMaps == null || gsonInstance == null) {
            System.err.println("Farm.performFullPostLoadObjectConversion: worldMaps atau gsonInstance null. Melewatkan.");
            return;
        }
        System.out.println("Farm: Memulai konversi objek pasca-pemuatan untuk semua tile...");
        int convertedCount = 0;
        for (GameMap map : worldMaps.values()) {
            if (map != null && map.getTiles() != null) {
                // System.out.println("  Memproses peta: " + map.getMapName()); // Debug
                for (Tile tile : map.getTiles()) {
                    if (tile != null) {
                        boolean converted = tile.convertObjectOnTile(gsonInstance);
                        if (converted) {
                            convertedCount++;
                        }
                    }
                }
            }
        }
        // Pastikan currentMap juga diproses jika instance-nya berbeda (seharusnya tidak jika dikelola dengan benar)
        if (currentMap != null && !worldMaps.containsValue(currentMap) && currentMap.getTiles() != null) {
             System.out.println("  Memproses currentMap secara terpisah (seharusnya sudah ada di worldMaps)...");
             for (Tile tile : currentMap.getTiles()) {
                if (tile != null) {
                    boolean converted = tile.convertObjectOnTile(gsonInstance);
                    if (converted) convertedCount++;
                }
            }
        }

        System.out.println("Farm: Konversi objek pasca-pemuatan selesai. " + convertedCount + " objek dikonversi/diperiksa.");
    }

    public Player getPlayer() { return player; }
    public int getGold() { return totalGold; }
    public int getCrop() { return croppedCrop; }
    public int getFish() { return fishCaught; }
    public TimeController getTimeController() { return timeController; }
    public SeasonController getSeasonController() { return seasonController; }
    public Weather getWeatherController() { return weatherController; }
    public NPCFactory getNpcFactory() { return npcFactory; }
    public House getHouse() { return house; }
    public ShippingBin getShippingBin() { return shippingBin; }
    public OngoingCooking getCurrentCookingTaskInfo() { return currentCookingTask; }
    public String getName() { return name; } 

    public String getFormattedTime() { return timeController.getFormattedTime(); }
    public SeasonType getCurrentSeason() { return seasonController.getCurrentSeason(); }
    public int getCurrentDayInSeason() { return seasonController.getCurrentDayInSeason(); }
    public WeatherType getCurrentWeather() { return weatherController.getTodayWeather(); }
    public int getTotalDaysPassed() { return this.days; }

    public void setPlayer(Player player) { this.player = player; }
    public void scheduleAutomaticSleep() {
        if (!this.isCurrentlySleeping && !this.automaticSleepScheduled) {
            this.automaticSleepScheduled = true;
        }
    }
    public boolean isAutomaticSleepScheduled() { return this.automaticSleepScheduled; }
    public boolean isSleepingScheduled() { return this.isCurrentlySleeping || this.automaticSleepScheduled; }
    public void clearAutomaticSleepSchedule() {
        this.automaticSleepScheduled = false;
        this.isCurrentlySleeping = false;
    }
    public void setCurrentlySleeping(boolean status) {
        this.isCurrentlySleeping = status;
    }
    public void addCropped(int amt){
        croppedCrop += amt;
    }
    public void addFish(){
        fishCaught++;
    }
}
