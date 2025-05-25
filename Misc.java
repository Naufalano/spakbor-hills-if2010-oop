// Diasumsikan Item.java sudah ada dan Misc adalah subclass dari Item
public class Misc extends Item {
    private int buyPrice;
    private int sellPrice;

    /**
     * Konstruktor untuk item lain-lain.
     * @param name Nama item.
     * @param buyPrice Harga beli item.
     * @param sellPrice Harga jual item (harus lebih rendah dari buyPrice).
     */
    public Misc(String name, int buyPrice, int sellPrice) {
        super(name);
        if (sellPrice >= buyPrice && buyPrice > 0) { // Hanya validasi jika buyPrice > 0, item gratis bisa dijual 0
            System.err.println("Peringatan untuk item '" + name + "': Harga jual (" + sellPrice +
                               ") tidak boleh lebih besar atau sama dengan harga beli (" + buyPrice + "). Menyesuaikan harga jual.");
            this.sellPrice = buyPrice / 2; // Contoh penyesuaian otomatis
        } else {
            this.sellPrice = sellPrice;
        }
        this.buyPrice = buyPrice;
    }

    @Override
    public int getSellPrice() {
        return sellPrice;
    }

    /**
     * Mendapatkan harga beli item ini.
     * @return Harga beli.
     */
    public int getBuyPrice() {
        return buyPrice;
    }

    @Override
    public void use() {
        // Aksi default saat item misc digunakan.
        // Sebagian besar item misc akan memiliki logika penggunaan khusus
        // yang ditangani oleh Action yang mengonsumsinya (misalnya, Coal/Firewood oleh CookingAction).
        System.out.println("Menggunakan barang " + name + ".");
        // Contoh: Jika ini adalah "Surat Misterius", membacanya bisa terjadi di sini.
        // Namun, untuk item seperti fuel, 'use' mungkin tidak melakukan apa-apa secara langsung.
    }
}
