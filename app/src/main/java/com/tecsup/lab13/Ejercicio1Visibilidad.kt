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
fun Ejercicio1Visibilidad() {
    var visible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Ejercicio 1: AnimatedVisibility", style = MaterialTheme.typography.titleLarge)

        Button(onClick = { visible = !visible }) {
            Text(if (visible) "Ocultar cuadro" else "Mostrar cuadro")
        }

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(durationMillis = 800)) + expandVertically(),
            exit = fadeOut(animationSpec = tween(durationMillis = 800)) + shrinkVertically()
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(Color(0xFF7E57C2), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("¡Hola!", color = Color.White, style = MaterialTheme.typography.headlineSmall)
            }
        }

        // Este texto sirve para ver como el resto de la pantalla se acomoda
        Text("Texto debajo del cuadro", style = MaterialTheme.typography.bodyLarge)
    }
}