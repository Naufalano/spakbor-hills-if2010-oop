import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Diasumsikan Misc.java dan Item.java sudah dapat diakses

public class MiscDataRegistry {
    private static final List<Misc> ALL_MISC_ITEMS = new ArrayList<>();

    static {
        // Item Misc. berdasarkan kriteria dan kebutuhan resep

        // 1. Firewood (Kayu Bakar) - Bahan bakar untuk memasak
        // Harga beli: 20g, Harga jual: 5g (Contoh, jual < beli)
        ALL_MISC_ITEMS.add(new Misc("Firewood", 20, 5));

        // 2. Coal (Arang) - Bahan bakar untuk memasak, lebih efisien
        // Harga beli: 50g, Harga jual: 15g (Contoh)
        ALL_MISC_ITEMS.add(new Misc("Coal", 50, 15));

        // 3. Egg (Telur) - Bahan resep
        // Harga beli: 30g, Harga jual: 10g (Contoh)
        ALL_MISC_ITEMS.add(new Misc("Egg", 30, 10));

        // 4. Eggplant (Terong) - Bahan resep
        // Harga beli: 80g, Harga jual: 35g (Contoh)
        // Jika Anda memutuskan Terong adalah hasil panen, pindahkan ke CropDataRegistry dan buat Seed-nya.
        // Untuk saat ini, kita anggap sebagai Misc yang bisa dibeli atau ditemukan.
        ALL_MISC_ITEMS.add(new Misc("Eggplant", 80, 35));

        // 5. Proposal Ring (Cincin Lamaran) - Item khusus untuk melamar NPC
        // Harga beli: 10000g (mahal, item penting), Harga jual: 0g (tidak untuk dijual kembali, atau sangat rendah)
        // PDF menyatakan ini reusable, jadi tidak akan hilang dari inventory saat digunakan oleh Action.
        ALL_MISC_ITEMS.add(new Misc("Proposal Ring", 10000, 0));
        
        // Tambahkan item misc. lainnya di sini jika ada
        // Contoh:
        // ALL_MISC_ITEMS.add(new Misc("Old Coin", 5, 2));
        // ALL_MISC_ITEMS.add(new Misc("Rare Gem", 500, 200));
    }

    /**
     * Mendapatkan daftar semua item misc. yang terdefinisi.
     * @return Sebuah List baru yang berisi semua item misc.
     */
    public static List<Misc> getAllMiscItems() {
        return new ArrayList<>(ALL_MISC_ITEMS); // Mengembalikan salinan untuk mencegah modifikasi eksternal
    }

    /**
     * Mendapatkan item misc. spesifik berdasarkan namanya (tidak case-sensitive).
     * @param name Nama item misc. yang dicari.
     * @return Objek Misc jika ditemukan, jika tidak null.
     */
    public static Misc getMiscItemByName(String name) {
        for (Misc item : ALL_MISC_ITEMS) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item; // Mengembalikan referensi ke objek prototipe
            }
        }
        System.err.println("Peringatan: Item Misc. dengan nama '" + name + "' tidak ditemukan di MiscDataRegistry.");
        return null;
    }

    /**
     * Mendapatkan daftar item misc. yang dapat dibeli (harga beli > 0).
     * @return Sebuah List item misc. yang dapat dibeli.
     */
    public static List<Misc> getPurchasableMiscItems() {
        return ALL_MISC_ITEMS.stream()
                .filter(item -> item.getBuyPrice() > 0)
                .collect(Collectors.toList());
    }
}
