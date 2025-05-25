// Diasumsikan Season.java dan SeasonType.java sudah ada

public class SeasonController {
    private Season season;
    private int totalDaysPassed; // Untuk statistik

    public SeasonController() {
        this.season = new Season(); // Season.java mengelola hari dalam musim (1-10)
        this.totalDaysPassed = 0; // Mulai dari hari ke-0 sebelum hari pertama dimulai
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
        // Jika season.nextDay() mengubah musim, getCurrentDayInSeason() akan menjadi 1
        return season.getCurrentDayInSeason() == 1 && totalDaysPassed > 0;
    }

    public int getTotalDaysPassed() {
        return totalDaysPassed;
    }

    public int getTotalSeasonsPassed() {
        if (totalDaysPassed == 0) return 0;
        // Asumsikan Season.java memiliki DAYS_PER_SEASON atau logika serupa
        // Jika Season.java menggunakan 10 hari per musim:
        return (totalDaysPassed -1) / 10; // Musim ke-0, 1, 2, dst.
                                         // (totalDaysPassed -1) karena hari ke-1 sampai 10 adalah musim pertama (indeks 0)
    }
     public void resetSeason() { // Opsional, jika game di-reset
        this.season = new Season();
        this.totalDaysPassed = 0;
    }
}
