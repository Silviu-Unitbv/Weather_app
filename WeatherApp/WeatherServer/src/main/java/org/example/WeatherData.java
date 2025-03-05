package org.example;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class WeatherData extends Forecast {
    private String currentWeather;
    private int currentTemp;
    private List<Forecast> forecast;
    public double latitude;
    public double longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private String location;

    public String getCurrentWeather() {
        return currentWeather;
    }

    public void setCurrentWeather(String currentWeather) {
        this.currentWeather = currentWeather;
    }

    public int getCurrentTemp() {
        return currentTemp;
    }

    public void setCurrentTemp(int currentTemp) {
        this.currentTemp = currentTemp;
    }

    public List<Forecast> getForecast() {
        return forecast;
    }

    public void setForecast(List<Forecast> forecast) {
        this.forecast = forecast;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Location: ").append(location).append("\n");
        sb.append("Current Weather: ").append(currentWeather).append("\n");
        sb.append("Current Temperature: ").append(currentTemp).append("°C\n");
        sb.append("Forecast:\n");
        for (Forecast f : forecast) {
            sb.append("  - Day: ").append(f.getDay())
                    .append(", Weather: ").append(f.getWeather())
                    .append(", Temp: ").append(f.getTemp()).append("°C\n");
        }
        return sb.toString();
    }


    public static WeatherData findWeatherData(String location, List<WeatherData> weatherData) {
        for (WeatherData wd : weatherData) {
            if (wd.getLocation().equalsIgnoreCase(location)) {
                return wd;
            }
        }
        return null;
    }

    public static WeatherData findNearestWeather(String location,double userLat, double userLon, List<WeatherData> weatherData) {
        WeatherData nearest = null;
        double minDistance = Double.MAX_VALUE;
        for (WeatherData wd : weatherData) {
            double distance = calculateDistance(userLat, userLon, wd.getLatitude(), wd.getLongitude());
            if (distance < minDistance) {
                minDistance = distance;
                nearest = wd;
            }
        }
        return nearest;
    }

    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public static List<WeatherData> loadFromJson(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(filePath), new TypeReference<List<WeatherData>>() {});
    }
}
