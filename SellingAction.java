// Assuming Player, Farm, GameMap, FarmMap, InteractionHelper, Item are accessible

public class SellingAction extends Action {
    private Item itemToSell;

    public SellingAction(Item itemToSell) {
        this.itemToSell = itemToSell;
    }

    @Override
    public boolean validate(Player player, Farm farm) {
        if (itemToSell == null) {
            System.out.println("No item selected to sell.");
            return false;
        }
        if (!player.getInventory().hasItem(itemToSell) || player.getInventory().getItemQuantity(itemToSell) <= 0) {
            System.out.println(itemToSell.getName() + " not found in inventory or quantity is zero.");
            return false;
        }

        // Must be on the Farm to use the Farm's shipping bin
        if (!player.getCurrentLocationName().equals("Farm")) {
            System.out.println("Validation Failed: You can only use the shipping bin on your farm.");
            return false;
        }
        GameMap currentMap = farm.getCurrentMap(); // Should be FarmMap instance here
        if (!(currentMap instanceof FarmMap)) { // Should not happen if location check passes
            System.out.println("Validation Error: Not on the farm map.");
            return false;
        }

        FarmMap.PlacedObject shippingBinStructure = ((FarmMap)currentMap).getShippingBinLocation();
        if (shippingBinStructure == null) {
            System.out.println("Validation Error: Shipping Bin location not found on map.");
            return false;
        }

        if (!InteractionHelper.isPlayerAdjacentToStructure(player, currentMap, shippingBinStructure)) {
            System.out.println("You need to be next to the Shipping Bin to sell items.");
            return false;
        }
        return true;
    }

    @Override
    public void execute(Player player, Farm farm) {
        // Selling logic remains the same (adds item to farm.getShippingBin() and removes from player inventory)
        // Time cost is applied after interaction.

        if (player.getInventory().hasItem(itemToSell) && player.getInventory().getItemQuantity(itemToSell) > 0) {
            Item itemInstanceInInventory = null; // Find the actual instance to remove
            for(Item it : player.getInventory().getInventoryMap().keySet()){
                 if(it.equals(itemToSell)){ // Use .equals() for items
                    itemInstanceInInventory = it;
                    break;
                }
            }
            
            if(itemInstanceInInventory == null) { // Fallback if .equals didn't match but name does (less robust)
                 for(Item it : player.getInventory().getInventoryMap().keySet()){
                    if(it.getName().equals(itemToSell.getName())){
                        itemInstanceInInventory = it;
                        break;
                    }
                }
            }


            if(itemInstanceInInventory == null) {
                System.err.println("Error: Item " + itemToSell.getName() + " validated but couldn't be re-fetched from inventory for selling.");
                return;
            }

            // The farm's logical shipping bin
            if (farm.getShippingBin().addItem(itemInstanceInInventory)) {
                player.getInventory().useItem(itemInstanceInInventory, 1);
                System.out.println(itemInstanceInInventory.getName() + " placed in the Shipping Bin.");
            } else {
                System.out.println("Shipping Bin is full. Failed to add " + itemInstanceInInventory.getName() + ".");
            }
        } else {
            System.out.println("Item " + itemToSell.getName() + " not found in inventory or quantity zero (should have been caught by validate).");
        }

        farm.advanceGameTime(15); // 15 minutes after selling interaction
        System.out.println("Selling action finished. Time advanced by 15 minutes.");
    }
}
