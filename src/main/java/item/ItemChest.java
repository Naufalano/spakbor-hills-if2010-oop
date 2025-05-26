package item;

import java.io.IOException;
import javax.imageio.ImageIO;

public class ItemChest extends SuperItem {
    public ItemChest(){
        this.name = "Chest";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/item/chest.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
