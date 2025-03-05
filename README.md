# 🌦️ WeatherApp 🌈

Welcome to **WeatherApp**! ☁️ This project is a weather server and client application built with Java. With this app, you can get real-time weather data, register and log in, and interact with the weather system 🌍 through a server-client setup. The server handles weather data and user authentication, while the client lets you query weather info and perform different actions! 🌞❄️

## Features 🚀

- **User Authentication** 🔑: Sign up, log in, and manage your profile like a pro!
- **Weather Data** 🌡️: Get accurate weather information based on your location or coordinates!
- **Admin Power** 🛠️: Admins can upload weather data from a JSON file (yes, you're the boss)!
- **Location-based Weather** 📍: Set your location and retrieve weather details tailored just for you!
- **Client-Server Magic** 🧙‍♂️: The client connects to the server through socket connections to make it all happen!

## Architecture 🏗️

The project is divided into these awesome components:

### 🌍 Server
- Listens for incoming client connections on port `5050` 🌐.
- Loads weather data from the file `weather.json` 📄.
- Authenticates users and processes their commands like a weather wizard ✨.
- Handles weather queries, user registration, and more! 📝

### 💻 Client
- Connects to the server to let you interact with the weather magic 💬.
- Provides a command-line interface (CLI) to log in, sign up, fetch weather, and more! 📲

### 🧑‍💻 Data Models
- **WeatherData** 🌦️: Holds all the weather info for a location—current weather and forecast 🌤️.
- **User** 👤: Keeps track of user info like username, password, role, and location 🏙️.
- **Forecast** 📅: Stores the forecast for upcoming days—because the future is always sunny! 😎

## Dependencies 📦

- **Jackson** 🍃: Used for processing JSON (serialization & deserialization) to make everything smoother!
  - `com.fasterxml.jackson.core:jackson-databind`
  - `com.fasterxml.jackson.core:jackson-annotations`
