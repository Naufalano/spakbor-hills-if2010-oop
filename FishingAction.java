import java.util.List;
import java.util.Random;
import java.util.Scanner;
// Assuming all necessary classes are imported/accessible

public class FishingAction extends Action {
    private Item fishingRod;
    private Random random = new Random();
    private Scanner scanner;

    private static final int ENERGY_COST = 5;
    private static final int TIME_COST_MINUTES = 15;

    public FishingAction() {
        this.fishingRod = new Equipment("Fishing Rod");
    }

    @Override
    public boolean validate(Player player, Farm farm) {
        if (!player.getInventory().hasItem(fishingRod)) {
            System.out.println("Fishing Rod not found in inventory.");
            return false;
        }
        if (player.getEnergy() < ENERGY_COST) {
            System.out.println("Not enough energy to fish (-" + ENERGY_COST + " energy).");
            return false;
        }

        GameMap currentMap = farm.getCurrentMap();
        String actualFishingSpotObjectId = InteractionHelper.getAdjacentInteractableObject(player, currentMap);

        if (actualFishingSpotObjectId == null) {
            System.out.println("You are not near a valid fishing spot (Pond, River, Lake, Ocean).");
            return false;
        }

        // Validate if the adjacent water type matches the current map's expected fishing spot
        String playerLocationName = player.getCurrentLocationName();
        boolean validSpotForMap = false;
        if (FarmMap.POND_ID.equals(actualFishingSpotObjectId) && playerLocationName.equals("Farm")) {
            validSpotForMap = true;
        } else if (ForestMap.RIVER_WATER_ID.equals(actualFishingSpotObjectId) && playerLocationName.equals("Forest Zone")) {
            validSpotForMap = true;
        } else if (MountainMap.LAKE_WATER_ID.equals(actualFishingSpotObjectId) && playerLocationName.equals("Mountain Area")) {
            validSpotForMap = true;
        } else if (CoastalMap.OCEAN_WATER_ID.equals(actualFishingSpotObjectId) && playerLocationName.equals("Coastal Region")) {
            validSpotForMap = true;
        }

        if (!validSpotForMap) {
            System.out.println("The water body you are next to is not a designated fishing spot for " + playerLocationName + " or is misidentified.");
            return false;
        }

        return true;
    }

    // getActualFishingSpotName maps the object ID to the generic location name for FishDataRegistry
    private String getActualFishingSpotName(String objectIdOnTile) {
        if (FarmMap.POND_ID.equals(objectIdOnTile)) return "Pond";
        if (ForestMap.RIVER_WATER_ID.equals(objectIdOnTile)) return "Forest River";
        if (MountainMap.LAKE_WATER_ID.equals(objectIdOnTile)) return "Mountain Lake";
        if (CoastalMap.OCEAN_WATER_ID.equals(objectIdOnTile)) return "Ocean";
        return null;
    }


    @Override
    public void execute(Player player, Farm farm) {
        this.scanner = new Scanner(System.in);

        player.setEnergy(player.getEnergy() - ENERGY_COST);
        farm.advanceGameTime(TIME_COST_MINUTES);

        GameMap currentMap = farm.getCurrentMap();
        String adjacentWaterObjectId = InteractionHelper.getAdjacentInteractableObject(player, currentMap);
        String fishingLocationName = getActualFishingSpotName(adjacentWaterObjectId);

        if (fishingLocationName == null) {
            System.out.println("Could not determine the fishing spot. Action failed.");
            player.setEnergy(player.getEnergy() + ENERGY_COST); // Refund energy
            // Consider refunding time if possible, or just note it.
            return;
        }
        System.out.println(player.getName() + " casts the line into the " + fishingLocationName + "... Energy: " + player.getEnergy());


        SeasonType currentSeason = farm.getCurrentSeason();
        int currentHour = farm.getTimeController().getGameTime().getHour();
        WeatherType currentWeather = farm.getCurrentWeather();

        List<Fish> availableFish = FishDataRegistry.getAvailableFish(currentSeason, currentHour, currentWeather, fishingLocationName);

        if (availableFish.isEmpty()) {
            System.out.println("Nothing seems to be biting here at this time...");
            return;
        }

        Fish fishToCatchPrototype = availableFish.get(random.nextInt(availableFish.size()));
        Fish caughtFishInstance = new Fish(fishToCatchPrototype.getName(), fishToCatchPrototype.getRarity(),
                                           fishToCatchPrototype.getAvailableSeasons(), fishToCatchPrototype.getAvailableTimeWindows(),
                                           fishToCatchPrototype.getAvailableWeathers(), fishToCatchPrototype.getLocations());

        System.out.println("You feel a tug... It might be a " + caughtFishInstance.getName() + " (" + caughtFishInstance.getRarity() + ").");

        int numberToGuess;
        int maxGuesses;
        int range;
        switch (caughtFishInstance.getRarity()) {
            case REGULAR:   range = 100; maxGuesses = 10; break;
            case LEGENDARY: range = 500; maxGuesses = 7;  break;
            case COMMON: default: range = 10;  maxGuesses = 10; break;
        }
        numberToGuess = random.nextInt(range) + 1;
        boolean caught = false;
        System.out.println("Guess the number between 1 and " + range + ". You have " + maxGuesses + " tries.");

        for (int i = 0; i < maxGuesses; i++) {
            System.out.print("Attempt " + (i + 1) + "/" + maxGuesses + ": Your guess? ");
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number. Fishing attempt failed.");
                if (scanner.hasNextLine()) scanner.nextLine();
                caught = false;
                break;
            }
            int guess = scanner.nextInt();
            if (scanner.hasNextLine()) scanner.nextLine();

            if (guess == numberToGuess) {
                caught = true;
                break;
            } else if (guess < numberToGuess) {
                System.out.println("Too low!");
            } else {
                System.out.println("Too high!");
            }
            if (i < maxGuesses - 1) {
                 System.out.println((maxGuesses - 1 - i) + " tries left.");
             }
        }

        if (caught) {
            player.getInventory().addItem(caughtFishInstance, 1);
            player.recordFishCaught(caughtFishInstance.getRarity());
            System.out.println("Congratulations! You caught a " + caughtFishInstance.getName() + "!");
            System.out.println(caughtFishInstance.getName() + " is worth " + caughtFishInstance.getSellPrice() + "g.");
            farm.addFish();
        } else {
            System.out.println("Oh no, the fish got away! The number was " + numberToGuess + ".");
        }
    }
}
