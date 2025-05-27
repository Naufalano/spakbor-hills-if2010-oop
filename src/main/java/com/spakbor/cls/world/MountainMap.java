package cls.world;
import cls.core.*;
import enums.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MountainMap implements GameMap {
    public static final int MAP_WIDTH = 25;
    public static final int MAP_HEIGHT = 20;
    public static final String MOUNTAIN_WALL_ID = "MountainWall"; 
    public static final String LAKE_WATER_ID = "LakeWater";

    private List<Tile> tiles;
    private Random random = new Random();

    public MountainMap() {
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
        int lakeStartX = random.nextInt(MAP_WIDTH - 8) + 2;
        int lakeStartY = random.nextInt(MAP_HEIGHT - 6) + 2;
        for (int y = lakeStartY; y < lakeStartY + 4; y++) {
            for (int x = lakeStartX; x < lakeStartX + 6; x++) {
                placeObjectOnTile(LAKE_WATER_ID, x, y);
            }
        }

        for (int x = 0; x < MAP_WIDTH; x++) {
            if (getTileAtPosition(x, 0).getObjectOnTile() == null) placeObjectOnTile(MOUNTAIN_WALL_ID, x, 0);
            if (getTileAtPosition(x, MAP_HEIGHT - 1).getObjectOnTile() == null && x != MAP_WIDTH / 2) {
                 placeObjectOnTile(MOUNTAIN_WALL_ID, x, MAP_HEIGHT - 1);
            }
        }
        for (int y = 1; y < MAP_HEIGHT - 1; y++) {
            if (getTileAtPosition(0, y).getObjectOnTile() == null) placeObjectOnTile(MOUNTAIN_WALL_ID, 0, y); 
            if (getTileAtPosition(MAP_WIDTH - 1, y).getObjectOnTile() == null) placeObjectOnTile(MOUNTAIN_WALL_ID, MAP_WIDTH - 1, y);
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
                } else if (tile.isOccupied()) {
                    Object obj = tile.getObjectOnTile();
                    if (MOUNTAIN_WALL_ID.equals(obj)) charToDisplay = 'M';
                    else if (LAKE_WATER_ID.equals(obj)) charToDisplay = 'W';
                    else if (obj instanceof NPC) charToDisplay = 'N';
                    else charToDisplay = 'X';
                } else {
                    charToDisplay = '.'; 
                }
                System.out.print(" " + charToDisplay + " ");
            }
            System.out.println();
        }
        System.out.println("P: Player, M: Gunung, W: Air");
    }

    @Override
    public int getWidth() { return MAP_WIDTH; }
    @Override
    public int getHeight() { return MAP_HEIGHT; }
    @Override
    public String getMapName() { return "Mountain Area"; }

    @Override
    public void placeObjectOnTile(Object obj, int x, int y) {
        Tile tile = getTileAtPosition(x, y);
        if (tile != null) {
            tile.setObjectOnTile(obj);
            if (MOUNTAIN_WALL_ID.equals(obj) || LAKE_WATER_ID.equals(obj)) {
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
        return new int[]{MAP_WIDTH / 2, MAP_HEIGHT - 1};
    }
}