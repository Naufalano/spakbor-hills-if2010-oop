package com.spakbor.data;
import com.spakbor.cls.items.*;
import com.spakbor.enums.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FishDataRegistry {
    private static final List<Fish> ALL_FISH = new ArrayList<>();

    private static Set<SeasonType> parseSeasons(String seasonStr, String fishNameForLogging) {
        Set<SeasonType> seasons = new HashSet<>();
        if (seasonStr.equalsIgnoreCase("Any")) {
            seasons.add(SeasonType.ANY);
        } else {
            for (String s : seasonStr.split(",\\s*")) {
                try {
                    seasons.add(SeasonType.valueOf(s.trim().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    System.err.println("Warning: Unknown season string '" + s + "' in fish data for: " + fishNameForLogging);
                }
            }
        }
        if (seasons.isEmpty() && !seasonStr.equalsIgnoreCase("Any")) {
             System.err.println("Warning: No valid seasons parsed for '" + seasonStr + "' for fish: " + fishNameForLogging + ". Defaulting to ANY.");
             seasons.add(SeasonType.ANY);
        }
        return seasons;
    }

    private static List<Fish.TimeWindow> parseTimes(String timeStr, String fishNameForLogging) {
        List<Fish.TimeWindow> windows = new ArrayList<>();
        if (timeStr.equalsIgnoreCase("Any")) {
            windows.add(new Fish.TimeWindow(0, 0));
        } else {
            for (String part : timeStr.split(",\\s*")) {
                try {
                    String[] hours = part.split("-");
                    if (hours.length == 2) {
                        int startHour = Integer.parseInt(hours[0].substring(0, 2));
                        int endHour = Integer.parseInt(hours[1].substring(0, 2));
                        windows.add(new Fish.TimeWindow(startHour, endHour));
                    } else {
                        System.err.println("Warning: Malformed time string part '" + part + "' for fish: " + fishNameForLogging);
                    }
                } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                     System.err.println("Warning: Error parsing time string part '" + part + "' for fish: " + fishNameForLogging + " - " + e.getMessage());
                }
            }
        }
         if (windows.isEmpty() && !timeStr.equalsIgnoreCase("Any")) {
             System.err.println("Warning: No valid time windows parsed for '" + timeStr + "' for fish: " + fishNameForLogging + ". Defaulting to ANY time.");
             windows.add(new Fish.TimeWindow(0,0)); // Fallback
         }
        return windows;
    }

    private static Set<WeatherType> parseWeathers(String weatherStr, String fishNameForLogging) {
        Set<WeatherType> weathers = new HashSet<>();
        if (weatherStr.equalsIgnoreCase("Any")) {
            weathers.add(WeatherType.ANY);
        } else {
            for (String s : weatherStr.split(",\\s*")) {
                try {
                    weathers.add(WeatherType.valueOf(s.trim().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    System.err.println("Warning: Unknown weather string '" + s + "' in fish data for: " + fishNameForLogging);
                }
            }
        }
        if (weathers.isEmpty() && !weatherStr.equalsIgnoreCase("Any")) {
             System.err.println("Warning: No valid weathers parsed for '" + weatherStr + "' for fish: " + fishNameForLogging + ". Defaulting to ANY.");
             weathers.add(WeatherType.ANY); // Fallback
        }
        return weathers;
    }

    private static Set<String> parseLocations(String locStr) {
        return new HashSet<>(Arrays.asList(locStr.split(",\\s*")));
    }

    static {
        String currentFish = "Bullhead";
        ALL_FISH.add(new Fish(currentFish, FishRarity.COMMON,
        parseSeasons("Any", currentFish), parseTimes("Any", currentFish), parseWeathers("Any", currentFish),
        parseLocations("Mountain Lake")));

        currentFish = "Carp";
        ALL_FISH.add(new Fish(currentFish, FishRarity.COMMON,
        parseSeasons("Any", currentFish), parseTimes("Any", currentFish), parseWeathers("Any", currentFish),
        parseLocations("Mountain Lake, Pond")));

        currentFish = "Chub";
        ALL_FISH.add(new Fish(currentFish, FishRarity.COMMON,
        parseSeasons("Any", currentFish), parseTimes("Any", currentFish), parseWeathers("Any", currentFish),
        parseLocations("Forest River, Mountain Lake")));

        currentFish = "Largemouth Bass";
        ALL_FISH.add(new Fish(currentFish, FishRarity.REGULAR,
        parseSeasons("Any", currentFish), parseTimes("06.00-18.00", currentFish), parseWeathers("Any", currentFish),
        parseLocations("Mountain Lake")));

        currentFish = "Rainbow Trout";
        ALL_FISH.add(new Fish(currentFish, FishRarity.REGULAR,
                parseSeasons("Summer", currentFish), parseTimes("06.00-18.00", currentFish), parseWeathers("Sunny", currentFish),
                parseLocations("Forest River, Mountain Lake")));

        currentFish = "Sturgeon";
        ALL_FISH.add(new Fish(currentFish, FishRarity.REGULAR,
        parseSeasons("Summer, Winter", currentFish), parseTimes("06.00-18.00", currentFish), parseWeathers("Any", currentFish),
        parseLocations("Mountain Lake")));

        currentFish = "Midnight Carp";
        ALL_FISH.add(new Fish(currentFish, FishRarity.REGULAR,
        parseSeasons("Winter, Fall", currentFish), parseTimes("20.00-02.00", currentFish), parseWeathers("Any", currentFish),
        parseLocations("Mountain Lake, Pond")));

        currentFish = "Flounder";
        ALL_FISH.add(new Fish(currentFish, FishRarity.REGULAR,
        parseSeasons("Spring, Summer", currentFish), parseTimes("06.00-22.00", currentFish), parseWeathers("Any", currentFish),
        parseLocations("Ocean")));

        currentFish = "Halibut";
        ALL_FISH.add(new Fish(currentFish, FishRarity.REGULAR,
        parseSeasons("Any", currentFish), parseTimes("06.00-11.00,19.00-02.00", currentFish), parseWeathers("Any", currentFish),
        parseLocations("Ocean")));

        currentFish = "Octopus";
        ALL_FISH.add(new Fish(currentFish, FishRarity.REGULAR,
        parseSeasons("Summer", currentFish), parseTimes("06.00-22.00", currentFish), parseWeathers("Any", currentFish),
        parseLocations("Ocean")));

        currentFish = "Pufferfish";
        ALL_FISH.add(new Fish(currentFish, FishRarity.REGULAR,
        parseSeasons("Summer", currentFish), parseTimes("00.00-16.00", currentFish), parseWeathers("Sunny", currentFish),
        parseLocations("Ocean")));

        currentFish = "Sardine";
        ALL_FISH.add(new Fish(currentFish, FishRarity.REGULAR,
        parseSeasons("Any", currentFish), parseTimes("06.00-18.00", currentFish), parseWeathers("Any", currentFish),
        parseLocations("Ocean")));

        currentFish = "Super Cucumber";
        ALL_FISH.add(new Fish(currentFish, FishRarity.REGULAR,
        parseSeasons("Summer, Fall, Winter", currentFish), parseTimes("18.00-02.00", currentFish), parseWeathers("Any", currentFish),
        parseLocations("Ocean")));

        currentFish = "Catfish";
        ALL_FISH.add(new Fish(currentFish, FishRarity.REGULAR,
        parseSeasons("Spring, Summer, Fall", currentFish), parseTimes("06.00-22.00", currentFish), parseWeathers("Rainy", currentFish),
        parseLocations("Forest River, Pond")));

        currentFish = "Salmon";
        ALL_FISH.add(new Fish(currentFish, FishRarity.REGULAR,
        parseSeasons("Fall", currentFish), parseTimes("06.00-18.00", currentFish), parseWeathers("Any", currentFish),
        parseLocations("Forest River")));

        // --- LEGENDARY FISH ---
        currentFish = "Angler";
        ALL_FISH.add(new Fish(currentFish, FishRarity.LEGENDARY,
        parseSeasons("Fall", currentFish), parseTimes("08.00-20.00", currentFish), parseWeathers("Any", currentFish),
        parseLocations("Pond")));

        currentFish = "Crimsonfish";
        ALL_FISH.add(new Fish(currentFish, FishRarity.LEGENDARY,
        parseSeasons("Summer", currentFish), parseTimes("08.00-20.00", currentFish), parseWeathers("Any", currentFish),
        parseLocations("Ocean")));

        currentFish = "Glacierfish";
        ALL_FISH.add(new Fish(currentFish, FishRarity.LEGENDARY,
        parseSeasons("Winter", currentFish), parseTimes("08.00-20.00", currentFish), parseWeathers("Any", currentFish),
        parseLocations("Forest River")));

        currentFish = "Legend";
        ALL_FISH.add(new Fish(currentFish, FishRarity.LEGENDARY,
        parseSeasons("Spring", currentFish), parseTimes("08.00-20.00", currentFish), parseWeathers("Rainy", currentFish),
        parseLocations("Mountain Lake")));
    }

    public static List<Fish> getAllFish() {
        return new ArrayList<>(ALL_FISH);
    }

    public static List<Fish> getAvailableFish(SeasonType currentSeason, int currentHour, WeatherType currentWeather, String currentLocation) {
        return ALL_FISH.stream().filter(fish -> fish.canCatch(currentSeason, currentHour, currentWeather, currentLocation)).collect(Collectors.toList());
    }

    public static Fish getFishByName(String name) {
        for (Fish fish : ALL_FISH) {
            if (fish.getName().equalsIgnoreCase(name)) {
                return fish;
            }
        }
        return null;
    }
}
