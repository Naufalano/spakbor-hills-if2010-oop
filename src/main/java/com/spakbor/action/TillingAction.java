package action;
import cls.core.*;
import cls.items.*;
import cls.world.*;
import enums.*;

public class TillingAction implements Action {
    private Item hoe;

    public TillingAction() {
        // Hoe diperiksa di validate
        this.hoe = new Equipment("Hoe"); 
    }

    @Override
    public boolean validate(Player player, Farm farm) {
        GameMap currentPlayersMap = farm.getCurrentMap();
        FarmMap playersActualFarmMap = farm.getFarmMap();

        if (currentPlayersMap == null || playersActualFarmMap == null) {
            System.out.println("Data peta tidak tersedia.");
            return false;
        }

        if (!(currentPlayersMap instanceof FarmMap) || !currentPlayersMap.getMapName().equals(playersActualFarmMap.getMapName())) {
             System.out.println("Tilling hanya bisa dilakukan di farm milikmu (" + playersActualFarmMap.getMapName() + "). Lokasi saat ini: " + player.getCurrentLocationName());
             return false;
        }


        Item heldItem = player.getHeldItem();
        if (heldItem == null || !(heldItem instanceof Equipment) || !heldItem.getName().equalsIgnoreCase("Hoe")) {
            System.out.println("Kamu harus memegang Hoe untuk tilling.");
            return false;
        }

        if (player.getEnergy() < 5) {
            System.out.println("Energi tidak cukup untuk mencangkul.");
            return false;
        }

        Tile currentTile = currentPlayersMap.getTileAtPosition(player.getX(), player.getY());

        if (currentTile == null) {
            System.out.println("Pemain tidak berada di tile yang valid.");
            return false;
        }
        if (currentTile.getState() != TileState.TILLABLE && currentTile.getState() != TileState.DEFAULT) {
            System.out.println("Tile ini tidak bisa dicangkul (status saat ini: " + currentTile.getState() + ").");
            return false;
        }
        if (currentTile.isOccupied()) {
            System.out.println("Tile ini sedang ditempati.");
            return false;
        }
        return true;
    }

    @Override
    public void execute(Player player, Farm farm) {
        player.setEnergy(player.getEnergy() - 5);
        farm.advanceGameTime(5);

        GameMap currentMap = farm.getCurrentMap();
        Tile currentTile = currentMap.getTileAtPosition(player.getX(), player.getY());
        if (currentTile != null) {
            currentTile.setState(TileState.TILLED);
            System.out.println(player.getName() + " mencangkul tanah di (" + player.getX() + "," + player.getY() + "). Energi: " + player.getEnergy());
            currentMap.display(player);
        } else {
            System.err.println("Tile pemain tidak valid.");
        }
    }
}
