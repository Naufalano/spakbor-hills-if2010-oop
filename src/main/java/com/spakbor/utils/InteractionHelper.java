package com.spakbor.utils;
import com.spakbor.cls.core.*;
import com.spakbor.cls.world.*;
import com.spakbor.data.*;

public class InteractionHelper {

    /**
     * Checks what object type the player is standing next to (not on top of).
     * This is for objects like Shipping Bin or Pond where interaction happens from an adjacent tile.
     * @param player The player.
     * @param map The current map the player is on.
     * @return The String ID of the adjacent interactable object (e.g., FarmMap.SHIPPING_BIN_ID, FarmMap.POND_ID),
     * or null if not next to a known interactable object of this type.
     */
    public static String getAdjacentInteractableObject(Player player, GameMap map) {
        if (player == null || map == null) return null;

        int px = player.getX();
        int py = player.getY();

        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}}; // Up, Down, Left, Right

        for (int[] dir : directions) {
            int adjX = px + dir[0];
            int adjY = py + dir[1];

            Tile adjacentTile = map.getTileAtPosition(adjX, adjY);
            if (adjacentTile != null && adjacentTile.isOccupied() && adjacentTile.getObjectOnTile() instanceof String) {
                String objectId = (String) adjacentTile.getObjectOnTile();

                if (FarmMap.SHIPPING_BIN_ID.equals(objectId) ||
                    FarmMap.POND_ID.equals(objectId) ||
                    PlayerHouseMap.BED_ID.equals(objectId) ||
                    PlayerHouseMap.STOVE_ID.equals(objectId) ||
                    ForestMap.RIVER_WATER_ID.equals(objectId) ||
                    MountainMap.LAKE_WATER_ID.equals(objectId) ||
                    CoastalMap.OCEAN_WATER_ID.equals(objectId)) {
                    return objectId; 
                }
            }
        }
        return null;
    }

    /**
     * Checks if the player is standing next to any part of a given structure (like a building).
     * @param player The player.
     * @param map The current map.
     * @param structure The PlacedObject representing the structure.
     * @return true if the player is adjacent to any tile of the structure, false otherwise.
     */
    public static boolean isPlayerAdjacentToStructure(Player player, GameMap map, FarmMap.PlacedObject structure) {
        if (player == null || map == null || structure == null) return false;
        if (!player.getCurrentLocationName().equals(map.getMapName())) return false;

        int px = player.getX();
        int py = player.getY();
        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

        for (int[] dir : directions) {
            int adjX = px + dir[0];
            int adjY = py + dir[1];

            if (adjX >= structure.x && adjX < (structure.x + structure.width) &&
                adjY >= structure.y && adjY < (structure.y + structure.height)) {
                
                Tile structureTilePart = map.getTileAtPosition(adjX, adjY);
                if (structureTilePart != null && structureTilePart.isOccupied() &&
                    structure.id.equals(structureTilePart.getObjectOnTile())) {
                    return true;
                }
            }
        }
        return false;
    }
}
