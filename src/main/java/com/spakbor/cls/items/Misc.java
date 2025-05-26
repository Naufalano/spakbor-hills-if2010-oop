package cls.items;

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
        if (sellPrice >= buyPrice && buyPrice > 0) {
            System.err.println("Peringatan untuk item '" + name + "': Harga jual (" + sellPrice +
                               ") tidak boleh lebih besar atau sama dengan harga beli (" + buyPrice + "). Menyesuaikan harga jual.");
            this.sellPrice = buyPrice / 2;
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
        System.out.println("Menggunakan barang " + name + ".");
    }
}
