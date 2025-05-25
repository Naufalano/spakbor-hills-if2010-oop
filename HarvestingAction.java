public class HarvestingAction extends Action {
    public HarvestingAction() {
        // Constructor
    }

    @Override
    public boolean validate(Player player, Farm farm) {
        GameMap currentPlayersMap = farm.getCurrentMap();
        FarmMap playersActualFarmMap = farm.getFarmMap();
        if (!(currentPlayersMap instanceof FarmMap) || !currentPlayersMap.getMapName().equals(playersActualFarmMap.getMapName())) {
            System.out.println("Harvesting can only be done on your farm.");
            return false;
        }

        Tile currentTile = farm.getFarmMap().getTileAtPosition(player.getX(), player.getY());
        if (currentTile == null) {
            System.out.println("Invalid tile position.");
            return false;
        }

        // Check if the tile is in a harvestable state and has a PlantedCrop object
        if (currentTile.getState() != TileState.HARVESTABLE || !(currentTile.getObjectOnTile() instanceof PlantedCrop)) {
            System.out.println("Nothing to harvest on this tile, or plant not ready.");
            return false;
        }

        PlantedCrop plant = (PlantedCrop) currentTile.getObjectOnTile();
        if (!plant.isMature()) { // Double check maturity, though HARVESTABLE state should imply this
            System.out.println("This plant is not yet mature for harvesting.");
            return false;
        }

        if (player.getEnergy() < 5) { // Energy cost per harvest action [cite: 1]
            System.out.println("Not enough energy to harvest.");
            return false;
        }
        return true;
    }

    @Override
    public void execute(Player player, Farm farm) {
        Tile currentTile = farm.getCurrentMap().getTileAtPosition(player.getX(), player.getY()); // Use getCurrentMap()
        PlantedCrop plant = (PlantedCrop) currentTile.getObjectOnTile();

        player.setEnergy(player.getEnergy() - 5); // Energy cost
        farm.advanceGameTime(5); // Time cost

        String cropName = plant.getResultingCropName();
        Crop cropToHarvest = CropDataRegistry.getCropByName(cropName); // Get the prototype from registry

        if (cropToHarvest == null) {
            System.err.println("Error: Could not find crop '" + cropName + "' in registry during harvest. Aborting harvest.");
            // Potentially refund energy/time if appropriate
            player.setEnergy(player.getEnergy() + 5);
            // farm.advanceGameTime(-5); // Reversing time is tricky, maybe just don't advance it if validate fails early
            return;
        }

        int amountHarvested = plant.getYieldAmountPerHarvest();
        player.getInventory().addItem(cropToHarvest, amountHarvested);
        farm.addCropped(amountHarvested);

        System.out.println(player.getName() + " harvested " + amountHarvested + " " + cropToHarvest.getName() + ".");
        System.out.println("Energy: " + player.getEnergy());

        currentTile.setObjectOnTile(null);
        currentTile.setState(TileState.TILLED); // Or DEFAULT, depending on desired mechanics
        farm.getCurrentMap().display(player);
    }
}