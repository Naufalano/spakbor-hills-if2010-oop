package com.spakbor.action;
import com.spakbor.cls.core.Farm;
import com.spakbor.cls.core.PlantedCrop;
import com.spakbor.cls.core.Player;
import com.spakbor.cls.items.Equipment;
import com.spakbor.cls.items.Item;
import com.spakbor.cls.world.FarmMap;
import com.spakbor.cls.world.GameMap;
import com.spakbor.cls.world.Tile;
import com.spakbor.enums.TileState;

public class WateringAction implements Action {
    private static final long serialVersionUID = 1L;

    private Item wateringCan;

    public WateringAction() {
        this.wateringCan = new Equipment("Watering Can");
    }

    @Override
    public boolean validate(Player player, Farm farm) {
        Item heldItem = player.getHeldItem();
        if (heldItem == null || !(heldItem instanceof Equipment) || !heldItem.getName().equalsIgnoreCase("Watering Can")) {
            System.out.println("Kamu harus memegang Watering Can untuk menyiram.");
            return false;
        }
        if (player.getEnergy() + 20 < 5) { 
            System.out.println("Energi tidak cukup.");
            return false;
        }

        GameMap currentPlayersMap = farm.getCurrentMap();
        FarmMap playersActualFarmMap = farm.getFarmMap();
        if (!(currentPlayersMap instanceof FarmMap) || !currentPlayersMap.getMapName().equals(playersActualFarmMap.getMapName())) {
             System.out.println("Watering hanya bisa dilakukan di farm.");
             return false;
        }

        FarmMap farmMap = farm.getFarmMap();
        Tile currentTile = farmMap.getTileAtPosition(player.getX(), player.getY());

        if (currentTile == null) {
            System.out.println("Tile player tidak valid.");
            return false;
        }
        if (currentTile.getState() != TileState.PLANTED) {
            System.out.println("Tile tidak ditanami atau sudah disiram.");
            return false;
        }
        return true;
    }

    @Override
    public void execute(Player player, Farm farm) {
        player.setEnergy(player.getEnergy() - 5);
        farm.advanceGameTime(5);

        FarmMap farmMap = farm.getFarmMap();
        Tile currentTile = farm.getFarmMap().getTileAtPosition(player.getX(), player.getY());
        if (currentTile.getObjectOnTile() instanceof PlantedCrop) {
            PlantedCrop plant = (PlantedCrop) currentTile.getObjectOnTile();
            plant.setWateredToday(true);
        }
        farmMap.display(player);
    }
}