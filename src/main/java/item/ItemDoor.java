package item;

import java.io.IOException;
import javax.imageio.ImageIO;

import main.GamePanel;
public class ItemDoor extends SuperItem {
    GamePanel gp;

    public ItemDoor(GamePanel gp){
        this.name = "Door";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/item/door.png"));
            uTool.scaleImage(image, gp.tileSize, gp.tileSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        collision = true;
    }
}
