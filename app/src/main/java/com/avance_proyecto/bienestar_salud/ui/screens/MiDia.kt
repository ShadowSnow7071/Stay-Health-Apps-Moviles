package com.avance_proyecto.bienestar_salud.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CenterFocusStrong
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.LocalDrink
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.avance_proyecto.bienestar_salud.AppViewModel
import com.avance_proyecto.bienestar_salud.data.Fechas
import com.avance_proyecto.bienestar_salud.data.RegistroDia
import com.avance_proyecto.bienestar_salud.data.Tarea
import com.avance_proyecto.bienestar_salud.model.Animo
import com.avance_proyecto.bienestar_salud.model.Energia
import com.avance_proyecto.bienestar_salud.model.Esfuerzo
import com.avance_proyecto.bienestar_salud.model.META_VASOS
import com.avance_proyecto.bienestar_salud.model.Sueno
import com.avance_proyecto.bienestar_salud.model.comoEnum
import com.avance_proyecto.bienestar_salud.ui.components.BarraProgreso
import com.avance_proyecto.bienestar_salud.ui.components.BotonCirculo
import com.avance_proyecto.bienestar_salud.ui.components.BotonIconoCuadrado
import com.avance_proyecto.bienestar_salud.ui.components.BotonPrincipal
import com.avance_proyecto.bienestar_salud.ui.components.BotonSecundario
import com.avance_proyecto.bienestar_salud.ui.components.EncabezadoTab
import com.avance_proyecto.bienestar_salud.ui.components.EtiquetaEsfuerzo
import com.avance_proyecto.bienestar_salud.ui.components.FilaFechaTarea
import com.avance_proyecto.bienestar_salud.ui.components.MosaicoDato
import com.avance_proyecto.bienestar_salud.ui.components.SeparadorSuave
import com.avance_proyecto.bienestar_salud.ui.components.Tarjeta
import com.avance_proyecto.bienestar_salud.ui.components.esPantallaAncha
import com.avance_proyecto.bienestar_salud.ui.theme.AzulPrimario
import com.avance_proyecto.bienestar_salud.ui.theme.AzulTexto
import com.avance_proyecto.bienestar_salud.ui.theme.BordeCampo
import com.avance_proyecto.bienestar_salud.ui.theme.FondoApp
import com.avance_proyecto.bienestar_salud.ui.theme.FondoBanner
import com.avance_proyecto.bienestar_salud.ui.theme.FondoSuave
import com.avance_proyecto.bienestar_salud.ui.theme.TextoSecundario

@Composable
fun PantallaMiDia(
    vm: AppViewModel,
    onAjustes: () -> Unit,
    onEditarCheckin: () -> Unit,
    onAbrirTarea: (Long) -> Unit,
    onAgregarTarea: () -> Unit,
    onVerTodas: () -> Unit
) {
    val registro by vm.registroHoy.collectAsState()
    val tareas by vm.tareas.collectAsState()
    val hoy = Fechas.hoy()
    val energia: Energia? = registro?.energia.comoEnum<Energia>()
    val pendientes = remember(tareas, energia) { ordenarPendientes(tareas, energia).take(3) }
    val vista = registro ?: RegistroDia(usuarioId = 0, fecha = hoy)

    val columnaIzquierda: @Composable ColumnScope.() -> Unit = {
        if (vista.checkinCompleto()) {
            TarjetaCheckin(vista, onEditarCheckin)
        } else {
            TarjetaSinCheckin(onEditarCheckin)
        }
        if (energia != null) BannerEnergia(energia)
        SeccionTareasHoy(
            pendientes = pendientes,
            hoy = hoy,
            onAbrir = onAbrirTarea,
            onAlternar = { vm.alternarTarea(it) },
            onVerTodas = onVerTodas,
            onAgregar = onAgregarTarea
        )
    }
    val columnaDerecha: @Composable ColumnScope.() -> Unit = {
        TarjetaAgua(
            vasos = vista.vasos,
            onMas = { vm.sumarVaso() },
            onMenos = { vm.restarVaso() }
        )
        TarjetaHabitos(
            registro = vista,
            onPausa = { vm.alternarPausa() },
            onConcentracion = { vm.alternarConcentracion() }
        )
    }

    Column(Modifier.fillMaxSize().background(FondoApp)) {
        EncabezadoTab(
            etiqueta = Fechas.encabezado(hoy),
            titulo = "Mi día",
            accion = {
                BotonIconoCuadrado(Icons.Outlined.Settings, "Ajustes", onAjustes)
            }
        )
        if (esPantallaAncha()) {
            Row(
                Modifier.fillMaxSize().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    content = columnaIzquierda
                )
                Column(
                    Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    content = columnaDerecha
                )
            }
        } else {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                columnaIzquierda()
                columnaDerecha()
            }
        }
    }
}

