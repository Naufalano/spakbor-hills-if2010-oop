package data;
import cls.items.*;
import enums.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Assuming Seeds.java and SeasonType.java (enum) are accessible

public class SeedDataRegistry {
    private static final List<Seeds> ALL_SEEDS = new ArrayList<>();

    static {
        // SPRING SEEDS
        ALL_SEEDS.add(new Seeds("Parsnip Seeds", "Spring", 1, 20));
        ALL_SEEDS.add(new Seeds("Cauliflower Seeds", "Spring", 5, 80));
        ALL_SEEDS.add(new Seeds("Potato Seeds", "Spring", 3, 50));
        ALL_SEEDS.add(new Seeds("Wheat Seeds (Spring)", "Spring", 1, 60)); 

        // SUMMER SEEDS
        ALL_SEEDS.add(new Seeds("Blueberry Seeds", "Summer", 7, 80));
        ALL_SEEDS.add(new Seeds("Tomato Seeds", "Summer", 3, 50));
        ALL_SEEDS.add(new Seeds("Hot Pepper Seeds", "Summer", 1, 40));
        ALL_SEEDS.add(new Seeds("Melon Seeds", "Summer", 4, 80));

        // FALL SEEDS
        ALL_SEEDS.add(new Seeds("Cranberry Seeds", "Fall", 2, 100));
        ALL_SEEDS.add(new Seeds("Pumpkin Seeds", "Fall", 7, 150));
        ALL_SEEDS.add(new Seeds("Wheat Seeds (Fall)", "Fall", 1, 60)); 
        ALL_SEEDS.add(new Seeds("Grape Seeds", "Fall", 3, 60));
    }

    /**
     * Gets a list of all defined seeds.
     * @return A new list containing all seeds.
     */
    public static List<Seeds> getAllSeeds() {
        return new ArrayList<>(ALL_SEEDS);
    }

    /**
     * Gets a specific seed by its exact name.
     * @param name The name of the seed.
     * @return The Seeds object if found, otherwise null.
     */
    public static Seeds getSeedByName(String name) {
        for (Seeds seed : ALL_SEEDS) {
            if (seed.getName().equalsIgnoreCase(name)) {
                return seed;
            }
        }
        return null;
    }

    /**
     * Gets all seeds available for a specific season.
     * This requires your SeasonType enum and Seeds.getSeason() to use matching string representations.
     * @param season The season for which to get available seeds.
     * @return A list of seeds that can be planted in the given season.
     */
    public static List<Seeds> getSeedsForSeason(SeasonType season) {
        String seasonName = season.toString(); 
        return ALL_SEEDS.stream()
                .filter(seed -> seed.getSeason().equalsIgnoreCase(seasonName))
                .collect(Collectors.toList());
    }

     /**
     * Gets all seeds available for a specific season, using a string for the season.
     * @param seasonName The name of the season (e.g., "Spring").
     * @return A list of seeds that can be planted in the given season.
     */
    public static List<Seeds> getSeedsForSeason(String seasonName) {
        return ALL_SEEDS.stream()
                .filter(seed -> seed.getSeason().equalsIgnoreCase(seasonName))
                .collect(Collectors.toList());
    }
}
