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
        // Wheat Seeds appear in Spring and Fall. We'll add two distinct entries if their
        // properties were different, or handle it by allowing a seed to have multiple seasons.
        // For simplicity with the current Seeds class (String season), we'll treat them as separate
        // if a single seed item could be bought that works in multiple seasons.
        // If "Wheat Seeds" is one item that can be planted in Spring OR Fall, Seeds class needs adjustment.
        // Assuming "Wheat Seeds" listed under Spring is for Spring, and under Fall is for Fall.
        // Or, if it's the *same* seed item, the Seeds class needs a Set<SeasonType> seasons.
        // Let's assume for now they are distinct entries if bought from a store that filters by current season,
        // or the Seeds class is modified.
        // For this registry, we list them as per PDF. If "Wheat Seeds" is a single item type,
        // then the Seeds class should store a list/set of compatible seasons.
        // Given current Seeds(name, seasonString, ...), we treat them as distinct for registry.
        ALL_SEEDS.add(new Seeds("Wheat Seeds (Spring)", "Spring", 1, 60)); // Differentiate for clarity if needed

        // SUMMER SEEDS
        ALL_SEEDS.add(new Seeds("Blueberry Seeds", "Summer", 7, 80));
        ALL_SEEDS.add(new Seeds("Tomato Seeds", "Summer", 3, 50));
        ALL_SEEDS.add(new Seeds("Hot Pepper Seeds", "Summer", 1, 40));
        ALL_SEEDS.add(new Seeds("Melon Seeds", "Summer", 4, 80));

        // FALL SEEDS
        ALL_SEEDS.add(new Seeds("Cranberry Seeds", "Fall", 2, 100));
        ALL_SEEDS.add(new Seeds("Pumpkin Seeds", "Fall", 7, 150));
        ALL_SEEDS.add(new Seeds("Wheat Seeds (Fall)", "Fall", 1, 60)); // Differentiate for clarity
        ALL_SEEDS.add(new Seeds("Grape Seeds", "Fall", 3, 60));

        // WINTER SEEDS
        // "Tidak ada seed yang dapat tumbuh saat winter" - So, no seeds listed for Winter.
    }

    /**
     * Gets a list of all defined seeds.
     * @return A new list containing all seeds.
     */
    public static List<Seeds> getAllSeeds() {
        return new ArrayList<>(ALL_SEEDS); // Return a copy to prevent external modification
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
        String seasonName = season.toString(); // Assumes SeasonType.toString() gives "Spring", "Summer", etc.
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
