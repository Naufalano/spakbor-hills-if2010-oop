public class HarvestingAction extends Action {

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

        if (currentTile.getState() != TileState.HARVESTABLE || !(currentTile.getObjectOnTile() instanceof PlantedCrop)) {
            System.out.println("Nothing to harvest on this tile, or plant not ready.");
            return false;
        }

        PlantedCrop plant = (PlantedCrop) currentTile.getObjectOnTile();
        if (!plant.isMature()) {
            System.out.println("This plant is not yet mature for harvesting.");
            return false;
        }

        if (player.getEnergy() < 5) { 
            System.out.println("Not enough energy to harvest.");
            return false;
        }
        return true;
    }

    @Override
    public void execute(Player player, Farm farm) {
        Tile currentTile = farm.getCurrentMap().getTileAtPosition(player.getX(), player.getY()); 
        PlantedCrop plant = (PlantedCrop) currentTile.getObjectOnTile();

        player.setEnergy(player.getEnergy() - 5);
        farm.advanceGameTime(5);

        String cropName = plant.getResultingCropName();
        Crop cropToHarvest = CropDataRegistry.getCropByName(cropName); 

        if (cropToHarvest == null) {
            System.err.println("Error: Could not find crop '" + cropName + "' in registry during harvest. Aborting harvest.");
            player.setEnergy(player.getEnergy() + 5);
            return;
        }

        int amountHarvested = plant.getYieldAmountPerHarvest();
        player.getInventory().addItem(cropToHarvest, amountHarvested);
        farm.addCropped(amountHarvested);

        System.out.println(player.getName() + " harvested " + amountHarvested + " " + cropToHarvest.getName() + ".");
        System.out.println("Energy: " + player.getEnergy());

        currentTile.setObjectOnTile(null);
        currentTile.setState(TileState.TILLED);
        farm.getCurrentMap().display(player);
    }
}