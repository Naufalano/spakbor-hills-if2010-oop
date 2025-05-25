import java.util.Map;
// Assuming Player, Farm, GameMap, FarmMap, InteractionHelper, Recipe, Item, Seeds, Food are accessible

public class CookingAction extends Action {
    private Recipe recipeToCook;
    private static final String FIREWOOD_NAME = "Firewood";
    private static final String COAL_NAME = "Coal";

    public CookingAction(Recipe recipe) {
        this.recipeToCook = recipe;
    }

    @Override
    public boolean validate(Player player, Farm farm) {
        if (recipeToCook == null) {
            System.out.println("No recipe selected for cooking.");
            return false;
        }

        // 1. Must be on the Farm
        GameMap map = farm.getCurrentMap();
        String adjacentObjectId = InteractionHelper.getAdjacentInteractableObject(player, map);
        if (!player.getCurrentLocationName().equals("Player's House") && !PlayerHouseMap.STOVE_ID.equals(adjacentObjectId)) {
            System.out.println("Validation Failed: Cooking can only be done on your stove.");
            return false;
        }

        // 2. Must be inside the house
        FarmMap farmMap = farm.getFarmMap();
        if (farmMap == null) {
            System.out.println("Validation Failed: Farm map data not available.");
            return false;
        }
        FarmMap.PlacedObject houseStructure = farmMap.getHouseStructureLocation();
        if (houseStructure == null) {
            System.out.println("Validation Failed: House location not found on farm map.");
            return false;
        }

        // 3. House must have a stove (logical check)
        if (farm.getHouse() == null) {
            System.out.println("Validation Failed: Your house can't cook!");
            return false;
        }

        // 4. Check for ingredients (assuming Inventory has getInventoryMap and Item has getName)
        for (Map.Entry<String, Integer> entry : recipeToCook.getIngredients().entrySet()) {
            boolean foundSufficient = false;
            for(Item itemInInv : player.getInventory().getInventoryMap().keySet()){
                if(itemInInv.getName().equalsIgnoreCase(entry.getKey()) && 
                   player.getInventory().getItemQuantity(itemInInv) >= entry.getValue()){
                    foundSufficient = true;
                    break;
                }
            }
            if(!foundSufficient){
                 System.out.println("Missing ingredient: " + entry.getValue() + "x " + entry.getKey());
                 return false;
            }
        }

        // 5. Check for fuel
        boolean hasFirewood = false;
        boolean hasCoal = false;
        for(Item itemInInv : player.getInventory().getInventoryMap().keySet()){
            if(FIREWOOD_NAME.equalsIgnoreCase(itemInInv.getName()) && player.getInventory().getItemQuantity(itemInInv) > 0) hasFirewood = true;
            if(COAL_NAME.equalsIgnoreCase(itemInInv.getName()) && player.getInventory().getItemQuantity(itemInInv) > 0) hasCoal = true;
        }
        if (!hasFirewood && !hasCoal) {
            System.out.println("No fuel (Firewood or Coal) available for cooking.");
            return false;
        }

        // 6. Check energy for initiation
        if (player.getEnergy() < 10) { // As per actions_criteria.pdf
            System.out.println("Not enough energy to start cooking (-10 energy).");
            return false;
        }
        return true;
    }

    @Override
    public void execute(Player player, Farm farm) {
        player.setEnergy(player.getEnergy() - 10); // Initiation energy cost

        // Consume ingredients
        for (Map.Entry<String, Integer> entry : recipeToCook.getIngredients().entrySet()) {
            Item ingredientItem = null;
            for(Item item : player.getInventory().getInventoryMap().keySet()){
                 if(item.getName().equalsIgnoreCase(entry.getKey())){
                    ingredientItem = item;
                    break;
                }
            }
            if (ingredientItem != null) {
                player.getInventory().useItem(ingredientItem, entry.getValue());
            } else {
                 System.err.println("Error: Ingredient " + entry.getKey() + " validated but not found during execution. Cooking might be incorrect.");
                 // Potentially abort cooking here
            }
        }

        // Consume fuel
        boolean fuelConsumed = false;
        Item coalItemInstance = null;
        Item firewoodItemInstance = null;

        for(Item item : player.getInventory().getInventoryMap().keySet()){
            if(COAL_NAME.equalsIgnoreCase(item.getName())) coalItemInstance = item;
            if(FIREWOOD_NAME.equalsIgnoreCase(item.getName())) firewoodItemInstance = item;
        }

        if (coalItemInstance != null && player.getInventory().getItemQuantity(coalItemInstance) > 0) {
            player.getInventory().useItem(coalItemInstance, 1);
            System.out.println("Used 1 Coal.");
            fuelConsumed = true;
        } else if (firewoodItemInstance != null && player.getInventory().getItemQuantity(firewoodItemInstance) > 0) {
            player.getInventory().useItem(firewoodItemInstance, 1);
            System.out.println("Used 1 Firewood.");
            fuelConsumed = true;
        }

        if (!fuelConsumed) {
             System.out.println("Error: Fuel was validated but not found/used during execution. Cooking failed.");
             player.setEnergy(player.getEnergy() + 10); // Refund initiation energy
             // Ideally, ingredients would be refunded too, but that's more complex.
             return;
        }

        farm.advanceGameTime(recipeToCook.getTimeToCookMinutes()); // e.g., 60 minutes

        Food cookedFood = recipeToCook.getResultItem();
        player.getInventory().addItem(cookedFood, 1);

        System.out.println(player.getName() + " cooked " + cookedFood.getName() + "!");
        System.out.println("It took " + recipeToCook.getTimeToCookMinutes() + " minutes. Energy: " + player.getEnergy());
    }
}
