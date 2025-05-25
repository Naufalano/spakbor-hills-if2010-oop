import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random; // Untuk shuffle

// Tile class untuk merepresentasikan elemen pada peta
class Tile {
    private int x;
    private int y;
    private boolean isOccupied;
    private Object objectOnTile;  // Menyimpan objek yang ada di tile (misalnya NPC, Rock, Tree etc.)
    private TileState state;      // Added TileState

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
        this.isOccupied = false;
        this.objectOnTile = null;
        this.state = TileState.DEFAULT; // Default state
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean isOccupied) {
        this.isOccupied = isOccupied;
    }

    public Object getObjectOnTile() {
        return objectOnTile;
    }

    public void setObjectOnTile(Object objectOnTile) {
        this.objectOnTile = objectOnTile;
        this.setOccupied(objectOnTile != null); // Tile is occupied if an object is on it
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public TileState getState() { // Getter for TileState
        return state;
    }

    public void setState(TileState state) { // Setter for TileState
        this.state = state;
    }
}

public class FarmMap implements GameMap {
    // --- Konstanta yang sudah ada ---
    public static final int MAP_WIDTH = 32;
    public static final int MAP_HEIGHT = 32;

    public static final String HOUSE_STRUCTURE_ID = "HouseStructure";
    public static final String HOUSE_ENTRANCE_EXTERIOR_ID = "HouseDoor_FarmToInterior";
    public static final int HOUSE_WIDTH = 6;
    public static final int HOUSE_HEIGHT = 6;

    public static final String POND_ID = "Pond";
    public static final int POND_WIDTH = 4;
    public static final int POND_HEIGHT = 3;

    public static final String SHIPPING_BIN_ID = "ShippingBin";
    public static final int SHIPPING_BIN_WIDTH = 3;
    public static final int SHIPPING_BIN_HEIGHT = 2;

    // --- Karakter Display (sudah ada) ---
    public static final char PLAYER_CHAR = 'P';
    public static final char HOUSE_STRUCTURE_CHAR = 'H';
    public static final char HOUSE_DOOR_CHAR = 'D';
    // ... dan seterusnya ...
    public static final char POND_CHAR = 'O';
    public static final char SHIPPING_BIN_CHAR = 'S';
    public static final char TILLABLE_CHAR = '.';
    public static final char TILLED_CHAR = 'T';
    public static final char PLANTED_CHAR = 'L';
    public static final char HARVESTABLE_CHAR = 'V';
    public static final char EMPTY_DEFAULT_CHAR = ' ';
    public static final char ERROR_CHAR = '?';
    public static final char NPC_CHAR = 'N';
    public static final char OCCUPIED_UNKNOWN_CHAR = 'X';


    private List<Tile> tiles;
    private Random random = new Random();

    public static class PlacedObject {
        public int x, y, width, height;
        public String id;
        public PlacedObject(int x, int y, int width, int height, String id) {
            this.x = x; this.y = y; this.width = width; this.height = height; this.id = id;
        }
        boolean overlaps(int otherX, int otherY, int otherWidth, int otherHeight) {
            if (x + width <= otherX || otherX + otherWidth <= x) return false;
            if (y + height <= otherY || otherY + otherHeight <= y) return false;
            return true;
        }
    }
    private List<PlacedObject> placedStructures = new ArrayList<>();
    private PlacedObject housePhysicalStructure;
    private int houseEntranceX, houseEntranceY; // Koordinat tile PINTU di FarmMap
    private int houseDoorAccessX, houseDoorAccessY; // Koordinat tile AKSES PINTU (di depannya)

    private PlacedObject pondLocation;
    private PlacedObject shippingBinLocation;

