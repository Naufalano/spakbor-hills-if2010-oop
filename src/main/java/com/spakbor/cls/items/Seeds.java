package com.spakbor.cls.items;

import java.util.ArrayList;
import java.util.List;

import com.spakbor.enums.SeasonType;

public class Seeds extends Item{
    private static final long serialVersionUID = 1L;

    private List<SeasonType> plantableSeasons;
    private int daysToHarvest;
    private int buyPrice;

    public Seeds(String name, List<SeasonType> plantableSeasons, int daysToHarvest, int buyPrice) {
        super(name);
        this.plantableSeasons = new ArrayList<>(plantableSeasons);
        this.daysToHarvest = daysToHarvest;
        this.buyPrice = buyPrice;
    }

    public Seeds(String name, String season, int daysToHarvest, int buyPrice) {
        super(name);
        this.plantableSeasons = new ArrayList<>();
        try {
            if (season.equalsIgnoreCase("ANY")) {
                this.plantableSeasons.add(SeasonType.SPRING);
                this.plantableSeasons.add(SeasonType.SUMMER);
                this.plantableSeasons.add(SeasonType.FALL);
            } else {
                for (String s : season.split(",\\s*")) {
                    this.plantableSeasons.add(SeasonType.valueOf(s.trim().toUpperCase()));
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        this.daysToHarvest = daysToHarvest;
        this.buyPrice = buyPrice;
    }

    @Override
    public int getSellPrice() {
        return buyPrice / 2;
    }

    @Override
    public void use() {
        // Penanaman dilakukan melalui mekanisme eksternal (tile / farming system)
    }

    public List<SeasonType> getPlantableSeasons() {
        return new ArrayList<>(plantableSeasons);
    }

    public boolean canBePlantedIn(SeasonType currentSeason) {
        return plantableSeasons.contains(currentSeason) || plantableSeasons.contains(SeasonType.ANY);
    }

    public int getDaysToHarvest() {
        return daysToHarvest;
    }

    public int getBuyPrice() {
        return buyPrice;
    }

    public String getDescription() {
        StringBuilder seasonsStr = new StringBuilder();
        if (plantableSeasons.contains(SeasonType.ANY) || 
            (plantableSeasons.contains(SeasonType.SPRING) &&
             plantableSeasons.contains(SeasonType.SUMMER) &&
             plantableSeasons.contains(SeasonType.FALL))) {
            seasonsStr.append("Any Season");
        } else {
            for (int i = 0; i < plantableSeasons.size(); i++) {
                seasonsStr.append(plantableSeasons.get(i).toString());
                if (i < plantableSeasons.size() - 1) {
                    seasonsStr.append(", ");
                }
            }
        }
        return "Benih untuk " + getName() + ". Tanam di musim: " + seasonsStr.toString() +
               ". Siap panen dalam " + daysToHarvest + " hari.";
    }
}
