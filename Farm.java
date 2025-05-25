import java.util.HashMap;
import java.util.Map;

public class Farm {
    private String name; 
    private Player player;
    private NPCFactory npcFactory;
    private int totalGold;
    private int croppedCrop;
    private int fishCaught;

    // Game state controllers
    private TimeController timeController;
    private SeasonController seasonController;
    private Weather weatherController;
    private int days = 0;

    // World map management
    private Map<String, GameMap> worldMaps; 
    private GameMap currentMap;

    // Components specific to the player's farm (logical instances)
    private House house;
    private ShippingBin shippingBin;

    // Flags for managing automatic sleep at 2 AM
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
            "Dasco's Lair", "Perry's Place", "Caroline's Home", 
            "Mayor's Manor", "Abigail's Room"
        };
        String[] npcHouseDoorIdsInTown = {
            TownMap.DASCO_HOUSE_ENTRANCE_ID, TownMap.PERRY_HOUSE_ENTRANCE_ID,
            TownMap.CAROLINE_HOUSE_ENTRANCE_ID, TownMap.MAYOR_HOUSE_ENTRANCE_ID,
            TownMap.ABIGAIL_HOUSE_ENTRANCE_ID
        };

        for (int i = 0; i < npcNamesForHouses.length; i++) {
            NPC resident = npcFactory.getNPC(npcNamesForHouses[i]);
            if (resident != null) {
                this.worldMaps.put(npcHouseMapKeys[i], new GenericInteriorMap(npcHouseMapKeys[i], resident));
            }
        }


        this.house = new House(0, 0, 0, 0);
        this.shippingBin = new ShippingBin();

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
        GameMap mapToLoad = worldMaps.get(mapName);
        if (mapToLoad != null) {
            this.currentMap = mapToLoad;
            this.player.setCurrentLocationName(this.currentMap.getMapName());
            int[] entryPoint = this.currentMap.getEntryPoint(comingFromMapName);
            this.player.setLocation(entryPoint[0], entryPoint[1]);

            System.out.println("\nTransitioned to " + this.currentMap.getMapName() + ".");
            System.out.println("Player at (" + player.getX() + "," + player.getY() + ")");
            // this.currentMap.display(this.player);
        } else {
            System.err.println("Error: Map '" + mapName + "' not found in worldMaps! Player remains on: " + (currentMap != null ? currentMap.getMapName() : "undefined map"));
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
        System.err.println("Error: Player's FarmMap instance not found in worldMaps with key 'Farm'.");
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
            System.out.println("Earnings from Shipping Bin: " + earningsToday + "g.");
        }
        shippingBin.getItems().clear(); 

        seasonController.nextDay();
        weatherController.nextDay(); 

        timeController.resetTime();

        System.out.println("\n--- A new day has begun! ---");
        System.out.println("Date: " + getCurrentSeason().toString() + ", Day " + getCurrentDayInSeason());
        System.out.println("Weather: " + getCurrentWeather().toString());

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

        if (specificFarmMap.getTiles() == null) {
             return;
        }

        for (Tile tile : specificFarmMap.getTiles()) {
            if (tile.getState() == TileState.PLANTED && tile.getObjectOnTile() instanceof PlantedCrop) {
                PlantedCrop plant = (PlantedCrop) tile.getObjectOnTile();
                boolean shouldRemovePlant = plant.grow(currentFarmSeason);

                if (shouldRemovePlant) {
                    tile.setObjectOnTile(null);
                    tile.setState(TileState.TILLED);
                    System.out.println("A plant at (" + tile.getX() + "," + tile.getY() + ") on the farm has withered.");
                } else {
                    if (plant.isMature()) {
                        tile.setState(TileState.HARVESTABLE);
                        System.out.println("A plant at (" + tile.getX() + "," + tile.getY() + ") on the farm is now harvestable!");
                    }
                }
            } else if (tile.getState() == TileState.HARVESTABLE && !(tile.getObjectOnTile() instanceof PlantedCrop)) {
                System.out.println("Warning: Farm tile ("+tile.getX()+","+tile.getY()+") is HARVESTABLE but has no crop data. Resetting to Tilled.");
                tile.setState(TileState.TILLED);
            }
        }
    }

    /**
     * Advances the in-game time by a specified number of minutes.
     * Also checks for automatic sleep if time passes 2:00 AM.
     * @param minutes The number of game minutes to advance.
     */
    public void advanceGameTime(int minutes) {
        this.timeController.getGameTime().advanceMinutes(minutes);
        if (this.timeController.getGameTime().getHour() == 2 && !isSleepingScheduled()) {
            System.out.println("\nIt's 2:00 AM due to your actions! Time to sleep automatically.");
            scheduleAutomaticSleep();
            System.out.print("Press enter to proceed.");
        }
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
    public String getName() { return name; } 

    public String getFormattedTime() { return timeController.getFormattedTime(); }
    public SeasonType getCurrentSeason() { return seasonController.getCurrentSeason(); }
    public int getCurrentDayInSeason() { return seasonController.getCurrentDayInSeason(); }
    public WeatherType getCurrentWeather() { return weatherController.getTodayWeather(); }
    public int getTotalDaysPassed() { return this.days; }

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
