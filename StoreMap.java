import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Diasumsikan Tile.java, TileState.java, Player.java, GameMap.java, NPC.java, NPCFactory.java dapat diakses

public class StoreMap implements GameMap {
    public static final int MAP_WIDTH = 15;
    public static final int MAP_HEIGHT = 10;
    public static final String WALL_ID = "StoreWall";
    public static final String COUNTER_ID = "StoreCounter";
    public static final String SHELF_ID = "StoreShelf";
    public static final String DOOR_ID = "StoreDoor_ExitToTown"; // Pintu keluar kembali ke Town

    private List<Tile> tiles;
    private Random random = new Random();
    private NPC shopkeeper; // Emily akan menjadi penjaga toko

    public StoreMap(NPCFactory npcFactory) { // Menerima NPCFactory untuk mendapatkan Emily
        this.tiles = new ArrayList<>();
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                Tile tile = new Tile(x, y);
                tile.setState(TileState.DEFAULT); // Lantai toko
                tiles.add(tile);
            }
        }
        this.shopkeeper = npcFactory.getNPC("Emily"); // Mengambil Emily dari factory
        generateLayout();
    }

    private void generateLayout() {
        // Dinding luar
        for (int x = 0; x < MAP_WIDTH; x++) {
            placeObjectOnTile(WALL_ID, x, 0);
            placeObjectOnTile(WALL_ID, x, MAP_HEIGHT - 1);
        }
        for (int y = 1; y < MAP_HEIGHT - 1; y++) {
            placeObjectOnTile(WALL_ID, 0, y);
            placeObjectOnTile(WALL_ID, MAP_WIDTH - 1, y);
        }

        // Pintu keluar
        int doorX = MAP_WIDTH / 2;
        removeObjectFromTile(doorX, MAP_HEIGHT - 1); // Hapus dinding untuk pintu
        placeObjectOnTile(DOOR_ID, doorX, MAP_HEIGHT - 1);

        // Meja kasir/Counter
        for (int x = 3; x < MAP_WIDTH - 3; x++) {
            placeObjectOnTile(COUNTER_ID, x, 3);
        }

        // Tempatkan Emily (shopkeeper) di belakang meja kasir
        if (shopkeeper != null) {
            // Pastikan posisi ini valid dan tidak tertimpa objek lain
            int shopkeeperX = MAP_WIDTH / 2;
            int shopkeeperY = 2; // Di belakang counter
            Tile shopkeeperTile = getTileAtPosition(shopkeeperX, shopkeeperY);
            if (shopkeeperTile != null && !shopkeeperTile.isOccupied()) {
                 placeObjectOnTile(shopkeeper, shopkeeperX, shopkeeperY);
            } else {
                System.err.println("Peringatan: Tidak dapat menempatkan Emily di StoreMap pada ("+shopkeeperX+","+shopkeeperY+"), posisi mungkin sudah terisi.");
            }
        }

        // Rak-rak barang
        for (int y = 5; y < MAP_HEIGHT - 2; y++) {
            placeObjectOnTile(SHELF_ID, 2, y);
            placeObjectOnTile(SHELF_ID, MAP_WIDTH - 3, y);
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
                char charToDisplay = ' '; // Lantai toko

                if (tile == null) { System.out.print("[?]"); continue; }

                if (player != null && player.getX() == x && player.getY() == y && player.getCurrentLocationName().equals(getMapName())) {
                    charToDisplay = 'P';
                } else if (tile.isOccupied()) {
                    Object obj = tile.getObjectOnTile();
                    if (WALL_ID.equals(obj)) charToDisplay = '#';
                    else if (COUNTER_ID.equals(obj)) charToDisplay = '=';
                    else if (SHELF_ID.equals(obj)) charToDisplay = '|';
                    else if (DOOR_ID.equals(obj)) charToDisplay = 'D';
                    else if (obj instanceof NPC) charToDisplay = ((NPC) obj).getName().charAt(0); // Inisial NPC
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
    public String getMapName() { return "Store"; }

    @Override
    public void placeObjectOnTile(Object obj, int x, int y) {
        Tile tile = getTileAtPosition(x, y);
        if (tile != null) {
            tile.setObjectOnTile(obj);
            if (WALL_ID.equals(obj) || COUNTER_ID.equals(obj) || SHELF_ID.equals(obj) || obj instanceof NPC) {
                tile.setOccupied(true);
            } else {
                tile.setOccupied(obj != null && !DOOR_ID.equals(obj)); // Pintu tidak solid
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
    public String getExitDestination(int attemptedOutOfBoundX, int attemptedOutOfBoundY) { return null; }

    @Override
    public int[] getEntryPoint(String comingFromMapName) {
        // Pemain masuk dari "Town" dan muncul di dekat pintu
        int doorX = MAP_WIDTH / 2;
        int entryY = MAP_HEIGHT - 2;
        return new int[]{doorX, entryY};
    }

    public NPC getShopkeeper() {
        return this.shopkeeper;
    }
}
