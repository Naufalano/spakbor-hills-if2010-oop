import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TownMap implements GameMap {
    public static final int MAP_WIDTH = 30;
    public static final int MAP_HEIGHT = 25;

    public static final String BUILDING_WALL_ID = "BuildingWall";
    public static final String STORE_WALL_ID = "StoreWall";
    public static final String ROAD_ID = "Road";

    public static final String STORE_ENTRANCE_ID = "TOWN_DOOR_TO_Store";
    public static final String DASCO_HOUSE_ENTRANCE_ID = "TOWN_DOOR_TO_DascosLair";
    public static final String PERRY_HOUSE_ENTRANCE_ID = "TOWN_DOOR_TO_PerrysPlace";
    public static final String CAROLINE_HOUSE_ENTRANCE_ID = "TOWN_DOOR_TO_CarolinesHome";
    public static final String MAYOR_HOUSE_ENTRANCE_ID = "TOWN_DOOR_TO_MayorsManor";
    public static final String ABIGAIL_HOUSE_ENTRANCE_ID = "TOWN_DOOR_TO_AbigailsRoom";
    public static final String PLAYER_HOUSE_ENTRANCE_ON_TOWN_ID = "TOWN_DOOR_TO_PlayersHouse"; 

    public static final String TOWN_EXIT_TO_FARM_ID = "TOWN_EXIT_TO_Farm";
    public static final String TOWN_EXIT_TO_FOREST_ID = "TOWN_EXIT_TO_ForestZone";

    public static final char ROAD_CHAR = '#';
    public static final char BUILDING_CHAR = 'B';
    public static final char STORE_CHAR = 'S';
    public static final char DOOR_CHAR = 'D';
    public static final char GROUND_CHAR = '.';


    private List<Tile> tiles;
    private Random random = new Random();
    private List<DoorInfo> doors; 

    public static class DoorInfo {
        public String doorId;
        public int x, y;
        public String destinationMapName;

        public DoorInfo(String doorId, int x, int y, String destinationMapName) {
            this.doorId = doorId;
            this.x = x;
            this.y = y;
            this.destinationMapName = destinationMapName;
        }
    }

    public TownMap() {
        this.tiles = new ArrayList<>();
        this.doors = new ArrayList<>();
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
            placeObjectOnTile(ROAD_ID, x, MAP_HEIGHT / 2, false);
        }
        for (int y = 0; y < MAP_HEIGHT; y++) {
            placeObjectOnTile(ROAD_ID, MAP_WIDTH / 3, y, false);
            placeObjectOnTile(ROAD_ID, (MAP_WIDTH / 3) * 2, y, false);
        }
        for (int x = MAP_WIDTH / 3; x < (MAP_WIDTH / 3) * 2; x++) {
            placeObjectOnTile(ROAD_ID, x, MAP_HEIGHT / 4, false);
            placeObjectOnTile(ROAD_ID, x, (MAP_HEIGHT / 4) * 3, false);
        }

        placeStoreWithEntrance(STORE_ENTRANCE_ID, "Eksterior Toko", 5, MAP_HEIGHT / 2 + 1, 7, 4, "Store");
        
        placeBuildingWithEntrance(DASCO_HOUSE_ENTRANCE_ID, "Eksterior Rumah Dasco", 15, MAP_HEIGHT / 2 + 1, 5, 4, "Dasco's Lair");
        placeBuildingWithEntrance(PERRY_HOUSE_ENTRANCE_ID, "Eksterior Rumah Perry", 5, MAP_HEIGHT / 4 + 1, 5, 4, "Perry's Place");
        placeBuildingWithEntrance(CAROLINE_HOUSE_ENTRANCE_ID, "Eksterior Rumah Caroline", 15, MAP_HEIGHT / 4 + 1, 5, 4, "Caroline's Home");
        placeBuildingWithEntrance(MAYOR_HOUSE_ENTRANCE_ID, "Eksterior Rumah Mayor", (MAP_WIDTH/3)*2 + 1, 5, 6, 5, "Mayor's Manor");
        placeBuildingWithEntrance(ABIGAIL_HOUSE_ENTRANCE_ID, "Eksterior Rumah Abigail", (MAP_WIDTH/3)*2 + 1, 15, 5, 4, "Abigail's Room");

        int exitToFarmX = 0;
        int exitToFarmY = MAP_HEIGHT / 2;
        placeObjectOnTile(TOWN_EXIT_TO_FARM_ID, exitToFarmX, exitToFarmY, false);
        doors.add(new DoorInfo(TOWN_EXIT_TO_FARM_ID, exitToFarmX, exitToFarmY, "Farm"));
    }

    /**
     * Menempatkan bangunan dan pintu masuknya.
     * Pintu akan ditempatkan di sisi bawah bangunan secara default, menghadap jalan jika memungkinkan.
     */
    private void placeBuildingWithEntrance(String entranceId, String buildingExteriorId, int preferredX, int preferredY, int width, int height, String destinationMapKey) {

        int startX = preferredX;
        int startY = preferredY;

        if (startX + width >= MAP_WIDTH) startX = MAP_WIDTH - width -1;
        if (startY + height >= MAP_HEIGHT) startY = MAP_HEIGHT - height -1;
        if (startX < 0) startX = 0;
        if (startY < 0) startY = 0;

        for (int y = startY; y < startY + height; y++) {
            for (int x = startX; x < startX + width; x++) {
                Tile currentTile = getTileAtPosition(x,y);
                if (currentTile != null && (currentTile.getObjectOnTile() == null || !ROAD_ID.equals(currentTile.getObjectOnTile()))) {
                    placeObjectOnTile(BUILDING_WALL_ID, x, y, true);
                } else if (currentTile != null && ROAD_ID.equals(currentTile.getObjectOnTile())) {
                    placeObjectOnTile(BUILDING_WALL_ID, x, y, true);
                }
            }
        }
        
        int doorX = startX + width / 2;
        int doorY = startY + height - 1;

        Tile accessTile = getTileAtPosition(doorX, doorY + 1);
        if (accessTile == null || (accessTile.isOccupied() && !ROAD_ID.equals(accessTile.getObjectOnTile()) )) {
            System.err.println("Peringatan: Akses ke pintu " + entranceId + " mungkin terhalang.");
        }

        removeObjectFromTile(doorX, doorY);
        placeObjectOnTile(entranceId, doorX, doorY, false); 
        doors.add(new DoorInfo(entranceId, doorX, doorY, destinationMapKey));
    }

    private void placeStoreWithEntrance(String entranceId, String buildingExteriorId, int preferredX, int preferredY, int width, int height, String destinationMapKey) {

        int startX = preferredX;
        int startY = preferredY;

        if (startX + width >= MAP_WIDTH) startX = MAP_WIDTH - width -1;
        if (startY + height >= MAP_HEIGHT) startY = MAP_HEIGHT - height -1;
        if (startX < 0) startX = 0;
        if (startY < 0) startY = 0;

        for (int y = startY; y < startY + height; y++) {
            for (int x = startX; x < startX + width; x++) {
                Tile currentTile = getTileAtPosition(x,y);
                if (currentTile != null && (currentTile.getObjectOnTile() == null || !ROAD_ID.equals(currentTile.getObjectOnTile()))) {
                    placeObjectOnTile(STORE_WALL_ID, x, y, true);
                } else if (currentTile != null && ROAD_ID.equals(currentTile.getObjectOnTile())) {
                    placeObjectOnTile(STORE_WALL_ID, x, y, true);
                }
            }
        }
        
        int doorX = startX + width / 2;
        int doorY = startY + height - 1;

        Tile accessTile = getTileAtPosition(doorX, doorY + 1);
        if (accessTile == null || (accessTile.isOccupied() && !ROAD_ID.equals(accessTile.getObjectOnTile()) )) {
            System.err.println("Peringatan: Akses ke pintu " + entranceId + " mungkin terhalang.");
        }

        removeObjectFromTile(doorX, doorY);
        placeObjectOnTile(entranceId, doorX, doorY, false); 
        doors.add(new DoorInfo(entranceId, doorX, doorY, destinationMapKey));
    }

    private void placeObjectOnTile(String objectId, int x, int y, boolean isSolid) {
        Tile tile = getTileAtPosition(x, y);
        if (tile != null) {
            if (tile.getObjectOnTile() instanceof String && ((String)tile.getObjectOnTile()).contains("DOOR")) {
                if (!objectId.contains("DOOR")) return; 
            }

            tile.setObjectOnTile(objectId);
            tile.setOccupied(isSolid);
            if (isSolid && !ROAD_ID.equals(objectId)) { 
                tile.setState(TileState.DEFAULT);
            } else if (ROAD_ID.equals(objectId)) {
                tile.setState(TileState.DEFAULT); 
            }
        }
    }

    @Override
    public void placeObjectOnTile(Object obj, int x, int y) {
        if (obj instanceof String) {
            boolean isSolid = !( ((String)obj).contains("DOOR") || ((String)obj).equals(ROAD_ID) );
            placeObjectOnTile((String)obj, x, y, isSolid);
        } else if (obj instanceof NPC) {
            Tile tile = getTileAtPosition(x,y);
            if(tile != null) {
                tile.setObjectOnTile(obj);
                tile.setOccupied(true);
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
        System.out.println("\n--- " + getMapName() + " ---");
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                Tile tile = getTileAtPosition(x, y);
                char charToDisplay = GROUND_CHAR;
                if (tile == null) { System.out.print("[?]"); continue; }

                if (player != null && player.getX() == x && player.getY() == y && player.getCurrentLocationName().equals(getMapName())) {
                    charToDisplay = 'P';
                } else if (tile.getObjectOnTile() != null) {
                    Object obj = tile.getObjectOnTile();
                    if (obj instanceof String) {
                        String objId = (String) obj;
                        if (objId.contains("DOOR") || objId.equals(TOWN_EXIT_TO_FARM_ID)) charToDisplay = DOOR_CHAR;
                        else if (objId.equals(BUILDING_WALL_ID) || objId.contains("Eksterior")) charToDisplay = BUILDING_CHAR;
                        else if (objId.equals(STORE_WALL_ID) || objId.contains("Eksterior")) charToDisplay = STORE_CHAR;
                        else if (objId.equals(ROAD_ID)) charToDisplay = ROAD_CHAR;
                        else if (tile.isOccupied()) charToDisplay = 'X';
                    } else if (obj instanceof NPC) {
                        charToDisplay = ((NPC) obj).getName().charAt(0);
                    } else if (tile.isOccupied()){
                        charToDisplay = 'X';
                    }
                }
                System.out.print(" " + charToDisplay + " ");
            }
            System.out.println();
        }
        System.out.println("P:Pemain, B:Bangunan, S:Toko, D:Pintu, #:Jalan, ,:Tanah, N:NPC, X:Lainnya");
    }

    @Override
    public int getWidth() { return MAP_WIDTH; }
    @Override
    public int getHeight() { return MAP_HEIGHT; }
    @Override
    public String getMapName() { return "Town"; }


    @Override
    public void removeObjectFromTile(int x, int y) {
        Tile tile = getTileAtPosition(x, y);
        if (tile != null) {
            tile.setObjectOnTile(null);
            tile.setOccupied(false);
            tile.setState(TileState.DEFAULT);
        }
    }

    @Override
    public String getExitDestination(int attemptedOutOfBoundX, int attemptedOutOfBoundY) {
        if(attemptedOutOfBoundX < 0 && attemptedOutOfBoundY == MAP_HEIGHT / 2) {
            return "Farm";
        }
        return null;
    }

    @Override
    public int[] getEntryPoint(String comingFromMapName) {
        if ("Farm".equals(comingFromMapName)) {
            for (DoorInfo door : doors) {
                if (TOWN_EXIT_TO_FARM_ID.equals(door.doorId)) {
                    return new int[]{door.x + 1 < MAP_WIDTH ? door.x +1 : door.x-1, door.y};
                }
            }
        }
        TownMap test = new TownMap();
        int doorX = 0;
        int entryY = test.getHeight() / 2;
        List<TownMap.DoorInfo> checker = test.getAllDoors();
        switch (comingFromMapName) {
            case "Perry's Place":
                for (int k = 0; k < checker.size(); k++) {
                    if (checker.get(k).destinationMapName.equals(comingFromMapName)) {
                        doorX = checker.get(k).x;
                        entryY = checker.get(k).y + 1;
                        break;
                    }
                }
                break;
            case "Dasco's Lair":
                for (int k = 0; k < checker.size(); k++) {
                    if (checker.get(k).destinationMapName.equals(comingFromMapName)) {
                        doorX = checker.get(k).x;
                        entryY = checker.get(k).y + 1;
                        break;
                    }
                }
                break;
            case "Caroline's Home":
                for (int k = 0; k < checker.size(); k++) {
                    if (checker.get(k).destinationMapName.equals(comingFromMapName)) {
                        doorX = checker.get(k).x;
                        entryY = checker.get(k).y + 1;
                        break;
                    }
                }
                break;
            case "Mayor's Manor":
                for (int k = 0; k < checker.size(); k++) {
                    if (checker.get(k).destinationMapName.equals(comingFromMapName)) {
                        doorX = checker.get(k).x;
                        entryY = checker.get(k).y + 1;
                        break;
                    }
                }
                break;
            case "Abigail's Room":
                for (int k = 0; k < checker.size(); k++) {
                    if (checker.get(k).destinationMapName.equals(comingFromMapName)) {
                        doorX = checker.get(k).x;
                        entryY = checker.get(k).y + 1;
                        break;
                    }
                }
                break;
            case "Store":
                for (int k = 0; k < checker.size(); k++) {
                    if (checker.get(k).destinationMapName.equals(comingFromMapName)) {
                        doorX = checker.get(k).x;
                        entryY = checker.get(k).y + 1;
                        break;
                    }
                }
                break;
            default:
                doorX = 0;
                entryY = test.getHeight() / 2;
        }
        return new int[]{doorX, entryY};
    }

    public DoorInfo getDoorInfo(String doorId) {
        for (DoorInfo door : doors) {
            if (door.doorId.equals(doorId)) {
                return door;
            }
        }
        return null;
    }

    public List<DoorInfo> getAllDoors() {
        return doors;
    }
}
