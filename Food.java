package spakbor.items;

public class Food extends Item implements EdibleItem {
    private int energy;
    private int buyPrice;
    private int sellPrice;

    public Food(String name, int energy, int buyPrice, int sellPrice) {
        super(name);
        this.energy = energy;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    @Override
    public int getSellPrice() {
        return sellPrice;
    }

    @Override
    public void use() {
        System.out.println("Memakan makanan " + name + ", energi +" + energy);
    }

    @Override
    public int getEnergyRestoration() {
        return energy;
    }
}
