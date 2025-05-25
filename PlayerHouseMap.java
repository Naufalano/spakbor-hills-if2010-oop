import java.util.ArrayList;
import java.util.List;

// Diasumsikan Tile.java, TileState.java, Player.java, GameMap.java dapat diakses

public class PlayerHouseMap implements GameMap {
    public static final int MAP_WIDTH = 10;
    public static final int MAP_HEIGHT = 8;

    public static final String WALL_ID = "HouseInteriorWall";
    public static final String FLOOR_ID = "HouseFloor";
    public static final String BED_ID = "Bed";
    public static final String STOVE_ID = "Stove";
    public static final String DOOR_TO_FARM_ID = "HouseDoor_ExitToFarm"; // Satu pintu keluar

    private List<Tile> tiles;
    // private Random random = new Random(); // Tidak terlalu dibutuhkan untuk layout tetap

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
        // 1. Dinding luar
        for (int x = 0; x < MAP_WIDTH; x++) {
            placeObjectOnTile(WALL_ID, x, 0); // Dinding atas
            if (x != MAP_WIDTH / 2) { // Sisakan ruang untuk pintu di dinding bawah
                 placeObjectOnTile(WALL_ID, x, MAP_HEIGHT - 1);
            }
        }
        for (int y = 1; y < MAP_HEIGHT - 1; y++) { // Mulai dari y=1 karena y=0 sudah dinding
            placeObjectOnTile(WALL_ID, 0, y); // Dinding kiri
            placeObjectOnTile(WALL_ID, MAP_WIDTH - 1, y); // Dinding kanan
        }

        // 2. Pintu keluar (di tengah dinding bawah)
        int doorX = MAP_WIDTH / 2;
        int doorY = MAP_HEIGHT - 1; // Pintu di dinding bawah
        // Hapus dinding jika ada, lalu tempatkan pintu
        Tile doorTile = getTileAtPosition(doorX, doorY);
        if(doorTile != null && WALL_ID.equals(doorTile.getObjectOnTile())){
            removeObjectFromTile(doorX, doorY); // Hapus dinding
        }
        placeObjectOnTile(DOOR_TO_FARM_ID, doorX, doorY);


        // 3. Tempatkan Kasur (Bed), misalnya di pojok kiri atas
        // Kasur bisa 1 tile interaktif atau 2 tile visual
        placeObjectOnTile(BED_ID, 1, 1); // Titik interaksi kasur
        if (MAP_WIDTH > 2) placeObjectOnTile(BED_ID, 2, 1); // Bagian visual kedua kasur jika muat

        // 4. Tempatkan Kompor (Stove), misalnya di dinding atas bagian kanan
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
        int doorX = MAP_WIDTH / 2;
        int entryY = MAP_HEIGHT - 2;
        return new int[]{doorX, entryY};
    }
}
