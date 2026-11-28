package com.avance_proyecto.bienestar_salud.data

import android.content.Context

// Sesión ("mantener sesión iniciada") y ajustes de recordatorios.
class Preferencias(contexto: Context) {
    private val sp = contexto.applicationContext
        .getSharedPreferences("stay_healthy_prefs", Context.MODE_PRIVATE)

    var sesionUsuarioId: Long
        get() = sp.getLong("sesion_usuario_id", -1L)
        set(valor) { sp.edit().putLong("sesion_usuario_id", valor).apply() }

    var recordarAgua: Boolean
        get() = sp.getBoolean("recordar_agua", false)
        set(valor) { sp.edit().putBoolean("recordar_agua", valor).apply() }

    var horasEntreAgua: Int
        get() = sp.getInt("horas_entre_agua", 2)
        set(valor) { sp.edit().putInt("horas_entre_agua", valor).apply() }

    var recordarPausas: Boolean
        get() = sp.getBoolean("recordar_pausas", false)
        set(valor) { sp.edit().putBoolean("recordar_pausas", valor).apply() }
}
