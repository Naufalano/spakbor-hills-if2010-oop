package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.BasicStroke;

import item.*;

public class UI {
    GamePanel gp;
    Graphics2D g2;
    Font Arial;
    // public boolean messageOn = false;
    public String message = "";
    int messageCounter = 0;
    public String currentDialogue;

    public UI(GamePanel gp){
        this.gp = gp;
        Arial = new Font("Arial", Font.PLAIN, 30);
    }

    public void showMessage(String message){
        this.message = message;
    }

    public void draw(Graphics2D g2){
        this.g2 = g2;
        g2.setFont(Arial); 
        g2.setColor(Color.white);

        // PLAY STATE
        if(gp.gameState == gp.playState){
            // gp.gameState = gp.pauseState;
        } 
        if (gp.gameState == gp.pauseState){
            drawPauseScreen();
        }

        // DIALOGUE STATE
        if (gp.gameState == gp.dialogueState){
            drawDialogueScreen();
        }
    }

    public void drawPauseScreen(){
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,80F));
        String text = "PAUSED";
        int x = getXAtCenter(text);
        int y = gp.screenHeight/2;

        g2.drawString(text, x, y);
    }

    public void drawDialogueScreen(){
        
        // WINDOW
        int x = gp.tileSize*2;
        int y = gp.tileSize/2;
        int width = gp.screenWidth - (gp.tileSize*4);
        int height = gp.tileSize*4;

        drawSubWindow(x, y, width, height);

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN,32));
        x += gp.tileSize;
        y += gp.tileSize;

        g2.drawString(currentDialogue, x, y);
    }

    public void drawSubWindow(int x, int y, int width, int height){

        Color c = new Color(0,0,0, 210);
        g2.setColor(c);
        g2.fillRoundRect(x, y, width, height, 35, 35);

        c = new Color(255,255,255);
        g2.setColor(c);
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(x+5, y+5, width-10, height-10, 25, 25);
    }

    public int getXAtCenter(String text){
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int x = gp.screenWidth/2 - length/2;
        return x;
    }
}
