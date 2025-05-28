package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;

import main.GamePanel;
import main.KeyHandler;
import main.UtilityTool;

import item.OBJ_WateringCan;
import item.OBJ_TrainingRod;
import item.OBJ_Coal;
import item.OBJ_Hoe;
import item.OBJ_Pickaxe;
import item.OBJ_ParsnipSeeds;

import entity.NPC_Abigail;

public class Player extends Entity {

    KeyHandler keyH;

    public final int screenX;
    public final int screenY;
    // public int hasKey = 0;
    public ArrayList<Entity> inventory = new ArrayList<>();
    public final int maxInventorySize = 20;

    public Player(GamePanel gp, KeyHandler keyH){

        super(gp);
        this.keyH = keyH;

        screenX = gp.screenWidth/2 - (gp.tileSize/2);
        screenY = gp.screenHeight/2 - (gp.tileSize/2);

        solidArea = new Rectangle(8, 16, 32, 32);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        setDefaultValues();
        getPlayerImage();
        setItems();
    }
    public void setDefaultValues() {
        WorldX = gp.tileSize * 24;
        WorldY = gp.tileSize * 21;
        speed = 4;
        direction = "down";
    }

    public void setItems(){
        // Add items to the inventory here
        inventory.add(new OBJ_WateringCan(gp));
        inventory.add(new OBJ_TrainingRod(gp));
        inventory.add(new OBJ_Coal(gp));
        inventory.add(new OBJ_Pickaxe(gp));
        inventory.add(new OBJ_Hoe(gp));
        inventory.add(new OBJ_ParsnipSeeds(gp));
    }
    public void getPlayerImage(){
        up1 = setup("/player/boy_up_1");
        up2 = setup("/player/boy_up_2");
        down1 = setup("/player/boy_down_1");
        down2 = setup("/player/boy_down_2");
        left1 = setup("/player/boy_left_1");
        left2 = setup("/player/boy_left_2");
        right1 = setup("/player/boy_right_1");
        right2 = setup("/player/boy_right_2");
    }

    
    public void update(){

        if(keyH.upPressed == true || keyH.downPressed == true || keyH.leftPressed == true || keyH.rightPressed == true){
            if(keyH.upPressed == true){
            direction = "up";
        }
        else if(keyH.downPressed == true){
            direction = "down";
        }
        else if(keyH.leftPressed == true){
            direction = "left";
        }
        else if(keyH.rightPressed == true){
            direction = "right";
        }

        collisionOn  = false;
        gp.checker.checkTile(this);

        int Idx = gp.checker.checkObject(this, true);
        pickUpObject(Idx);

        int npcIdx = gp.checker.checkNPC(this, gp.NPC);
        interactNPC(npcIdx);

        if(!collisionOn){

        switch (direction) {
            case "up":
                WorldY -= speed;
                break;
            case "down":
                WorldY += speed;
                break;
            case "left":
                WorldX -= speed;
                break;
            case "right":
                WorldX += speed;
                break;
        }
    }

        spriteCounter++;
        if(spriteCounter > 12){
            if(spriteNum == 1){
                spriteNum = 2;
            }
            else if(spriteNum == 2){
                spriteNum = 1;
            }
            spriteCounter = 0;
        }
        }
    }
    public void pickUpObject(int i){
        if(i != 999){
            
        }
    }

    public void interactNPC(int i){
        if(i != 999){
            
            gp.gameState = gp.dialogueState; // Tambahkan pengecekan null untuk keamanan
            gp.NPC[i].speak(); // <--- Ganti jadi seperti ini
        }
    }


    public void draw(Graphics2D g2){
        // g2.setColor(Color.white);

        // g2.fillRect(x, y, gp.tileSize, gp.tileSize);

        BufferedImage image = null;

        switch(direction){
        case "up":
            if(spriteNum == 1){
                image = up1;
            }
            if(spriteNum == 2){
                image = up2;
            }
            break;
        case "down":
            if(spriteNum == 1){
                image = down1;
            }
            if(spriteNum == 2){
                image = down2;
            }
            break;
        case "left":
            if(spriteNum == 1){
                image = left1;
            }
            if(spriteNum == 2){
                image = left2;
            }
            break;
        case "right":
            if(spriteNum == 1){
                image = right1;
            }
            if(spriteNum == 2){
                image = right2;
            }
            break;
        }

        g2.drawImage(image, screenX, screenY, null);
    }
}
