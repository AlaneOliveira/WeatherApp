package com.weatherapp.api

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.android.gms.maps.model.LatLng  // import correto
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.weatherapp.LoginActivity
import com.weatherapp.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherApp : Application() {
    val FLAGS =
        FLAG_ACTIVITY_SINGLE_TOP or
                FLAG_ACTIVITY_NEW_TASK or
                FLAG_ACTIVITY_CLEAR_TASK

    override fun onCreate() {
        super.onCreate()
        Firebase.auth.addAuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                goToMain()
            } else {
                goToLogin()
            }
        }
    }

    private fun goToMain() {
        this.startActivity(Intent(this, MainActivity::class.java).setFlags(FLAGS))
    }

    private fun goToLogin() {
        this.startActivity(Intent(this, LoginActivity::class.java).setFlags(FLAGS))
    }
}

class WeatherService(private val context: Context) {

    private val weatherAPI: WeatherServiceAPI = Retrofit.Builder()
        .baseUrl(WeatherServiceAPI.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherServiceAPI::class.java)

    private val imageLoader = ImageLoader(context)

    private fun search(query: String): APILocation? {
        val call: Call<List<APILocation>?> = weatherAPI.search(query)
        val apiLoc = call.execute().body()
        return if (!apiLoc.isNullOrEmpty()) apiLoc[0] else null
    }

    suspend fun getLocation(name: String): LatLng? = withContext(Dispatchers.IO) {
        val call: Call<List<APILocation>?> = weatherAPI.search(name)
        val apiLoc = call.execute().body()
        if (!apiLoc.isNullOrEmpty()) {
            val lat = apiLoc[0].lat ?: return@withContext null
            val lon = apiLoc[0].lon ?: return@withContext null
            LatLng(lat, lon)
        } else null
    }

    suspend fun getName(lat: Double, lon: Double): String? = withContext(Dispatchers.IO) {
        val call: Call<List<APILocation>?> = weatherAPI.search("$lat,$lon")
        val apiLoc = call.execute().body()
        if (!apiLoc.isNullOrEmpty()) apiLoc[0].name else null
    }

    suspend fun getWeather(name: String): APICurrentWeather? =
        withContext(Dispatchers.IO) {
            try {
                val call: Call<APICurrentWeather?> = weatherAPI.weather(name)
                val response = call.execute()
                android.util.Log.d("WeatherService", "getWeather code: ${response.code()}")
                android.util.Log.d("WeatherService", "getWeather body: ${response.body()}")
                android.util.Log.d("WeatherService", "getWeather error: ${response.errorBody()?.string()}")
                response.body()
            } catch (e: Exception) {
                android.util.Log.e("WeatherService", "getWeather exception: ${e.message}")
                null
            }
        }

    suspend fun getForecast(name: String): APIWeatherForecast? =
        withContext(Dispatchers.IO) {
            val call: Call<APIWeatherForecast?> = weatherAPI.forecast(name)
            call.execute().body()
        }

    suspend fun getBitmap(imgUrl: String): Bitmap? =
        withContext(Dispatchers.IO) {
            val request = ImageRequest.Builder(context).data(imgUrl)
                .allowHardware(false).build()
            val response = imageLoader.execute(request)
            response.drawable?.toBitmap()
        }
}