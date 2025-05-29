package com.spakbor.action;
import com.spakbor.cls.core.*;
import com.spakbor.cls.items.*;
import com.spakbor.cls.world.*;
import com.spakbor.enums.*;

public class PlantingAction implements Action {
    private static final long serialVersionUID = 1L;

    private final int PLANTING_ENERGY_COST = 5;
    private final int PLANTING_TIME_COST = 5;
    private Seeds seedToPlant;

    public PlantingAction(Seeds seedToPlant) {
        if (seedToPlant == null) {
            throw new IllegalArgumentException("Seed to plant cannot be null.");
        }
        this.seedToPlant = seedToPlant;
    }

    @Override
    public boolean validate(Player player, Farm farm) {
        GameMap currentPlayersMap = farm.getCurrentMap();
        FarmMap playersActualFarmMap = farm.getFarmMap();
        if (!player.getInventory().hasItem(seedToPlant) || player.getInventory().getItemQuantity(seedToPlant) <= 0) {
            System.out.println(seedToPlant.getName() + " tidak ada di inventory.");
            return false;
        }
        if (!(currentPlayersMap instanceof FarmMap) || !currentPlayersMap.getMapName().equals(playersActualFarmMap.getMapName())) {
             System.out.println("Planting hanya bisa di farm.");
             return false;
        }

        FarmMap farmMap = farm.getFarmMap();
        Tile currentTile = farmMap.getTileAtPosition(player.getX(), player.getY());

        if (currentTile == null) {
            System.out.println("Tile ga valid.");
            return false;
        }
        if (currentTile.getState() != TileState.TILLED) {
            System.out.println("Belum dicangkul.");
            return false;
        }
        if (!seedToPlant.canBePlantedIn(farm.getCurrentSeason()) && !seedToPlant.canBePlantedIn(farm.getCurrentSeason()) || farm.getCurrentSeason().equals(SeasonType.WINTER)) {
            System.out.println(seedToPlant.getName() + " ga bisa ditanem pas " + farm.getCurrentSeason().toString() + ".");
            return false;
        }
        if (player.getEnergy() + 20 < PLANTING_ENERGY_COST) {
            System.out.println("Ah capek ah.");
            return false;
        }
        return true;
    }

    @Override
    public void execute(Player player, Farm farm) {
        player.setEnergy(player.getEnergy() - PLANTING_ENERGY_COST);
        farm.advanceGameTime(PLANTING_TIME_COST);

        player.getInventory().useItem(seedToPlant, 1);

        FarmMap farmMap = farm.getFarmMap();
        Tile currentTile = farmMap.getTileAtPosition(player.getX(), player.getY());

        PlantedCrop newPlant = new PlantedCrop(this.seedToPlant); 
        currentTile.setObjectOnTile(newPlant);
        currentTile.setState(TileState.PLANTED);
        if (farm.getCurrentWeather() == WeatherType.RAINY) {
            newPlant.setWateredToday(true);
        }

        System.out.println(player.getName() + " nanem " + seedToPlant.getName() + ".");
        farmMap.display(player);
    }
}