// ---------- Check-in ----------

@Composable
private fun TarjetaCheckin(registro: RegistroDia, onEditar: () -> Unit) {
    Tarjeta {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("CHECK-IN DE HOY", style = MaterialTheme.typography.labelMedium, color = TextoSecundario)
                Text(
                    "Tu punto de partida",
                    style = MaterialTheme.typography.titleMedium,
                    color = AzulTexto,
                    modifier = Modifier.semantics { heading() }
                )
            }
            TextButton(onClick = onEditar) {
                Icon(Icons.Outlined.Edit, contentDescription = null, tint = AzulPrimario, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Editar", fontWeight = FontWeight.Bold, color = AzulPrimario)
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(
            Modifier.fillMaxWidth().height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MosaicoDato(
                Icons.Outlined.Bedtime, "Sueño",
                registro.sueno.comoEnum<Sueno>()?.etiqueta ?: "—",
                Modifier.weight(1f).fillMaxHeight()
            )
            MosaicoDato(
                Icons.Outlined.Bolt, "Energía",
                registro.energia.comoEnum<Energia>()?.etiqueta ?: "—",
                Modifier.weight(1f).fillMaxHeight()
            )
            MosaicoDato(
                Icons.Outlined.SentimentSatisfied, "Ánimo",
                registro.animo.comoEnum<Animo>()?.etiqueta ?: "—",
                Modifier.weight(1f).fillMaxHeight()
            )
        }
    }
}

@Composable
private fun TarjetaSinCheckin(onHacerCheckin: () -> Unit) {
    Tarjeta {
        Text("Aún no haces tu check-in de hoy", style = MaterialTheme.typography.titleMedium, color = AzulTexto)
        Spacer(Modifier.height(4.dp))
        Text(
            "Cuéntanos cómo te sientes para organizar tu día.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario
        )
        Spacer(Modifier.height(12.dp))
        BotonPrincipal("Hacer check-in", onHacerCheckin)
    }
}

// Sugerencia según la energía registrada (conexión energía -> tareas).
@Composable
private fun BannerEnergia(energia: Energia) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(FondoBanner)
            .height(IntrinsicSize.Min)
    ) {
        Box(Modifier.width(4.dp).fillMaxHeight().background(AzulPrimario))
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Lightbulb, contentDescription = null, tint = AzulPrimario, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    "SEGÚN TU ENERGÍA " + energia.etiqueta.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = TextoSecundario
                )
                Text(
                    energia.sugerencia,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = AzulTexto
                )
            }
        }
    }
}

// ---------- Tareas para hoy ----------

@Composable
private fun SeccionTareasHoy(
    pendientes: List<Tarea>,
    hoy: String,
    onAbrir: (Long) -> Unit,
    onAlternar: (Tarea) -> Unit,
    onVerTodas: () -> Unit,
    onAgregar: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("PRIORIDAD SUGERIDA", style = MaterialTheme.typography.labelMedium, color = TextoSecundario)
                Text(
                    "Tareas para hoy",
                    style = MaterialTheme.typography.titleMedium,
                    color = AzulTexto,
                    modifier = Modifier.semantics { heading() }
                )
            }
            TextButton(onClick = onVerTodas) {
                Text("Ver todas", fontWeight = FontWeight.Bold, color = AzulPrimario)
            }
        }
        if (pendientes.isEmpty()) {
            Text(
                "No tienes tareas pendientes. Es un buen momento para descansar o planear algo nuevo.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextoSecundario
            )
        }
        pendientes.forEach { tarea ->
            TarjetaTareaCorta(tarea, hoy, onAbrir, onAlternar)
        }
        BotonSecundario("Agregar tarea", onAgregar, icono = Icons.Outlined.Add)
    }
}

@Composable
private fun TarjetaTareaCorta(
    tarea: Tarea,
    hoy: String,
    onAbrir: (Long) -> Unit,
    onAlternar: (Tarea) -> Unit
) {
    val esfuerzo: Esfuerzo = tarea.esfuerzo.comoEnum<Esfuerzo>() ?: Esfuerzo.MEDIA
    Tarjeta(Modifier.clickable(onClickLabel = "Editar tarea") { onAbrir(tarea.id) }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BotonCirculo(
                hecha = tarea.hecha,
                descripcion = "Marcar como hecha: " + tarea.nombre,
                onClick = { onAlternar(tarea) }
            )
            Spacer(Modifier.width(4.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    tarea.nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = AzulTexto
                )
                EtiquetaEsfuerzo(esfuerzo)
                FilaFechaTarea(tarea, hoy)
            }
            Icon(
                Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = AzulTexto
            )
        }
    }
}

