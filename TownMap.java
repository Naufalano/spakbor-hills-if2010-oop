import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Diasumsikan Tile.java, TileState.java, Player.java, GameMap.java, NPC.java dapat diakses

public class TownMap implements GameMap {
    public static final int MAP_WIDTH = 30;
    public static final int MAP_HEIGHT = 25;

    // Identifier untuk objek peta
    public static final String BUILDING_WALL_ID = "BuildingWall";
    public static final String ROAD_ID = "Road";

    // ID unik untuk setiap pintu masuk/keluar di TownMap
    // Pintu masuk ke bangunan dari TownMap
    public static final String STORE_ENTRANCE_ID = "TOWN_DOOR_TO_Store";
    public static final String DASCO_HOUSE_ENTRANCE_ID = "TOWN_DOOR_TO_DascosLair";
    public static final String PERRY_HOUSE_ENTRANCE_ID = "TOWN_DOOR_TO_PerrysPlace";
    public static final String CAROLINE_HOUSE_ENTRANCE_ID = "TOWN_DOOR_TO_CarolinesHome";
    public static final String MAYOR_HOUSE_ENTRANCE_ID = "TOWN_DOOR_TO_MayorsManor";
    public static final String ABIGAIL_HOUSE_ENTRANCE_ID = "TOWN_DOOR_TO_AbigailsRoom";
    public static final String PLAYER_HOUSE_ENTRANCE_ON_TOWN_ID = "TOWN_DOOR_TO_PlayersHouse"; // Jika rumah pemain ada di kota

    // Pintu keluar dari TownMap ke peta lain
    public static final String TOWN_EXIT_TO_FARM_ID = "TOWN_EXIT_TO_Farm";
    public static final String TOWN_EXIT_TO_FOREST_ID = "TOWN_EXIT_TO_ForestZone";
    // Tambahkan ID pintu keluar lain jika perlu

    // Karakter untuk display
    public static final char ROAD_CHAR = '#';
    public static final char BUILDING_CHAR = 'B';
    public static final char DOOR_CHAR = 'D';
    public static final char GROUND_CHAR = ',';


    private List<Tile> tiles;
    private Random random = new Random();
    private List<DoorInfo> doors; // Untuk menyimpan informasi semua pintu

