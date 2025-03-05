package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server extends WeatherDataLoader{

    public void start(){
        try {
            System.out.println("Server started...");
            int PORT = 5050;
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {

                List<WeatherData> weatherData = WeatherDataLoader.loadData("C:\\\\Info probleme\\\\INFO_AN_2\\\\SEM_1\\\\MIP\\\\WeatherApp\\\\WeatherServer\\\\src\\\\data\\\\weather.json");

                if (weatherData == null || weatherData.isEmpty()) {
                    System.err.println("Weather data could not be loaded or is empty.");
                    return;
                } else {
                    System.out.println("Weather data loaded successfully: " + weatherData.size() + " entries.");
                }

                while (true) {
                    Socket received = serverSocket.accept();
                    new ClientThread(received, weatherData).start();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