// ---------- Agua ----------

@Composable
private fun TarjetaAgua(vasos: Int, onMas: () -> Unit, onMenos: () -> Unit) {
    Tarjeta {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(FondoSuave),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.WaterDrop, contentDescription = null, tint = AzulPrimario)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Agua", style = MaterialTheme.typography.titleMedium, color = AzulTexto)
                Text(
                    "Meta diaria: $META_VASOS vasos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario
                )
            }
            Text(
                "$vasos/$META_VASOS vasos",
                style = MaterialTheme.typography.titleMedium,
                color = AzulTexto,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
            )
        }
        Spacer(Modifier.height(12.dp))
        BarraProgreso(vasos / META_VASOS.toFloat(), "Vasos de agua: $vasos de $META_VASOS")
        Spacer(Modifier.height(12.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BotonIconoCuadrado(
                icono = Icons.Outlined.Remove,
                descripcion = "Quitar un vaso",
                onClick = onMenos,
                enabled = vasos > 0,
                borde = BordeCampo,
                tamano = 52.dp
            )
            Text(
                "Meta: $META_VASOS vasos",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = AzulTexto
            )
            BotonIconoCuadrado(
                icono = Icons.Outlined.Add,
                descripcion = "Agregar un vaso",
                onClick = onMas,
                enabled = vasos < META_VASOS,
                borde = BordeCampo,
                tamano = 52.dp
            )
        }
    }
}

// ---------- Hábitos ----------

@Composable
private fun TarjetaHabitos(registro: RegistroDia, onPausa: () -> Unit, onConcentracion: () -> Unit) {
    val completados = listOf(
        registro.pausaHecha,
        registro.concentracionHecha,
        registro.hidratacionHecha()
    ).count { it }

    Tarjeta {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("BIENESTAR", style = MaterialTheme.typography.labelMedium, color = TextoSecundario)
                Text(
                    "Hábitos del día",
                    style = MaterialTheme.typography.titleMedium,
                    color = AzulTexto,
                    modifier = Modifier.semantics { heading() }
                )
            }
            Box(
                Modifier.clip(RoundedCornerShape(12.dp)).background(FondoSuave).padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    "$completados/3",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = AzulTexto
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        SeparadorSuave()
        FilaHabito(Icons.Outlined.SelfImprovement, "Pausa activa", null, registro.pausaHecha, onPausa)
        SeparadorSuave()
        FilaHabito(Icons.Outlined.CenterFocusStrong, "Concentración", null, registro.concentracionHecha, onConcentracion)
        SeparadorSuave()
        FilaHabito(
            Icons.Outlined.LocalDrink, "Hidratación", "Se completa con tu meta de agua",
            registro.hidratacionHecha(), null
        )

        if (completados == 3) {
            Spacer(Modifier.height(12.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(FondoBanner)
                    .padding(14.dp)
                    .semantics { liveRegion = LiveRegionMode.Polite },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.TaskAlt, contentDescription = null, tint = AzulPrimario, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("¡Día completado!", style = MaterialTheme.typography.titleMedium, color = AzulTexto)
                    Text(
                        "Cumpliste tus 3 hábitos de hoy.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario
                    )
                }
            }
        }
    }
}

@Composable
private fun FilaHabito(
    icono: ImageVector,
    nombre: String,
    detalle: String?,
    hecho: Boolean,
    onAlternar: (() -> Unit)?
) {
    val base = Modifier.fillMaxWidth().heightIn(min = 56.dp)
    val modificador = if (onAlternar != null) {
        base.toggleable(value = hecho, role = Role.Checkbox, onValueChange = { onAlternar() })
    } else {
        base.semantics(mergeDescendants = true) {
            stateDescription = if (hecho) "Completado" else "Pendiente"
        }
    }
    Row(modificador.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = AzulPrimario, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(nombre, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = AzulTexto)
            if (detalle != null) {
                Text(detalle, style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
            }
        }
        Icon(
            imageVector = if (hecho) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
            contentDescription = null,
            tint = AzulPrimario,
            modifier = Modifier.size(28.dp)
        )
    }
}
