package item;

import entity.Entity; // Assuming your items are entities
import main.GamePanel;
import main.UtilityTool; // If you use UtilityTool for image setup

import java.io.IOException;
import javax.imageio.ImageIO;

public class OBJ_ParsnipSeeds extends Entity {

    public OBJ_ParsnipSeeds(GamePanel gp) {
        super(gp);
        name = "Parsnip Seeds";
        down1 = setup("/item/Parsnip_Seeds"); // Assuming items are in /items folder
        // You might have other sprites for different directions if needed, but for inventory display, down1 is often sufficient.
        description = "[" + name + "]\nParsnip Seeds aja";
    }

    // You might add specific functionality for the watering can here
}

// And in your UtilityTool class, you might have a setup method like this:
// public BufferedImage setup(String imagePath, int width, int height) {
//     UtilityTool uTool = new UtilityTool();
//     BufferedImage image = null;
//     try {
//         image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
//         image = uTool.scaleImage(image, width, height); // Assuming you have a scaleImage method
//     } catch (IOException e) {
//         e.printStackTrace();
//     }
//     return image;
// }