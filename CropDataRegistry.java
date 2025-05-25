import java.util.ArrayList;
import java.util.List;

// Assuming Crop.java and Item.java are accessible

public class CropDataRegistry {
    private static final List<Crop> ALL_CROPS = new ArrayList<>();
    private static final int DEFAULT_CROP_ENERGY_RESTORATION = 3; // As per PDF

    static {
        // Data from crop_criteria.pdf
        // No, Nama Crop, Harga Beli (per crop), Harga Jual (per crop), Jumlah Crop per Panen

        // 1. Parsnip
        ALL_CROPS.add(new Crop("Parsnip", 50, 35, DEFAULT_CROP_ENERGY_RESTORATION, 1));
        // 2. Cauliflower
        ALL_CROPS.add(new Crop("Cauliflower", 200, 150, DEFAULT_CROP_ENERGY_RESTORATION, 1));
        // 3. Potato (Harga Beli not specified, using 0)
        ALL_CROPS.add(new Crop("Potato", 0, 80, DEFAULT_CROP_ENERGY_RESTORATION, 1));
        // 4. Wheat
        ALL_CROPS.add(new Crop("Wheat", 50, 30, DEFAULT_CROP_ENERGY_RESTORATION, 3));
        // 5. Blueberry
        ALL_CROPS.add(new Crop("Blueberry", 150, 40, DEFAULT_CROP_ENERGY_RESTORATION, 3));
        // 6. Tomato
        ALL_CROPS.add(new Crop("Tomato", 90, 60, DEFAULT_CROP_ENERGY_RESTORATION, 1));
        // 7. Hot Pepper (Harga Beli not specified, using 0)
        ALL_CROPS.add(new Crop("Hot Pepper", 0, 40, DEFAULT_CROP_ENERGY_RESTORATION, 1));
        // 8. Melon (Harga Beli not specified, using 0)
        ALL_CROPS.add(new Crop("Melon", 0, 250, DEFAULT_CROP_ENERGY_RESTORATION, 1));
        // 9. Cranberry (Harga Beli not specified, using 0)
        ALL_CROPS.add(new Crop("Cranberry", 0, 25, DEFAULT_CROP_ENERGY_RESTORATION, 10));
        // 10. Pumpkin
        ALL_CROPS.add(new Crop("Pumpkin", 300, 250, DEFAULT_CROP_ENERGY_RESTORATION, 1));
        // 11. Grape
        ALL_CROPS.add(new Crop("Grape", 100, 10, DEFAULT_CROP_ENERGY_RESTORATION, 20));
    }

    /**
     * Gets a list of all defined crops.
     * @return A new list containing all crop definitions.
     */
    public static List<Crop> getAllCrops() {
        return new ArrayList<>(ALL_CROPS); // Return a copy
    }

    /**
     * Gets a specific crop by its exact name (case-insensitive).
     * @param name The name of the crop.
     * @return The Crop object if found, otherwise null.
     */
    public static Crop getCropByName(String name) {
        for (Crop crop : ALL_CROPS) {
            if (crop.getName().equalsIgnoreCase(name)) {
                return crop; // Return a direct reference to the prototype crop object
            }
        }
        System.err.println("Warning: Crop with name '" + name + "' not found in CropDataRegistry.");
        return null;
    }
}
