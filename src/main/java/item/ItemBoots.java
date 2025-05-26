package item;

import java.io.IOException;
import javax.imageio.ImageIO;

import main.GamePanel;

public class ItemBoots extends SuperItem {
    GamePanel gp;

    public ItemBoots(GamePanel gp){
        this.name = "Boots";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/item/boots.png"));
            uTool.scaleImage(image, gp.tileSize, gp.tileSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