    // Kelas dalam untuk informasi pintu
    public static class DoorInfo {
        public String doorId;           // ID unik pintu (misalnya, TownMap.STORE_ENTRANCE_ID)
        public int x, y;                // Koordinat tile pintu di TownMap
        public String destinationMapName; // Nama peta tujuan (misalnya, "Store", "Dasco's Lair")
        // Anda bisa menambahkan atribut lain jika perlu, misal orientasi pintu

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
                tile.setState(TileState.DEFAULT); // Tanah kota
                tiles.add(tile);
            }
        }
        generateLayout();
    }

    private void generateLayout() {
        // 1. Buat Jalan (Roads) Terlebih Dahulu
        // Jalan utama horizontal di tengah
        for (int x = 0; x < MAP_WIDTH; x++) {
            placeObjectOnTile(ROAD_ID, x, MAP_HEIGHT / 2, false); // Jalan tidak solid
        }
        // Jalan utama vertikal di sepertiga peta
        for (int y = 0; y < MAP_HEIGHT; y++) {
            placeObjectOnTile(ROAD_ID, MAP_WIDTH / 3, y, false);
            placeObjectOnTile(ROAD_ID, (MAP_WIDTH / 3) * 2, y, false);
        }
        // Percabangan jalan kecil
        for (int x = MAP_WIDTH / 3; x < (MAP_WIDTH / 3) * 2; x++) {
            placeObjectOnTile(ROAD_ID, x, MAP_HEIGHT / 4, false);
            placeObjectOnTile(ROAD_ID, x, (MAP_HEIGHT / 4) * 3, false);
        }


        // 2. Tempatkan Bangunan dan Pintunya
        // Parameter: entranceId, buildingDisplayName, preferX, preferY, width, height, destinationMapKey
        // Toko
        placeBuildingWithEntrance(STORE_ENTRANCE_ID, "Eksterior Toko", 5, MAP_HEIGHT / 2 + 1, 7, 4, "Store");
        
        // Rumah NPC (penempatan disederhanakan, idealnya dengan algoritma yang lebih baik)
        placeBuildingWithEntrance(DASCO_HOUSE_ENTRANCE_ID, "Eksterior Rumah Dasco", 15, MAP_HEIGHT / 2 + 1, 5, 4, "Dasco's Lair");
        placeBuildingWithEntrance(PERRY_HOUSE_ENTRANCE_ID, "Eksterior Rumah Perry", 5, MAP_HEIGHT / 4 + 1, 5, 4, "Perry's Place");
        placeBuildingWithEntrance(CAROLINE_HOUSE_ENTRANCE_ID, "Eksterior Rumah Caroline", 15, MAP_HEIGHT / 4 + 1, 5, 4, "Caroline's Home");
        placeBuildingWithEntrance(MAYOR_HOUSE_ENTRANCE_ID, "Eksterior Rumah Mayor", (MAP_WIDTH/3)*2 + 1, 5, 6, 5, "Mayor's Manor");
        placeBuildingWithEntrance(ABIGAIL_HOUSE_ENTRANCE_ID, "Eksterior Rumah Abigail", (MAP_WIDTH/3)*2 + 1, 15, 5, 4, "Abigail's Room");

        // 3. Tempatkan Pintu Keluar Utama dari Kota
        // Keluar ke Farm di sisi kiri tengah peta
        int exitToFarmX = 0;
        int exitToFarmY = MAP_HEIGHT / 2;
        placeObjectOnTile(TOWN_EXIT_TO_FARM_ID, exitToFarmX, exitToFarmY, false); // Pintu tidak solid
        doors.add(new DoorInfo(TOWN_EXIT_TO_FARM_ID, exitToFarmX, exitToFarmY, "Farm"));
        // Pastikan tile di (0, MAP_HEIGHT/2) adalah jalan atau area akses yang sesuai.
        // Jika tidak, mundurkan pintu satu tile ke dalam dan pastikan ada jalan menuju ke sana.
        // Untuk contoh ini, kita asumsikan pemain bisa mencapai tepi peta.
    }

    /**
     * Menempatkan bangunan dan pintu masuknya.
     * Pintu akan ditempatkan di sisi bawah bangunan secara default, menghadap jalan jika memungkinkan.
     */
    private void placeBuildingWithEntrance(String entranceId, String buildingExteriorId, 
                                           int preferredX, int preferredY, int width, int height, 
                                           String destinationMapKey) {
        // Cari posisi yang valid (tidak overlap dengan jalan utama atau bangunan lain)
        // Logika penempatan yang lebih canggih diperlukan di sini untuk menghindari overlap total
        // Untuk contoh ini, kita akan menempatkan berdasarkan preferredX,Y dan berharap tidak overlap parah.
        // Sebuah sistem layout yang baik akan mencoba beberapa posisi atau menggunakan grid yang telah ditentukan.

        int startX = preferredX;
        int startY = preferredY;

        // Validasi sederhana agar tidak keluar batas
        if (startX + width >= MAP_WIDTH) startX = MAP_WIDTH - width -1;
        if (startY + height >= MAP_HEIGHT) startY = MAP_HEIGHT - height -1;
        if (startX < 0) startX = 0;
        if (startY < 0) startY = 0;

        // Tandai area bangunan sebagai solid (dinding)
        for (int y = startY; y < startY + height; y++) {
            for (int x = startX; x < startX + width; x++) {
                // Hanya tempatkan dinding jika tile tersebut bukan jalan utama
                Tile currentTile = getTileAtPosition(x,y);
                if (currentTile != null && (currentTile.getObjectOnTile() == null || !ROAD_ID.equals(currentTile.getObjectOnTile()))) {
                    placeObjectOnTile(BUILDING_WALL_ID, x, y, true);
                } else if (currentTile != null && ROAD_ID.equals(currentTile.getObjectOnTile())) {
                    // Ada jalan di bawah bangunan, ini tidak ideal. Perlu logika penempatan yang lebih baik.
                    // Untuk sekarang, kita timpa saja jalan dengan dinding jika ini terjadi.
                    placeObjectOnTile(BUILDING_WALL_ID, x, y, true);
                }
            }
        }
        
        // Tempatkan pintu di tengah sisi bawah bangunan
        int doorX = startX + width / 2;
        int doorY = startY + height - 1; // Pintu adalah bagian dari dinding bawah

        // Pastikan tile di depan pintu (tempat pemain berdiri) adalah jalan atau bisa diakses
        Tile accessTile = getTileAtPosition(doorX, doorY + 1);
        if (accessTile == null || (accessTile.isOccupied() && !ROAD_ID.equals(accessTile.getObjectOnTile()) )) {
            // Jika akses terhalang atau di luar map, coba pindahkan pintu ke sisi lain atau sesuaikan posisi bangunan
            // Untuk contoh ini, kita tetap letakkan pintu, tapi ini adalah area yang perlu perbaikan
            System.err.println("Peringatan: Akses ke pintu " + entranceId + " mungkin terhalang.");
        }

        // Hapus dinding di lokasi pintu dan tempatkan ID pintu
        removeObjectFromTile(doorX, doorY); // Hapus dinding
        placeObjectOnTile(entranceId, doorX, doorY, false); // Pintu tidak solid
        doors.add(new DoorInfo(entranceId, doorX, doorY, destinationMapKey));
        System.out.println("Pintu " + entranceId + " ke " + destinationMapKey + " ditempatkan di (" + doorX + "," + doorY + ")");
    }

    // Modifikasi placeObjectOnTile untuk menerima parameter 'isSolid'
    private void placeObjectOnTile(String objectId, int x, int y, boolean isSolid) {
        Tile tile = getTileAtPosition(x, y);
        if (tile != null) {
            // Jangan menimpa pintu lain atau objek penting lainnya tanpa logika khusus
            if (tile.getObjectOnTile() instanceof String && ((String)tile.getObjectOnTile()).contains("DOOR")) {
                if (!objectId.contains("DOOR")) return; // Jangan timpa pintu dengan non-pintu
            }

            tile.setObjectOnTile(objectId);
            tile.setOccupied(isSolid);
            if (isSolid && !ROAD_ID.equals(objectId)) { // Struktur solid mengubah tile state
                tile.setState(TileState.DEFAULT);
            } else if (ROAD_ID.equals(objectId)) {
                tile.setState(TileState.DEFAULT); // Jalan juga bisa state default
            }
        }
    }
    // Overload untuk kompatibilitas dengan GameMap interface (jika obj bukan String)
    @Override
    public void placeObjectOnTile(Object obj, int x, int y) {
        if (obj instanceof String) {
            // Tentukan apakah solid berdasarkan jenis string ID, atau default ke false
            boolean isSolid = !( ((String)obj).contains("DOOR") || ((String)obj).equals(ROAD_ID) );
            placeObjectOnTile((String)obj, x, y, isSolid);
        } else if (obj instanceof NPC) {
            Tile tile = getTileAtPosition(x,y);
            if(tile != null) {
                tile.setObjectOnTile(obj);
                tile.setOccupied(true); // NPC solid
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
                        else if (objId.equals(ROAD_ID)) charToDisplay = ROAD_CHAR;
                        else if (tile.isOccupied()) charToDisplay = 'X'; // Struktur lain
                    } else if (obj instanceof NPC) {
                        charToDisplay = ((NPC) obj).getName().charAt(0);
                    } else if (tile.isOccupied()){
                        charToDisplay = 'X';
                    }
                }
                System.out.print("[" + charToDisplay + "]");
            }
            System.out.println();
        }
        System.out.println("P:Pemain, B:Bangunan, D:Pintu, #:Jalan, ,:Tanah, N:NPC, X:Lainnya");
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
            tile.setObjectOnTile(null); // Atau set ke ROAD_ID jika ini adalah jalan yang tertimpa
            tile.setOccupied(false);
            tile.setState(TileState.DEFAULT);
        }
    }

    @Override
    public String getExitDestination(int attemptedOutOfBoundX, int attemptedOutOfBoundY) {
        Tile edgeTile = null;
        if (attemptedOutOfBoundX < 0) edgeTile = getTileAtPosition(0, attemptedOutOfBoundY);
        else if (attemptedOutOfBoundX >= MAP_WIDTH) edgeTile = getTileAtPosition(MAP_WIDTH-1, attemptedOutOfBoundY);
        else if (attemptedOutOfBoundY < 0) edgeTile = getTileAtPosition(attemptedOutOfBoundX, 0);
        else if (attemptedOutOfBoundY >= MAP_HEIGHT) edgeTile = getTileAtPosition(attemptedOutOfBoundX, MAP_HEIGHT-1);

        if(edgeTile != null && TOWN_EXIT_TO_FARM_ID.equals(edgeTile.getObjectOnTile())) {
            return "Farm";
        }
        return null;
    }

    @Override
    public int[] getEntryPoint(String comingFromMapName) {
        for (DoorInfo door : doors) {
            if (door.destinationMapName.equals(comingFromMapName)) {
                int spawnX = door.x;
                int spawnY = door.y + 1;
                if (spawnY >= MAP_HEIGHT || (getTileAtPosition(spawnX, spawnY) != null && getTileAtPosition(spawnX, spawnY).isOccupied() && !ROAD_ID.equals(getTileAtPosition(spawnX, spawnY).getObjectOnTile()))) {
                    spawnY = door.y - 1;
                }
                if (spawnY < 0 || (getTileAtPosition(spawnX, spawnY) != null && getTileAtPosition(spawnX, spawnY).isOccupied() && !ROAD_ID.equals(getTileAtPosition(spawnX, spawnY).getObjectOnTile()))) {
                     spawnY = door.y;
                     spawnX = (door.x +1 < MAP_WIDTH) ? door.x+1 : door.x-1;
                }
                return new int[]{spawnX, spawnY};
            }
        }
        if ("Farm".equals(comingFromMapName)) {
             for (DoorInfo door : doors) {
                 if (TOWN_EXIT_TO_FARM_ID.equals(door.doorId)) {
                     return new int[]{door.x + 1 < MAP_WIDTH ? door.x +1 : door.x-1, door.y}; // Di samping pintu keluar ke Farm
                 }
             }
        }
        return new int[]{MAP_WIDTH / 3 + 1, MAP_HEIGHT / 2}; // Titik masuk default (misal di jalan)
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
