package com.avance_proyecto.bienestar_salud.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.avance_proyecto.bienestar_salud.model.META_VASOS

@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["nombreUsuario"], unique = true)]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombreUsuario: String,
    val passwordHash: String
)

// Una fila por usuario y por día: check-in + agua + hábitos.
// Así se conserva el historial y se puede consultar por día.
@Entity(
    tableName = "registros_dia",
    primaryKeys = ["usuarioId", "fecha"],
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("usuarioId")]
)
data class RegistroDia(
    val usuarioId: Long,
    val fecha: String,                    // formato yyyy-MM-dd
    val sueno: String? = null,            // nombre del enum Sueno
    val energia: String? = null,          // nombre del enum Energia
    val animo: String? = null,            // nombre del enum Animo
    val vasos: Int = 0,
    val pausaHecha: Boolean = false,
    val concentracionHecha: Boolean = false
) {
    fun checkinCompleto(): Boolean = sueno != null && energia != null && animo != null

    // La hidratación se completa sola al llegar a la meta de agua.
    fun hidratacionHecha(): Boolean = vasos >= META_VASOS
}

@Entity(
    tableName = "tareas",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("usuarioId")]
)
data class Tarea(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Long,
    val nombre: String,
    val fecha: String,                    // yyyy-MM-dd
    val hora: String? = null,             // HH:mm (opcional)
    val esfuerzo: String,                 // nombre del enum Esfuerzo
    val hecha: Boolean = false,
    val fechaHecha: String? = null        // para contar tareas listas por semana
)
