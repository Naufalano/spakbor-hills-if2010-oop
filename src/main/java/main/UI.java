package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

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
    public int commandNum = 0;
    public int titleScreenState = 0; // 0: the first screen 1: the second screen
    public int slotCol = 0;
    public int slotRow = 0;

    BufferedImage titleScreenImage;

    public UI(GamePanel gp){
        this.gp = gp;
        Arial = new Font("Arial", Font.PLAIN, 30);

        // Load the title screen image when the UI is initialized
        try {
            // Attempt to load the image from the specified path.
            // This assumes 'titlescreen.jpg' is in the project's root directory
            // or a path accessible by the application.
            titleScreenImage = ImageIO.read(getClass().getResourceAsStream("/background/titlescreen.jpg"));
        } catch (IOException e) {
            // Print the stack trace if the image cannot be loaded
            e.printStackTrace();
            // Optionally, set a fallback color or message if the image fails to load
            System.err.println("Error loading titlescreen.jpg. Please ensure it's in the correct resource path.");
        }
    }

    public void showMessage(String message){
        this.message = message;
    }

    public void draw(Graphics2D g2){
        this.g2 = g2;
        g2.setFont(Arial); 
        g2.setColor(Color.white);

        // TITLE STATE
        if(gp.gameState == gp.titleState){
            drawTitleScreen();
        }
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

        // CHARACTER STATE
        if(gp.gameState == gp.inventoryState){
            drawInventory();
        }
    }

    public void drawTitleScreen(){
        
        if(titleScreenState == 0){
            // Draw the loaded image as the background
        if (titleScreenImage != null) {
            // Draw the image to fill the entire screen
            g2.drawImage(titleScreenImage, 0, 0, gp.screenWidth, gp.screenHeight, null);
        } else {
            // Fallback: if image failed to load, draw a default green background
            g2.setColor(new Color(70,120,80));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight );
        }

        // TITLE NAME
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
        String text = "Spakbor Hills";
        int x = getXAtCenter(text);
        int y = gp.tileSize*3;

        // SHADOW
        g2.setColor(Color.black);
        g2.drawString(text, x+5, y+5);
        // MAIN COLOR
        g2.setColor(Color.white);
        g2.drawString(text,x,y);

        // MENU
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 40F));

        text = "NEW GAME";
        x = getXAtCenter(text);
        y += gp.tileSize*1.6;
        g2.drawString(text,x,y);
        if(commandNum == 0){
            g2.drawString(">", x-gp.tileSize, y);
        }

        text = "HELP";
        x = getXAtCenter(text);
        y += gp.tileSize;
        g2.drawString(text,x,y);
        if(commandNum == 1){
            g2.drawString(">", x-gp.tileSize, y);
        }

        text = "CREDITS";
        x = getXAtCenter(text);
        y += gp.tileSize;
        g2.drawString(text,x,y);
        if(commandNum == 2){
            g2.drawString(">", x-gp.tileSize, y);
        }

        text = "EXIT";
        x = getXAtCenter(text);
        y += gp.tileSize;
        g2.drawString(text,x,y);
        if(commandNum == 3){
            g2.drawString(">", x-gp.tileSize, y);
        }
        }
        else if(titleScreenState == 1){
                // Draw the loaded image as the background
            if (titleScreenImage != null) {
                // Draw the image to fill the entire screen
                g2.drawImage(titleScreenImage, 0, 0, gp.screenWidth, gp.screenHeight, null);
            } else {
                // Fallback: if image failed to load, draw a default green background
                g2.setColor(new Color(70,120,80));
                g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight );
            }

            g2.setColor(Color.white);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 15F));

            String text = "Selamat datang di Spakbor Hills, game simulasi kebun dan kehidupan!\n" +
                                "Tujuanmu adalah mengelola kebun, menanam tanaman, berinteraksi dengan penduduk,\n" +
                                "dan membangun kehidupan yang makmur.";
            // Pisahkan teks menjadi beberapa baris berdasarkan karakter newline
            String[] lines = text.split("\n");

            // Tentukan posisi Y awal untuk teks
            int textY = gp.tileSize; // Mulai dari gp.tileSize (bisa disesuaikan)
            int lineHeight = g2.getFontMetrics().getHeight() + 10; // Tinggi baris teks + spasi tambahan antar baris

            // Gambar setiap baris teks
            for (String line : lines) {
                int x = getXAtCenter(line); // Pastikan setiap baris di tengah
                g2.drawString(line, x, textY);
                textY += lineHeight; // Tambahkan tinggi baris untuk posisi baris berikutnya
            }

            // --- Bagian baru untuk teks "Cara Bermain" ---
            textY += gp.tileSize*0.5;

            String howToPlayText = "Cara Bermain (Dalam Game):\n" +
                                   "- Gunakan perintah seperti 'move', 'till', 'plant', 'water', 'harvest' untuk mengelola kebunmu.\n" +
                                   "- 'interact' dengan objek seperti rumahmu (untuk tidur/memasak),\n" + 
                                   " kotak pengiriman (untuk menjual), atau perairan (untuk memancing).\n" +
                                   "- Kelola energi dan waktumu. Tidur memulihkan energi.\n" +
                                   "- Jelajahi area berbeda dengan bergerak ke tepi kebunmu.\n" +
                                   "- Ketik 'help' di dalam game untuk daftar perintah aksi spesifik.";

            String[] howToPlayLines = howToPlayText.split("\n");

                        // Atur font untuk teks "Cara Bermain", mungkin sedikit lebih kecil jika ingin
            g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 15F)); // Ubah ukuran font untuk bagian ini

            // Gambar setiap baris teks "Cara Bermain"
            for (String line : howToPlayLines) {
                int x = getXAtCenter(line);
                g2.drawString(line, x, textY);
                textY += g2.getFontMetrics().getHeight() + 5; // Gunakan tinggi font baru + padding
            }
            // --- Akhir bagian baru ---
            
            g2.setColor(Color.black);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD));
            text = "BACK";
            int x = getXAtCenter(text);
            int y = textY + gp.tileSize;
            g2.drawString(text, x, y);
            if(commandNum == 0){
                g2.drawString(">", x-gp.tileSize, y);
            }
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

    public void drawInventory(){

        // FRAME
        int frameX = gp.tileSize*5;
        int frameY = gp.tileSize;
        int frameWidth = gp.tileSize*6;
        int frameHeight = gp.tileSize*5;

        drawSubWindow(frameX, frameY, frameWidth, frameHeight);

        // SLOT
        final int slotXstart = frameX + 20;
        final int slotYstart = frameY + 20;
        int slotX = slotXstart;
        int slotY = slotYstart;

        // DRAW PLAYER'S ITEMS
        for (int i = 0; i<gp.player.inventory.size(); i++){

            g2.drawImage(gp.player.inventory.get(i).down1, slotX, slotY, null);

            slotX += gp.tileSize;

            if(i == 4 || i == 5 || i == 14){
                slotX = slotXstart;
                slotY += gp.tileSize;
            }
        }

        // CURSOR
        int cursorX = slotXstart + (gp.tileSize * slotCol);
        int cursorY = slotYstart + (gp.tileSize * slotRow);
        int cursorWidth = gp.tileSize;
        int cursorHeight = gp.tileSize;

        // DRAW CURSOR
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(cursorX, cursorY, cursorWidth, cursorHeight, 10, 10);

        // DESCRIPTION FRAME
        int dFrameX = frameX;
        int dFrameY = frameY + frameHeight;
        int dFrameWidth = frameWidth;
        int dFrameHeight = gp.tileSize*3;
        drawSubWindow(dFrameX, dFrameY, dFrameWidth, dFrameHeight);
        
        // DRAW DESCRIPTION TEXT
        int textX = dFrameX + 20;
        int textY = dFrameY + gp.tileSize;
        g2.setFont(g2.getFont().deriveFont(25F));

        int itemIndex = getItemIndexOnSlot();

        if(itemIndex < gp.player.inventory.size()){

            for (String line: gp.player.inventory.get(itemIndex).description.split("\n")){
                g2.drawString(line, textX, textY);
                textY += 32;
            }

        }

    }
    public int getItemIndexOnSlot(){
        int itemIndex = slotCol + (slotRow*5);
        return itemIndex;
    }
}