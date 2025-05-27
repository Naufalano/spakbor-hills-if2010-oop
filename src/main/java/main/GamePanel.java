package main;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.rmi.server.UID;
import java.awt.Graphics;
import java.awt.Color;

import javax.sound.midi.Soundbank;
import javax.swing.JPanel;

import entity.Entity;
import entity.Player;
import item.SuperItem;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable{
    
    // SCREEN SETTINGS
    final int originalTileSize = 16; // 16X16 tile
    final int scale = 3;

    public final int tileSize = originalTileSize * scale; // 48x48 tile
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // 768 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 576 pixels

    // WORLD SETTINGS

    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int worldWidth = tileSize * maxWorldCol; 
    public final int worldHeight = tileSize * maxWorldRow;  

    // FPS
    int FPS = 60;

    TileManager tileM = new TileManager(this);
    Sound music = new Sound();
    Sound SE = new Sound();
    KeyHandler keyH = new KeyHandler(this);
    public UI ui = new UI(this);
    Thread gameThread;
    public CollisionChecker checker = new CollisionChecker(this);
    public AssetSetter setter = new AssetSetter(this);
    
    public int gameState;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int dialogueState = 3;
    
    public Player player = new Player(this,keyH);
    public SuperItem[] items = new SuperItem[10];
    public Entity[] NPC = new Entity[10];

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    public void setUpGame(){
        setter.setItem();
        setter.setNPC();
        playMusic(5);
        gameState = playState;
    }

    public void startGameThread(){

        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
//    public void run(){ //

//        double drawInterval = 1000000000/FPS; //0.01666 seconds
//        double nextDrawTime = System.nanoTime() + drawInterval;

//        while(gameThread != null){

            // 1. UPDATE: update information such as character position
//             update();
            // 2. DRAW: draw the screen with the updated information
//             repaint();

//             try{
//                 double remainingTime = nextDrawTime - System.nanoTime();
//                 remainingTime = remainingTime/1000000;

   //              if(remainingTime < 0){
     //                remainingTime = 0;
     //           }

        //         Thread.sleep((long) remainingTime);

           //      nextDrawTime += drawInterval;

 //            } catch (InterruptedException e){
 //                e.printStackTrace();
 //            }
  //       }
  //   } //

    public void run(){
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null){

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;

            lastTime = currentTime;

            if(delta >= 1){
                update();
                repaint(); 
                delta--;
            }
        }
    }

    public void update(){
        if(gameState == playState){
        player.update();
        for(int i = 0; i < NPC.length; i++){
            if(NPC[i] != null){
                NPC[i].update();;
            }
        }
        if(gameState == pauseState){

        }
    }
}

    public void paintComponent(Graphics g){

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;

        tileM.draw(g2);

        for(int i = 0; i < items.length; i++){
            if(items[i] != null){
                items[i].draw(g2, this);
            }
        }

        for(int i = 0; i < NPC.length; i++){
            if(NPC[i] != null){
                NPC[i].draw(g2);
            }
        }

        player.draw(g2);

        ui.draw(g2);

        g2.dispose();
    }

    public void playMusic(int i){
        music.setFile(i);
        music.play();
        music.loop();
    }

    public void stopMusic(){
        music.stop();
        
    }

    public void playSE(int i){
        SE.setFile(i);
        SE.play();
    }
}