package item;

import java.io.IOException;
import javax.imageio.ImageIO;

import main.GamePanel;

public class ItemKey extends SuperItem {
    GamePanel gp;
    
    public ItemKey(GamePanel gp){
        this.gp = gp;
        this.name = "Key";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/item/key.png"));
            uTool.scaleImage(image, gp.tileSize, gp.tileSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
