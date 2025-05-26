package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import item.*;

public class UI {
    GamePanel gp;
    Font Arial;
    BufferedImage keyImage;
    public boolean messageOn = false;
    public String message = "";
    int messageCounter = 0;

    public UI(GamePanel gp){
        this.gp = gp;
        Arial = new Font("Arial", Font.PLAIN, 30);
        ItemKey key = new ItemKey(gp);
        keyImage = key.image;
    }

    public void showMessage(String message){
        this.message = message;
        this.messageOn = true;
    }

    public void draw(Graphics2D g2){
        g2.setFont(Arial);
        g2.setColor(Color.white);
        g2.drawImage(keyImage, gp.tileSize/2, gp.tileSize/2, gp.tileSize/2, gp.tileSize/2, null);
        g2.drawString(("x "+ gp.player.hasKey), 55, 47);
        if(messageOn == true){
            g2.setFont(g2.getFont().deriveFont(26F));
            g2.drawString(message, gp.tileSize*6 - 10, gp.tileSize*11);
            messageCounter++;
            if(messageCounter == 90){
                messageCounter = 0;
                messageOn = false;
            }
        }
    }
}
