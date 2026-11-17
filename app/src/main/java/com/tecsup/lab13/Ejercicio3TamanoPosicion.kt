package com.tecsup.lab13

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Ejercicio3TamanoPosicion() {
    var movido by remember { mutableStateOf(false) }

    val tamano by animateDpAsState(
        targetValue = if (movido) 140.dp else 80.dp,
        animationSpec = tween(durationMillis = 700),
        label = "tamano"
    )
    val posX by animateDpAsState(
        targetValue = if (movido) 140.dp else 0.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "posX"
    )
    val posY by animateDpAsState(
        targetValue = if (movido) 80.dp else 0.dp,
        animationSpec = tween(durationMillis = 700),
        label = "posY"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Ejercicio 3: Tamaño y posición", style = MaterialTheme.typography.titleLarge)

        Button(onClick = { movido = !movido }) {
            Text(if (movido) "Regresar" else "Mover y agrandar")
        }

        // A) offset va primero: se mueve el cuadro completo
        Text("A) offset ANTES de background")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
            Box(
                modifier = Modifier
                    .offset(x = posX, y = posY)
                    .size(tamano)
                    .background(Color(0xFFFF7043), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("A", color = Color.White, style = MaterialTheme.typography.titleLarge)
            }
        }

        // B) offset va al final: el color se queda y solo se mueve la letra
        Text("B) background ANTES de offset")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(tamano)
                    .background(Color(0xFF26A69A), RoundedCornerShape(12.dp))
                    .offset(x = posX)
            ) {
                Text(
                    "B",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}