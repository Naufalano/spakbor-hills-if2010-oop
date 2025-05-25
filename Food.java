public class Food extends Item implements EdibleItem {
    private int energyRestoration;
    private int buyPrice;
    private int sellPrice;

    public Food(String name, int energyRestoration, int buyPrice, int sellPrice) {
        super(name);
        this.energyRestoration = energyRestoration;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    @Override
    public int getSellPrice() {
        return sellPrice;
    }

    public int getBuyPrice() { 
        return buyPrice;
    }

    @Override
    public void use() {
        System.out.println("Eating " + getName() + ", +" + this.energyRestoration + " Energy.");
    }

    @Override
    public int getEnergyRestoration() {
        return this.energyRestoration;
    }
}