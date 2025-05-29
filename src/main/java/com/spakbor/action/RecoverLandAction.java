package com.spakbor.action;
import com.spakbor.cls.core.*;
import com.spakbor.cls.items.*;
import com.spakbor.cls.world.*;
import com.spakbor.enums.*;

public class RecoverLandAction implements Action {
    private static final long serialVersionUID = 1L;

    private Item pickaxe; 
    
    private static final int ENERGY_COST_PER_TILE = 5;
    private static final int TIME_COST_PER_TILE_MINUTES = 5;

    public RecoverLandAction() {
        this.pickaxe = new Equipment("Pickaxe");
    }

    @Override
    public boolean validate(Player player, Farm farm) {
        GameMap currentPlayersMap = farm.getCurrentMap();
        FarmMap playersActualFarmMap = farm.getFarmMap();

        if (currentPlayersMap == null || playersActualFarmMap == null) {
            System.out.println("Data peta tidak tersedia.");
            return false;
        }
        if (currentPlayersMap != playersActualFarmMap) {
             System.out.println("Recovery lahan hanya bisa dilakukan di farm milikmu (" + playersActualFarmMap.getMapName() + "). Lokasi saat ini: " + player.getCurrentLocationName());
             return false;
        }

        Item heldItem = player.getHeldItem();
        if (heldItem == null || !(heldItem instanceof Equipment) || !heldItem.getName().equalsIgnoreCase("Pickaxe")) {
            System.out.println("Kamu harus memegang Pickaxe untuk memulihkan lahan.");
            return false;
        }

        if (player.getEnergy() + 20 < ENERGY_COST_PER_TILE) {
            System.out.println("Energi tidak cukup untuk memulihkan lahan (-" + ENERGY_COST_PER_TILE + " energi).");
            return false;
        }

        Tile currentTile = currentPlayersMap.getTileAtPosition(player.getX(), player.getY());
        if (currentTile == null) {
            System.out.println("Pemain tidak berada di tile yang valid.");
            return false;
        }

        if (currentTile.getState() != TileState.TILLED && currentTile.getState() != TileState.PLANTED) {
            System.out.println("Tile ini tidak dalam kondisi tilled atau planted.");
            return false;
        }

        if (currentTile.isOccupied() && !(currentTile.getObjectOnTile() instanceof PlantedCrop)) {
             System.out.println("Tile ini ditempati oleh objek yang tidak bisa dihilangkan dengan pickaxe.");
             return false;
        }

        return true;
    }

    @Override
    public void execute(Player player, Farm farm) {
        player.setEnergy(player.getEnergy() - ENERGY_COST_PER_TILE);
        farm.advanceGameTime(TIME_COST_PER_TILE_MINUTES);

        GameMap currentMap = farm.getCurrentMap();
        Tile currentTile = currentMap.getTileAtPosition(player.getX(), player.getY());

        if (currentTile == null) { // Pengecekan keamanan tambahan
            System.err.println("Tile pemain tidak valid.");
            player.setEnergy(player.getEnergy() + ENERGY_COST_PER_TILE);
            return;
        }

        String message = "";
        if (currentTile.getState() == TileState.PLANTED) {
            Object plant = currentTile.getObjectOnTile();
            String plantName = "tanaman";
            
            currentTile.setObjectOnTile(null); 
            currentTile.setOccupied(false);
            currentTile.setState(TileState.TILLED);
            message = player.getName() + " menghilangkan " + plantName + " dan tanah kembali menjadi soil.";
        } else if (currentTile.getState() == TileState.TILLED) {
            currentTile.setState(TileState.TILLABLE); 
            message = player.getName() + " memulihkan soil menjadi land.";
        }

        System.out.println(message);
        System.out.println("Energi tersisa: " + player.getEnergy());
        currentMap.display(player);
    }
}