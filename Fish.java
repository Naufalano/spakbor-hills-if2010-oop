import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Fish extends Item implements EdibleItem {
    private FishRarity rarity;
    private Set<SeasonType> availableSeasons; // Using Set for unique seasons
    private List<TimeWindow> availableTimeWindows;
    private Set<WeatherType> availableWeathers; // Using Set for unique weathers
    private Set<String> locations; // e.g., "Mountain Lake", "Pond", "Forest River", "Ocean"
    // sellPrice will be calculated
    // energy is always 1 when eaten raw

    // Helper class for time windows
    static class TimeWindow {
        int startHour; // 0-23
        int endHour;   // 0-23 (can wrap around, e.g. 20:00 to 02:00)

        public TimeWindow(int startHour, int endHour) {
            this.startHour = startHour;
            this.endHour = endHour;
        }

        public boolean isTimeWithin(int currentHour) {
            if (startHour <= endHour) { // Normal window (e.g., 06:00-18:00)
                return currentHour >= startHour && currentHour < endHour; // Exclusive endHour for simplicity
            } else { // Wraparound window (e.g., 20:00-02:00)
                return currentHour >= startHour || currentHour < endHour;
            }
        }

        public int getDurationHours() {
            if (startHour <= endHour) {
                return endHour - startHour;
            } else {
                return (24 - startHour) + endHour;
            }
        }

        @Override
        public String toString() {
            return String.format("%02d:00-%02d:00", startHour, endHour);
        }
    }

    public Fish(String name, FishRarity rarity,
                Set<SeasonType> availableSeasons, List<TimeWindow> availableTimeWindows,
                Set<WeatherType> availableWeathers, Set<String> locations) {
        super(name); // Name is from Item class
        this.rarity = rarity;
        this.availableSeasons = availableSeasons;
        this.availableTimeWindows = availableTimeWindows;
        this.availableWeathers = availableWeathers;
        this.locations = locations;
        // sellPrice is calculated via getSellPrice()
        // energy is fixed at 1
    }

    public FishRarity getRarity() {
        return rarity;
    }

    public Set<SeasonType> getAvailableSeasons() {
        return availableSeasons;
    }

    public List<TimeWindow> getAvailableTimeWindows() {
        return availableTimeWindows;
    }

    public Set<WeatherType> getAvailableWeathers() {
        return availableWeathers;
    }

    public Set<String> getLocations() {
        return locations;
    }

    public boolean canCatch(SeasonType currentSeason, int currentHour, WeatherType currentWeather, String currentLocation) {
        if (!locations.contains(currentLocation)) {
            return false;
        }
        if (!availableSeasons.contains(SeasonType.ANY) && !availableSeasons.contains(currentSeason)) {
            return false;
        }
        if (!availableWeathers.contains(WeatherType.ANY) && !availableWeathers.contains(currentWeather)) {
            return false;
        }

        boolean timeMatch = false;
        if (availableTimeWindows.isEmpty() || (availableTimeWindows.size() == 1 && availableTimeWindows.get(0).startHour == 0 && availableTimeWindows.get(0).endHour == 0)) { // Represents "Any" time
            timeMatch = true;
        } else {
            for (TimeWindow window : availableTimeWindows) {
                if (window.isTimeWithin(currentHour)) {
                    timeMatch = true;
                    break;
                }
            }
        }
        return timeMatch;
    }


    @Override
    public int getSellPrice() {
        // Formula: (4 / num_seasons) * (24 / num_hours) * (2 / num_weather_variations) * (4 / num_locations) * C
        // C = 10 for common, 5 for regular, 25 for legendary

        double numSeasons = availableSeasons.contains(SeasonType.ANY) ? 4.0 : (double) availableSeasons.size();
        if (numSeasons == 0) numSeasons = 4; // Should not happen if "Any" is handled

        double totalHours = 0;
        if (availableTimeWindows.isEmpty() || (availableTimeWindows.size() == 1 && availableTimeWindows.get(0).startHour == 0 && availableTimeWindows.get(0).endHour == 0)) { // "Any" time
            totalHours = 24.0;
        } else {
            for (TimeWindow window : availableTimeWindows) {
                totalHours += window.getDurationHours();
            }
        }
        if (totalHours == 0) totalHours = 24; // Default to 24 if no specific window means any time

        double numWeatherVariations = availableWeathers.contains(WeatherType.ANY) ? 2.0 : (double) availableWeathers.size();
        if (numWeatherVariations == 0) numWeatherVariations = 2;

        double numLocations = (double) locations.size();
        if (numLocations == 0) numLocations = 1; // Should always have at least one location

        double cValue;
        switch (rarity) {
            case COMMON:    cValue = 10.0; break;
            case REGULAR:   cValue = 5.0;  break;
            case LEGENDARY: cValue = 25.0; break;
            default:        cValue = 1.0;  break; // Should not happen
        }

        // Defensive checks for division by zero, though logic above tries to prevent it
        if (numSeasons == 0 || totalHours == 0 || numWeatherVariations == 0 || numLocations == 0) {
            System.err.println("Warning: Division by zero avoided in sell price calculation for " + getName());
            return (int) cValue; // Return base C value if factors are zero
        }
        
        double price = (4.0 / numSeasons) * (24.0 / totalHours) * (2.0 / numWeatherVariations) * (4.0 / numLocations) * cValue;
        return (int) Math.round(price);
    }

    @Override
    public void use() {
        // Eating the fish
        System.out.println("Ate " + getName() + ". +1 Energy.");
        // The actual energy addition will be handled by EatingAction using getEnergyRestoration()
    }

    @Override
    public int getEnergyRestoration() {
        return 1; // All fish restore 1 energy when eaten raw
    }

    // Override equals and hashCode if Fish objects are stored in Sets or as Map keys directly
    // Based on name, as fish types are unique by name.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false; // Checks name from Item class
        Fish fish = (Fish) o;
        return rarity == fish.rarity; // Further differentiate by rarity if names could collide (unlikely for types)
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), rarity);
    }

    @Override
    public String toString() {
        return "Fish{name='" + getName() + "', rarity=" + rarity + ", sellPrice=" + getSellPrice() + "g}";
    }
}
