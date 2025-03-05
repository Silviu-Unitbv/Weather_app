package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ClientThread extends Thread {
    private static final ConcurrentHashMap<Socket, User> userSessions = new ConcurrentHashMap<>();
    public final Socket client;
    public ObjectInputStream in;
    public ObjectOutputStream out;
    private final List<WeatherData> weatherData;

    public ClientThread(Socket client, List<WeatherData> weatherData) {
        this.client = client;
        this.weatherData = weatherData;
        try {
            this.in = new ObjectInputStream(client.getInputStream());
            this.out = new ObjectOutputStream(client.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                MessageClass message = (MessageClass) this.in.readObject();
                System.out.println("Received message: " + message.message);

                if (message.message.startsWith("login")) {
                    String[] loginData = message.message.split(" ");
                    String username = loginData[1];
                    String password = loginData[2];

                    User user = UserSerevice.authenticateUser(username, password);
                    if (user != null) {
                        userSessions.put(this.client, user);
                        this.out.writeObject("Login successful! Role: " + user.getRole());
                    } else {
                        this.out.writeObject("Invalid username or password.");
                    }
                }

                else if (message.message.startsWith("signup")) {

                    String[] signupData = message.message.split(" ");
                    String username = signupData[1];
                    String password = signupData[2];
                    String location = signupData[3];

                    User existingUser = UserSerevice.authenticateUser(username, password);
                    if (existingUser != null) {
                        this.out.writeObject("Username already exists. Please choose another.");
                    } else {
                        UserSerevice.registerUser(username, password, "user", location);
                        this.out.writeObject("Sign up successful! You can now log in.");
                    }
                }
                else if (message.message.startsWith("addWeatherData")) {
                    User authenticatedUser = userSessions.get(this.client);
                    if (authenticatedUser == null) {
                        this.out.writeObject("You must login first.");
                    } else if (!"admin".equalsIgnoreCase(authenticatedUser.getRole())) {
                        this.out.writeObject("Permission denied. Only admins can add weather data.");
                    } else {
                        String[] parts = message.message.split(" ", 2);
                        if (parts.length < 2) {
                            this.out.writeObject("Please provide the path to the JSON file.");
                        } else {
                            String jsonFilePath = parts[1];
                            try {
                                List<WeatherData> newWeatherData = WeatherData.loadFromJson(jsonFilePath);

                                synchronized (weatherData) {
                                    weatherData.addAll(newWeatherData);

                                    ObjectMapper objectMapper = new ObjectMapper();
                                    objectMapper.writeValue(new File("C:\\\\Info probleme\\\\INFO_AN_2\\\\MIP\\\\WeatherApp\\\\WeatherServer\\\\src\\\\data\\\\weather.json"), weatherData);
                                }

                                this.out.writeObject("Weather data added successfully from file: " + jsonFilePath);
                            } catch (IOException e) {
                                this.out.writeObject("Error reading JSON file: " + e.getMessage());
                            } catch (Exception e) {
                                this.out.writeObject("Invalid JSON format: " + e.getMessage());
                            }
                        }
                    }
                }

                else if (message.message.startsWith("weather")) {
                    User authenticatedUser = userSessions.get(this.client);
                    if (authenticatedUser == null) {
                        this.out.writeObject("You must login first.");
                    } else {
                        String[] parts = message.message.split(" ");
                        String location = null;
                        Double latitude = null;
                        Double longitude = null;

                        if (parts.length >= 2) {
                            try {
                                if (parts.length == 3 && isNumeric(parts[1]) && isNumeric(parts[2])) {
                                    latitude = Double.parseDouble(parts[1]);
                                    longitude = Double.parseDouble(parts[2]);
                                } else {
                                    location = message.message.substring(message.message.indexOf(" ") + 1).trim();
                                }
                            } catch (NumberFormatException e) {
                                this.out.writeObject("Invalid latitude or longitude format. Please provide numeric values.");
                                return;
                            }
                        }


                        if (location != null) {
                            WeatherData weather = WeatherData.findWeatherData(location, weatherData);
                            if (weather != null) {
                                this.out.writeObject(weather.toString());
                            } else {
                                WeatherData nearestWeather = WeatherData.findNearestWeather(location, authenticatedUser.getLatitude(), authenticatedUser.getLongitude(), weatherData);
                                if (nearestWeather != null) {
                                    this.out.writeObject("No exact data for the specified location. Nearest location:\n" + nearestWeather);
                                } else {
                                    this.out.writeObject("No weather data available for this location or nearby.");
                                }
                            }
                        } else if (latitude != null) {
                            WeatherData nearestWeather = WeatherData.findNearestWeather(null, latitude, longitude, weatherData);
                            if (nearestWeather != null) {
                                this.out.writeObject("Nearest location to the specified coordinates:\n" + nearestWeather);
                            } else {
                                this.out.writeObject("No weather data available near these coordinates.");
                            }
                        }
                    }
                }

                else if (message.message.startsWith("setL")) {
                    String[] parts = message.message.split(" ", 2);
                    String newLocation = parts.length > 1 ? parts[1].trim() : null;

                    if (newLocation != null && !newLocation.isEmpty()) {
                        User authenticatedUser = userSessions.get(this.client);
                        if (authenticatedUser != null) {
                            WeatherData weather = WeatherData.findWeatherData(newLocation, weatherData);
                            if (weather != null) {
                                double latitude = weather.getLatitude();
                                double longitude = weather.getLongitude();

                                UserSerevice.updateUserLocation(authenticatedUser.getUsername(), newLocation, latitude, longitude);
                                this.out.writeObject("Location updated successfully to " + newLocation + " (Lat: " + latitude + ", Lon: " + longitude + ").");
                            } else {
                                this.out.writeObject("Location '" + newLocation + "' not found in weather data. Unable to update location.");
                            }
                        } else {
                            this.out.writeObject("You must login first.");
                        }
                    } else {
                        this.out.writeObject("Please provide a valid location name.");
                    }
                }

                else if (message.message.equalsIgnoreCase("logout")) {
                    User user = userSessions.get(this.client);
                    if (user != null) {
                        user.setLocation(null);
                        this.out.writeObject("You have been logged out, but your session is still active.");
                    } else {
                        this.out.writeObject("No active session found to log out from.");
                    }
                }
                else if(message.message.equalsIgnoreCase("exit")){
                    this.out.writeObject("Exiting application.");
                    break;
                }

                else {
                    this.out.writeObject("Invalid command. Check available commands.");
                }
            }
        } catch (IOException e) {
            System.err.println("Client disconnected: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Invalid message format: " + e.getMessage());
        } finally {
            try {
                userSessions.remove(this.client);
                if (this.in != null) this.in.close();
                if (this.out != null) this.out.close();
                this.client.close();
                System.out.println("Client connection closed.");
            } catch (IOException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}