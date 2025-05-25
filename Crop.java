// Crop.java - Assuming it looks like this:
public class Crop extends Item implements EdibleItem {
    private int buyPrice;    // Harga Beli (per crop)
    private int sellPrice;   // Harga Jual (per crop)
    private int energyRestoration; // Energy restored when eaten (fixed at 3 for these crops)
    private int yieldAmount; // Jumlah Crop per Panen (how many of this crop item you get from one harvest action on a plant)

    public Crop(String name, int buyPrice, int sellPrice, int energyRestoration, int yieldAmount) {
        super(name);
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.energyRestoration = energyRestoration; // This will be 3 for crops from the PDF
        this.yieldAmount = yieldAmount;
    }

    // Getter for buy price (if crops can be bought directly)
    public int getBuyPrice() {
        return buyPrice;
    }

    @Override
    public int getSellPrice() {
        return sellPrice;
    }

    @Override
    public void use() {
        // Eating the crop
        System.out.println("Ate " + getName() + ". +" + this.energyRestoration + " Energy.");
        // Actual energy addition is handled by EatingAction using getEnergyRestoration()
    }

    @Override
    public int getEnergyRestoration() {
        return this.energyRestoration;
    }

    /**
     * This represents the number of this specific crop item obtained when a single
     * corresponding plant is harvested.
     * For example, if harvesting one "Blueberry plant" yields 3 "Blueberry" crop items.
     */
    public int getYieldAmount() {
        return yieldAmount;
    }

    // It's good practice to override equals and hashCode for Item subclasses
    // if they are stored in HashMaps or HashSets directly.
    // For simplicity, we'll rely on the Item's equals/hashCode if it's based on name.
}