package com.spakbor.action;
import com.spakbor.cls.core.*;
import com.spakbor.cls.items.*;
import com.spakbor.cls.world.*;
import com.spakbor.data.*;
import com.spakbor.enums.*;


public class HarvestingAction implements Action {
    private static final long serialVersionUID = 1L;


    @Override
    public boolean validate(Player player, Farm farm) {
        GameMap currentPlayersMap = farm.getCurrentMap();
        FarmMap playersActualFarmMap = farm.getFarmMap();
        if (!(currentPlayersMap instanceof FarmMap) || !currentPlayersMap.getMapName().equals(playersActualFarmMap.getMapName())) {
            System.out.println("Harvesting hanya bisa di farm.");
            return false;
        }

        Tile currentTile = farm.getFarmMap().getTileAtPosition(player.getX(), player.getY());
        if (currentTile == null) {
            System.out.println("Posisi tidak valid.");
            return false;
        }

        if (currentTile.getState() != TileState.HARVESTABLE || !(currentTile.getObjectOnTile() instanceof PlantedCrop)) {
            System.out.println("Ga bisa harvest. Belom siap panen juga mungkin.");
            return false;
        }

        PlantedCrop plant = (PlantedCrop) currentTile.getObjectOnTile();
        if (!plant.isMature()) {
            System.out.println("Belom siap panen oi.");
            return false;
        }

        if (player.getEnergy() < 5) { 
            System.out.println("Tidak ada energi buat panen.");
            return false;
        }
        return true;
    }

    @Override
    public void execute(Player player, Farm farm) {
        Tile currentTile = farm.getCurrentMap().getTileAtPosition(player.getX(), player.getY()); 
        PlantedCrop plant = (PlantedCrop) currentTile.getObjectOnTile();

        player.setEnergy(player.getEnergy() - 5);
        farm.advanceGameTime(5);

        String cropName = plant.getResultingCropName();
        Crop cropToHarvest = CropDataRegistry.getCropByName(cropName); 

        if (cropToHarvest == null) {
            // System.err.println("Could not find crop '" + cropName + "' in registry during harvest. Aborting harvest.");
            player.setEnergy(player.getEnergy() + 5);
            return;
        }

        int amountHarvested = plant.getYieldAmountPerHarvest();
        player.obtainItem(cropToHarvest, amountHarvested); 
        player.recordCropHarvested(cropToHarvest.getName(), amountHarvested);
        farm.addCropped(amountHarvested);

        System.out.println(player.getName() + " manen " + amountHarvested + " " + cropToHarvest.getName() + ".");
        System.out.println("Energi: " + player.getEnergy());

        currentTile.setObjectOnTile(null);
        currentTile.setState(TileState.TILLED);
        farm.getCurrentMap().display(player);
    }
}