import java.util.ArrayList;
import java.util.List;

public class CropDataRegistry {
    private static final List<Crop> ALL_CROPS = new ArrayList<>();
    private static final int DEFAULT_CROP_ENERGY_RESTORATION = 3;

    static {
        ALL_CROPS.add(new Crop("Parsnip", 50, 35, DEFAULT_CROP_ENERGY_RESTORATION, 1));
        ALL_CROPS.add(new Crop("Cauliflower", 200, 150, DEFAULT_CROP_ENERGY_RESTORATION, 1));
        ALL_CROPS.add(new Crop("Potato", 0, 80, DEFAULT_CROP_ENERGY_RESTORATION, 1));
        ALL_CROPS.add(new Crop("Wheat", 50, 30, DEFAULT_CROP_ENERGY_RESTORATION, 3));
        ALL_CROPS.add(new Crop("Blueberry", 150, 40, DEFAULT_CROP_ENERGY_RESTORATION, 3));
        ALL_CROPS.add(new Crop("Tomato", 90, 60, DEFAULT_CROP_ENERGY_RESTORATION, 1));
        ALL_CROPS.add(new Crop("Hot Pepper", 0, 40, DEFAULT_CROP_ENERGY_RESTORATION, 1));
        ALL_CROPS.add(new Crop("Melon", 0, 250, DEFAULT_CROP_ENERGY_RESTORATION, 1));
        ALL_CROPS.add(new Crop("Cranberry", 0, 25, DEFAULT_CROP_ENERGY_RESTORATION, 10));
        ALL_CROPS.add(new Crop("Pumpkin", 300, 250, DEFAULT_CROP_ENERGY_RESTORATION, 1));
        ALL_CROPS.add(new Crop("Grape", 100, 10, DEFAULT_CROP_ENERGY_RESTORATION, 20));
    }

    /**
     * Gets a list of all defined crops.
     * @return A new list containing all crop definitions.
     */
    public static List<Crop> getAllCrops() {
        return new ArrayList<>(ALL_CROPS);
    }

    /**
     * Gets a specific crop by its exact name (case-insensitive).
     * @param name The name of the crop.
     * @return The Crop object if found, otherwise null.
     */
    public static Crop getCropByName(String name) {
        for (Crop crop : ALL_CROPS) {
            if (crop.getName().equalsIgnoreCase(name)) {
                return crop;
            }
        }
        System.err.println("Warning: Crop with name '" + name + "' not found in CropDataRegistry.");
        return null;
    }
}
