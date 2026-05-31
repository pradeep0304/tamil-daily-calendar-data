package com.example.network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

data class WeatherResponse(
    val current_weather: CurrentWeather?,
    val daily: DailyWeather?
)

data class CurrentWeather(
    val temperature: Double,
    val weathercode: Int
)

data class DailyWeather(
    val precipitation_sum: List<Double>?
)

interface WeatherApiService {
    @GET("v1/forecast?current_weather=true&daily=precipitation_sum&timezone=auto")
    suspend fun getWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double
    ): WeatherResponse
}

object WeatherClient {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val service: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(WeatherApiService::class.java)
    }
}
