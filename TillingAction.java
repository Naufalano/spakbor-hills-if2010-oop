// Diasumsikan Player, Farm, FarmMap, Tile, TileState, Item, Equipment dapat diakses

public class TillingAction extends Action {
    private Item hoe; // Hoe adalah equipment, kita cari berdasarkan namanya

    public TillingAction() {
        // Hoe diperiksa di validate
        this.hoe = new Equipment("Hoe"); // Untuk pengecekan hasItem
    }

    @Override
    public boolean validate(Player player, Farm farm) {
        // 1. Cek apakah pemain berada di peta kebun mereka
        // Kita bandingkan nama lokasi pemain saat ini dengan nama peta kebun yang sebenarnya
        GameMap currentPlayersMap = farm.getCurrentMap();
        FarmMap playersActualFarmMap = farm.getFarmMap(); // Mendapatkan instance FarmMap pemain

        if (currentPlayersMap == null || playersActualFarmMap == null) {
            System.out.println("Validasi Gagal: Data peta tidak tersedia.");
            return false;
        }

        // Pengecekan yang lebih baik: apakah peta saat ini adalah instance FarmMap milik pemain?
        if (!(currentPlayersMap instanceof FarmMap) || !currentPlayersMap.getMapName().equals(playersActualFarmMap.getMapName())) {
             System.out.println("Validasi Gagal: Tilling hanya bisa dilakukan di kebun utama Anda (" + playersActualFarmMap.getMapName() + "). Lokasi saat ini: " + player.getCurrentLocationName());
             return false;
        }
        // Alternatif, jika farm.getName() adalah nama kebun yang unik:
        // if (!player.getCurrentLocationName().equals(farm.getName())) {
        //     System.out.println("Validasi Gagal: Tilling hanya bisa dilakukan di kebun Anda. Lokasi saat ini: " + player.getCurrentLocationName());
        //     return false;
        // }


        // 2. Cek apakah pemain memiliki Hoe
        if (!player.getInventory().hasItem(this.hoe)) { // Membutuhkan equals/hashCode di Item
            System.out.println("Validasi Gagal: Cangkul (Hoe) tidak ditemukan di inventaris.");
            return false;
        }

        // 3. Cek energi pemain
        if (player.getEnergy() < 5) { // Biaya energi per tile
            System.out.println("Validasi Gagal: Energi tidak cukup untuk mencangkul.");
            return false;
        }

        // 4. Cek tile tempat pemain berdiri
        // Kita menggunakan currentPlayersMap karena sudah divalidasi bahwa ini adalah FarmMap yang benar
        Tile currentTile = currentPlayersMap.getTileAtPosition(player.getX(), player.getY());

        if (currentTile == null) {
            System.out.println("Validasi Gagal: Pemain tidak berada di tile yang valid.");
            return false;
        }
        if (currentTile.getState() != TileState.TILLABLE && currentTile.getState() != TileState.DEFAULT) {
            System.out.println("Validasi Gagal: Tile ini tidak bisa dicangkul (status saat ini: " + currentTile.getState() + ").");
            return false;
        }
        if (currentTile.isOccupied()) {
            // Pengecualian: jika "occupied" oleh sesuatu yang bisa dihilangkan dengan cangkul (misal, rumput liar kecil)
            // Untuk saat ini, tile yang occupied tidak bisa dicangkul.
            System.out.println("Validasi Gagal: Tile ini sedang ditempati.");
            return false;
        }
        return true;
    }

    @Override
    public void execute(Player player, Farm farm) {
        player.setEnergy(player.getEnergy() - 5);
        farm.advanceGameTime(5); // 5 menit per tile

        // Gunakan currentMap untuk mendapatkan tile yang akan diubah
        GameMap currentMap = farm.getCurrentMap();
        Tile currentTile = currentMap.getTileAtPosition(player.getX(), player.getY());
        if (currentTile != null) { // Periksa lagi untuk keamanan
            currentTile.setState(TileState.TILLED);
            System.out.println(player.getName() + " mencangkul tanah di (" + player.getX() + "," + player.getY() + "). Energi: " + player.getEnergy());
            currentMap.display(player); // Tampilkan peta yang diperbarui
        } else {
            System.err.println("Kesalahan saat eksekusi Tilling: tile pemain tidak valid.");
        }
    }
}
