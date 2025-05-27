package cls.world;
import cls.core.*;
import enums.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GenericInteriorMap implements GameMap {
    public static final int MAP_WIDTH = 12;
    public static final int MAP_HEIGHT = 10;
    public static final String WALL_ID = "HouseWall";
    public static final String FURNITURE_ID = "Furniture";
    public static final String DOOR_ID = "HouseDoor_ExitToTown"; 

    private List<Tile> tiles;
    private Random random = new Random();
    private String mapDisplayName; 
    private NPC resident; 

    public GenericInteriorMap(String mapDisplayName, NPC resident) {
        this.mapDisplayName = mapDisplayName;
        this.resident = resident; 
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
     public GenericInteriorMap(String mapDisplayName) { 
        this(mapDisplayName, null);
    }


    private void generateLayout() {
        for (int x = 0; x < MAP_WIDTH; x++) {
            placeObjectOnTile(WALL_ID, x, 0);
            placeObjectOnTile(WALL_ID, x, MAP_HEIGHT - 1);
        }
        for (int y = 1; y < MAP_HEIGHT - 1; y++) {
            placeObjectOnTile(WALL_ID, 0, y);
            placeObjectOnTile(WALL_ID, MAP_WIDTH - 1, y);
        }

        int doorX = MAP_WIDTH / 2;
        removeObjectFromTile(doorX, MAP_HEIGHT - 1);
        placeObjectOnTile(DOOR_ID, doorX, MAP_HEIGHT - 1);

        placeObjectOnTile(FURNITURE_ID, 2, 2);
        placeObjectOnTile(FURNITURE_ID, MAP_WIDTH - 3, 2);
        placeObjectOnTile(FURNITURE_ID, 2, MAP_HEIGHT - 3);

        if (resident != null) {
            int npcX, npcY;
            do {
                npcX = random.nextInt(MAP_WIDTH - 2) + 1;
                npcY = random.nextInt(MAP_HEIGHT - 2) + 1;
            } while (getTileAtPosition(npcX, npcY) == null || getTileAtPosition(npcX, npcY).isOccupied());
            placeObjectOnTile(resident, npcX, npcY);
        }
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
                char charToDisplay = '.';

                if (tile == null) { System.out.print("[?]"); continue; }

                if (player != null && player.getX() == x && player.getY() == y && player.getCurrentLocationName().equals(getMapName())) {
                    charToDisplay = 'P';
                } else if (tile.isOccupied()) {
                    Object obj = tile.getObjectOnTile();
                    if (WALL_ID.equals(obj)) charToDisplay = '#';
                    else if (FURNITURE_ID.equals(obj)) charToDisplay = 'F';
                    else if (DOOR_ID.equals(obj)) charToDisplay = 'D';
                    else if (obj instanceof NPC) charToDisplay = ((NPC) obj).getName().charAt(0);
                    else charToDisplay = 'X';
                }
                System.out.print(" " + charToDisplay + " ");
            }
            System.out.println();
        }
    }

    @Override
    public int getWidth() { return MAP_WIDTH; }
    @Override
    public int getHeight() { return MAP_HEIGHT; }
    @Override
    public String getMapName() { return this.mapDisplayName; }

    @Override
    public void placeObjectOnTile(Object obj, int x, int y) {
        Tile tile = getTileAtPosition(x, y);
        if (tile != null) {
            tile.setObjectOnTile(obj);
            if (WALL_ID.equals(obj) || FURNITURE_ID.equals(obj) || obj instanceof NPC) {
                tile.setOccupied(true);
            } else {
                tile.setOccupied(obj != null && !DOOR_ID.equals(obj));
            }
        }
    }
    @Override
    public void removeObjectFromTile(int x, int y) {}

    @Override
    public String getExitDestination(int attemptedOutOfBoundX, int attemptedOutOfBoundY) { return null; }

    @Override
    public int[] getEntryPoint(String comingFromMapName) {
        int doorX = MAP_WIDTH / 2;
        int entryY = MAP_HEIGHT - 2;
        return new int[]{doorX, entryY};
    }

    public NPC getResident() {
        return this.resident;
    }
}
