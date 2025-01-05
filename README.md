# Weather Application

## Project Description
A weather application built with Kotlin and Jetpack Compose that fetches current weather and a 7-day forecast for the user's location or a searched city using the OpenWeatherMap API. It supports offline mode by caching weather data locally.

## Project Screensort
![App Screenshot](app-screensort.png "App Main Screen")
---

## Project Setup Instructions

### Prerequisites
1. Android Studio installed (latest stable version recommended).
2. A working internet connection.
3. Basic knowledge of Kotlin and Android development.

### Steps to Set Up
1. Clone the repository:
   ```bash
   git clone <repository_url>
   cd <repository_name>
   ```
2. Open the project in Android Studio:
   - File > Open > Select the cloned folder.

3. Sync the Gradle files:
   - Click `Sync Now` in the notification bar or go to `File > Sync Project with Gradle Files`.

4. Ensure the required dependencies are installed in your `build.gradle` files:
   - Jetpack Compose
   - Volley for networking
   - Location Services
   
5. Update the package name if necessary:
   - Refactor > Rename > Update package name throughout the project.

---

## API Key Setup Instructions

### OpenWeatherMap API
1. Sign up for an account at [OpenWeatherMap](https://openweathermap.org/).
2. Generate an API key:
   - Go to your account dashboard.
   - Click on `API Keys`.
   - Create a new API key.
3. Add your API key to the project:
   - Open the file where the `fetchWeatherData` and `fetchWeatherDataFromGeoLoc` functions are implemented.
   - Replace `<your_api_key>` with your generated API key:
     ```kotlin
     val apiKey = "<your_api_key>"
     ```

---

## How to Run the Project

1. Open the project in Android Studio.
2. Connect your Android device or start an emulator.
3. Build and run the project:
   - Click the green "Run" button or press `Shift + F10`.
4. Allow location permissions on the device/emulator when prompted.
5. Use the search bar to find weather data for specific cities or wait for the app to fetch data for your current location.

---

## Limitations or Known Issues

1. **Offline Mode**:
   - If there is no cached data, the app cannot display weather information.
   - Cached data does not auto-update when coming back online.

2. **API Rate Limiting**:
   - The OpenWeatherMap API has rate limits for free accounts. Upgrade your plan if the app exceeds these limits.

3. **Location Permissions**:
   - The app requires location permissions to fetch weather for the current location. If permissions are denied, the feature will not work.

4. **Error Handling**:
   - Limited error messages are displayed for API errors and connection issues. Enhance error feedback for better UX.

5. **UI Scaling**:
   - Some UI elements may not scale properly on very small or large devices. Additional optimization is needed.

---

## Contributing
Feel free to fork this repository and submit pull requests for enhancements or bug fixes.

---

## License
This project is licensed under the MIT License. See the LICENSE file for details.

