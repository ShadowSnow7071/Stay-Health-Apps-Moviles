package com.avance_proyecto.bienestar_salud.ui.screens

import com.avance_proyecto.bienestar_salud.data.Tarea
import com.avance_proyecto.bienestar_salud.model.Energia
import com.avance_proyecto.bienestar_salud.model.Esfuerzo
import com.avance_proyecto.bienestar_salud.model.comoEnum
import com.avance_proyecto.bienestar_salud.model.prioridad

// Conexión energía -> tareas: energía baja pone primero las ligeras, energía alta las exigentes.
// Con la misma prioridad se ordena por fecha y luego por hora.
fun ordenarPendientes(tareas: List<Tarea>, energia: Energia?): List<Tarea> =
    tareas.filter { !it.hecha }.sortedWith(
        compareBy<Tarea>(
            { (it.esfuerzo.comoEnum<Esfuerzo>() ?: Esfuerzo.MEDIA).prioridad(energia) },
            { it.fecha },
            { it.hora ?: "99:99" }
        )
    )
