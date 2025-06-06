package com.spakbor.cls.core;
import com.spakbor.cls.items.*;
import java.io.Serializable;

public class NPCController implements Serializable{
    private static final long serialVersionUID = 1L;
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

    public void gift(Item item) {
        if(this.command.getAction().equals("Gift")){
            npc.giftCheck(item);
        }
    }

    public void gift(String item) {
        if(this.command.getAction().equals("Gift")){
            npc.giftCheck(item);
        }
    }

    public void chat() throws InterruptedException{
        if(this.command.getAction().equals("Chat")){
            npc.chat();
        }
    }
}
