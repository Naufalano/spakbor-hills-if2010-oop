package action;
import cls.core.*;
import cls.items.*;
import cls.world.*;
import enums.*;

public class WateringAction extends Action {
    private Item wateringCan;

    public WateringAction() {
        this.wateringCan = new Equipment("Watering Can");
    }

    @Override
    public boolean validate(Player player, Farm farm) {
        Item heldItem = player.getHeldItem();
        if (heldItem == null || !(heldItem instanceof Equipment) || !heldItem.getName().equalsIgnoreCase("Watering Can")) {
            System.out.println("Anda harus memegang Watering Can untuk menyiram.");
            return false;
        }
        if (player.getEnergy() < 5) { 
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
        // if (!((PlantedCrop)currentTile.getObjectOnTile()).isWateredToday()) {}
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
            // System.out.println(player.getName() + " watered the plant at (" + player.getX() + "," + player.getY() + ").");
        } else {
            // System.out.println("Nothing to water here that requires it in this way.");
        }
        // System.out.println(player.getName() + " watered the plant at (" + player.getX() + "," + player.getY() + "). Energy: " + player.getEnergy());
        farmMap.display(player);
    }
}