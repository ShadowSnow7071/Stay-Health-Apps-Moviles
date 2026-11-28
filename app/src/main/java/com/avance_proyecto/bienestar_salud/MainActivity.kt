package com.avance_proyecto.bienestar_salud

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.avance_proyecto.bienestar_salud.recordatorios.Recordatorios
import com.avance_proyecto.bienestar_salud.ui.theme.Bienestar_SaludTheme

class MainActivity : ComponentActivity() {

    private val vm: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Pantalla completa con íconos de las barras del sistema oscuros: la app es de fondo claro.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )

        Recordatorios.crearCanal(this)

        setContent {
            Bienestar_SaludTheme {
                AppNav(vm)
            }
        }
    }

    // Si la app queda abierta pasada la medianoche, "hoy" se actualiza al volver.
    override fun onResume() {
        super.onResume()
        vm.actualizarFecha()
    }
}
