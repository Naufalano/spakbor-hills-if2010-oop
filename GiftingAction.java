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
            System.out.println("NPC or gift item not specified.");
            return false;
        }
        if (!player.getInventory().hasItem(giftItem) || player.getInventory().getItemQuantity(giftItem) <= 0) {
            System.out.println(giftItem.getName() + " tidak ada di inventory.");
            return false;
        }
        if (player.getEnergy() < 5) {
            System.out.println("Ga ada tenaga buat kasih hadiah.");
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

        System.out.println(player.getName() + " memberi " + giftItem.getName() + " ke " + targetNpc.getName() + ".");
        System.out.println(targetNpc.getName() + " jadi nambah suka dari " + oldAffection + " ke " + targetNpc.getAffection() + ".");
        System.out.println("Energi: " + player.getEnergy());
    }
}