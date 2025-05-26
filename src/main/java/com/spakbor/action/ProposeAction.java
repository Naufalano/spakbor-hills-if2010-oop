package action;
import cls.core.*;
import cls.items.*;

public class ProposeAction extends Action {
    private NPC targetNpc;
    private static final String PROPOSAL_RING_NAME = "Proposal Ring";

    private static final int TIME_COST_MINUTES = 60; 

    public ProposeAction(NPC targetNpc) {
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
            System.out.println("Perlu '" + PROPOSAL_RING_NAME + "' untuk melamar.");
            return false;
        }
        
        if (!targetNpc.proposeCheck()) { 
            System.out.println(targetNpc.getName() + " belum siap untuk dilamar (cinta belum tulus atau status tidak memungkinkan).");
            return false;
        }
        return true;
    }

    @Override
    public void execute(Player player, Farm farm) {
        farm.advanceGameTime(TIME_COST_MINUTES);
        targetNpc.propose();

        if (targetNpc.getStatus().equals("Fiance")) {
            player.setEnergy(player.getEnergy() - 10);
            System.out.println(targetNpc.getName() + " menerima lamaran! Kalian sekarang bertunangan.");
            player.updateNpcRelationshipStatus(targetNpc.getName(), targetNpc.getStatus());
        } else {
            player.setEnergy(player.getEnergy() - 20);
            System.out.println(targetNpc.getName() + " menolak lamaran Anda saat ini chuaks.");
        }
        System.out.println("Energi tersisa: " + player.getEnergy());
    }
}