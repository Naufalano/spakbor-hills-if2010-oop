public class TimeController {
    private Time gameTime;
    private long lastRealTimeUpdate;

    public TimeController() {
        this.gameTime = new Time();
        this.lastRealTimeUpdate = System.currentTimeMillis();
    }

    public void updateTime() {
        long now = System.currentTimeMillis();
        long delta = now - lastRealTimeUpdate;
        int gameMinutesToAdvance = (int) (delta / 1000.0 * 5.0); // Use floating point for intermediate calc
        if (gameMinutesToAdvance > 0) {
            gameTime.advanceMinutes(gameMinutesToAdvance);
            lastRealTimeUpdate = now;
        }
    }

    public Time getGameTime() {
        return gameTime;
    }

    public String getFormattedTime() {
        return gameTime.getTimeString();
    }

    public void resetTime() {
        gameTime.reset();
    }
}
