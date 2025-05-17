package com.spakborhills;
public class SeasonController {
    private Season season;

    public SeasonController() {
        this.season = new Season();
    }

    public void nextDay() {
        season.nextDay();
    }

    public void resetSeason() {
        season = new Season(); // optional
    }

    public SeasonType getCurrentSeason() {
        return season.getCurrentSeason();
    }

    public int getCurrentDayInSeason() {
        return season.getCurrentDayInSeason();
    }

    public boolean isSeasonChangeDay() {
        return season.getCurrentDayInSeason() == 1;
    }
}
