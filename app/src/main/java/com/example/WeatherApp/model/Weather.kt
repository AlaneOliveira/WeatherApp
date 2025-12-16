package com.example.WeatherApp.model

import android.graphics.Bitmap

//Essa classe contém as informações climáticas usadas na UI. É uma
//simplificação das classes da API climática
data class Weather ( // usando data na classe para armazenar dados
    val date: String,
    val desc: String,
    val temp: Double,
    val imgUrl: String,
    var bitmap: Bitmap? = null) {
    companion object {
        val LOADING = Weather(date = "LOADING", desc = "LOADING",
            temp = -1.0, imgUrl = "LOADING", bitmap = null )
    }
}