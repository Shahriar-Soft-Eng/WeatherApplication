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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.weatherapplication.ui.theme.WeatherApplicationTheme

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
fun WeatherApp() {
    var searchQuery by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var weatherData by remember { mutableStateOf(getMockWeatherData()) } // Replace with actual data-fetching logic

    Scaffold(
        topBar = {
            SearchBar(
                searchQuery = searchQuery,
                onSearch = { query ->
                    if (query.isBlank()) {
                        errorMessage = "Please enter a city name."
                    } else {
                        // Replace this logic with actual API call
                        weatherData = getMockWeatherData() // Mock data for demonstration
                        errorMessage = ""
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
                if (errorMessage.isNotBlank()) {
                    ErrorMessage(errorMessage)
                } else {
                    TodayWeather(weatherData.todayWeather)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "7-Day Forecast",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                    ForecastList(weatherData.weeklyForecast)
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

// Mock Data
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

fun getMockWeatherData(): WeatherData {
    return WeatherData(
        todayWeather = TodayWeather(25, "Sunny", 60, 10),
        weeklyForecast = listOf(
            DayWeather("Monday", 20, 25, "Cloudy"),
            DayWeather("Tuesday", 22, 27, "Sunny"),
            DayWeather("Wednesday", 18, 23, "Rainy"),
            DayWeather("Thursday", 19, 24, "Partly Cloudy"),
            DayWeather("Friday", 21, 26, "Thunderstorms"),
            DayWeather("Saturday", 23, 28, "Sunny"),
            DayWeather("Sunday", 20, 25, "Windy")
        )
    )
}

@Preview(showBackground = true)
@Composable
fun WeatherAppPreview() {
    WeatherApplicationTheme {
        WeatherApp()
    }
}
