package org.example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private boolean loggedIn = false; // Track login state

    public void start() {
        try {
            Socket socket = new Socket("localhost", 5050);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Scanner scanner = new Scanner(System.in);

            while (true) {
                if(!loggedIn)
                {
                    System.out.println("\nAvailable commands:");
                    System.out.println("1. Login/Signup:");
                    System.out.println("   - Type 'login <username> <password>' to log in.");
                    System.out.println("   - Type 'signup <username> <password> <location>' to create an account.");
                    System.out.println("2. Exit:");
                    System.out.println("   - Type 'exit' to leave the program.");
                }

                if (loggedIn) {
                    System.out.println("1. Weather Information:");
                    System.out.println("   - Type 'weather <location>' or 'weather <latitude> <longitude>' to get the weather.");
                    System.out.println("2. Set Location:");
                    System.out.println("   - Type 'setL <new_location>' to update your default location.");
                    System.out.println("3. Admin Options (if logged in as admin):");
                    System.out.println("   - Type 'addWeatherData <json_file_path>' to add weather data from a JSON file.");
                    System.out.println("4. Logout:");
                    System.out.println("   - Type 'logout' to log out.");
                    System.out.println("5. Exit:");
                    System.out.println("   - Type 'exit' to leave the program.");
                }

                System.out.print("Enter your command: ");
                String message = scanner.nextLine();
                MessageClass messageClass = new MessageClass();
                messageClass.message = message;

                out.writeObject(messageClass);

                Object read = in.readObject();
                if (read instanceof String) {
                    String response = (String) read;
                    System.out.println(response);

                    if (response.contains("Login successful")) {
                        loggedIn = true;
                    } else if (response.equalsIgnoreCase("You have been logged out.")) {
                        loggedIn = false;
                    }
                } else if (read instanceof StringBuilder) {
                    System.out.println(read);
                } else {
                    System.err.println("Unexpected response from server of type: " + read.getClass().getName());
                }

                if (message.equalsIgnoreCase("logout")) {
                    loggedIn = false;
                }

                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
