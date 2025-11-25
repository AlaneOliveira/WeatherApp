package com.example.WeatherApp.dbfb

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore

/*
* Essa classe nos fornecerá acesso ao nosso BD no Firebase. Ela define um
listener que é chamado sempre que houver um evento de cidade adicionada ou
removida (onCityAdded/onCityUpdated/onCityRemoved), ou quando as
informações do usuário logado forem carregas (onUserLoaded). Vamos
instânciar essa classe nas atividades e setar o viewModel como listener. */

class FBDatabase {
    interface Listener {
        fun onUserLoaded(user: FBUser)
        fun onUserSignOut()
        fun onCityAdded(city: FBCity)
        fun onCityUpdated(city: FBCity)
        fun onCityRemoved(city: FBCity)
    }
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private var citiesListReg: ListenerRegistration? = null
    private var listener : Listener? = null
    /*
    * Esse código é chamado na criação do FBDatabase. Ele registra um (outro)
listener que é disparado quando o usuário faz login ou logout. Em caso de
login, é iniciada a carga dos dados do usuário atual, a qual ao ser completada
dispara o evento listener?.onUserLoaded(...) (a ? indica que o listener
pode ser null, nesse caso nada é feito). Também é registrado o listener
citiesListReg que notifica sempre que uma cidade for adicionada
(onCityAdded()) ou removida (onCityRemoved()) no BD. Quando o usuário
faz sign out, o citiesListReg registrado é removido. */
    init {
        auth.addAuthStateListener { auth ->
            if (auth.currentUser == null) {
                citiesListReg?.remove()
                listener?.onUserSignOut()
                return@addAuthStateListener
            }
        }

        val refCurrUser = db.collection("users").document(auth.currentUser!!.uid)
        refCurrUser.get().addOnSuccessListener {
            it.toObject(FBUser::class.java)?.let { user ->
                listener?.onUserLoaded(user)
            }
        }
        citiesListReg = refCurrUser.collection("cities")
            .addSnapshotListener { snapshots, ex ->
                if (ex != null) return@addSnapshotListener
                snapshots?.documentChanges?.forEach { change ->
                    val fbCity = change.document.toObject(FBCity::class.java)
                    if (change.type == DocumentChange.Type.ADDED) {
                        listener?.onCityAdded(fbCity)
                    } else if (change.type == DocumentChange.Type.MODIFIED) {
                        listener?.onCityUpdated(fbCity)
                    } else if (change.type == DocumentChange.Type.REMOVED) {
                        listener?.onCityRemoved(fbCity)
                    }
                }
            }  }

    fun setListener(listener: Listener? = null) {
        this.listener = listener
    }
    /*
    * Esse métoodo salva um objeto FBUser na coleção users no Firebase Firestore
e deve ser chamado em RegisterPage (ver mais a frente). */
    fun register(user: FBUser) {
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid + "").set(user);
    }
    /*
    * Nesse métodoo, caso haja um usuário logado, é criado um registro para a
cidade na coleção cities do usuário atual, com o nome da cidade como id. */
    fun add(city: FBCity) {
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")
        if (city.name == null || city.name!!.isEmpty())
            throw RuntimeException("City with null or empty name!")
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).collection("cities")
            .document(city.name!!).set(city)
    }
    /*
    * Nesse métoodo, a cidade é removida da lista de cidades associada ao usuário
atualmente logado.*/
    fun remove(city: FBCity) {
        if (auth.currentUser == null)
            throw RuntimeException("User not logged in!")
        if (city.name == null || city.name!!.isEmpty())
            throw RuntimeException("City with null or empty name!")
        val uid = auth.currentUser!!.uid
        db.collection("users").document(uid).collection("cities")
            .document(city.name!!).delete()
    }

    
}