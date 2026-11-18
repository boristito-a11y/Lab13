package com.tecsup.lab13.juego

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

private data class PuntoDeLuz(val x: Float, val y: Float, val tamano: Float, val titila: Boolean)

// Cielo con degradado y estrellas que bajan despacio y titilan
@Composable
fun FondoEstrellado(modifier: Modifier = Modifier) {
    val estrellas = remember {
        val azar = Random(13)
        List(90) {
            PuntoDeLuz(
                x = azar.nextFloat(),
                y = azar.nextFloat(),
                tamano = azar.nextFloat(),
                titila = azar.nextInt(3) == 0
            )
        }
    }

    val cielo = rememberInfiniteTransition(label = "cielo")
    val avance by cielo.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 25000, easing = LinearEasing)),
        label = "avance"
    )
    val brillo by cielo.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 1200), RepeatMode.Reverse),
        label = "brillo"
    )

    Canvas(
        modifier = modifier.background(
            Brush.verticalGradient(
                listOf(Color(0xFF0B1026), Color(0xFF1B1F4B), Color(0xFF3B1E5E))
            )
        )
    ) {
        estrellas.forEach { e ->
            // Las estrellas grandes bajan mas rapido: da sensacion de profundidad
            val y = ((e.y + avance * (0.3f + e.tamano)) % 1f) * size.height
            val alfa = if (e.titila) brillo else 0.35f + e.tamano * 0.5f
            drawCircle(
                color = Color.White.copy(alpha = alfa.coerceIn(0f, 1f)),
                radius = 1f + e.tamano * 2.5f,
                center = Offset(e.x * size.width, y)
            )
        }
    }
}