    public FarmMap() {
        this.tiles = new ArrayList<>(MAP_WIDTH * MAP_HEIGHT);
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                Tile tile = new Tile(x, y);
                tile.setState(TileState.TILLABLE);
                tiles.add(tile);
            }
        }
        initializeLayout();
    }

    private void initializeLayout() {
        placedStructures.clear();
        housePhysicalStructure = null;
        pondLocation = null;
        shippingBinLocation = null;

        placeHouseAndDoor(); // Ini akan menentukan houseEntranceX/Y dan houseDoorAccessX/Y
        placePond();
        placeShippingBin();

        // Set state tile di bawah struktur menjadi DEFAULT setelah semua ditempatkan
        for (PlacedObject structure : placedStructures) {
            if (HOUSE_ENTRANCE_EXTERIOR_ID.equals(structure.id)) continue; // Pintu tidak mengubah state jadi DEFAULT solid
            for (int y = structure.y; y < structure.y + structure.height; y++) {
                for (int x = structure.x; x < structure.x + structure.width; x++) {
                    Tile tile = getTileAtPosition(x, y);
                    if (tile != null && tile.getObjectOnTile() != null && tile.getObjectOnTile().equals(structure.id)) {
                         tile.setState(TileState.DEFAULT);
                    }
                }
            }
        }
    }

    private void placeHouseAndDoor() {
        int houseStartX, houseStartY;
        int attempts = 0;
        do {
            houseStartX = random.nextInt(MAP_WIDTH - HOUSE_WIDTH); // Sisakan 1 tile di kanan/bawah untuk akses pintu
            houseStartY = random.nextInt(MAP_HEIGHT - HOUSE_HEIGHT -1); // Sisakan 1 tile di bawah untuk akses pintu
            attempts++;
            if (attempts > 1000) { houseStartX = 1; houseStartY = 1; break; }
        } while (!isAreaFree(houseStartX, houseStartY, HOUSE_WIDTH, HOUSE_HEIGHT, null, null));

        this.housePhysicalStructure = new PlacedObject(houseStartX, houseStartY, HOUSE_WIDTH, HOUSE_HEIGHT, HOUSE_STRUCTURE_ID);
        placedStructures.add(this.housePhysicalStructure); // Tambahkan rumah sebagai struktur solid

        for (int y = houseStartY; y < houseStartY + HOUSE_HEIGHT; y++) {
            for (int x = houseStartX; x < houseStartX + HOUSE_WIDTH; x++) {
                placeIndividualTileObject(x, y, HOUSE_STRUCTURE_ID, TileState.DEFAULT, true);
            }
        }
        
        // Pintu di tengah sisi bawah rumah
        this.houseEntranceX = houseStartX + HOUSE_WIDTH / 2;
        this.houseEntranceY = houseStartY + HOUSE_HEIGHT - 1;
        // Tile akses pintu adalah satu tile di bawah (luar) pintu
        this.houseDoorAccessX = this.houseEntranceX;
        this.houseDoorAccessY = this.houseEntranceY + 1;

        // Pastikan tile akses pintu ada dalam batas peta
        if (this.houseDoorAccessY >= MAP_HEIGHT) {
            // Jika pintu di tepi bawah, pindahkan akses ke atas pintu (jarang terjadi jika penempatan rumah benar)
            this.houseDoorAccessY = this.houseEntranceY -1;
            if (this.houseDoorAccessY <0) { // Jika rumah di tepi atas juga, ini masalah layout
                 System.err.println("Peringatan: Tidak bisa menentukan akses pintu rumah yang valid.");
                 this.houseDoorAccessY = houseStartY; // Fallback
            }
        }
        
        // Tandai tile pintu di FarmMap
        Tile doorTile = getTileAtPosition(this.houseEntranceX, this.houseEntranceY);
        if (doorTile != null) {
            doorTile.setObjectOnTile(HOUSE_ENTRANCE_EXTERIOR_ID);
            doorTile.setOccupied(false); // Pintu tidak solid, bisa diinteraksikan
            doorTile.setState(TileState.DEFAULT);
        }
        System.out.println("Rumah ditempatkan di: (" + houseStartX + "," + houseStartY + ") Pintu: ("+this.houseEntranceX+","+this.houseEntranceY+"). Akses Pintu: ("+this.houseDoorAccessX+","+this.houseDoorAccessY+")");
    }
    
    private void placeIndividualTileObject(int x, int y, String objectId, TileState state, boolean occupied) {
        Tile tile = getTileAtPosition(x,y);
        if (tile != null) {
            tile.setObjectOnTile(objectId);
            tile.setState(state);
            tile.setOccupied(occupied);
        }
    }

    // Overload isAreaFree untuk menyertakan koordinat terlarang (misalnya, akses pintu)
    private boolean isAreaFree(int checkX, int checkY, int checkWidth, int checkHeight, String excludeId, List<int[]> forbiddenCoords) {
        if (checkX < 0 || checkX + checkWidth > MAP_WIDTH || checkY < 0 || checkY + checkHeight > MAP_HEIGHT) {
            return false;
        }
        // Cek terhadap struktur yang sudah ada
        for (PlacedObject placed : placedStructures) {
            if (excludeId != null && placed.id.equals(excludeId)) continue;
            if (placed.overlaps(checkX, checkY, checkWidth, checkHeight)) {
                return false;
            }
        }
        // Cek terhadap koordinat terlarang (misalnya, tile akses pintu rumah)
        if (forbiddenCoords != null) {
            for (int y = checkY; y < checkY + checkHeight; y++) {
                for (int x = checkX; x < checkX + checkWidth; x++) {
                    for (int[] forbidden : forbiddenCoords) {
                        if (x == forbidden[0] && y == forbidden[1]) {
                            return false; // Area yang dicek tumpang tindih dengan koordinat terlarang
                        }
                    }
                }
            }
        }
        return true;
    }

    private void placePond() {
        List<int[]> forbiddenForPond = new ArrayList<>();
        if (housePhysicalStructure != null) { // Pastikan rumah sudah ditempatkan
            forbiddenForPond.add(new int[]{this.houseDoorAccessX, this.houseDoorAccessY});
        }

        int pondX, pondY, attempts = 0;
        do {
            pondX = random.nextInt(MAP_WIDTH - POND_WIDTH + 1);
            pondY = random.nextInt(MAP_HEIGHT - POND_HEIGHT + 1);
            attempts++; if (attempts > 1000) {System.err.println("Gagal menempatkan kolam."); return;}
        } while (!isAreaFree(pondX, pondY, POND_WIDTH, POND_HEIGHT, null, forbiddenForPond));
        
        for (int y = pondY; y < pondY + POND_HEIGHT; y++) {
            for (int x = pondX; x < pondX + POND_WIDTH; x++) {
                placeIndividualTileObject(x, y, POND_ID, TileState.DEFAULT, true);
            }
        }
        this.pondLocation = new PlacedObject(pondX, pondY, POND_WIDTH, POND_HEIGHT, POND_ID);
        placedStructures.add(this.pondLocation);
        System.out.println("Kolam ditempatkan di: (" + pondX + "," + pondY + ")");
    }

    private void placeShippingBin() {
        if (housePhysicalStructure == null) { /* ... (logika fallback seperti sebelumnya) ... */ return; }

        List<int[]> forbiddenForBin = new ArrayList<>();
        forbiddenForBin.add(new int[]{this.houseDoorAccessX, this.houseDoorAccessY}); // Tile akses pintu rumah
        // Bisa juga menambahkan tile pintu itu sendiri jika perlu
        // forbiddenForBin.add(new int[]{this.houseEntranceX, this.houseEntranceY});


        List<int[]> potentialSpots = new ArrayList<>();
        // Atas rumah
        potentialSpots.add(new int[]{housePhysicalStructure.x + housePhysicalStructure.width / 2 - SHIPPING_BIN_WIDTH / 2, housePhysicalStructure.y - SHIPPING_BIN_HEIGHT});
        // Bawah rumah (hindari menutupi akses pintu)
        if(!( (housePhysicalStructure.x + housePhysicalStructure.width / 2 - SHIPPING_BIN_WIDTH / 2 == houseDoorAccessX && housePhysicalStructure.y + housePhysicalStructure.height == houseDoorAccessY ) || 
              (new PlacedObject(housePhysicalStructure.x + housePhysicalStructure.width / 2 - SHIPPING_BIN_WIDTH / 2, housePhysicalStructure.y + housePhysicalStructure.height, SHIPPING_BIN_WIDTH, SHIPPING_BIN_HEIGHT, "").overlaps(houseDoorAccessX, houseDoorAccessY, 1,1))
            )) {
            potentialSpots.add(new int[]{housePhysicalStructure.x + housePhysicalStructure.width / 2 - SHIPPING_BIN_WIDTH / 2, housePhysicalStructure.y + housePhysicalStructure.height});
        }
        // Kiri rumah
        potentialSpots.add(new int[]{housePhysicalStructure.x - SHIPPING_BIN_WIDTH, housePhysicalStructure.y + housePhysicalStructure.height / 2 - SHIPPING_BIN_HEIGHT / 2});
        // Kanan rumah
        potentialSpots.add(new int[]{housePhysicalStructure.x + housePhysicalStructure.width, housePhysicalStructure.y + housePhysicalStructure.height / 2 - SHIPPING_BIN_HEIGHT / 2});
        
        Collections.shuffle(potentialSpots, random);

        for (int[] spot : potentialSpots) {
            if (isAreaFree(spot[0], spot[1], SHIPPING_BIN_WIDTH, SHIPPING_BIN_HEIGHT, HOUSE_STRUCTURE_ID, forbiddenForBin)) {
                for (int y = spot[1]; y < spot[1] + SHIPPING_BIN_HEIGHT; y++) {
                    for (int x = spot[0]; x < spot[0] + SHIPPING_BIN_WIDTH; x++) {
                        placeIndividualTileObject(x, y, SHIPPING_BIN_ID, TileState.DEFAULT, true);
                    }
                }
                this.shippingBinLocation = new PlacedObject(spot[0], spot[1], SHIPPING_BIN_WIDTH, SHIPPING_BIN_HEIGHT, SHIPPING_BIN_ID);
                placedStructures.add(this.shippingBinLocation);
                System.out.println("Kotak Kirim ditempatkan di: (" + spot[0] + "," + spot[1] + ") dekat rumah.");
                return;
            }
        }
        System.err.println("Tidak dapat menemukan spot untuk Kotak Kirim dekat rumah (mungkin terhalang akses pintu). Menempatkan secara acak.");
        // ... (logika fallback penempatan acak dengan pengecekan forbiddenForBin)
        int binX, binY, attempts = 0;
        do {
            binX = random.nextInt(MAP_WIDTH - SHIPPING_BIN_WIDTH + 1);
            binY = random.nextInt(MAP_HEIGHT - SHIPPING_BIN_HEIGHT + 1);
            attempts++; if (attempts > 100) { System.err.println("Kritis: Gagal menempatkan kotak pengiriman (acak)."); return; }
        } while (!isAreaFree(binX, binY, SHIPPING_BIN_WIDTH, SHIPPING_BIN_HEIGHT, null, forbiddenForBin));
        for (int y = binY; y < binY + SHIPPING_BIN_HEIGHT; y++) {
            for (int x = binX; x < binX + SHIPPING_BIN_WIDTH; x++) {
                placeIndividualTileObject(x, y, SHIPPING_BIN_ID, TileState.DEFAULT, true);
            }
        }
        this.shippingBinLocation = new PlacedObject(binX, binY, SHIPPING_BIN_WIDTH, SHIPPING_BIN_HEIGHT, SHIPPING_BIN_ID);
        placedStructures.add(this.shippingBinLocation);
        System.out.println("Kotak Kirim (acak) ditempatkan di: (" + binX + "," + binY + ")");
    }

    @Override
    public Tile getTileAtPosition(int x, int y) {
        if (x < 0 || x >= MAP_WIDTH || y < 0 || y >= MAP_HEIGHT) return null;
        return tiles.get(y * MAP_WIDTH + x);
    }

    @Override
    public void display(Player player) {
        // ... (Sama seperti sebelumnya, pastikan karakter untuk HOUSE_STRUCTURE_ID dan HOUSE_ENTRANCE_EXTERIOR_ID benar)
        System.out.println("\n--- " + getMapName() + " ---");
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                Tile tile = getTileAtPosition(x, y);
                char charToDisplay = EMPTY_DEFAULT_CHAR;
                if (tile == null) { System.out.print("[" + ERROR_CHAR + "]"); continue; }

                if (player != null && player.getX() == x && player.getY() == y && player.getCurrentLocationName().equals(getMapName())) {
                    charToDisplay = PLAYER_CHAR;
                } else if (tile.getObjectOnTile() != null && tile.getObjectOnTile().equals(HOUSE_ENTRANCE_EXTERIOR_ID)) {
                    charToDisplay = HOUSE_DOOR_CHAR;
                } else if (tile.getState() == TileState.PLANTED) {
                    charToDisplay = PLANTED_CHAR;
                } else if (tile.getState() == TileState.HARVESTABLE) {
                    charToDisplay = HARVESTABLE_CHAR;
                } else if (tile.isOccupied()) {
                    Object objOnTile = tile.getObjectOnTile();
                    if (objOnTile instanceof String) {
                        String objId = (String) objOnTile;
                        if (HOUSE_STRUCTURE_ID.equals(objId)) charToDisplay = HOUSE_STRUCTURE_CHAR;
                        else if (POND_ID.equals(objId)) charToDisplay = POND_CHAR;
                        else if (SHIPPING_BIN_ID.equals(objId)) charToDisplay = SHIPPING_BIN_CHAR;
                        else charToDisplay = OCCUPIED_UNKNOWN_CHAR;
                    } else if (objOnTile instanceof NPC) {
                        charToDisplay = NPC_CHAR;
                    } else {
                        charToDisplay = OCCUPIED_UNKNOWN_CHAR;
                    }
                } else {
                    switch (tile.getState()) {
                        case TILLABLE: charToDisplay = TILLABLE_CHAR; break;
                        case TILLED:   charToDisplay = TILLED_CHAR;   break;
                        case DEFAULT:  charToDisplay = EMPTY_DEFAULT_CHAR; break;
                        default:       charToDisplay = EMPTY_DEFAULT_CHAR; break;
                    }
                }
                System.out.print(" " + charToDisplay + " ");
            }
            System.out.println();
        }
        System.out.println("Legenda: P:Pemain, H:Rumah, D:Pintu Rumah, O:Kolam, S:Kotak Kirim, .:Bisa Dicangkul, T:Tercangkul, L:Ditanami, V:Siap Panen");
    }


    @Override
    public String getMapName() { return "Farm"; }
    @Override
    public int getWidth() { return MAP_WIDTH; }
    @Override
    public int getHeight() { return MAP_HEIGHT; }

    @Override
    public void placeObjectOnTile(Object obj, int x, int y) {
        // Metode ini lebih untuk objek dinamis seperti NPC atau item yang dijatuhkan.
        // Struktur statis ditempatkan oleh placeIndividualTileObject atau placeStructureOnMap.
        Tile tile = getTileAtPosition(x, y);
        if (tile != null && !tile.isOccupied()) { // Hanya tempatkan jika tile tidak ditempati oleh struktur solid
            tile.setObjectOnTile(obj);
            if (obj instanceof NPC) { // NPC membuat tile ditempati
                tile.setOccupied(true);
            }
            // Item yang dijatuhkan mungkin tidak membuat tile occupied.
        }
    }
    @Override
    public void removeObjectFromTile(int x, int y) {
        Tile tile = getTileAtPosition(x,y);
        if (tile != null) {
            // Jangan hapus struktur statis dengan cara ini
            if (!(tile.getObjectOnTile() instanceof String && 
                  (((String)tile.getObjectOnTile()).equals(HOUSE_STRUCTURE_ID) ||
                   ((String)tile.getObjectOnTile()).equals(POND_ID) ||
                   ((String)tile.getObjectOnTile()).equals(SHIPPING_BIN_ID) ||
                   ((String)tile.getObjectOnTile()).equals(HOUSE_ENTRANCE_EXTERIOR_ID) ))) {
                tile.setObjectOnTile(null);
                tile.setOccupied(false);
                // Kembalikan ke TILLABLE hanya jika bukan bagian dari footprint struktur
                if (housePhysicalStructure == null || !housePhysicalStructure.overlaps(x,y,1,1)) {
                     tile.setState(TileState.TILLABLE);
                } else {
                     tile.setState(TileState.DEFAULT);
                }
            }
        }
    }
    @Override
    public String getExitDestination(int attemptedOutOfBoundX, int attemptedOutOfBoundY) {
        // Pemain berada di tepi peta (x,y) dan mencoba bergerak ke (attemptedOutOfBoundX, attemptedOutOfBoundY)
        if (attemptedOutOfBoundY < 0 && attemptedOutOfBoundX == 0) return "Mountain Area";    // Keluar dari atas
        if (attemptedOutOfBoundX < 0 && attemptedOutOfBoundY == 0) return "Forest Zone";      // Keluar dari kiri
        if (attemptedOutOfBoundY >= MAP_HEIGHT && attemptedOutOfBoundX == 0) return "Coastal Region"; // Keluar dari bawah
        if (attemptedOutOfBoundX >= MAP_WIDTH && attemptedOutOfBoundY == 0) return "Town";  // Keluar dari kanan (langsung ke Town)
        return null;
    }

    @Override
    public int[] getEntryPoint(String comingFromMapName) {
        if ("Player's House".equals(comingFromMapName)) {
            // Pemain muncul di tile akses pintu (luar rumah)
            if (this.houseDoorAccessX >= 0 && this.houseDoorAccessY >=0) {
                 Tile spawnAccessTile = getTileAtPosition(this.houseDoorAccessX, this.houseDoorAccessY);
                 if(spawnAccessTile != null && !spawnAccessTile.isOccupied()){
                    return new int[]{this.houseDoorAccessX, this.houseDoorAccessY};
                 } else {
                     // Fallback jika akses tile terblokir
                     int[][] offsets = {{0,0}, {0,1}, {0,-1}, {-1,0}, {1,0}}; // Cek tile pintu dulu, lalu sekitar
                     for(int[] offset : offsets) {
                         int checkX = this.houseEntranceX + offset[0];
                         int checkY = this.houseEntranceY + (offset[1] == 0 ? 1 : offset[1]); // Prioritaskan bawah pintu
                         if(isTileValidForSpawn(checkX, checkY)) return new int[]{checkX, checkY};
                     }
                 }
            }
        } else if ("Forest Zone".equals(comingFromMapName)) {
            return new int[]{0, 0}; 
        } else if ("Mountain Area".equals(comingFromMapName)) {
            return new int[]{0, 0}; 
        } else if ("Coastal Region".equals(comingFromMapName)) {
            return new int[]{MAP_WIDTH - 1, 0}; 
        } else if ("Town".equals(comingFromMapName)) {
            return new int[]{MAP_WIDTH - 1, 0}; 
        }
        // Titik spawn awal game (jika houseDoorAccess sudah ada)
        if (comingFromMapName == null && this.houseDoorAccessX >= 0 && this.houseDoorAccessY >=0) {
            return new int[]{this.houseDoorAccessX, this.houseDoorAccessY};
        }
        return new int[]{MAP_WIDTH / 2, MAP_HEIGHT / 2}; // Fallback umum
    }
    
    private boolean isTileValidForSpawn(int x, int y){
        Tile t = getTileAtPosition(x,y);
        return t != null && !t.isOccupied() && !HOUSE_ENTRANCE_EXTERIOR_ID.equals(t.getObjectOnTile());
    }

    public List<Tile> getTiles() { return this.tiles; }
    public PlacedObject getHouseStructureLocation() { return this.housePhysicalStructure; }
    public int getHouseEntranceX() { return this.houseEntranceX; }
    public int getHouseEntranceY() { return this.houseEntranceY; }
    public int getHouseDoorAccessX() { return this.houseDoorAccessX; }
    public int getHouseDoorAccessY() { return this.houseDoorAccessY; }
    public PlacedObject getPondLocation() { return this.pondLocation;}
    public PlacedObject getShippingBinLocation() { return this.shippingBinLocation;}
}
