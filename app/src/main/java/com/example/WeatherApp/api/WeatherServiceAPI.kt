package com.example.WeatherApp.api

import com.example.WeatherApp.BuildConfig
import com.example.WeatherApp.api.WeatherServiceAPI.Companion.API_KEY
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherServiceAPI {
    /*Essa interface define as URLs usadas para acessar a API climática, neste caso
somente a usada para buscar cidades por nome ou coordenadas*/
    companion object {
        const val BASE_URL = "https://api.weatherapi.com/v1/"
        const val API_KEY = BuildConfig.WEATHER_API_KEY
    }
    // Procura a localização baseado no nome ou coordenadas
    @GET("search.json?key=$API_KEY&lang=pt_br")
    fun search(@Query("q") query: String): Call<List<APILocation>?>

    // Esse novo endpoint retorna as condições climáticas atuais da cidade.
    @GET("current.json?key=$API_KEY&lang=pt")
    fun weather(@Query("q") query: String): Call<APICurrentWeather?>
}

