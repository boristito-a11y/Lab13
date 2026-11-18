package com.tecsup.lab13.juego

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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun Cazaestrellas(
    record: Int,
    onNuevoRecord: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var fase by remember { mutableStateOf(Fase.Menu) }
    var puntos by remember { mutableIntStateOf(0) }
    var vidas by remember { mutableIntStateOf(ReglasJuego.VIDAS_INICIALES) }
    var combo by remember { mutableIntStateOf(0) }
    var mejorCombo by remember { mutableIntStateOf(0) }
    var pausado by remember { mutableStateOf(false) }
    var nuevoRecord by remember { mutableStateOf(false) }
    var golpe by remember { mutableStateOf(false) }
    var mostrarNivel by remember { mutableStateOf(false) }
    var siguienteId by remember { mutableLongStateOf(0L) }

    val objetos = remember { mutableStateListOf<ObjetoQueCae>() }
    val flotantes = remember { mutableStateListOf<TextoFlotante>() }

    val nivel = ReglasJuego.nivelPara(puntos)
    val temblor = remember { Animatable(0f) }
    val escopo = rememberCoroutineScope()
    val vibracion = LocalHapticFeedback.current

    fun reiniciar() {
        puntos = 0
        vidas = ReglasJuego.VIDAS_INICIALES
        combo = 0
        mejorCombo = 0
        pausado = false
        nuevoRecord = false
        objetos.clear()
        flotantes.clear()
    }

    fun perderVida() {
        vidas--
        combo = 0
        golpe = true
        vibracion.performHapticFeedback(HapticFeedbackType.LongPress)
        // La pantalla tiembla
        escopo.launch {
            repeat(3) {
                temblor.animateTo(14f, tween(durationMillis = 40))
                temblor.animateTo(-14f, tween(durationMillis = 40))
            }
            temblor.animateTo(0f, tween(durationMillis = 40))
        }
        if (vidas <= 0) {
            if (puntos > record) {
                nuevoRecord = true
                onNuevoRecord(puntos)
            }
            objetos.clear()
            fase = Fase.Fin
        }
    }

    fun atrapar(objeto: ObjetoQueCae, x: Float, y: Float) {
        if (!objetos.remove(objeto)) return
        if (objeto.tipo == TipoObjeto.Bomba) {
            flotantes.add(TextoFlotante(siguienteId++, "💥", x, y, esMalo = true))
            perderVida()
        } else {
            combo++
            if (combo > mejorCombo) mejorCombo = combo
            val ganados = objeto.tipo.puntos * ReglasJuego.multiplicador(combo)
            puntos += ganados
            flotantes.add(TextoFlotante(siguienteId++, "+$ganados", x, y, esMalo = false))
            vibracion.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    fun seEscapo(objeto: ObjetoQueCae) {
        // Solo las estrellas que se escapan quitan vida
        if (objetos.remove(objeto) && objeto.tipo == TipoObjeto.Estrella) perderVida()
    }

    // Va soltando objetos mientras se juega; se detiene en pausa
    LaunchedEffect(fase, pausado) {
        if (fase == Fase.Jugando && !pausado) {
            while (true) {
                val nivelActual = ReglasJuego.nivelPara(puntos)
                objetos.add(
                    ObjetoQueCae(
                        id = siguienteId++,
                        tipo = ReglasJuego.tipoAlAzar(Random),
                        x = Random.nextFloat(),
                        duracionMs = ReglasJuego.duracionCaida(nivelActual)
                    )
                )
                delay(ReglasJuego.intervaloAparicion(nivelActual))
            }
        }
    }

    // Aviso de "NIVEL X" cada vez que se sube de nivel
    LaunchedEffect(nivel) {
        if (fase == Fase.Jugando && nivel > 1) {
            mostrarNivel = true
            delay(1400)
            mostrarNivel = false
        }
    }

    // Destello rojo al perder una vida
    LaunchedEffect(golpe) {
        if (golpe) {
            delay(150)
            golpe = false
        }
    }
    val destello by animateColorAsState(
        targetValue = if (golpe) Color(0x77FF1744) else Color.Transparent,
        animationSpec = tween(durationMillis = 150),
        label = "destello"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { translationX = temblor.value }
    ) {
        FondoEstrellado(modifier = Modifier.fillMaxSize())

        // Transicion suave entre menu, cuenta regresiva y juego
        AnimatedContent(
            targetState = if (fase == Fase.Fin) Fase.Jugando else fase,
            transitionSpec = { fadeIn(tween(500)) togetherWith fadeOut(tween(300)) },
            label = "fase"
        ) { pantalla ->
            when (pantalla) {
                Fase.Menu -> PantallaMenu(
                    record = record,
                    onJugar = {
                        reiniciar()
                        fase = Fase.Cuenta
                    }
                )

                Fase.Cuenta -> CuentaRegresiva(onTerminar = { fase = Fase.Jugando })

                else -> BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val ancho = maxWidth
                    val alto = maxHeight

                    objetos.forEach { objeto ->
                        key(objeto.id) {
                            ObjetoCayendo(
                                objeto = objeto,
                                anchoCampo = ancho,
                                altoCampo = alto,
                                congelado = pausado || fase != Fase.Jugando,
                                onTocado = { x, y -> atrapar(objeto, x, y) },
                                onLlegoAlSuelo = { seEscapo(objeto) }
                            )
                        }
                    }

                    flotantes.forEach { texto ->
                        key(texto.id) {
                            TextoFlotanteAnimado(texto, onTerminar = { flotantes.remove(texto) })
                        }
                    }

                    BarraSuperior(
                        puntos = puntos,
                        vidas = vidas,
                        nivel = nivel,
                        combo = combo,
                        onPausa = { pausado = true },
                        modifier = Modifier.align(Alignment.TopCenter)
                    )

                    AnimatedVisibility(
                        visible = mostrarNivel,
                        modifier = Modifier.align(Alignment.Center),
                        enter = slideInVertically { altura -> -altura } + fadeIn(),
                        exit = slideOutVertically { altura -> altura } + fadeOut()
                    ) {
                        Text(
                            "NIVEL $nivel",
                            color = Color(0xFF80DEEA),
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier
                                .background(Color(0x55000000), RoundedCornerShape(16.dp))
                                .padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }

                    AnimatedVisibility(visible = pausado, enter = fadeIn(), exit = fadeOut()) {
                        MenuPausa(
                            onContinuar = { pausado = false },
                            onSalir = {
                                reiniciar()
                                fase = Fase.Menu
                            }
                        )
                    }
                }
            }
        }

        if (fase == Fase.Fin) {
            PantallaFin(
                puntos = puntos,
                record = maxOf(record, puntos),
                nuevoRecord = nuevoRecord,
                nivel = nivel,
                mejorCombo = mejorCombo,
                onReintentar = {
                    reiniciar()
                    fase = Fase.Cuenta
                },
                onMenu = {
                    reiniciar()
                    fase = Fase.Menu
                }
            )
        }

        // Capa del destello rojo (no bloquea los toques)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(destello)
        )
    }
}