package com.example.WeatherApp.model

import com.google.android.gms.maps.model.LatLng

data class City(
    val name: String,
    //val weather: String? = null, // retirando já que não precisa buscar pelo nome
    val location: LatLng? = null
) {

}