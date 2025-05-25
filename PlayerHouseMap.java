import java.util.ArrayList;
import java.util.List;

public class PlayerHouseMap implements GameMap {
    public static final int MAP_WIDTH = 10;
    public static final int MAP_HEIGHT = 8;

    public static final String WALL_ID = "HouseInteriorWall";
    public static final String FLOOR_ID = "HouseFloor";
    public static final String BED_ID = "Bed";
    public static final String STOVE_ID = "Stove";
    public static final String DOOR_TO_FARM_ID = "HouseDoor_ExitToFarm";

    private List<Tile> tiles;
    // private Random random = new Random();

    public PlayerHouseMap() {
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
        for (int x = 0; x < MAP_WIDTH; x++) {
            placeObjectOnTile(WALL_ID, x, 0);
            if (x != MAP_WIDTH / 2) { 
                 placeObjectOnTile(WALL_ID, x, MAP_HEIGHT - 1);
            }
        }
        for (int y = 1; y < MAP_HEIGHT - 1; y++) {
            placeObjectOnTile(WALL_ID, 0, y);
            placeObjectOnTile(WALL_ID, MAP_WIDTH - 1, y);
        }

        int doorX = MAP_WIDTH / 2;
        int doorY = MAP_HEIGHT - 1;
        Tile doorTile = getTileAtPosition(doorX, doorY);
        if(doorTile != null && WALL_ID.equals(doorTile.getObjectOnTile())){
            removeObjectFromTile(doorX, doorY); 
        }
        placeObjectOnTile(DOOR_TO_FARM_ID, doorX, doorY);

        placeObjectOnTile(BED_ID, 1, 1);
        if (MAP_WIDTH > 2) placeObjectOnTile(BED_ID, 2, 1); 

        placeObjectOnTile(STOVE_ID, MAP_WIDTH - 2, 1);
    }

    @Override
    public Tile getTileAtPosition(int x, int y) {
        if (x < 0 || x >= MAP_WIDTH || y < 0 || y >= MAP_HEIGHT) return null;
        return tiles.get(y * MAP_WIDTH + x);
    }

    @Override
    public void display(Player player) {
        System.out.println("\n--- " + getMapName() + " ---");
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                Tile tile = getTileAtPosition(x, y);
                char charToDisplay = ' ';

                if (tile == null) { System.out.print("[?]"); continue; }

                if (player != null && player.getX() == x && player.getY() == y && player.getCurrentLocationName().equals(getMapName())) {
                    charToDisplay = 'P';
                } else if (tile.isOccupied()) {
                    Object obj = tile.getObjectOnTile();
                    if (WALL_ID.equals(obj)) charToDisplay = '#';
                    else if (BED_ID.equals(obj)) charToDisplay = 'B';
                    else if (STOVE_ID.equals(obj)) charToDisplay = 'C';
                    else if (DOOR_TO_FARM_ID.equals(obj)) charToDisplay = 'D';
                } else { charToDisplay = '.'; }
                System.out.print(" " + charToDisplay + " ");
            }
            System.out.println();
        }
        System.out.println("P: Pemain, #: Dinding, B: Kasur, C: Kompor, D: Pintu, .: Lantai");
    }

    @Override
    public int getWidth() { return MAP_WIDTH; }
    @Override
    public int getHeight() { return MAP_HEIGHT; }
    @Override
    public String getMapName() { return "Player's House"; }

    @Override
    public void placeObjectOnTile(Object obj, int x, int y) {
        Tile tile = getTileAtPosition(x, y);
        if (tile != null) {
            tile.setObjectOnTile(obj);
            if (WALL_ID.equals(obj) || BED_ID.equals(obj) || STOVE_ID.equals(obj)) {
                tile.setOccupied(true);
            } else {
                tile.setOccupied(obj != null && !DOOR_TO_FARM_ID.equals(obj) && !FLOOR_ID.equals(obj));
            }
        }
    }

    @Override
    public void removeObjectFromTile(int x, int y) {
        Tile tile = getTileAtPosition(x, y);
        if (tile != null) {
            tile.setObjectOnTile(FLOOR_ID);
            tile.setOccupied(false);
        }
    }

    @Override
    public String getExitDestination(int attemptedOutOfBoundX, int attemptedOutOfBoundY) {
        if (attemptedOutOfBoundX == MAP_WIDTH / 2 && attemptedOutOfBoundY == MAP_HEIGHT - 1){
            return "Farm";
        }
        return null;
    }

    @Override
    public int[] getEntryPoint(String comingFromMapName) {
        return new int[]{MAP_WIDTH / 2, MAP_HEIGHT - 2};
    }
}
