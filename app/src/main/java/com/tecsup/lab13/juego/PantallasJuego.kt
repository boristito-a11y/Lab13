package com.tecsup.lab13.juego

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val Dorado = Color(0xFFFFD54F)
private val AzulNoche = Color(0xFF1B1F4B)

// Pantalla de inicio: titulo que flota, reglas y boton que late
@Composable
fun PantallaMenu(record: Int, onJugar: () -> Unit) {
    val animacion = rememberInfiniteTransition(label = "menu")
    val flotar by animacion.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(tween(1600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "flotar"
    )
    val latido by animacion.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "latido"
    )

    var mostrar by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { mostrar = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = mostrar,
            enter = fadeIn(tween(700)) + slideInVertically(tween(700)) { altura -> -altura / 2 }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "⭐",
                    fontSize = 72.sp,
                    modifier = Modifier.graphicsLayer { translationY = flotar.dp.toPx() }
                )
                Text(
                    "CAZAESTRELLAS",
                    color = Dorado,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                Text(
                    "Atrapa todo lo que brilla. No toques las bombas.",
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedVisibility(
            visible = mostrar,
            enter = fadeIn(tween(durationMillis = 700, delayMillis = 300))
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaRegla("⭐", "+1")
                TarjetaRegla("☄️", "+5")
                TarjetaRegla("💣", "-1 ❤️")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("RÉCORD: $record", color = Color.White, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onJugar,
            modifier = Modifier
                .scale(latido)
                .height(56.dp)
                .width(220.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Dorado, contentColor = AzulNoche),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("JUGAR", fontSize = 20.sp, fontWeight = FontWeight.Black)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Si se te escapa una estrella pierdes una vida",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun TarjetaRegla(emoji: String, valor: String) {
    Column(
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emoji, fontSize = 28.sp)
        Text(valor, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

// 3, 2, 1, ¡YA!
@Composable
fun CuentaRegresiva(onTerminar: () -> Unit) {
    var numero by remember { mutableIntStateOf(3) }
    val alTerminar by rememberUpdatedState(onTerminar)

    LaunchedEffect(Unit) {
        while (numero > 0) {
            delay(800)
            numero--
        }
        delay(500)
        alTerminar()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AnimatedContent(
            targetState = numero,
            transitionSpec = {
                (scaleIn(initialScale = 2f) + fadeIn()) togetherWith
                        (scaleOut(targetScale = 0.5f) + fadeOut())
            },
            label = "cuenta"
        ) { n ->
            Text(
                if (n > 0) "$n" else "¡YA!",
                color = if (n > 0) Color.White else Dorado,
                fontSize = 96.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

// Resumen de la partida
@Composable
fun PantallaFin(
    puntos: Int,
    record: Int,
    nuevoRecord: Boolean,
    nivel: Int,
    mejorCombo: Int,
    onReintentar: () -> Unit,
    onMenu: () -> Unit
) {
    var mostrar by remember { mutableStateOf(false) }
    var objetivo by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        mostrar = true
        delay(300)
        objetivo = puntos
    }

    // El puntaje "cuenta" desde 0 hasta el resultado
    val puntosMostrados by animateIntAsState(
        targetValue = objetivo,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "conteo"
    )
    val parpadeo = rememberInfiniteTransition(label = "record")
    val colorRecord by parpadeo.animateColor(
        initialValue = Dorado,
        targetValue = Color(0xFFFF8A65),
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
        label = "colorRecord"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .pointerInput(Unit) { detectTapGestures { } },
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = mostrar,
            enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .background(AzulNoche, RoundedCornerShape(24.dp))
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("FIN DEL JUEGO", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Black)

                AnimatedVisibility(visible = nuevoRecord) {
                    Text("¡NUEVO RÉCORD!", color = colorRecord, fontSize = 18.sp, fontWeight = FontWeight.Black)
                }

                Text("$puntosMostrados", color = Dorado, fontSize = 64.sp, fontWeight = FontWeight.Black)

                FilaDato("Récord", "$record")
                FilaDato("Nivel alcanzado", "$nivel")
                FilaDato("Mejor combo", "$mejorCombo seguidas")

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onReintentar,
                    colors = ButtonDefaults.buttonColors(containerColor = Dorado, contentColor = AzulNoche),
                    modifier = Modifier.width(220.dp)
                ) {
                    Text("Jugar otra vez", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(onClick = onMenu, modifier = Modifier.width(220.dp)) {
                    Text("Menú principal", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun FilaDato(nombre: String, valor: String) {
    Row(modifier = Modifier.width(220.dp)) {
        Text(nombre, color = Color.White.copy(alpha = 0.7f), modifier = Modifier.weight(1f))
        Text(valor, color = Color.White, fontWeight = FontWeight.Bold)
    }
}