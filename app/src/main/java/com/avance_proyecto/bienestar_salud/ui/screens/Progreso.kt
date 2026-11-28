package com.avance_proyecto.bienestar_salud.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.avance_proyecto.bienestar_salud.AppViewModel
import com.avance_proyecto.bienestar_salud.data.Fechas
import com.avance_proyecto.bienestar_salud.data.RegistroDia
import com.avance_proyecto.bienestar_salud.model.Animo
import com.avance_proyecto.bienestar_salud.model.Energia
import com.avance_proyecto.bienestar_salud.model.META_VASOS
import com.avance_proyecto.bienestar_salud.model.Sueno
import com.avance_proyecto.bienestar_salud.model.comoEnum
import com.avance_proyecto.bienestar_salud.ui.components.BotonIconoCuadrado
import com.avance_proyecto.bienestar_salud.ui.components.EncabezadoTab
import com.avance_proyecto.bienestar_salud.ui.components.MosaicoDato
import com.avance_proyecto.bienestar_salud.ui.components.Tarjeta
import com.avance_proyecto.bienestar_salud.ui.components.esPantallaAncha
import com.avance_proyecto.bienestar_salud.ui.theme.AzulPrimario
import com.avance_proyecto.bienestar_salud.ui.theme.AzulTexto
import com.avance_proyecto.bienestar_salud.ui.theme.BarraGrafica
import com.avance_proyecto.bienestar_salud.ui.theme.BordeCampo
import com.avance_proyecto.bienestar_salud.ui.theme.FondoApp
import com.avance_proyecto.bienestar_salud.ui.theme.FondoSuave
import com.avance_proyecto.bienestar_salud.ui.theme.TextoSecundario
import java.util.Calendar
import java.util.Locale

@Composable
fun PantallaProgreso(vm: AppViewModel, onAjustes: () -> Unit) {
    val semana by vm.semana.collectAsState()
    val tareas by vm.tareas.collectAsState()
    val detalle by vm.registroDetalle.collectAsState()
    val diaDetalle by vm.diaDetalle.collectAsState()
    val contexto = LocalContext.current

    val hoy = Fechas.hoy()
    val dias = remember(hoy) { Fechas.ultimosSieteDias(hoy) }
    val aguaPorDia = semana.associate { it.fecha to it.vasos }
    val totalAgua = dias.sumOf { aguaPorDia[it] ?: 0 }
    val tareasListas = tareas.count { tarea ->
        val hecha = tarea.fechaHecha
        tarea.hecha && hecha != null && dias.contains(hecha)
    }
    val horas = semana.mapNotNull { it.sueno.comoEnum<Sueno>()?.horasEstimadas }
    val promedioSueno: Double? = if (horas.isEmpty()) null else horas.average()

    fun elegirDia() {
        val actual = Fechas.parsear(diaDetalle)
        val dialogo = DatePickerDialog(
            contexto,
            { _, anio, mes, dia ->
                val elegida = Calendar.getInstance()
                elegida.set(anio, mes, dia)
                vm.seleccionarDia(Fechas.formatear(elegida))
            },
            actual.get(Calendar.YEAR),
            actual.get(Calendar.MONTH),
            actual.get(Calendar.DAY_OF_MONTH)
        )
        dialogo.datePicker.maxDate = System.currentTimeMillis()
        dialogo.show()
    }

    val constancia: @Composable ColumnScope.() -> Unit = {
        TarjetaConstancia(dias, aguaPorDia, hoy)
    }
    val resto: @Composable ColumnScope.() -> Unit = {
        TarjetaDetalle(diaDetalle, hoy, detalle, onElegirDia = { elegirDia() })
        TarjetaSemana(tareasListas, totalAgua, promedioSueno)
    }

    Column(Modifier.fillMaxSize().background(FondoApp)) {
        EncabezadoTab(
            etiqueta = "ÚLTIMOS 7 DÍAS",
            titulo = "Mi progreso",
            accion = { BotonIconoCuadrado(Icons.Outlined.Settings, "Ajustes", onAjustes) }
        )
        if (esPantallaAncha()) {
            Row(
                Modifier.fillMaxSize().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    content = constancia
                )
                Column(
                    Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    content = resto
                )
            }
        } else {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                constancia()
                resto()
            }
        }
    }
}

// ---------- Gráfica de agua de los últimos 7 días ----------

