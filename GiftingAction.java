public class GiftingAction extends Action {
    private NPC targetNpc;
    private Item giftItem;

    public GiftingAction(NPC targetNpc, Item giftItem) {
        this.targetNpc = targetNpc;
        this.giftItem = giftItem;
    }

    @Override
    public boolean validate(Player player, Farm farm) {
        if (targetNpc == null || giftItem == null) {
            System.out.println("Validation Failed: NPC or gift item not specified.");
            return false;
        }
        if (!player.getInventory().hasItem(giftItem) || player.getInventory().getItemQuantity(giftItem) <= 0) {
            System.out.println("Validation Failed: " + giftItem.getName() + " not found in inventory or quantity is zero.");
            return false;
        }
        if (player.getEnergy() < 5) { // Energy cost [cite: 4]
            System.out.println("Validation Failed: Not enough energy to give a gift.");
            return false;
        }
        // Check if player is near NPC (requires NPC location tracking and player location)
        // For now, assume player can gift if NPC is specified.
        return true;
    }

    @Override
    public void execute(Player player, Farm farm) {
        player.setEnergy(player.getEnergy() - 5); // [cite: 4]
        farm.advanceGameTime(10); // [cite: 3]

        // Store NPC's affection before gifting to show change
        int oldAffection = targetNpc.getAffection();

        // Use the Player's existing giveItemToNPC logic which handles inventory and NPC giftCheck
        // However, giveItemToNPC itself prints messages and uses inventory.
        // We need to adapt. The Player.giveItemToNPC method handles the core logic.
        // Let's call the NPC's giftCheck directly and manage inventory here.
        
        targetNpc.giftCheck(giftItem); // NPC processes the gift [cite: 4]
        player.getInventory().useItem(giftItem, 1); // Item is removed from inventory [cite: 3, 4]

        System.out.println(player.getName() + " gave " + giftItem.getName() + " to " + targetNpc.getName() + ".");
        System.out.println(targetNpc.getName() + "'s affection changed from " + oldAffection + " to " + targetNpc.getAffection() + ".");
        System.out.println("Player energy: " + player.getEnergy());
    }
}