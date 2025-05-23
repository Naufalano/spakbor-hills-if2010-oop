package spakbor.items;

public class Fish extends Item implements EdibleItem {
    private int energy;
    private int sellPrice;

    public Fish(String name, int sellPrice, int energy) {
        super(name);
        this.sellPrice = sellPrice;
        this.energy = energy;
    }

    @Override
    public int getSellPrice() {
        return sellPrice;
    }

    @Override
    public void use() {
        System.out.println("Memakan ikan " + name + ", energi +" + energy);
    }

    @Override
    public int getEnergyRestoration() {
        return energy;
    }
}
