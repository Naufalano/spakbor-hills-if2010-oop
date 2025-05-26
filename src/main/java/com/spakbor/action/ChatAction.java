package action;
import cls.core.*;

public class ChatAction extends Action {
    private NPC targetNpc;

    private static final int ENERGY_COST = 10;
    private static final int TIME_COST_MINUTES = 10;
    private static final int AFFECTION_GAIN = 10;

    public ChatAction(NPC targetNpc) {
        this.targetNpc = targetNpc;
    }

    @Override
    public boolean validate(Player player, Farm farm) {
        if (targetNpc == null) {
            System.out.println("NPC target tidak valid.");
            return false;
        }
        if (player.getEnergy() < ENERGY_COST) {
            System.out.println("Energi tidak cukup untuk chat (-" + ENERGY_COST + ").");
            return false;
        }
        
        return true;
    }

    @Override
    public void execute(Player player, Farm farm) {
        player.setEnergy(player.getEnergy() - ENERGY_COST);
        farm.advanceGameTime(TIME_COST_MINUTES);

        
        System.out.println("Ngobrol dengan " + targetNpc.getName() + ".");
        
        if (targetNpc.getStatus().equals("Spouse")) {
            System.out.println(targetNpc.getName() + ": \"Halo, Sayang! Cerita hari ini...\"");
        } else if (targetNpc.getStatus().equals("Fiance")) {
            System.out.println(targetNpc.getName() + ": \"Halo, " + player.getName() + "! Cerita hari ini...\"");
        } else {
            System.out.println(targetNpc.getName() + ": \"Halo, " + player.getName() + "! Senang bertemu denganmu.\"");
        }
        
        targetNpc.setAffection(targetNpc.getAffection() + AFFECTION_GAIN);
        player.recordChatWithNPC(targetNpc.getName());

        System.out.println("Rasa suka " + targetNpc.getName() + " bertambah menjadi " + targetNpc.getAffection() + ".");
        System.out.println("Energi tersisa: " + player.getEnergy());
    }
}