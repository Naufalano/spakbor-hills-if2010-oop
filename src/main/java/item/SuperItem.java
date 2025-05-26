package item;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.GamePanel;
import main.UtilityTool;

public class SuperItem {
    public BufferedImage image;
    public String name;
    public boolean collision = false;
    public int WorldX, WorldY;
    public Rectangle solidArea = new Rectangle (0, 0, 48, 48);
    public int solidAreaDefaultX = 0;
    public int solidAreaDefaultY = 0;
    UtilityTool uTool = new UtilityTool();

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
