public class TimeController {
    private Time gameTime;
    private long lastRealTimeUpdate; // in milliseconds

    public TimeController() {
        this.gameTime = new Time();
        this.lastRealTimeUpdate = System.currentTimeMillis();
    }

    // In TimeController.java
    public void updateTime() {
        long now = System.currentTimeMillis();
        long delta = now - lastRealTimeUpdate; // ms
        // 1 real second (1000ms) = 5 game minutes
        int gameMinutesToAdvance = (int) (delta / 1000.0 * 5.0); // Use floating point for intermediate calc
        if (gameMinutesToAdvance > 0) {
            gameTime.advanceMinutes(gameMinutesToAdvance);
            lastRealTimeUpdate = now; // Update lastRealTimeUpdate only when time has advanced
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
