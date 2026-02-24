package com.weatherapp

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.weatherapp.db.fb.FBDatabase
import com.weatherapp.db.fb.toFBUser
import com.weatherapp.model.User
import com.weatherapp.ui.theme.WeatherAppTheme

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RegisterPage(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPage(modifier: Modifier = Modifier) {
    var userName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var remeberPassword by rememberSaveable { mutableStateOf("") }
    val activity = LocalActivity.current as Activity

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally,
    ) {
        Text(text = "Registre-se", fontSize = 24.sp)

        OutlinedTextField(
            value = userName,
            label = { Text(text = "Digite o nome de usuário") },
            modifier = modifier.fillMaxWidth(fraction = 0.9f),
            onValueChange = { userName = it })
        OutlinedTextField(
            value = email,
            label = { Text(text = "Digite seu email") },
            modifier = modifier.fillMaxWidth(fraction = 0.9f),
            onValueChange = { email = it })
        OutlinedTextField(
            value = password,
            label = { Text(text = "Digite sua senha") },
            modifier = modifier.fillMaxWidth(fraction = 0.9f),
            visualTransformation = PasswordVisualTransformation(),
            onValueChange = { password = it })

        OutlinedTextField(
            value = remeberPassword,
            label = { Text(text = "Confirme a senha") },
            modifier = modifier.fillMaxWidth(fraction = 0.9f),
            visualTransformation = PasswordVisualTransformation(),
            onValueChange = { remeberPassword = it })

        Row(modifier = modifier) {

            Button(
                onClick = {
                    Firebase.auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(activity) { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    activity,
                                    "Registro OK!", Toast.LENGTH_LONG
                                ).show()
                               FBDatabase().register(User(userName, email).toFBUser())
                            } else {
                                Toast.makeText(
                                    activity,
                                    "Registro FALHOU!", Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                },
                enabled = userName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && password.equals(
                    remeberPassword
                )
            ) {
                Text("Registrar")
            }

            Spacer(modifier = modifier.size(24.dp))

            Button(onClick = { activity.finish() }) {
                Text("Voltar")
            }
        }


    }
}