package system;
import enums.*;

public class Season {
    private SeasonType currentSeason;
    private int currentDayInSeason; // 1 - 10

    public Season() {
        this.currentSeason = SeasonType.SPRING;
        this.currentDayInSeason = 1;
    }

    public void nextDay() {
        currentDayInSeason++;
        if (currentDayInSeason > 10) {
            currentDayInSeason = 1;
            changeToNextSeason();
        }
    }

    private void changeToNextSeason() {
        switch (currentSeason) {
            case SPRING:
                currentSeason = SeasonType.SUMMER;
                break;
            case SUMMER:
                currentSeason = SeasonType.FALL;
                break;
            case FALL:
                currentSeason = SeasonType.WINTER;
                break;
            case WINTER:
                currentSeason = SeasonType.SPRING;
                break;
        }
    }

    public SeasonType getCurrentSeason() {
        return currentSeason;
    }

    public int getCurrentDayInSeason() {
        return currentDayInSeason;
    }
}
