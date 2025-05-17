package com.spakborhills;

public class TimeController {
    private Time gameTime;
    private long lastRealTimeUpdate; // in milliseconds

    public TimeController() {
        this.gameTime = new Time();
        this.lastRealTimeUpdate = System.currentTimeMillis();
    }

    public void updateTime() {
        long now = System.currentTimeMillis();
        long delta = now - lastRealTimeUpdate; // ms
        int gameMinutesToAdvance = (int) (delta / 1000) * 5; // 1 real second = 5 game minutes
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
