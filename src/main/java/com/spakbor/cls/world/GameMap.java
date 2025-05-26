package cls.world;
import cls.core.*;

public interface GameMap {
    /**
     * Retrieves the tile at the given coordinates.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return The Tile object, or null if coordinates are out of bounds.
     */
    Tile getTileAtPosition(int x, int y);

    /**
     * Displays the current state of the map, including the player.
     * The implementation will handle how to represent tiles, objects, and the player.
     * @param player The player object, to show their position on this map.
     */
    void display(Player player);

    /**
     * @return The width of the map in tiles.
     */
    int getWidth();

    /**
     * @return The height of the map in tiles.
     */
    int getHeight();

    /**
     * @return The unique name of this map (e.g., "Farm", "Forest Zone", "Town").
     */
    String getMapName();

    /**
     * Places an object (like an NPC, item, or a string identifier for a structure part) onto a specific tile.
     * This method should also handle setting the tile's 'occupied' status if the object is solid.
     * @param obj The object to place.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     */
    void placeObjectOnTile(Object obj, int x, int y);

    /**
     * Removes an object from a specific tile.
     * This method should also handle updating the tile's 'occupied' status.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     */
    void removeObjectFromTile(int x, int y);

    /**
     * Determines the destination map name if the player attempts to move to the given
     * out-of-bounds coordinates from an edge of this map.
     * @param x The x-coordinate the player is attempting to move to (which is out of bounds).
     * @param y The y-coordinate the player is attempting to move to (which is out of bounds).
     * @return The name of the destination map (e.g., "Forest Zone"), or null if it's not a defined exit.
     */
    String getExitDestination(int x, int y);

    /**
     * Gets the entry coordinates for a player arriving on this map.
     * This helps position the player correctly when they transition from another map.
     * @param comingFromMapName The name of the map the player is arriving from (can be null for default spawn/initial game start).
     * @return An array [x, y] representing the entry coordinates on this map.
     */
    int[] getEntryPoint(String comingFromMapName);
}
