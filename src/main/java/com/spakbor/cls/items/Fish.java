package com.spakbor.cls.items;

import com.spakbor.enums.FishRarity;
import com.spakbor.enums.SeasonType;
import com.spakbor.enums.WeatherType;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Fish extends Item implements EdibleItem {
    private static final long serialVersionUID = 1L;

    private FishRarity rarity;
    private Set<SeasonType> availableSeasons;
    private List<TimeWindow> availableTimeWindows;
    private Set<WeatherType> availableWeathers;
    private Set<String> locations;

    public static class TimeWindow implements Serializable {
        private static final long serialVersionUID = 1L;

        int startHour;
        int endHour;

        public TimeWindow(int startHour, int endHour) {
            this.startHour = startHour;
            this.endHour = endHour;
        }

        public boolean isTimeWithin(int currentHour) {
            if (startHour <= endHour) {
                return currentHour >= startHour && currentHour < endHour;
            } else {
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
        super(name);
        this.rarity = rarity;
        this.availableSeasons = availableSeasons;
        this.availableTimeWindows = availableTimeWindows;
        this.availableWeathers = availableWeathers;
        this.locations = locations;
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
        if (!locations.contains(currentLocation)) return false;
        if (!availableSeasons.contains(SeasonType.ANY) && !availableSeasons.contains(currentSeason)) return false;
        if (!availableWeathers.contains(WeatherType.ANY) && !availableWeathers.contains(currentWeather)) return false;

        if (availableTimeWindows.isEmpty() || 
            (availableTimeWindows.size() == 1 && availableTimeWindows.get(0).startHour == 0 && availableTimeWindows.get(0).endHour == 0)) {
            return true;
        }

        for (TimeWindow window : availableTimeWindows) {
            if (window.isTimeWithin(currentHour)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getSellPrice() {
        double numSeasons = availableSeasons.contains(SeasonType.ANY) ? 4.0 : availableSeasons.size();
        double totalHours = 0;

        if (availableTimeWindows.isEmpty() ||
            (availableTimeWindows.size() == 1 && availableTimeWindows.get(0).startHour == 0 && availableTimeWindows.get(0).endHour == 0)) {
            totalHours = 24.0;
        } else {
            for (TimeWindow window : availableTimeWindows) {
                totalHours += window.getDurationHours();
            }
        }

        double numWeatherVariations = availableWeathers.contains(WeatherType.ANY) ? 2.0 : availableWeathers.size();
        double numLocations = locations.size();

        double cValue;
        switch (rarity) {
            case COMMON: cValue = 10.0; break;
            case REGULAR: cValue = 5.0; break;
            case LEGENDARY: cValue = 25.0; break;
            default: cValue = 1.0; break;
        }

        double price = (4.0 / numSeasons) * (24.0 / totalHours) * (2.0 / numWeatherVariations) * (4.0 / numLocations) * cValue;
        return (int) Math.round(price);
    }

    @Override
    public void use() {
        System.out.println("Ate " + getName() + ". +1 Energy.");
    }

    @Override
    public int getEnergyRestoration() {
        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fish)) return false;
        if (!super.equals(o)) return false;
        Fish fish = (Fish) o;
        return rarity == fish.rarity;
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
