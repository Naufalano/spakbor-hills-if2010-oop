public class EatingAction extends Action {
    private EdibleItem foodToEat;
    private Item itemInstance; // To remove from inventory

    public EatingAction(Item item) {
        if (!(item instanceof EdibleItem)) {
            throw new IllegalArgumentException("Ga bisa didahar.");
        }
        this.itemInstance = item;
        this.foodToEat = (EdibleItem) item;
    }

    @Override
    public boolean validate(Player player, Farm farm) {
        if (!player.getInventory().hasItem(itemInstance) || player.getInventory().getItemQuantity(itemInstance) <= 0) {
            System.out.println(itemInstance.getName() + " tidak ada di inventory.");
            return false;
        }
        if (player.getEnergy() == Player.MAX_ENERGY) {
            System.out.println("Dah wareg cik.");
            return false;
        }
        return true;
    }

    @Override
    public void execute(Player player, Farm farm) {
        int energyRestored = foodToEat.getEnergyRestoration();
        player.setEnergy(player.getEnergy() + energyRestored);
        player.getInventory().useItem(itemInstance, 1); // Consume the item
        farm.advanceGameTime(5); // [cite: 1]

        System.out.println(player.getName() + " dahar " + itemInstance.getName() + " dan dapet " + energyRestored + " energi.");
        System.out.println("Energi: " + player.getEnergy() + "/" + Player.MAX_ENERGY);
    }
}