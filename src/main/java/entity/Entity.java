package entity;

import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.UtilityTool;

public class Entity {
    GamePanel gp;
    
    public int WorldX, WorldY;
    public int speed;

    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    public String direction;

    public int spriteCounter = 0;
    public int spriteNum = 1;

    public int solidAreaDefaultX, solidAreaDefaultY;

    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);
    public boolean collisionOn = false;
    public int actionLockCounter = 0;
    
    String dialogue[] = new String[20];

    int dialogueIndex = 0;

    public Entity(GamePanel gp){
        this.gp = gp;
    }

    public void setDialogue(){
        
        dialogue[0] = "Hello, how are you?";
        dialogue[1] = "abcde";
        dialogue[2] = "hai hai halo";
    }

    public void setAction(){}

    public void speak(){}

    public void update(){
        setAction();

        collisionOn  = false;
        gp.checker.checkTile(this);
        gp.checker.checkObject(this, false);
        gp.checker.checkPlayer(this);

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
}
    
    public void draw(Graphics2D g2){
        BufferedImage image = null;
        int screenX = WorldX - gp.player.WorldX + gp.player.screenX;
        int screenY = WorldY - gp.player.WorldY + gp.player.screenY;

        if (WorldX + gp.tileSize > gp.player.WorldX - gp.player.screenX && 
            WorldX - gp.tileSize < gp.player.WorldX + gp.player.screenX &&
            WorldY + gp.tileSize > gp.player.WorldY - gp.player.screenY &&
            WorldY - gp.tileSize < gp.player.WorldY + gp.player.screenY) {
            
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
                g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        }
    }

    public BufferedImage setup(String imagePath) {
        UtilityTool uTool = new UtilityTool();
        BufferedImage image = null;

        try {

            image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
            image = uTool.scaleImage(image, gp.tileSize, gp.tileSize);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}
