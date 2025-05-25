public class WateringAction extends Action {
    private Item wateringCan;

    public WateringAction() {
        this.wateringCan = new Equipment("Watering Can"); // For hasItem check
    }

    @Override
    public boolean validate(Player player, Farm farm) {
        if (!player.getInventory().hasItem(this.wateringCan)) {
            System.out.println("Validation Failed: Watering Can not found in inventory.");
            return false;
        }
        if (player.getEnergy() < 5) { // Energy cost per tile/seed [cite: 1]
            System.out.println("Validation Failed: Not enough energy to water.");
            return false;
        }

        GameMap currentPlayersMap = farm.getCurrentMap();
        FarmMap playersActualFarmMap = farm.getFarmMap();
        if (!(currentPlayersMap instanceof FarmMap) || !currentPlayersMap.getMapName().equals(playersActualFarmMap.getMapName())) {
             System.out.println("Validation Failed: Watering can only be done on the farm.");
             return false;
        }

        FarmMap farmMap = farm.getFarmMap();
        Tile currentTile = farmMap.getTileAtPosition(player.getX(), player.getY());

        if (currentTile == null) {
            System.out.println("Validation Failed: Player is not on a valid tile.");
            return false;
        }
        if (currentTile.getState() != TileState.PLANTED) {
            // Or if the tile needs water (e.g., has a 'isWateredToday' flag)
            System.out.println("Validation Failed: Tile is not planted or does not need watering.");
            return false;
        }
        // You might want to add a flag to Tile or PlantedCrop: `boolean isWateredToday;`
        // if (!((PlantedCrop)currentTile.getObjectOnTile()).isWateredToday()) { ... }
        return true;
    }

    @Override
    public void execute(Player player, Farm farm) {
        player.setEnergy(player.getEnergy() - 5); // [cite: 1]
        farm.advanceGameTime(5); // 5 minutes per tile/seed [cite: 1]

        FarmMap farmMap = farm.getFarmMap();
        Tile currentTile = farm.getFarmMap().getTileAtPosition(player.getX(), player.getY());
        if (currentTile.getObjectOnTile() instanceof PlantedCrop) {
            PlantedCrop plant = (PlantedCrop) currentTile.getObjectOnTile();
            plant.setWateredToday(true); // Mark as watered for today's growth cycle (which happens on nextDay)
            System.out.println(player.getName() + " watered the plant at (" + player.getX() + "," + player.getY() + ").");
        } else {
            System.out.println("Nothing to water here that requires it in this way.");
        }
        System.out.println(player.getName() + " watered the plant at (" + player.getX() + "," + player.getY() + "). Energy: " + player.getEnergy());
        farmMap.display(player);
    }
}