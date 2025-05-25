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
        if (player.getEnergy() < 5) {
            System.out.println("Validation Failed: Not enough energy to give a gift.");
            return false;
        }
        return true;
    }

    @Override
    public void execute(Player player, Farm farm) {
        player.setEnergy(player.getEnergy() - 5);
        farm.advanceGameTime(10);

        int oldAffection = targetNpc.getAffection();
        
        targetNpc.giftCheck(giftItem);
        player.getInventory().useItem(giftItem, 1); 

        System.out.println(player.getName() + " gave " + giftItem.getName() + " to " + targetNpc.getName() + ".");
        System.out.println(targetNpc.getName() + "'s affection changed from " + oldAffection + " to " + targetNpc.getAffection() + ".");
        System.out.println("Player energy: " + player.getEnergy());
    }
}