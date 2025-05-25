public class Crop extends Item implements EdibleItem {
    private int buyPrice; 
    private int sellPrice;  
    private int energyRestoration;
    private int yieldAmount;

    public Crop(String name, int buyPrice, int sellPrice, int energyRestoration, int yieldAmount) {
        super(name);
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.energyRestoration = energyRestoration;
        this.yieldAmount = yieldAmount;
    }

    public int getBuyPrice() {
        return buyPrice;
    }

    @Override
    public int getSellPrice() {
        return sellPrice;
    }

    @Override
    public void use() {
        System.out.println("Ate " + getName() + ". +" + this.energyRestoration + " Energy.");
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
}