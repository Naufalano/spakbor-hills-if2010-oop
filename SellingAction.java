public class SellingAction extends Action {
    private Item itemToSell;
    private int amtToSell;

    public SellingAction(Item itemToSell, int amtToSell) {
        this.itemToSell = itemToSell;
        this.amtToSell = amtToSell;
    }

    @Override
    public boolean validate(Player player, Farm farm) {
        if (itemToSell == null) {
            System.out.println("Tidak ada item yang dapat dijual.");
            return false;
        }

        if (amtToSell < 0) {
            System.out.println("Item yang dijual harus lebih dari 0.");
            return false;
        }

        if (!player.getInventory().hasItem(itemToSell) || player.getInventory().getItemQuantity(itemToSell) <= 0) {
            System.out.println(itemToSell.getName() + " tidak ditemukan dalam inventory.");
            return false;
        }

        int availableQuantity = player.getInventory().getItemQuantity(itemToSell);
        if (availableQuantity < amtToSell) {
            System.out.println("Anda hanya memiliki " + availableQuantity + " " + itemToSell.getName() + ". Tidak cukup untuk menjual " + amtToSell + ".");
            return false;
        }

        if (!player.getCurrentLocationName().equals("Farm")) {
            System.out.println("Hanya bisa digunakan di farm.");
            return false;
        }
        GameMap currentMap = farm.getCurrentMap();
        if (!(currentMap instanceof FarmMap)) {
            System.out.println("Tidak sedang di farm.");
            return false;
        }

        FarmMap.PlacedObject shippingBinStructure = ((FarmMap)currentMap).getShippingBinLocation();
        if (shippingBinStructure == null) {
            System.out.println("Shipping Bin ga ada di map.");
            return false;
        }

        if (!InteractionHelper.isPlayerAdjacentToStructure(player, currentMap, shippingBinStructure)) {
            System.out.println("Harus di sebelah Shipping Bin.");
            return false;
        }
        return true;
    }

    @Override
    public void execute(Player player, Farm farm) {
        int soldCount = 0;
        ShippingBin farmShippingBin = farm.getShippingBin();

        Item itemInstanceInInventory = player.getInventory().getItemByName(itemToSell.getName());
        if (itemInstanceInInventory == null || player.getInventory().getItemQuantity(itemInstanceInInventory) < amtToSell) {
            System.err.println("Item '" + itemToSell.getName() + "' tidak ditemukan dalam jumlah yang cukup di inventaris.");
            return;
        }

        for (int i = 0; i < amtToSell; i++) {
            if (farmShippingBin.addItem(itemInstanceInInventory)) { 
                player.getInventory().useItem(itemInstanceInInventory, 1);
                soldCount++;
            } else {
                System.out.println("Shipping Bin penuh. Hanya " + soldCount + " " + itemToSell.getName() + " yang berhasil dimasukkan.");
                break; 
            }
        }

        if (soldCount > 0) {
            System.out.println(soldCount + " " + itemToSell.getName() + " telah dimasukkan ke Shipping Bin.");
        } else if (amtToSell > 0) {
             System.out.println("Shipping Bin sudah penuh.");
        }

        // if (player.getInventory().hasItem(itemToSell) && player.getInventory().getItemQuantity(itemToSell) > 0) {
        //     itemInstanceInInventory = null;
        //     for(Item it : player.getInventory().getInventoryMap().keySet()){
        //          if(it.equals(itemToSell)){ 
        //             itemInstanceInInventory = it;
        //             break;
        //         }
        //     }
            
        //     if(itemInstanceInInventory == null) {
        //          for(Item it : player.getInventory().getInventoryMap().keySet()){
        //             if(it.getName().equals(itemToSell.getName())){
        //                 itemInstanceInInventory = it;
        //                 break;
        //             }
        //         }
        //     }


        //     if(itemInstanceInInventory == null) {
        //         System.err.println("Error: Item " + itemToSell.getName() + " validated but couldn't be re-fetched from inventory for selling.");
        //         return;
        //     }

        //     if (farm.getShippingBin().addItem(itemInstanceInInventory)) {
        //         player.getInventory().useItem(itemInstanceInInventory, amtToSell);
        //         System.out.println(itemInstanceInInventory.getName() + " placed in the Shipping Bin.");
        //     } else {
        //         System.out.println("Shipping Bin is full. Failed to add " + itemInstanceInInventory.getName() + ".");
        //     }
        // } else {
        //     System.out.println("Item " + itemToSell.getName() + " not found in inventory or quantity zero (should have been caught by validate).");
        // }

        farm.advanceGameTime(15);
    }
}
