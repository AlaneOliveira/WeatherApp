package com.example.WeatherApp.model

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.WeatherApp.api.WeatherService
import com.example.WeatherApp.api.toForecast
import com.example.WeatherApp.api.toWeather
import com.example.WeatherApp.dbfb.FBCity
import com.example.WeatherApp.dbfb.FBDatabase
import com.example.WeatherApp.dbfb.FBUser
import com.example.WeatherApp.dbfb.toFBCity
import com.example.WeatherApp.ui.nav.Route
import com.google.android.gms.maps.model.LatLng

class MainViewModel(
    private val db: FBDatabase,           // Instância do banco de dados Firebase
    private val service: WeatherService   // Instância do serviço de clima
) : ViewModel(), FBDatabase.Listener {

    // Armazena as cidades usando um mapa para acesso rápido pelo nome
    private val _cities = mutableStateMapOf<String, City>()

    // Expõe a lista de cidades ordenada pelo nome (somente leitura)
    val cities: List<City>
        get() = _cities.values.toList().sortedBy { it.name }

    // Armazena os dados do clima para cada cidade
    private val _weather = mutableStateMapOf<String, Weather>()

    // Armazena os dados do usuário atual
    private val _user = mutableStateOf<User?>(null)
    val user: User?
        get() = _user.value

    init {
        db.setListener(this) // Registra o ViewModel como listener de alterações no banco
    }
    private val _forecast = mutableStateMapOf<String, List<Forecast>?>()

    fun forecast (name: String) = _forecast.getOrPut(name) {
        loadForecast(name)
        emptyList() // return
    }
    private fun loadForecast(name: String) {
        service.getForecast(name) { apiForecast ->
            apiForecast?.let {
                _forecast[name] = apiForecast.toForecast()
            }
        }
    }
    private var _city = mutableStateOf<String?>(null)
    var city: String?
        get() = _city.value
        set(tmp) { _city.value = tmp } // Essa propriedade registra a cidade atualmente selecionada na lista, cuja previsão será exibida na página Home

    // Adiciona uma cidade pelo nome (busca coordenadas usando WeatherService)
    fun addCity(name: String) {
        service.getLocation(name) { lat, lng ->
            if (lat != null && lng != null) {
                // Cria a cidade e adiciona ao Firebase
                db.add(City(name = name, location = LatLng(lat, lng)).toFBCity())
            }
        }
    }

    // Adiciona uma cidade pela localização (busca o nome usando WeatherService)
    fun addCity(location: LatLng) {
        service.getName(location.latitude, location.longitude) { name ->
            if (name != null) {
                db.add(City(name = name, location = location).toFBCity())
            }
        }
    }

    // Remove uma cidade do Firebase
    fun remove(city: City) {
        db.remove(city.toFBCity())
    }

    //  FBDatabase.Listener

    // Atualiza os dados do usuário quando carregados do Firebase
    override fun onUserLoaded(user: FBUser) {
        _user.value = user.toUser()
    }

    // Executa ações quando o usuário faz logout
    override fun onUserSignOut() {
        // Implementar se necessário
    }

    // Adiciona uma cidade no mapa local quando é adicionada no Firebase
    override fun onCityAdded(city: FBCity) {
        _cities[city.name!!] = city.toCity()
    }

    // Atualiza os dados de uma cidade existente
    override fun onCityUpdated(city: FBCity) {
        _cities.remove(city.name)          // Remove a versão antiga
        _cities[city.name!!] = city.toCity() // Adiciona a nova versão
    }

    // Remove uma cidade do mapa local quando é removida do Firebase
    override fun onCityRemoved(city: FBCity) {
        _cities.remove(city.name)
    }

    fun weather(name: String) = _weather.getOrPut(name) {
        loadWeather(name)
        Weather.LOADING // return
    }
    // Função para carregar os dados de clima de uma cidade pelo nome
    private fun loadWeather(name: String) {
        // Chama o WeatherService para buscar o clima da cidade
        service.getWeather(name) { apiWeather ->
            // Se o retorno da API não for nulo, converte para o modelo local e salva no mapa _weather
            apiWeather?.let {
                _weather[name] = apiWeather.toWeather()
            }
        }
    }
    private var _page = mutableStateOf<Route>(Route.Home)
    var page: Route
        get() = _page.value
        set(tmp) { _page.value = tmp }

}
