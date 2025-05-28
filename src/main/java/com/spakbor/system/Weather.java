package com.spakbor.system;
import com.spakbor.enums.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Weather {
    private WeatherType todayWeather;
    private List<WeatherType> seasonWeather;
    private int dayIndex;

    public Weather() {
        this.dayIndex = 0;
        this.seasonWeather = generateWeather();
        this.todayWeather = seasonWeather.get(dayIndex);
    }

    private List<WeatherType> generateWeather() {
        List<WeatherType> weatherForecast = new ArrayList<>(Collections.nCopies(10, WeatherType.SUNNY));

        weatherForecast.set(2, WeatherType.RAINY);
        weatherForecast.set(7, WeatherType.RAINY);

        Random random = new Random();
        for (int i = 0; i < weatherForecast.size(); i++) {
            if (weatherForecast.get(i) == WeatherType.SUNNY && random.nextDouble() < 0.2) {
                weatherForecast.set(i, WeatherType.RAINY);
            }
        }

        Collections.shuffle(weatherForecast);
        return weatherForecast;
    }

    public void nextDay() {
        dayIndex = (dayIndex + 1) % 10;
        todayWeather = seasonWeather.get(dayIndex);
    }

    public WeatherType getTodayWeather() {
        return todayWeather;
    }

    public void resetWeather() {
        dayIndex = 0;
        seasonWeather = generateWeather();
        todayWeather = seasonWeather.get(dayIndex);
    }
}
