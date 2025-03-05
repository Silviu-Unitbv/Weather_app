package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class UserSerevice extends User {
    private static final String USER_FILE_PATH = "C:\\\\Info probleme\\\\INFO_AN_2\\\\SEM_1\\\\MIP\\\\WeatherApp\\\\WeatherServer\\\\src\\\\data\\\\client.json";

    public static List<User> readUsersFromFile() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(new File(USER_FILE_PATH), new TypeReference<>() {
        });
    }

    public static void writeUsersToFile(List<User> users) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(USER_FILE_PATH), users);
    }

    public static User authenticateUser(String username, String password) throws IOException {
        List<User> users = readUsersFromFile();
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public static void registerUser(String username, String password, String role, String location) throws IOException {
        List<User> users = readUsersFromFile();
        if (role == null || role.isEmpty()) {
            role = "user";
        }
        users.add(new User(username, password, role, location));
        writeUsersToFile(users);
    }

    public static void updateUserLocation(String username, String newLocation,double latitude, double longitude) {
        try {
            List<User> users = readUsersFromFile();
            for (User user : users) {
                if (user.getUsername().equals(username)) {
                    user.setLocation(newLocation);
                    user.setLatitude(latitude);
                    user.setLongitude(longitude);
                    writeUsersToFile(users);
                    return;
                }
            }

            System.out.println("User not found!");
        } catch (IOException e) {
            System.err.println("Error updating user location: " + e.getMessage());
        }
    }
}
