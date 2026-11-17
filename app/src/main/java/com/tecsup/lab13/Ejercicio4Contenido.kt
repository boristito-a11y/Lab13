package com.tecsup.lab13

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class EstadoPantalla { Cargando, Contenido, Error }

@Composable
fun Ejercicio4Contenido() {
    var estado by remember { mutableStateOf(EstadoPantalla.Cargando) }
    var intentos by remember { mutableIntStateOf(0) }

    // Simula una carga: 2 segundos cargando y luego sale Contenido o Error al azar
    LaunchedEffect(intentos) {
        if (intentos > 0) {
            estado = EstadoPantalla.Cargando
            delay(2000)
            estado = if (Random.nextBoolean()) EstadoPantalla.Contenido else EstadoPantalla.Error
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Ejercicio 4: AnimatedContent", style = MaterialTheme.typography.titleLarge)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EstadoPantalla.entries.forEach { opcion ->
                OutlinedButton(
                    onClick = { estado = opcion },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                ) {
                    Text(opcion.name, maxLines = 1)
                }
            }
        }

        Button(onClick = { intentos++ }, modifier = Modifier.fillMaxWidth()) {
            Text("Simular carga (2 segundos)")
        }

        AnimatedContent(
            targetState = estado,
            modifier = Modifier.fillMaxWidth(),
            transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = 700, delayMillis = 200)) togetherWith
                        fadeOut(animationSpec = tween(durationMillis = 300))
            },
            contentAlignment = Alignment.Center,
            label = "estado"
        ) { estadoActual ->
            when (estadoActual) {
                EstadoPantalla.Cargando -> Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Cargando datos...")
                }

                EstadoPantalla.Contenido -> Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Ranking de jugadores", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("1. Ana - 1200 pts")
                        Text("2. Luis - 950 pts")
                        Text("3. Boris - 870 pts")
                    }
                }

                EstadoPantalla.Error -> Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "No se pudo cargar la información",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("Revisa tu conexión e intenta otra vez.")
                }
            }
        }
    }
}