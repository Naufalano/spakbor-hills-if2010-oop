package item;

import java.io.IOException;
import javax.imageio.ImageIO;

import main.GamePanel;

public class ItemChest extends SuperItem {
    GamePanel gp;

    public ItemChest(GamePanel gp){
        this.name = "Chest";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/item/chest.png"));
            uTool.scaleImage(image, gp.tileSize, gp.tileSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
