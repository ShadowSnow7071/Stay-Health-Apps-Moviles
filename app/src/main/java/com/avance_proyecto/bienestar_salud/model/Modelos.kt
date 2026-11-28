package com.avance_proyecto.bienestar_salud.model

const val NOMBRE_APP = "Stay Healthy"
const val META_VASOS = 8

// Rangos de sueño sin huecos ni traslapes (feedback del profe).
enum class Sueno(val etiqueta: String, val horasEstimadas: Double) {
    MENOS_5("Menos de 5 h", 4.0),
    DE_5_A_7("5 a menos de 7 h", 6.0),
    DE_7_A_9("7 a menos de 9 h", 8.0),
    MAS_9("9 h o más", 9.5)
}

enum class Energia(val etiqueta: String, val sugerencia: String, val orden: String) {
    BAJA("Baja", "Hoy te conviene empezar por tareas ligeras", "Primero las tareas ligeras."),
    MEDIA("Media", "Combina tareas ligeras y medianas", "Primero las tareas medianas y ligeras."),
    ALTA("Alta", "Buen día para avanzar en las tareas más demandantes", "Primero las tareas más exigentes.")
}

enum class Animo(val etiqueta: String) {
    FELIZ("Feliz"),
    TRISTE("Triste"),
    ENOJADO("Enojado"),
    ABURRIDO("Aburrido"),
    CANSADO("Cansado")
}

enum class Esfuerzo(val etiqueta: String, val descripcion: String, val nivel: Int) {
    LIGERA("Ligera", "Poca energía", 1),
    MEDIA("Media", "Energía moderada", 2),
    EXIGENTE("Exigente", "Mucha energía", 3)
}

// Convierte el texto guardado en la base de datos de vuelta a su enum (o null si no hay dato).
inline fun <reified T : Enum<T>> String?.comoEnum(): T? =
    enumValues<T>().firstOrNull { it.name == this }

// Conexión energía -> tareas: menor número = va primero en la lista.
fun Esfuerzo.prioridad(energia: Energia?): Int {
    return when (energia ?: Energia.MEDIA) {
        Energia.BAJA -> when (this) {
            Esfuerzo.LIGERA -> 0
            Esfuerzo.MEDIA -> 1
            Esfuerzo.EXIGENTE -> 2
        }
        Energia.MEDIA -> when (this) {
            Esfuerzo.MEDIA -> 0
            Esfuerzo.LIGERA -> 1
            Esfuerzo.EXIGENTE -> 2
        }
        Energia.ALTA -> when (this) {
            Esfuerzo.EXIGENTE -> 0
            Esfuerzo.MEDIA -> 1
            Esfuerzo.LIGERA -> 2
        }
    }
}
