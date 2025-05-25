public class EatingAction extends Action {
    private EdibleItem foodToEat;
    private Item itemInstance; // To remove from inventory

    public EatingAction(Item item) {
        if (!(item instanceof EdibleItem)) {
            throw new IllegalArgumentException("Item is not edible.");
        }
        this.itemInstance = item;
        this.foodToEat = (EdibleItem) item;
    }

    @Override
    public boolean validate(Player player, Farm farm) {
        if (!player.getInventory().hasItem(itemInstance) || player.getInventory().getItemQuantity(itemInstance) <= 0) {
            System.out.println("Validation Failed: " + itemInstance.getName() + " not found in inventory or quantity is zero.");
            return false;
        }
        if (player.getEnergy() == Player.MAX_ENERGY) {
            System.out.println("Validation Failed: Player energy is already full.");
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

        System.out.println(player.getName() + " ate " + itemInstance.getName() + " and restored " + energyRestored + " energy.");
        System.out.println("Current energy: " + player.getEnergy() + "/" + Player.MAX_ENERGY);
    }
}