package cls.core;
import cls.items.*;
import data.*;
import enums.*;

public class PlantedCrop {
    private String resultingCropName;
    private int yieldAmountPerHarvest;
    private int daysToMature;
    private int growthDays;
    private boolean wateredToday;
    private String seasonToGrowIn;
    private boolean canSurviveOutOfSeason;

    public PlantedCrop(Seeds seed) {
        this.resultingCropName = seed.getName().replace(" Seeds", "");
        this.daysToMature = seed.getDaysToHarvest();
        this.seasonToGrowIn = seed.getSeason().toUpperCase();
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
        if (!this.seasonToGrowIn.equalsIgnoreCase("ANY") && !this.seasonToGrowIn.equals(currentSeason.toString().toUpperCase())) {
            if (!canSurviveOutOfSeason) {
                System.out.println("Info: " + resultingCropName + " cannot survive in " + currentSeason + " and has withered.");
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
                System.out.println("Info: " + resultingCropName + " grew. Growth: " + growthDays + "/" + daysToMature);
            }
        } else {
            // System.out.println("Info: " + cropType.getName() + " was not watered and did not grow.");
        }
        wateredToday = false; 
        return false;
    }

    public boolean isMature() {
        return growthDays >= daysToMature;
    }

    public Crop getCropType() {
        Crop cropDefinition = CropDataRegistry.getCropByName(this.resultingCropName);
        return cropDefinition;
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