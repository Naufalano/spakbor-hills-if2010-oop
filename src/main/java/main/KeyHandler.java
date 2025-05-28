package main;

import java.awt.RenderingHints.Key;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import main.GamePanel;

public class KeyHandler implements KeyListener{

    GamePanel gp;

    public boolean upPressed, downPressed, leftPressed, rightPressed;
    
    public KeyHandler(GamePanel gp){
        this.gp = gp;
    }
    
    @Override
    public void keyTyped(KeyEvent e){
        
    }

    @Override
    public void keyPressed(KeyEvent e){

        int code = e.getKeyCode();

        // TITLE STATE
        if(gp.gameState == gp.titleState) {
            if(gp.ui.titleScreenState == 0){
                if(code == KeyEvent.VK_W){
                    gp.ui.commandNum--;
                    if(gp.ui.commandNum < 0){
                        gp.ui.commandNum = 3;
                    }
                }
                if(code == KeyEvent.VK_S){
                    gp.ui.commandNum++;
                    if(gp.ui.commandNum > 3){
                        gp.ui.commandNum = 0;
                    }
                }
                if(code == KeyEvent.VK_ENTER){
                    if(gp.ui.commandNum == 0){
                        gp.gameState = gp.playState;
                    }
                    if(gp.ui.commandNum == 1){
                        gp.ui.titleScreenState = 1;
                    }
                    if(gp.ui.commandNum == 2){

                    }
                    if(gp.ui.commandNum == 3){
                        System.exit(0);
                    }
                }
            }
            else if(gp.ui.titleScreenState == 1){
                if(code == KeyEvent.VK_W){
                    gp.ui.commandNum = 0;
                }
                if(code == KeyEvent.VK_S){
                    gp.ui.commandNum = 0;
                }
                if(code == KeyEvent.VK_ENTER){
                    if(gp.ui.commandNum == 0){
                        gp.ui.titleScreenState = 0;
                    }
                }
            }
        }


        // PLAY STATE
        if(gp.gameState == gp.playState){
            if(code == KeyEvent.VK_W){
                upPressed = true;
            }
            if(code == KeyEvent.VK_S){
                downPressed = true;
            }
            if(code == KeyEvent.VK_A){
                leftPressed = true;
            }
            if(code == KeyEvent.VK_D){
                rightPressed = true;
            }
            if(code == KeyEvent.VK_P){
                gp.gameState = gp.pauseState;
            }

            if(code == KeyEvent.VK_I){
                gp.gameState = gp.inventoryState;
            }
        }

        // PAUSE STATE
        else if(gp.gameState == gp.pauseState){
            if(code == KeyEvent.VK_P){
            gp.gameState = gp.playState;
            }
        }

        // DIALOGUE STATE
        else if(gp.gameState == gp.dialogueState){
            if (code == KeyEvent.VK_ENTER){
                gp.gameState = gp.playState;
            }
        }

        // INVENTORY STATE
        else if(gp.gameState == gp.inventoryState){
            if(code == KeyEvent.VK_I){
                gp.gameState = gp.playState;
            }
            if(code == KeyEvent.VK_W){
                if(gp.ui.slotRow != 0){
                    gp.ui.slotRow--;
                    gp.playSE(6);
                }
            }
            if(code == KeyEvent.VK_A){
                if(gp.ui.slotCol != 0){
                    gp.ui.slotCol--;
                    gp.playSE(6);
                }
            }
            if(code == KeyEvent.VK_S){
                if(gp.ui.slotRow != 3){
                    gp.ui.slotRow++;
                    gp.playSE(6);
                }
            }
            if(code == KeyEvent.VK_D){
                if(gp.ui.slotCol != 4){
                    gp.ui.slotCol++;
                    gp.playSE(6);
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e){

        int code = e.getKeyCode();

        if(code == KeyEvent.VK_W){
            upPressed = false;
        }
        if(code == KeyEvent.VK_S){
            downPressed = false;
        }
        if(code == KeyEvent.VK_A){
            leftPressed = false;
        }
        if(code == KeyEvent.VK_D){
            rightPressed = false;
        }
    }
}
