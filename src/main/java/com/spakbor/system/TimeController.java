package system;

public class TimeController {
    private Time gameTime;
    private long lastRealTimeUpdate;
    private boolean isPaused;

    public TimeController() {
        this.gameTime = new Time();
        this.lastRealTimeUpdate = System.currentTimeMillis();
        this.isPaused = false;
    }

    public void updateTime() {
        if (isPaused) {
            lastRealTimeUpdate = System.currentTimeMillis();
            return;
        }

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
        this.lastRealTimeUpdate = System.currentTimeMillis();
        this.isPaused = false;
    }

    public void pauseGameTime() {
        if (!this.isPaused) {
            this.lastRealTimeUpdate = System.currentTimeMillis();
            this.isPaused = true;
            System.out.println("[Sistem Waktu Game Di-pause]");
        }
    }

    public void resumeGameTime() {
        if (this.isPaused) {
            this.isPaused = false;
            this.lastRealTimeUpdate = System.currentTimeMillis();
            System.out.println("[Sistem Waktu Game Dilanjutkan]");
        }
    }

    public boolean isPaused() {
        return isPaused;
    }
}
