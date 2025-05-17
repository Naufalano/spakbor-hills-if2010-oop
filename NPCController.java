public class NPCController {
    private NPC npc;
    private NPCView command;

    public NPCController(NPC npc, NPCView command) {
        this.npc = npc;
        this.command = command;
    }

    public void marriage() {
        if(this.command.getAction().equals("Marry")){
            npc.marry();
        }
    }

    public void propose() {
        if(this.command.getAction().equals("Propose")){
            npc.propose();
        }
    }

    public void gift(String name) {
        if(this.command.getAction().equals("Gift")){
            npc.giftCheck(name);
        }
    }

    public void chat() throws InterruptedException{
        if(this.command.getAction().equals("Chat")){
            npc.chat();
        }
    }
}
