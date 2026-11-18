package com.tecsup.lab13

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.tecsup.lab13.juego.Cazaestrellas

// Conecta el juego con la app y guarda el record en el celular
@Composable
fun EjercicioFinalJuego() {
    val context = LocalContext.current
    val preferencias = remember {
        context.getSharedPreferences("cazaestrellas", Context.MODE_PRIVATE)
    }
    var record by remember { mutableIntStateOf(preferencias.getInt("record", 0)) }

    Cazaestrellas(
        record = record,
        onNuevoRecord = { nuevo ->
            record = nuevo
            preferencias.edit().putInt("record", nuevo).apply()
        }
    )
}