package spakbor.items;

public class Crop extends Item implements EdibleItem {
    private int buyPrice;
    private int sellPrice;
    private int energy;
    private int amountPerHarvest;

    public Crop(String name, int buyPrice, int sellPrice, int energy, int amountPerHarvest) {
        super(name);
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.energy = energy;
        this.amountPerHarvest = amountPerHarvest;
    }

    @Override
    public int getSellPrice() {
        return sellPrice;
    }

    @Override
    public void use() {
        System.out.println("Memakan hasil panen " + name + ", energi +" + energy);
    }

    @Override
    public int getEnergyRestoration() {
        return energy;
    }

    public int getAmountPerHarvest() {
        return amountPerHarvest;
    }
}
