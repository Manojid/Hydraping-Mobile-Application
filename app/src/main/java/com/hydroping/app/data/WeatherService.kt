package com.hydroping.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class WeatherData(
    val temperatureC: Double,
    val weatherCode: Int,
    val conditionTitle: String,
    val iconEmoji: String,
    val extraHydrationMl: Int,
    val advisoryText: String
)

object WeatherService {

    private const val TIMEOUT_MS = 6000

    /**
     * Fetches live real-time weather from Open-Meteo API (Free, high accuracy, keyless).
     * Defaults to Delhi/Central latitude/longitude coordinates (28.61, 77.21) or provided coordinates.
     */
    suspend fun fetchCurrentWeather(
        latitude: Double = 28.6139,
        longitude: Double = 77.2090
    ): WeatherData = withContext(Dispatchers.IO) {
        try {
            val urlString = "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current_weather=true"
            val url = URL(urlString)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = TIMEOUT_MS
                readTimeout = TIMEOUT_MS
                setRequestProperty("Accept", "application/json")
            }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val responseText = connection.inputStream.bufferedReader().use(BufferedReader::readText)
                val json = JSONObject(responseText)
                if (json.has("current_weather")) {
                    val cw = json.getJSONObject("current_weather")
                    val temp = cw.getDouble("temperature")
                    val code = cw.getInt("weathercode")
                    return@withContext parseWeatherData(temp, code)
                }
            }
        } catch (_: Exception) {
            // Fallback graceful offline / default estimation
        }
        return@withContext getFallbackWeatherData()
    }

    fun parseWeatherData(tempC: Double, weatherCode: Int): WeatherData {
        val (title, emoji) = when (weatherCode) {
            0 -> Pair("Clear Sky", "☀️")
            1, 2 -> Pair("Mainly Clear", "🌤️")
            3 -> Pair("Overcast", "☁️")
            45, 48 -> Pair("Foggy", "🌫️")
            51, 53, 55, 61, 63, 65 -> Pair("Rainy", "🌧️")
            71, 73, 75 -> Pair("Snow", "❄️")
            95, 96, 99 -> Pair("Thunderstorm", "⚡")
            else -> Pair("Fair Weather", "⛅")
        }

        val (extraMl, advisory) = when {
            tempC >= 35.0 -> Pair(
                500,
                "Intense Heat ($tempC°C): +500 ml boost applied to protect against dehydration."
            )
            tempC >= 30.0 -> Pair(
                350,
                "Warm Climate ($tempC°C): +350 ml booster added for optimal hydration."
            )
            tempC >= 25.0 -> Pair(
                200,
                "Mildly Warm ($tempC°C): +200 ml extra water suggested today."
            )
            tempC <= 10.0 -> Pair(
                100,
                "Cold Dry Air ($tempC°C): +100 ml suggested to stay moisturized."
            )
            else -> Pair(
                0,
                "Comfortable Temp ($tempC°C): Standard daily hydration pacing active."
            )
        }

        return WeatherData(
            temperatureC = tempC,
            weatherCode = weatherCode,
            conditionTitle = title,
            iconEmoji = emoji,
            extraHydrationMl = extraMl,
            advisoryText = advisory
        )
    }

    fun getFallbackWeatherData(): WeatherData {
        return WeatherData(
            temperatureC = 29.0,
            weatherCode = 1,
            conditionTitle = "Sunny & Warm",
            iconEmoji = "☀️",
            extraHydrationMl = 300,
            advisoryText = "Warm Climate (29.0°C): +300 ml climate booster applied to your daily goal."
        )
    }
}
