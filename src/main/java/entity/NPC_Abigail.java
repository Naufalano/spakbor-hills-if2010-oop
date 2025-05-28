package entity;

import java.util.Random;

import main.GamePanel;

public class NPC_Abigail extends Entity {
    public NPC_Abigail(GamePanel gp){
        super(gp);

        direction = "down";
        speed = 1;

        getPlayerImage();
        setDialogue();
    }

    public void getPlayerImage(){
        up1 = setup("/npc/abigail_up_1");
        up2 = setup("/npc/abigail_up_2");
        down1 = setup("/npc/abigail_down_1");
        down2 = setup("/npc/abigail_down_2");
        left1 = setup("/npc/abigail_left_1");
        left2 = setup("/npc/abigail_left_2");
        right1 = setup("/npc/abigail_right_1");
        right2 = setup("/npc/abigail_right_2");
    }

    public void setAction(){
        actionLockCounter++;

        if(actionLockCounter == 120){
            Random random = new Random();
        int i = random.nextInt(100) + 1;

        if(i <= 25) {
            direction = "up";
        }

        if(i > 25 && i <= 50) {
            direction = "down";
        }

        if(i > 50 && i <= 75) {
            direction = "left";
        }

        if(i > 75 && i <= 100) {
            direction = "right";
        }
        actionLockCounter = 0;
        }
    }
    public void speak(){

        if(dialogue[dialogueIndex] == null){
            dialogueIndex = 0;
        }
        gp.ui.currentDialogue = dialogue[dialogueIndex];
        dialogueIndex++;

        switch(gp.player.direction){
            case "up":
                direction = "down";
                break;
            case "down":
                direction = "up";
                break;
            case "left":
                direction = "right";
                break;
            case "right":
                direction = "left";
                break;
        }
    }
}
