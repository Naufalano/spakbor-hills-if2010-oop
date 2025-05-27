package action;
import cls.core.*;
import cls.items.*;
import cls.world.*;
import data.*;
import enums.*;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import utils.*;

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
        Item heldItem = player.getHeldItem();
        if (heldItem == null || !(heldItem instanceof Equipment) || !heldItem.getName().equalsIgnoreCase("Fishing Rod")) {
            System.out.println("Anda harus memegang Fishing Rod untuk memancing.");
            return false;
        }
        if (player.getEnergy() + 20 < ENERGY_COST) {
            System.out.println("Energi tidak cukup untuk memancing.");
            return false;
        }

        GameMap currentMap = farm.getCurrentMap();
        String actualFishingSpotObjectId = InteractionHelper.getAdjacentInteractableObject(player, currentMap);

        if (actualFishingSpotObjectId == null) {
            System.out.println("Tidak ada perairan valid.");
            return false;
        }

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
            // System.out.println("Could not determine the fishing spot. Action failed.");
            player.setEnergy(player.getEnergy() + ENERGY_COST);
            return;
        }
        // System.out.println(player.getName() + " casts the line into the " + fishingLocationName + "... Energy: " + player.getEnergy());


        SeasonType currentSeason = farm.getCurrentSeason();
        int currentHour = farm.getTimeController().getGameTime().getHour();
        WeatherType currentWeather = farm.getCurrentWeather();

        List<Fish> availableFish = FishDataRegistry.getAvailableFish(currentSeason, currentHour, currentWeather, fishingLocationName);

        if (availableFish.isEmpty()) {
            System.out.println("Ni ikan pada wareg apa gimana ya ga ada yang gigit...");
            return;
        }

        Fish fishToCatchPrototype = availableFish.get(random.nextInt(availableFish.size()));
        Fish caughtFishInstance = new Fish(fishToCatchPrototype.getName(), fishToCatchPrototype.getRarity(), fishToCatchPrototype.getAvailableSeasons(), fishToCatchPrototype.getAvailableTimeWindows(), fishToCatchPrototype.getAvailableWeathers(), fishToCatchPrototype.getLocations());

        System.out.println("Blablabla blebleble blublublu. Waduh kayaknya dapet " + caughtFishInstance.getName() + " (" + caughtFishInstance.getRarity() + ").");

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
        System.out.println("Tebak antara 1 sampe " + range + ". " + maxGuesses + " percobaan.");

        for (int i = 0; i < maxGuesses; i++) {
            System.out.print("Attempt " + (i + 1) + "/" + maxGuesses + ": Berapa coba? ");
            if (!scanner.hasNextInt()) {
                System.out.println("Masukkan angka valid. Mancing gagal.");
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
                System.out.println("Kekecilan!");
            } else {
                System.out.println("Kegedean!");
            }
            if (i < maxGuesses - 1) {
                 System.out.println((maxGuesses - 1 - i) + " kali coba lagi.");
             }
        }
        
        if (caught) {
            player.recordFishCaught(caughtFishInstance.getRarity(), caughtFishInstance.getName());
            System.out.println("Glepak glepak! Mata pancing yes dapet " + caughtFishInstance.getName() + "!");
            System.out.println(caughtFishInstance.getName() + " cuan " + caughtFishInstance.getSellPrice() + "g.");
            farm.addFish();
        } else {
            System.out.println("Ikannya kabur. Nebak angka " + numberToGuess + " aja susah amat cih.");
        }
    }
}
