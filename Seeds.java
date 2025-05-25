public class Seeds extends Item {
    private String season;
    private int daysToHarvest;
    private int buyPrice;

    public Seeds(String name, String season, int daysToHarvest, int buyPrice) {
        super(name);
        this.season = season;
        this.daysToHarvest = daysToHarvest;
        this.buyPrice = buyPrice;
    }

    @Override
    public int getSellPrice() {
        return buyPrice / 2;
    }

    @Override
    public void use() {
        System.out.println("Menanam " + name + " di musim " + season + ".");
    }

    public String getSeason() {
        return season;
    }

    public int getDaysToHarvest() {
        return daysToHarvest;
    }

    public int getBuyPrice() {
        return buyPrice;
    }
}
