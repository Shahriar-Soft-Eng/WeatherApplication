package com.example.weatherapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherapplication.ui.theme.WeatherApplicationTheme
import androidx.compose.ui.Alignment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.FusedLocationProviderClient
import androidx.core.app.ActivityCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherApplicationTheme {
                WeatherApp()
            }
        }
    }
}
@Composable
fun GetUserLocation(onLocationReceived: (Location) -> Unit) {
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Permission granted, get the current location
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        onLocationReceived(it)
                    }
                }
            } else {
                // Permission denial
            }
        }
    )

    LaunchedEffect(Unit) {
        // Check and request location permission
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Already have permission, get the location
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    onLocationReceived(it)
                }
            }
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}
@Composable
fun WeatherApp() {
    var searchQuery by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var weatherData by remember { mutableStateOf<WeatherData?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var currentLocation by remember { mutableStateOf<Location?>(null) }

    // Get the user's current location
    GetUserLocation { location ->
        currentLocation = location
        // Fetch weather for the current location
        fetchWeatherDataFromGeoLoc(
            lat = "${location.latitude}",
            lon = "${location.longitude}",
            onSuccess = { data ->
                weatherData = data
                errorMessage = ""
                isLoading = false
            },
            onError = { error ->
                errorMessage = error
                isLoading = false
            }
        )
    }

    Scaffold(
        topBar = {
            SearchBar(
                searchQuery = searchQuery,
                onSearch = { query ->
                    if (query.isBlank()) {
                        errorMessage = "Please enter a city name."
                    } else {
                        isLoading = true
                        fetchWeatherData(
                            cityName = query,
                            onSuccess = { data ->
                                weatherData = data
                                errorMessage = ""
                                isLoading = false
                            },
                            onError = { error ->
                                errorMessage = error
                                isLoading = false
                            }
                        )
                    }
                },
                onQueryChange = { newQuery -> searchQuery = newQuery },
                modifier = Modifier.fillMaxWidth()
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else if (errorMessage.isNotBlank()) {
                    ErrorMessage(errorMessage)
                } else if (weatherData != null) {
                    TodayWeather(weatherData!!.todayWeather)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "7-Day Forecast",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                    ForecastList(weatherData!!.weeklyForecast)
                }
            }
        }
    )
}


@Composable
fun SearchBar(
    searchQuery: String,
    onSearch: (String) -> Unit,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(8.dp)
    ) {
        BasicTextField(
            value = searchQuery,
            onValueChange = { newValue -> onQueryChange(newValue) },
            modifier = Modifier.weight(1f),
            decorationBox = { innerTextField ->
                Box(
                    Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                ) {
                    if (searchQuery.isEmpty()) {
                        Text("Enter city name")
                    }
                    innerTextField()
                }
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = { onSearch(searchQuery) }) {
            Text("Search")
        }
    }
}

@Composable
fun TodayWeather(todayWeather: TodayWeather) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Today's Weather",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Temperature: ${todayWeather.temperature}°C")
        Text("Condition: ${todayWeather.condition}")
        Text("Humidity: ${todayWeather.humidity}%")
        Text("Wind Speed: ${todayWeather.windSpeed} km/h")
    }
}

@Composable
fun ForecastList(weeklyForecast: List<DayWeather>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(weeklyForecast.size) { index ->
            val forecast = weeklyForecast[index]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("${forecast.date}: ${forecast.minTemp}°C - ${forecast.maxTemp}°C, ${forecast.description}")
            }
        }
    }
}

@Composable
fun ErrorMessage(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(16.dp)
    )
}

fun fetchWeatherData(
    cityName: String,
    onSuccess: (WeatherData) -> Unit,
    onError: (String) -> Unit
) {
    val apiKey = "3901e2132b90493a261b5a0037b9460e"
    val url = "https://api.openweathermap.org/data/2.5/forecast?q=$cityName&units=metric&appid=$apiKey"

    val requestQueue = Volley.newRequestQueue(AppContext.instance)

    val jsonObjectRequest = JsonObjectRequest(
        Request.Method.GET, url, null,
        { response ->
            try {
                val todayWeather = parseTodayWeather(response)
                val weeklyForecast = parseWeeklyForecast(response)
                onSuccess(WeatherData(todayWeather, weeklyForecast))
            } catch (e: Exception) {
                onError("Error parsing weather data")
            }
        },
        { error ->
            onError("Error fetching data: ${error.message}")
        }
    )

    requestQueue.add(jsonObjectRequest)
}

fun fetchWeatherDataFromGeoLoc(
    lat: String,
    lon: String,
    onSuccess: (WeatherData) -> Unit,
    onError: (String) -> Unit
) {
    val apiKey = "3901e2132b90493a261b5a0037b9460e"
    val url = "https://api.openweathermap.org/data/2.5/forecast?lat=$lat&lon=$lon&appid=$apiKey&units=metric"

    val requestQueue = Volley.newRequestQueue(AppContext.instance)

    val jsonObjectRequest = JsonObjectRequest(
        Request.Method.GET, url, null,
        { response ->
            try {
                val todayWeather = parseTodayWeather(response)
                val weeklyForecast = parseWeeklyForecast(response)
                onSuccess(WeatherData(todayWeather, weeklyForecast))
            } catch (e: Exception) {
                onError("Error parsing weather data")
            }
        },
        { error ->
            onError("Error fetching data: ${error.message}")
        }
    )

    requestQueue.add(jsonObjectRequest)
}
fun parseTodayWeather(response: JSONObject): TodayWeather {
    val main = response.getJSONArray("list").getJSONObject(0).getJSONObject("main")
    val weather = response.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0)

    return TodayWeather(
        temperature = main.getDouble("temp").toInt(),
        condition = weather.getString("description").capitalize(),
        humidity = main.getInt("humidity"),
        windSpeed = response.getJSONArray("list").getJSONObject(0).getJSONObject("wind").getDouble("speed").toInt()
    )
}

fun parseWeeklyForecast(response: JSONObject): List<DayWeather> {
    val forecastList = response.getJSONArray("list")
    val weeklyForecast = mutableListOf<DayWeather>()

    for (i in 0 until forecastList.length() step 8) {
        val item = forecastList.getJSONObject(i)
        val main = item.getJSONObject("main")
        val weather = item.getJSONArray("weather").getJSONObject(0)
        val date = item.getString("dt_txt").split(" ")[0]

        weeklyForecast.add(
            DayWeather(
                date = date,
                minTemp = main.getDouble("temp_min").toInt(),
                maxTemp = main.getDouble("temp_max").toInt(),
                description = weather.getString("description").capitalize()
            )
        )
    }
    return weeklyForecast
}

data class TodayWeather(
    val temperature: Int,
    val condition: String,
    val humidity: Int,
    val windSpeed: Int
)

data class DayWeather(
    val date: String,
    val minTemp: Int,
    val maxTemp: Int,
    val description: String
)

data class WeatherData(
    val todayWeather: TodayWeather,
    val weeklyForecast: List<DayWeather>
)