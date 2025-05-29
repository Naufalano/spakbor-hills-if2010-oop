package com.spakbor.cls.core;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;


import com.spakbor.cls.items.*;
import com.spakbor.data.*;
import com.spakbor.enums.*;

public class PlantedCrop implements Serializable {
    private static final long serialVersionUID = 1L;

    private String resultingCropName;
    private Crop cropPrototype;
    private int yieldAmountPerHarvest;
    private int daysToMature;
    private int growthDays;
    private boolean wateredToday;
    private List<SeasonType> growableSeasons;
    private boolean canSurviveOutOfSeason;

    public PlantedCrop(Seeds seed) {
        this.resultingCropName = seed.getName().replace(" Seeds", "").replace(" Seed", "");
        this.cropPrototype = CropDataRegistry.getCropByName(this.resultingCropName);
        if (this.cropPrototype == null) {
            System.err.println("Tidak dapat menemukan prototipe Crop untuk '" + this.resultingCropName + "' dari benih '" + seed.getName() + "'. Tanaman mungkin tidak berfungsi dengan benar.");
            this.daysToMature = seed.getDaysToHarvest();
        } else {
            this.daysToMature = seed.getDaysToHarvest();
        }
        this.daysToMature = seed.getDaysToHarvest();
        this.growableSeasons = new ArrayList<>(seed.getPlantableSeasons());
        this.growthDays = 0;
        this.wateredToday = false;
        this.canSurviveOutOfSeason = false;

        Crop cropDefinition = CropDataRegistry.getCropByName(this.resultingCropName);
        if (cropDefinition != null) {
            this.yieldAmountPerHarvest = cropDefinition.getYieldAmount();
        } else {
            System.err.println("Warning: No crop definition found in registry for: " + this.resultingCropName +
                               ". Defaulting yield to 1.");
            this.yieldAmountPerHarvest = 1;
        }
    }

    public String getResultingCropName() {
        return resultingCropName;
    }

    public int getYieldAmountPerHarvest() {
        return yieldAmountPerHarvest;
    }

    /**
     * Attempts to grow the crop for one day.
     * Growth only occurs if watered and in the correct season.
     * @param currentSeason The current farm season.
     * @return true if the crop should be removed (e.g., died), false otherwise.
     */
    public boolean grow(SeasonType currentSeason) {
        if (!growableSeasons.contains(currentSeason) && !growableSeasons.contains(SeasonType.ANY)) {
            if (!canSurviveOutOfSeason) {
                System.out.println(resultingCropName + " ga bertahan saat " + currentSeason + " terus mati.");
                return true;
            } else {
                // System.out.println("Info: " + cropType.getName() + " is out of season and will not grow.");
                wateredToday = false;
                return false; 
            }
        }

        if (wateredToday) {
            if (growthDays < daysToMature) {
                growthDays++;
                System.out.println(resultingCropName + " tumbuh. Growth: " + growthDays + "/" + daysToMature);
            }
        }
        wateredToday = false; 
        return false;
    }

    public String getCropName() {
        return cropPrototype != null ? cropPrototype.getName() : this.resultingCropName;
    }

    public boolean isMature() {
        return growthDays >= daysToMature;
    }

    public Crop getCropType() {
        return cropPrototype;
    }

    public List<SeasonType> getGrowableSeasons() {
        return new ArrayList<>(growableSeasons);
    }

    public int getAmountPerHarvest() {
        return yieldAmountPerHarvest;
    }

    public void setWateredToday(boolean watered) {
        this.wateredToday = watered;
    }

    public boolean isWateredToday() {
        return wateredToday;
    }
}