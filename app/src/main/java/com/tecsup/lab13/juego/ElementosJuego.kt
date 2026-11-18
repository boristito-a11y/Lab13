package com.tecsup.lab13.juego

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

private val TAMANO_OBJETO = 56.dp

// Un objeto que cae desde arriba hasta abajo. Si se pausa, se queda quieto donde esta.
@Composable
fun ObjetoCayendo(
    objeto: ObjetoQueCae,
    anchoCampo: Dp,
    altoCampo: Dp,
    congelado: Boolean,
    onTocado: (x: Float, y: Float) -> Unit,
    onLlegoAlSuelo: () -> Unit
) {
    val progreso = remember(objeto.id) { Animatable(0f) }
    val densidad = LocalDensity.current

    LaunchedEffect(objeto.id, congelado) {
        if (!congelado) {
            val restante = 1f - progreso.value
            progreso.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = (objeto.duracionMs * restante).toInt().coerceAtLeast(1),
                    easing = LinearEasing
                )
            )
            onLlegoAlSuelo()
        }
    }

    // Las estrellas y cometas giran; la bomba se tambalea
    val giro = rememberInfiniteTransition(label = "giro")
    val esBomba = objeto.tipo == TipoObjeto.Bomba
    val angulo by giro.animateFloat(
        initialValue = if (esBomba) -15f else 0f,
        targetValue = if (esBomba) 15f else 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (esBomba) 350 else 2400, easing = LinearEasing),
            repeatMode = if (esBomba) RepeatMode.Reverse else RepeatMode.Restart
        ),
        label = "angulo"
    )

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = ((anchoCampo - TAMANO_OBJETO) * objeto.x).roundToPx(),
                    y = ((altoCampo + TAMANO_OBJETO) * progreso.value - TAMANO_OBJETO).roundToPx()
                )
            }
            .size(TAMANO_OBJETO)
            .graphicsLayer { rotationZ = angulo }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = !congelado
            ) {
                with(densidad) {
                    val centroX = ((anchoCampo - TAMANO_OBJETO) * objeto.x + TAMANO_OBJETO / 2).toPx()
                    val centroY = ((altoCampo + TAMANO_OBJETO) * progreso.value - TAMANO_OBJETO).toPx()
                    onTocado(centroX, centroY)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(objeto.tipo.emoji, fontSize = 36.sp)
    }
}

// El "+3" que sube y se desvanece donde tocaste
@Composable
fun TextoFlotanteAnimado(texto: TextoFlotante, onTerminar: () -> Unit) {
    val avance = remember(texto.id) { Animatable(0f) }
    val alTerminar by rememberUpdatedState(onTerminar)

    LaunchedEffect(texto.id) {
        avance.animateTo(1f, tween(durationMillis = 800, easing = FastOutSlowInEasing))
        alTerminar()
    }

    Text(
        text = texto.texto,
        color = if (texto.esMalo) Color(0xFFFF5252) else Color(0xFFFFD54F),
        fontSize = 26.sp,
        fontWeight = FontWeight.Black,
        modifier = Modifier
            .offset {
                IntOffset(
                    x = (texto.x - 20.dp.toPx()).roundToInt(),
                    y = (texto.y - 90.dp.toPx() * avance.value).roundToInt()
                )
            }
            .graphicsLayer {
                alpha = 1f - avance.value
                scaleX = 1f + avance.value * 0.5f
                scaleY = 1f + avance.value * 0.5f
            }
    )
}

// Vidas, puntaje, nivel, combo y boton de pausa
@Composable
fun BarraSuperior(
    puntos: Int,
    vidas: Int,
    nivel: Int,
    combo: Int,
    onPausa: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Row {
                repeat(ReglasJuego.VIDAS_INICIALES) { i ->
                    AnimatedVisibility(
                        visible = i < vidas,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut(tween(durationMillis = 300)) + fadeOut()
                    ) {
                        Text("❤️", fontSize = 22.sp, modifier = Modifier.padding(end = 2.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            AnimatedContent(
                targetState = puntos,
                transitionSpec = {
                    (slideInVertically { altura -> altura } + fadeIn()) togetherWith
                            (slideOutVertically { altura -> -altura } + fadeOut()) using
                            SizeTransform(clip = false)
                },
                label = "puntos"
            ) { valor ->
                Text("$valor", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Black)
            }

            Spacer(modifier = Modifier.weight(1f))

            TextButton(onClick = onPausa) {
                Text("❚❚", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("NIVEL $nivel", color = Color(0xFF80DEEA), fontSize = 14.sp, fontWeight = FontWeight.Bold)

            val multiplicador = ReglasJuego.multiplicador(combo)
            AnimatedVisibility(
                visible = multiplicador > 1,
                enter = scaleIn(spring(dampingRatio = Spring.DampingRatioHighBouncy)) + fadeIn(),
                exit = fadeOut()
            ) {
                AnimatedContent(
                    targetState = multiplicador,
                    transitionSpec = { scaleIn(initialScale = 1.8f) togetherWith fadeOut(tween(100)) },
                    label = "multiplicador"
                ) { m ->
                    Text(
                        "COMBO x$m",
                        color = Color(0xFFFFD54F),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier
                            .background(Color(0x33FFD54F), RoundedCornerShape(50))
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

// Capa de pausa: bloquea los toques al juego que esta debajo
@Composable
fun MenuPausa(onContinuar: () -> Unit, onSalir: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            .pointerInput(Unit) { detectTapGestures { } },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("PAUSA", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Black)
            Button(
                onClick = onContinuar,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD54F),
                    contentColor = Color(0xFF1B1F4B)
                ),
                modifier = Modifier.width(220.dp)
            ) {
                Text("Continuar", fontWeight = FontWeight.Bold)
            }
            OutlinedButton(onClick = onSalir, modifier = Modifier.width(220.dp)) {
                Text("Salir al menú", color = Color.White)
            }
        }
    }
}