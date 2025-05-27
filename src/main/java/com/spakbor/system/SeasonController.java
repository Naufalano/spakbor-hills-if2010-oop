package system;
import enums.*;

public class SeasonController {
    private Season season;
    private int totalDaysPassed; 

    public SeasonController() {
        this.season = new Season(); 
        this.totalDaysPassed = 0;
    }

    public void nextDay() {
        season.nextDay();
        totalDaysPassed++;
    }

    public SeasonType getCurrentSeason() {
        return season.getCurrentSeason();
    }

    public int getCurrentDayInSeason() {
        return season.getCurrentDayInSeason();
    }

    public boolean isSeasonChangeDay() {
        return season.getCurrentDayInSeason() == 1 && totalDaysPassed > 0;
    }

    public int getTotalDaysPassed() {
        return totalDaysPassed;
    }

    public int getTotalSeasonsPassed() {
        if (totalDaysPassed == 0) return 0;
        return (totalDaysPassed -1) / 10; 
    }
     public void resetSeason() { 
        this.season = new Season();
        this.totalDaysPassed = 0;
    }
}
