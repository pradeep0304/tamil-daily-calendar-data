package com.example

import com.example.network.WeatherClient
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*

class WeatherApiTest {
    @Test
    fun testWeatherApi() = runBlocking {
        try {
            val response = WeatherClient.service.getWeather(13.08, 80.27)
            println("API Response: $response")
            assertNotNull(response.current_weather)
            assertNotNull(response.current_weather?.temperature)
        } catch (e: Exception) {
            e.printStackTrace()
            fail("API Call failed: ${e.message}")
        }
    }
}
