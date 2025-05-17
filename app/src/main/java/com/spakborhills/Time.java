package com.spakborhills;

public class Time {
    private int hour;   // 0 - 23
    private int minute; // 0 - 59

    public Time() {
        this.hour = 6; // Start at 06:00
        this.minute = 0;
    }

    public void advanceMinutes(int minutes) {
        minute += minutes;
        while (minute >= 60) {
            minute -= 60;
            hour = (hour + 1) % 24;
        }
    }

    public void reset() {
        hour = 6;
        minute = 0;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public String getTimeString() {
        return String.format("%02d:%02d", hour, minute);
    }
}
