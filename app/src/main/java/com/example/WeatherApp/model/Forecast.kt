package com.example.WeatherApp.model

// usamos Forecast para exibir as informação de previsão do tempo na UI
data class Forecast (
    val date: String, val weather: String,
    val tempMin: Double, val tempMax: Double, val imgUrl: String,
)