package services;

import interfaces.IWeatherObserver;

import java.util.ArrayList;
import java.util.List;

public class WeatherService {
    private static WeatherService instance;
    private boolean isRainy;
    private List<IWeatherObserver> observers;

    private WeatherService() {
        this.observers = new ArrayList<>();
        this.isRainy = false;
    }

    public static synchronized WeatherService getInstance() {
        if (instance == null) {
            instance = new WeatherService();
        }
        return instance;
    }

    public void addObserver(IWeatherObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(IWeatherObserver observer) {
        observers.remove(observer);
    }

    public void generateWeather() {
        this.isRainy = Math.random() < 0.5; // 50% chance of rain
        System.out.println("[WeatherService] Weather generated: " + (isRainy ? "RAINY" : "CLEAR"));
        notifyObservers();
    }

    public boolean isRainy() {
        return isRainy;
    }

    private void notifyObservers() {
        for (IWeatherObserver observer : observers) {
            observer.onWeatherChanged(isRainy);
        }
    }
}
