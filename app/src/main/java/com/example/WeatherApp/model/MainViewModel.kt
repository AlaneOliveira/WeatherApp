package com.example.WeatherApp.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.WeatherApp.dbfb.FBCity
import com.example.WeatherApp.dbfb.FBDatabase
import com.example.WeatherApp.dbfb.FBUser
import com.example.WeatherApp.dbfb.toFBCity
import com.google.android.gms.maps.model.LatLng

class MainViewModel (private val db: FBDatabase): ViewModel(),
    FBDatabase.Listener {


    private val _cities = mutableStateListOf<City>()
    val cities
        get() = _cities.toList()

    private val _user = mutableStateOf<User?> (null)
    val user: User?
        get() = _user.value

    init {
        db.setListener(this)
    }

    fun remove(city: City) {
        db.remove(city.toFBCity())
    }

    fun add(name: String, location : LatLng? = null) {
        db.add(City(name = name, location = location).toFBCity())
    }

    override fun onUserLoaded(user: FBUser) {
        _user.value = user.toUser()
    }

    override fun onUserSignOut() {
        //TODO("Not yet implemented")
    }

    override fun onCityAdded(city: FBCity) {
        _cities.add(city.toCity())
    }

    override fun onCityUpdated(city: FBCity) {
        //TODO("Not yet implemented")
    }

    override fun onCityRemoved(city: FBCity) {
        _cities.remove(city.toCity())
    }
}
