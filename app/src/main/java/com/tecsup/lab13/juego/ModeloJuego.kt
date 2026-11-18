package com.tecsup.lab13.juego

import kotlin.random.Random
// Pantallas por las que pasa el juego
enum class Fase { Menu, Cuenta, Jugando, Fin }

// Lo que cae del cielo: cada tipo tiene su emoji y sus puntos
enum class TipoObjeto(val emoji: String, val puntos: Int) {
    Estrella("⭐", 1),
    Cometa("☄️", 5),
    Bomba("💣", 0)
}

data class ObjetoQueCae(
    val id: Long,
    val tipo: TipoObjeto,
    val x: Float,          // posicion horizontal de 0f (izquierda) a 1f (derecha)
    val duracionMs: Int    // cuanto tarda en llegar al suelo
)

data class TextoFlotante(
    val id: Long,
    val texto: String,
    val x: Float,          // en pixeles
    val y: Float,
    val esMalo: Boolean
)

// Todas las reglas en un solo lugar, para ajustar la dificultad facil
object ReglasJuego {
    const val VIDAS_INICIALES = 3
    const val PUNTOS_POR_NIVEL = 15

    fun nivelPara(puntos: Int): Int = 1 + puntos / PUNTOS_POR_NIVEL

    // Cada nivel los objetos caen mas rapido...
    fun duracionCaida(nivel: Int): Int = (3200 - (nivel - 1) * 300).coerceAtLeast(1100)

    // ...y aparecen mas seguido
    fun intervaloAparicion(nivel: Int): Long = (900L - (nivel - 1) * 90L).coerceAtLeast(350L)

    // Cada 5 aciertos seguidos el multiplicador sube (maximo x5)
    fun multiplicador(combo: Int): Int = (1 + combo / 5).coerceAtMost(5)

    fun tipoAlAzar(random: Random): TipoObjeto {
        val numero = random.nextInt(100)
        return when {
            numero < 8 -> TipoObjeto.Cometa
            numero < 30 -> TipoObjeto.Bomba
            else -> TipoObjeto.Estrella
        }
    }
}