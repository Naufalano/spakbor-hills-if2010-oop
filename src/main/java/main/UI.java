package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import item.*;

public class UI {
    GamePanel gp;
    Graphics2D g2;
    Font Arial;
    // public boolean messageOn = false;
    public String message = "";
    int messageCounter = 0;

    public UI(GamePanel gp){
        this.gp = gp;
        Arial = new Font("Arial", Font.PLAIN, 30);
        // ItemKey key = new ItemKey(gp);
        // keyImage = key.image;
    }

    public void showMessage(String message){
        this.message = message;
    }

    public void draw(Graphics2D g2){
        this.g2 = g2;
        g2.setFont(Arial); 
        g2.setColor(Color.white);

        if(gp.gameState == gp.playState){
            // gp.gameState = gp.pauseState;
        } 
        if (gp.gameState == gp.pauseState){
            drawPauseScreen();
        }
    }

    public void drawPauseScreen(){
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,80F));
        String text = "PAUSED";
        int x = getXAtCenter(text);
        int y = gp.screenHeight/2;

        g2.drawString(text, x, y);
    }

    public int getXAtCenter(String text){
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = gp.screenWidth/2 - length/2;
        return x;
    }
}
