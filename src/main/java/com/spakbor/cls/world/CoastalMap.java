package cls.world;
import cls.core.*;
import enums.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CoastalMap implements GameMap {
    public static final int MAP_WIDTH = 30;
    public static final int MAP_HEIGHT = 12;
    public static final String OCEAN_WATER_ID = "OceanWater";
    public static final String SAND_ID = "Sand"; 

    private List<Tile> tiles;
    private Random random = new Random();

    public CoastalMap() {
        this.tiles = new ArrayList<>();
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                Tile tile = new Tile(x, y);
                tile.setState(TileState.DEFAULT);
                tiles.add(tile);
            }
        }
        generateLayout();
    }

    private void generateLayout() {
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                if (y < MAP_HEIGHT / 2) {
                    placeObjectOnTile(OCEAN_WATER_ID, x, y);
                }
            }
        }
    }

    @Override
    public Tile getTileAtPosition(int x, int y) {
        if (x < 0 || x >= MAP_WIDTH || y < 0 || y >= MAP_HEIGHT) return null;
        return tiles.get(y * MAP_WIDTH + x);
    }

    @Override
    public void display(Player player) {
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                Tile tile = getTileAtPosition(x, y);
                char charToDisplay = ' ';
                if (tile == null) { System.out.print("[?]"); continue; }

                if (player != null && player.getX() == x && player.getY() == y && player.getCurrentLocationName().equals(getMapName())) {
                    charToDisplay = 'P';
                } else if (tile.isOccupied() && OCEAN_WATER_ID.equals(tile.getObjectOnTile())) {
                    charToDisplay = 'W'; 
                } else if (tile.isOccupied() && tile.getObjectOnTile() instanceof NPC) {
                    charToDisplay = 'N';
                } else if (tile.isOccupied()){
                    charToDisplay = 'X';
                } else if (x == MAP_WIDTH / 2 && y == MAP_HEIGHT - 1) {
                    charToDisplay = 'F';
                }
                else {
                    charToDisplay = '.';
                }
                System.out.print(" " + charToDisplay + " ");
            }
            System.out.println();
        }
        System.out.println("Legenda: P:Player, W:Air");
    }
    @Override
    public int getWidth() { return MAP_WIDTH; }
    @Override
    public int getHeight() { return MAP_HEIGHT; }
    @Override
    public String getMapName() { return "Coastal Region"; }

    @Override
    public void placeObjectOnTile(Object obj, int x, int y) {
        Tile tile = getTileAtPosition(x, y);
        if (tile != null) {
            tile.setObjectOnTile(obj);
            if (OCEAN_WATER_ID.equals(obj)) {
                tile.setOccupied(true);
            } else {
                tile.setOccupied(obj != null);
            }
        }
    }
    @Override
    public void removeObjectFromTile(int x, int y) {}

    @Override
    public String getExitDestination(int attemptedOutOfBoundX, int attemptedOutOfBoundY) {
        if (attemptedOutOfBoundY >= MAP_HEIGHT && attemptedOutOfBoundX == MAP_WIDTH / 2) return "Farm";
        return null;
    }

    @Override
    public int[] getEntryPoint(String comingFromMapName) {
        if ("Farm".equals(comingFromMapName)) {
            return new int[]{MAP_WIDTH / 2, MAP_HEIGHT - 1};
        }
        return new int[]{MAP_WIDTH / 2, 0};
    }
}