package entity;

import java.util.Random;

import main.GamePanel;

public class NPC_Dasco extends Entity {
    public NPC_Dasco(GamePanel gp){
        super(gp);

        direction = "down";
        speed = 1;

        getPlayerImage();
    }

    public void getPlayerImage(){
        up1 = setup("/npc/dasco_up_1");
        up2 = setup("/npc/dasco_up_2");
        down1 = setup("/npc/dasco_down_1");
        down2 = setup("/npc/dasco_down_2");
        left1 = setup("/npc/dasco_left_1");
        left2 = setup("/npc/dasco_left_2");
        right1 = setup("/npc/dasco_right_1");
        right2 = setup("/npc/dasco_right_2");
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
