package item;

import java.io.IOException;
import javax.imageio.ImageIO;

public class ItemDoor extends SuperItem {
    public ItemDoor(){
        this.name = "Door";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/item/door.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
