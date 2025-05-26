package item;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.GamePanel;

public class SuperItem {
    public BufferedImage image;
    public String name;
    public boolean collision = false;
    public int WorldX, WorldY;

    public void draw (Graphics2D g2, GamePanel gp){
         int screenX = WorldX - gp.player.WorldX + gp.player.screenX;
            int screenY = WorldY - gp.player.WorldY + gp.player.screenY;

            if (WorldX + gp.tileSize > gp.player.WorldX - gp.player.screenX && 
                WorldX - gp.tileSize < gp.player.WorldX + gp.player.screenX &&
                WorldY + gp.tileSize > gp.player.WorldY - gp.player.screenY &&
                WorldY - gp.tileSize < gp.player.WorldY + gp.player.screenY) {
                    g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
                }            
    }
}
