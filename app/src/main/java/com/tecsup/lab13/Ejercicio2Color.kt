package com.tecsup.lab13

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Ejercicio2Color() {
    var esAzul by remember { mutableStateOf(true) }
    var tipo by remember { mutableStateOf("tween") }

    val colorDestino = if (esAzul) Color(0xFF1E88E5) else Color(0xFF43A047)

    // Elegimos la especificacion segun el RadioButton marcado
    val especificacion: AnimationSpec<Color> =
        if (tipo == "spring") spring(stiffness = Spring.StiffnessVeryLow)
        else tween(durationMillis = 1500)

    val colorAnimado by animateColorAsState(
        targetValue = colorDestino,
        animationSpec = especificacion,
        label = "colorCuadro"
    )

    // Con "sin animación" usamos el color directo para comparar
    val colorFinal = if (tipo == "sin animación") colorDestino else colorAnimado

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Ejercicio 2: animateColorAsState", style = MaterialTheme.typography.titleLarge)

        Box(
            modifier = Modifier
                .size(200.dp)
                .background(colorFinal, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (esAzul) "Azul" else "Verde",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Button(onClick = { esAzul = !esAzul }) {
            Text("Cambiar color")
        }

        Text("Tipo de animación:", style = MaterialTheme.typography.titleMedium)
        Column {
            listOf("tween", "spring", "sin animación").forEach { opcion ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = tipo == opcion, onClick = { tipo = opcion })
                    Text(opcion)
                }
            }
        }
    }
}