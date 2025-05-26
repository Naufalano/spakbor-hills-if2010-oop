import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ForestMap implements GameMap {
    public static final int MAP_WIDTH = 25; 
    public static final int MAP_HEIGHT = 18; 
    public static final String TREE_ID = "Tree"; 
    public static final String RIVER_WATER_ID = "RiverWater"; 
    public static final String FOREST_GROUND_CHAR = ".";
    public static final String TREE_CHAR = "T";
    public static final String RIVER_CHAR = "~";

    private List<Tile> tiles;
    private Random random = new Random();

    public ForestMap() {
        this.tiles = new ArrayList<>();
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                Tile tile = new Tile(x, y);
                tile.setState(TileState.DEFAULT);
                tiles.add(tile);
            }
        }
        generateForestLayout();
    }

    private void generateForestLayout() {
        int riverStartRow = random.nextInt(MAP_HEIGHT / 3) + (MAP_HEIGHT / 4);
        int currentRiverY = riverStartRow;

        for (int x = 0; x < MAP_WIDTH; x++) {
            placeObjectOnTile(RIVER_WATER_ID, x, currentRiverY);
            if (x > 0 && x < MAP_WIDTH -1) { 
                int bend = random.nextInt(5); 
                if (bend <= 1 && currentRiverY < MAP_HEIGHT - 2) {
                    currentRiverY++;
                    placeObjectOnTile(RIVER_WATER_ID, x, currentRiverY);
                } else if (bend >= 3 && currentRiverY > 1) {
                    currentRiverY--;
                    placeObjectOnTile(RIVER_WATER_ID, x, currentRiverY); 
                }
            }
            Tile mainRiverTile = getTileAtPosition(x, currentRiverY);
            if(mainRiverTile != null) mainRiverTile.setObjectOnTile(RIVER_WATER_ID);


            if (random.nextDouble() < 0.3 && currentRiverY + 1 < MAP_HEIGHT) {
                placeObjectOnTile(RIVER_WATER_ID, x, currentRiverY + 1);
            }
            if (random.nextDouble() < 0.3 && currentRiverY - 1 >= 0) {
                placeObjectOnTile(RIVER_WATER_ID, x, currentRiverY - 1);
            }
        }


        int numberOfTrees = 20 + random.nextInt(15);
        for (int i = 0; i < numberOfTrees; i++) {
            int treeX = random.nextInt(MAP_WIDTH);
            int treeY = random.nextInt(MAP_HEIGHT);
            Tile tile = getTileAtPosition(treeX, treeY);
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
                char charToDisplay = ' ';

                if (tile == null) {
                    System.out.print("[?]");
                    continue;
                }

                if (player != null && player.getX() == x && player.getY() == y && player.getCurrentLocationName().equals(getMapName())) {
                    charToDisplay = 'P';
                }
                else if (tile.isOccupied()) {
                    Object objOnTile = tile.getObjectOnTile();
                    if (y == MAP_HEIGHT / 2 && x == MAP_WIDTH - 1) {
                        charToDisplay = 'D';
                    } else if (RIVER_WATER_ID.equals(objOnTile)) {
                        charToDisplay = RIVER_CHAR.charAt(0);
                    } else if (objOnTile instanceof NPC) { 
                        charToDisplay = 'N';
                    } else if (TREE_ID.equals(objOnTile)) {
                        charToDisplay = TREE_CHAR.charAt(0);
                    } else {
                        charToDisplay = 'X';
                    }
                }
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
        return "Forest Zone"; 
    }

    @Override
    public void placeObjectOnTile(Object obj, int x, int y) {
        Tile tile = getTileAtPosition(x, y);
        if (tile != null) {
            tile.setObjectOnTile(obj);
            if (TREE_ID.equals(obj) || RIVER_WATER_ID.equals(obj)) {
                tile.setOccupied(true);
            } else {
                tile.setOccupied(obj != null);
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
        if (attemptedOutOfBoundX >= MAP_WIDTH) return "Farm"; 
        if (attemptedOutOfBoundX < 0) return "Deep Forest";
        return null;
    }

    @Override
    public int[] getEntryPoint(String comingFromMapName) {
        if ("Farm".equals(comingFromMapName)) {
            return new int[]{MAP_WIDTH - 1, MAP_HEIGHT / 2}; 
        } else if ("Deep Forest".equals(comingFromMapName)) {
            return new int[]{0, MAP_HEIGHT / 2}; 
        }
        return new int[]{0, MAP_HEIGHT / 2};
    }
}
