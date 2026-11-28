package com.avance_proyecto.bienestar_salud.data

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Las fechas se guardan como texto yyyy-MM-dd (ordenan bien en SQLite) y se muestran en español.
object Fechas {
    private val ES: Locale = Locale.forLanguageTag("es-MX")
    private const val PATRON = "yyyy-MM-dd"
    private val DIAS_CORTOS = arrayOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")

    private fun formato(patron: String) = SimpleDateFormat(patron, ES)

    fun hoy(): String = formatear(Calendar.getInstance())

    fun formatear(cal: Calendar): String = SimpleDateFormat(PATRON, Locale.US).format(cal.time)

    fun parsear(fecha: String): Calendar {
        val cal = Calendar.getInstance()
        SimpleDateFormat(PATRON, Locale.US).parse(fecha)?.let { cal.time = it }
        return cal
    }

    fun sumarDias(fecha: String, dias: Int): String {
        val cal = parsear(fecha)
        cal.add(Calendar.DAY_OF_YEAR, dias)
        return formatear(cal)
    }

    fun ultimosSieteDias(hoy: String): List<String> = (-6..0).map { sumarDias(hoy, it) }

    // "SÁBADO, 31 DE OCTUBRE"
    fun encabezado(fecha: String): String =
        formato("EEEE, d 'de' MMMM").format(parsear(fecha).time).uppercase(ES)

    // "Hoy, 31 de octubre" o "Jueves, 29 de octubre"
    fun detalle(fecha: String, hoy: String): String {
        val cal = parsear(fecha).time
        return if (fecha == hoy) {
            "Hoy, " + formato("d 'de' MMMM").format(cal)
        } else {
            formato("EEEE, d 'de' MMMM").format(cal).replaceFirstChar { it.uppercase(ES) }
        }
    }

    fun diaCorto(fecha: String): String = DIAS_CORTOS[parsear(fecha).get(Calendar.DAY_OF_WEEK) - 1]

    // "31/10/2026"
    fun dmY(fecha: String): String = formato("dd/MM/yyyy").format(parsear(fecha).time)

    // "Hoy", "Mañana" o "31 oct"
    fun etiqueta(fecha: String, hoy: String): String {
        return when (fecha) {
            hoy -> "Hoy"
            sumarDias(hoy, 1) -> "Mañana"
            else -> formato("d MMM").format(parsear(fecha).time)
        }
    }

    fun hora(hora: Int, minuto: Int): String = String.format(Locale.US, "%02d:%02d", hora, minuto)

    fun saludo(): String {
        val h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            h < 12 -> "Buenos días"
            h < 19 -> "Buenas tardes"
            else -> "Buenas noches"
        }
    }
}