@Composable
private fun TarjetaConstancia(dias: List<String>, aguaPorDia: Map<String, Int>, hoy: String) {
    val resumen = "Vasos de agua por día: " +
        dias.joinToString(", ") { dia ->
            (if (dia == hoy) "Hoy" else Fechas.diaCorto(dia)) + " " + (aguaPorDia[dia] ?: 0)
        } + ". Meta: $META_VASOS al día."

    Tarjeta {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("RESUMEN SEMANAL", style = MaterialTheme.typography.labelMedium, color = TextoSecundario)
                Text(
                    "Tu constancia",
                    style = MaterialTheme.typography.titleMedium,
                    color = AzulTexto,
                    modifier = Modifier.semantics { heading() }
                )
            }
            Box(
                Modifier.clip(RoundedCornerShape(12.dp)).background(FondoSuave).padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("7 días", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = AzulTexto)
            }
        }
        Spacer(Modifier.height(16.dp))

        val alturaBarras = 120.dp
        val colorMeta = TextoSecundario
        // La gráfica se describe completa para TalkBack y se ocultan sus piezas sueltas.
        Column(Modifier.clearAndSetSemantics { contentDescription = resumen }) {
            Box(Modifier.fillMaxWidth().height(alturaBarras + 30.dp)) {
                // Línea punteada de la meta (8 vasos), en la parte alta de las barras.
                Canvas(Modifier.fillMaxSize()) {
                    val y = 30.dp.toPx()
                    drawLine(
                        color = colorMeta,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f))
                    )
                }
                Row(Modifier.fillMaxSize()) {
                    dias.forEach { dia ->
                        val vasos = aguaPorDia[dia] ?: 0
                        val llegoMeta = vasos >= META_VASOS
                        Column(
                            Modifier.weight(1f).fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (llegoMeta) {
                                    Icon(
                                        Icons.Outlined.Check,
                                        contentDescription = null,
                                        tint = AzulPrimario,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    "$vasos",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AzulTexto
                                )
                            }
                            Spacer(Modifier.height(2.dp))
                            Box(
                                Modifier
                                    .width(26.dp)
                                    .height(alturaBarras * (vasos.coerceAtMost(META_VASOS) / META_VASOS.toFloat()))
                                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                    .background(if (dia == hoy) AzulPrimario else BarraGrafica)
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth()) {
                dias.forEach { dia ->
                    Text(
                        text = if (dia == hoy) "Hoy" else Fechas.diaCorto(dia),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (dia == hoy) FontWeight.Bold else FontWeight.Normal,
                        color = if (dia == hoy) AzulTexto else TextoSecundario
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(12.dp).clip(RoundedCornerShape(3.dp)).background(AzulPrimario))
                Spacer(Modifier.width(8.dp))
                Text(
                    "Vasos de agua",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "Meta: $META_VASOS diarios (✓ = meta cumplida)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = AzulTexto
                )
            }
        }
    }
}

// ---------- Detalle de un día ----------

@Composable
private fun TarjetaDetalle(
    dia: String,
    hoy: String,
    registro: RegistroDia?,
    onElegirDia: () -> Unit
) {
    Tarjeta {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("DETALLE DEL DÍA", style = MaterialTheme.typography.labelMedium, color = TextoSecundario)
                Text(
                    Fechas.detalle(dia, hoy),
                    style = MaterialTheme.typography.titleMedium,
                    color = AzulTexto,
                    modifier = Modifier.semantics { heading() }
                )
            }
            BotonIconoCuadrado(
                icono = Icons.Outlined.CalendarMonth,
                descripcion = "Elegir otro día",
                onClick = onElegirDia,
                fondo = FondoSuave,
                borde = BordeCampo
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(
            Modifier.fillMaxWidth().height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MosaicoDato(
                Icons.Outlined.Bedtime, "Sueño",
                registro?.sueno.comoEnum<Sueno>()?.etiqueta ?: "—",
                Modifier.weight(1f).fillMaxHeight()
            )
            MosaicoDato(
                Icons.Outlined.Bolt, "Energía",
                registro?.energia.comoEnum<Energia>()?.etiqueta ?: "—",
                Modifier.weight(1f).fillMaxHeight()
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            Modifier.fillMaxWidth().height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MosaicoDato(
                Icons.Outlined.SentimentSatisfied, "Ánimo",
                registro?.animo.comoEnum<Animo>()?.etiqueta ?: "—",
                Modifier.weight(1f).fillMaxHeight()
            )
            MosaicoDato(
                Icons.Outlined.WaterDrop, "Agua",
                (registro?.vasos ?: 0).toString() + "/" + META_VASOS,
                Modifier.weight(1f).fillMaxHeight()
            )
        }
        if (registro == null) {
            Spacer(Modifier.height(10.dp))
            Text(
                "No hay registro de este día.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )
        }
    }
}

// ---------- Totales de la semana ----------

@Composable
private fun TarjetaSemana(tareasListas: Int, totalAgua: Int, promedioSueno: Double?) {
    val textoSueno = if (promedioSueno == null) "—"
    else "≈ " + String.format(Locale.US, "%.1f", promedioSueno) + " h"

    Tarjeta {
        Text(
            "Esta semana",
            style = MaterialTheme.typography.titleMedium,
            color = AzulTexto,
            modifier = Modifier.semantics { heading() }
        )
        Spacer(Modifier.height(12.dp))
        Row(
            Modifier.fillMaxWidth().height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MosaicoDato(Icons.Outlined.TaskAlt, "Tareas listas", tareasListas.toString(), Modifier.weight(1f).fillMaxHeight())
            MosaicoDato(Icons.Outlined.WaterDrop, "Vasos de agua", totalAgua.toString(), Modifier.weight(1f).fillMaxHeight())
            MosaicoDato(Icons.Outlined.Bedtime, "Sueño promedio", textoSueno, Modifier.weight(1f).fillMaxHeight())
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "El sueño promedio es una estimación a partir de los rangos que registras.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario
        )
    }
}
