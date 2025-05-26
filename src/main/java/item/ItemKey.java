package item;

import java.io.IOException;
import javax.imageio.ImageIO;

public class ItemKey extends SuperItem {
    public ItemKey(){
        this.name = "Key";
        try {
            image = ImageIO.read(getClass().getResourceAsStream("/item/key.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
