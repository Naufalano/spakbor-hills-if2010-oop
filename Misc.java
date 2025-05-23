package spakbor.items;

public class Misc extends Item {
    private int buyPrice;
    private int sellPrice;

    public Misc(String name, int buyPrice, int sellPrice) {
        super(name);
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    @Override
    public int getSellPrice() {
        return sellPrice;
    }

    @Override
    public void use() {
        System.out.println("Menggunakan barang " + name);
    }
}
