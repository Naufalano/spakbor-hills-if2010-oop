package com.spakbor.cls.core; 

import com.spakbor.cls.items.Food;
import com.spakbor.system.Time;
import java.io.Serializable;

public class OngoingCooking implements Serializable{
    private static final long serialVersionUID = 1L;
    private Recipe recipeBeingCooked;
    private Food resultItemPrototype;
    private int startDay;
    private int startHour;
    private int startMinute;
    private int durationMinutes;
    private boolean isReadyToClaim;
    private boolean isClaimed;

    public OngoingCooking(Recipe recipe, Food resultItemPrototype, int startDay, Time startTime) {
        this.recipeBeingCooked = recipe;
        this.resultItemPrototype = resultItemPrototype;
        this.startDay = startDay;
        this.startHour = startTime.getHour();
        this.startMinute = startTime.getMinute();
        this.durationMinutes = Recipe.COOKING_DURATION_MINUTES;
        this.isReadyToClaim = false;
        this.isClaimed = false;
    }

    public Recipe getRecipeBeingCooked() {
        return recipeBeingCooked;
    }

    public Food getResultItemPrototype() {
        return resultItemPrototype;
    }

    public boolean isReadyToClaim() {
        return isReadyToClaim;
    }

    public void setReadyToClaim(boolean readyToClaim) {
        isReadyToClaim = readyToClaim;
    }

    public boolean isClaimed() {
        return isClaimed;
    }

    public void setClaimed(boolean claimed) {
        isClaimed = claimed;
    }

    /**
     * Memeriksa apakah makanan sudah matang berdasarkan waktu game saat ini.
     * @param currentTotalDaysPassed Total hari yang telah berlalu dalam game.
     * @param currentTime Objek Time saat ini.
     * @return true jika makanan sudah matang, false jika tidak.
     */
    public boolean checkIsReady(int currentTotalDaysPassed, Time currentTime) {
        if (isReadyToClaim || isClaimed) {
            return isReadyToClaim;
        }

        long cookingStartTimeInMinutes = ((long)startDay * 24 * 60) + (startHour * 60) + startMinute;
        long currentTimeInMinutes = ((long)currentTotalDaysPassed * 24 * 60) + (currentTime.getHour() * 60) + currentTime.getMinute();

        if (currentTimeInMinutes >= cookingStartTimeInMinutes + this.durationMinutes) {
            this.isReadyToClaim = true;
            return true;
        }
        return false;
    }

    public String getCookedItemName() {
        return resultItemPrototype.getName();
    }
}
