package com.example.WeatherApp.dbfb

import com.example.WeatherApp.model.City
import com.google.android.gms.maps.model.LatLng

/*
* Essa classe é usada para serializar as cidades no Firebase Firestore. Ela
precisa ter construtor default vazio e atributos “setáveis” que podem ser nulos.
Também adicionamos métodos para transforma de/para model.City. */
class FBCity {
    var name : String? = null
    var lat : Double? = null
    var lng : Double? = null
    fun toCity(): City {
        val latlng = if (lat!=null&&lng!=null) LatLng(lat!!, lng!!) else null
        return City(name!!, location = latlng)
    }
}
fun City.toFBCity() : FBCity {
    val fbCity = FBCity()
    fbCity.name = this.name
    fbCity.lat = this.location?.latitude ?: 0.0
    fbCity.lng = this.location?.longitude ?: 0.0
    return fbCity
}