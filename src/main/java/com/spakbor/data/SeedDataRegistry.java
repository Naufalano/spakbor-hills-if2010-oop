package com.spakbor.data;
import com.spakbor.cls.items.*;
import com.spakbor.enums.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Assuming Seeds.java and SeasonType.java (enum) are accessible

public class SeedDataRegistry {
    private static final List<Seeds> ALL_SEEDS = new ArrayList<>();

    static {
        // SPRING SEEDS
        ALL_SEEDS.add(new Seeds("Parsnip Seeds", List.of(SeasonType.SPRING), 1, 20));
        ALL_SEEDS.add(new Seeds("Cauliflower Seeds", List.of(SeasonType.SPRING), 5, 80));
        ALL_SEEDS.add(new Seeds("Potato Seeds", List.of(SeasonType.SPRING), 3, 50));
        ALL_SEEDS.add(new Seeds("Wheat Seeds", List.of(SeasonType.SPRING, SeasonType.FALL), 1, 60)); 

        // SUMMER SEEDS
        ALL_SEEDS.add(new Seeds("Blueberry Seeds", List.of(SeasonType.SUMMER), 7, 80));
        ALL_SEEDS.add(new Seeds("Tomato Seeds", List.of(SeasonType.SUMMER), 3, 50));
        ALL_SEEDS.add(new Seeds("Hot Pepper Seeds", List.of(SeasonType.SUMMER), 1, 40));
        ALL_SEEDS.add(new Seeds("Melon Seeds", List.of(SeasonType.SUMMER), 4, 80));

        // FALL SEEDS
        ALL_SEEDS.add(new Seeds("Cranberry Seeds", List.of(SeasonType.FALL), 2, 100));
        ALL_SEEDS.add(new Seeds("Pumpkin Seeds", List.of(SeasonType.FALL), 7, 150));
        ALL_SEEDS.add(new Seeds("Grape Seeds", List.of(SeasonType.FALL), 3, 60));
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
        return ALL_SEEDS.stream()
                .filter(seed -> seed.canBePlantedIn(season))
                .collect(Collectors.toList());
    }
}
