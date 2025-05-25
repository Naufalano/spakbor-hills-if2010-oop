import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Assuming Tile.java, TileState.java, Player.java, NPC.java, GameMap.java are accessible

public class ForestMap implements GameMap {
    public static final int MAP_WIDTH = 25; // Example width
    public static final int MAP_HEIGHT = 18; // Example height
    public static final String TREE_ID = "Tree"; // Identifier for tree objects
    public static final String RIVER_WATER_ID = "RiverWater"; // Identifier for river water tiles
    public static final String FOREST_GROUND_CHAR = "-"; // Visual representation for forest ground
    public static final String TREE_CHAR = "T";
    public static final String RIVER_CHAR = "~";

    private List<Tile> tiles;
    private Random random = new Random();

    public ForestMap() {
        this.tiles = new ArrayList<>();
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                Tile tile = new Tile(x, y);
                // Forest ground is generally not tillable like farm land.
                // Its state can be DEFAULT, or you could introduce a new TileState like WILDERNESS.
                tile.setState(TileState.DEFAULT);
                tiles.add(tile);
            }
        }
        generateForestLayout();
    }

    private void generateForestLayout() {
        // Add a winding river
        // For simplicity, let's make a mostly horizontal river with some bends.
        int riverStartRow = random.nextInt(MAP_HEIGHT / 3) + (MAP_HEIGHT / 4); // Start river somewhat in middle
        int currentRiverY = riverStartRow;

        for (int x = 0; x < MAP_WIDTH; x++) {
            placeObjectOnTile(RIVER_WATER_ID, x, currentRiverY);
            if (x > 0 && x < MAP_WIDTH -1) { // Don't change y at the very edges for simpler exits
                int bend = random.nextInt(5); // 0,1 down, 2 stay, 3,4 up
                if (bend <= 1 && currentRiverY < MAP_HEIGHT - 2) {
                    currentRiverY++;
                    placeObjectOnTile(RIVER_WATER_ID, x, currentRiverY); // Widen bend
                } else if (bend >= 3 && currentRiverY > 1) {
                    currentRiverY--;
                    placeObjectOnTile(RIVER_WATER_ID, x, currentRiverY); // Widen bend
                }
            }
             // Ensure the main river path is clear
            Tile mainRiverTile = getTileAtPosition(x, currentRiverY);
            if(mainRiverTile != null) mainRiverTile.setObjectOnTile(RIVER_WATER_ID);


            // Add some extra water patches near the main river path
            if (random.nextDouble() < 0.3 && currentRiverY + 1 < MAP_HEIGHT) {
                placeObjectOnTile(RIVER_WATER_ID, x, currentRiverY + 1);
            }
            if (random.nextDouble() < 0.3 && currentRiverY - 1 >= 0) {
                placeObjectOnTile(RIVER_WATER_ID, x, currentRiverY - 1);
            }
        }


        // Scatter some trees, avoiding the river
        int numberOfTrees = 20 + random.nextInt(15);
        for (int i = 0; i < numberOfTrees; i++) {
            int treeX = random.nextInt(MAP_WIDTH);
            int treeY = random.nextInt(MAP_HEIGHT);
            Tile tile = getTileAtPosition(treeX, treeY);
            // Ensure tile exists and is not already part of the river or occupied by another tree
            if (tile != null && !tile.isOccupied() && (tile.getObjectOnTile() == null || !RIVER_WATER_ID.equals(tile.getObjectOnTile()))) {
                placeObjectOnTile(TREE_ID, treeX, treeY);
            }
        }
    }

    @Override
    public Tile getTileAtPosition(int x, int y) {
        if (x < 0 || x >= MAP_WIDTH || y < 0 || y >= MAP_HEIGHT) {
            return null;
        }
        return tiles.get(y * MAP_WIDTH + x);
    }

    @Override
    public void display(Player player) {
        System.out.println("\n--- " + getMapName() + " ---");
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                Tile tile = getTileAtPosition(x, y);
                char charToDisplay = ' '; // Default

                if (tile == null) {
                    System.out.print("[?]"); // Should not happen
                    continue;
                }

                // Check for player first
                if (player != null && player.getX() == x && player.getY() == y && player.getCurrentLocationName().equals(getMapName())) {
                    charToDisplay = 'P';
                }
                // Then check for specific objects on the tile
                else if (tile.isOccupied()) {
                    Object objOnTile = tile.getObjectOnTile();
                    if (TREE_ID.equals(objOnTile)) {
                        charToDisplay = TREE_CHAR.charAt(0);
                    } else if (RIVER_WATER_ID.equals(objOnTile)) {
                        charToDisplay = RIVER_CHAR.charAt(0);
                    } else if (objOnTile instanceof NPC) { // If NPCs can be on this map
                        charToDisplay = 'N';
                    } else {
                        charToDisplay = 'X'; // Other unknown occupied object
                    }
                }
                // If not occupied by specific objects, display ground character
                else {
                    charToDisplay = FOREST_GROUND_CHAR.charAt(0);
                }
                System.out.print(" " + charToDisplay + " ");
            }
            System.out.println();
        }
        System.out.println("P: Player, T: Tree, ~: River Water, -: Ground, N: NPC, X: Obstacle");
    }

    @Override
    public int getWidth() {
        return MAP_WIDTH;
    }

    @Override
    public int getHeight() {
        return MAP_HEIGHT;
    }

    @Override
    public String getMapName() {
        return "Forest Zone"; // This name is used as a key in Farm's worldMaps
    }

    @Override
    public void placeObjectOnTile(Object obj, int x, int y) {
        Tile tile = getTileAtPosition(x, y);
        if (tile != null) {
            tile.setObjectOnTile(obj);
            // Trees and River Water are considered obstacles for movement
            if (TREE_ID.equals(obj) || RIVER_WATER_ID.equals(obj)) {
                tile.setOccupied(true);
            } else {
                tile.setOccupied(obj != null); // General case for other objects
            }
        }
    }

    @Override
    public void removeObjectFromTile(int x, int y) {
        Tile tile = getTileAtPosition(x, y);
        if (tile != null) {
            tile.setObjectOnTile(null);
            tile.setOccupied(false);
        }
    }

    @Override
    public String getExitDestination(int attemptedOutOfBoundX, int attemptedOutOfBoundY) {
        if (attemptedOutOfBoundX >= MAP_WIDTH) return "Farm"; // Keluar dari kanan ke Farm
        if (attemptedOutOfBoundX < 0) return "Deep Forest"; // Contoh: Keluar dari kiri ke Deep Forest (peta baru)
        // Tambahkan exit lain jika perlu (misal, atas ke Mountain)
        return null;
    }

    @Override
    public int[] getEntryPoint(String comingFromMapName) {
        if ("Farm".equals(comingFromMapName)) {
            return new int[]{MAP_WIDTH - 1, MAP_HEIGHT / 2}; // Masuk dari kanan
        } else if ("Deep Forest".equals(comingFromMapName)) {
            return new int[]{0, MAP_HEIGHT / 2}; // Masuk dari kiri
        }
        return new int[]{0, MAP_HEIGHT / 2}; // Default masuk dari kiri
    }
}
