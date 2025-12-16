package com.example.WeatherApp.api

import com.example.WeatherApp.model.Weather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// Classe que representa a resposta da API
data class APICurrentWeather(
    var location: APILocation? = null,
    var current: APIWeather? = null
)

// Extensão para converter para o modelo usado na UI
fun APICurrentWeather.toWeather(): Weather {
    return Weather(
        date = current?.last_updated ?: "...",
        desc = current?.condition?.text ?: "...",
        temp = current?.temp_c ?: -1.0,
        imgUrl = "https:" + (current?.condition?.icon ?: ""),
        bitmap = null
    )
}

