package entity;

import java.util.Random;

import main.GamePanel;

public class NPC_MayorTadi extends Entity {
    public NPC_MayorTadi(GamePanel gp){
        super(gp);

        direction = "down";
        speed = 1;

        getPlayerImage();
    }

    public void getPlayerImage(){
        up1 = setup("/npc/mayortadi_up1");
        up2 = setup("/npc/mayortadi_up2");
        down1 = setup("/npc/mayortadi_down1");
        down2 = setup("/npc/mayortadi_down2");
        left1 = setup("/npc/mayortadi_left1");
        left2 = setup("/npc/mayortadi_left2");
        right1 = setup("/npc/mayortadi_right1");
        right2 = setup("/npc/mayortadi_right2");
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
        

}
