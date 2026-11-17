package com.tecsup.lab13

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.lab13.ui.theme.Lab13Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab13Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppLab13(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AppLab13(modifier: Modifier = Modifier) {
    var pantalla by remember { mutableStateOf("menu") }

    // Con el boton atras del celular regresas al menu
    BackHandler(enabled = pantalla != "menu") { pantalla = "menu" }

    Box(modifier = modifier.fillMaxSize()) {
        when (pantalla) {
            "menu" -> MenuPrincipal(onElegir = { pantalla = it })
            // Aqui vas agregando una linea por cada ejercicio
            "ej1" -> Ejercicio1Visibilidad()
            "ej2" -> Ejercicio2Color()
            else -> Text("Proximamente", modifier = Modifier.padding(24.dp))
        }
    }
}

@Composable
fun MenuPrincipal(onElegir: (String) -> Unit) {
    val opciones = listOf(
        "ej1" to "Ejercicio 1: AnimatedVisibility",
        "ej2" to "Ejercicio 2: animateColorAsState",
        "ej3" to "Ejercicio 3: Tamaño y posición",
        "ej4" to "Ejercicio 4: AnimatedContent",
        "juego" to "Ejercicio final: Atrapa la Estrella"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Lab 13 - Animaciones", style = MaterialTheme.typography.headlineMedium)
        opciones.forEach { (clave, titulo) ->
            Button(onClick = { onElegir(clave) }, modifier = Modifier.fillMaxWidth()) {
                Text(titulo)
            }
        }
    }
}