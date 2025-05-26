public class MarryAction extends Action {
    private NPC targetNpc;
    private static final String PROPOSAL_RING_NAME = "Proposal Ring";

    private static final int ENERGY_COST = 80;

    public MarryAction(NPC targetNpc) {
        this.targetNpc = targetNpc;
    }

    @Override
    public boolean validate(Player player, Farm farm) {
        if (targetNpc == null) {
            System.out.println("NPC target tidak valid.");
            return false;
        }
        Item proposalRing = player.getInventory().getItemByName(PROPOSAL_RING_NAME);
        if (proposalRing == null) {
            System.out.println("Perlu '" + PROPOSAL_RING_NAME + "' untuk menikah.");
            return false;
        }
        if (player.getEnergy() < ENERGY_COST) {
            System.out.println("Energi tidak cukup untuk upacara pernikahan (-" + ENERGY_COST + ").");
            return false;
        }

        if (!targetNpc.marriageCheck()) {
            System.out.println("Belum bisa menikah dengan " + targetNpc.getName() + " (status bukan tunangan atau belum cukup waktu).");
            return false;
        }
        return true;
    }

    @Override
    public void execute(Player player, Farm farm) {
        player.setEnergy(player.getEnergy() - ENERGY_COST);
        
        targetNpc.marry();

        if (targetNpc.getStatus().equals("Spouse")) {
            player.addPartner(targetNpc);
            player.updateNpcRelationshipStatus(targetNpc.getName(), targetNpc.getStatus());

            System.out.println("Selamat! Udah sah dengan " + targetNpc.getName() + ".");
            System.out.println("Hari pernikahan dihabiskan bersama....");
            
            Time gameTime = farm.getTimeController().getGameTime();
            int currentHour = gameTime.getHour();
            if (currentHour < 22) {
                farm.advanceGameTime((22 - currentHour) * 60 - gameTime.getMinute());
            }
            if (!player.getCurrentLocationName().equals("Player's House")) {
                 farm.loadMap("Player's House", player.getCurrentLocationName());
            }

        } else {
            System.out.println("Upacara pernikahan tidak dapat dilanjutkan saat ini.");
            player.setEnergy(player.getEnergy() + ENERGY_COST);
        }
        System.out.println("Energi tersisa: " + player.getEnergy());
    }
}