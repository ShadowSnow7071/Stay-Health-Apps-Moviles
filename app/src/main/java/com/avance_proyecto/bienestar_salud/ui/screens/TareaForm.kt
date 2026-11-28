package com.avance_proyecto.bienestar_salud.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.avance_proyecto.bienestar_salud.AppViewModel
import com.avance_proyecto.bienestar_salud.data.Fechas
import com.avance_proyecto.bienestar_salud.model.Esfuerzo
import com.avance_proyecto.bienestar_salud.model.comoEnum
import com.avance_proyecto.bienestar_salud.ui.components.BotonPrincipal
import com.avance_proyecto.bienestar_salud.ui.components.BotonSecundario
import com.avance_proyecto.bienestar_salud.ui.components.CampoTexto
import com.avance_proyecto.bienestar_salud.ui.components.EncabezadoAtras
import com.avance_proyecto.bienestar_salud.ui.components.IconoEsfuerzo
import com.avance_proyecto.bienestar_salud.ui.components.MensajeError
import com.avance_proyecto.bienestar_salud.ui.components.Tarjeta
import com.avance_proyecto.bienestar_salud.ui.theme.AzulPrimario
import com.avance_proyecto.bienestar_salud.ui.theme.AzulTexto
import com.avance_proyecto.bienestar_salud.ui.theme.BordeCampo
import com.avance_proyecto.bienestar_salud.ui.theme.ChipBorde
import com.avance_proyecto.bienestar_salud.ui.theme.ChipFondo
import com.avance_proyecto.bienestar_salud.ui.theme.FondoApp
import com.avance_proyecto.bienestar_salud.ui.theme.FondoSuave
import com.avance_proyecto.bienestar_salud.ui.theme.TextoSecundario
import java.util.Calendar

// id == 0 -> tarea nueva; id > 0 -> editar una tarea existente.
@Composable
fun PantallaTareaForm(vm: AppViewModel, id: Long, onVolver: () -> Unit) {
    val contexto = LocalContext.current

    var cargado by rememberSaveable { mutableStateOf(id == 0L) }
    var nombre by rememberSaveable { mutableStateOf("") }
    var fecha by rememberSaveable { mutableStateOf(Fechas.hoy()) }
    var hora by rememberSaveable { mutableStateOf<String?>(null) }
    var esfuerzo by rememberSaveable { mutableStateOf(Esfuerzo.MEDIA.name) }
    var mostrarError by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(id) {
        if (!cargado) {
            val existente = vm.obtenerTarea(id)
            if (existente != null) {
                nombre = existente.nombre
                fecha = existente.fecha
                hora = existente.hora
                esfuerzo = existente.esfuerzo
            }
            cargado = true
        }
    }

    val esfuerzoSel: Esfuerzo = esfuerzo.comoEnum<Esfuerzo>() ?: Esfuerzo.MEDIA

    fun elegirFecha() {
        val actual = Fechas.parsear(fecha)
        DatePickerDialog(
            contexto,
            { _, anio, mes, dia ->
                val elegida = Calendar.getInstance()
                elegida.set(anio, mes, dia)
                fecha = Fechas.formatear(elegida)
            },
            actual.get(Calendar.YEAR),
            actual.get(Calendar.MONTH),
            actual.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun elegirHora() {
        val partes = hora?.split(":")
        val h = partes?.getOrNull(0)?.toIntOrNull() ?: 12
        val m = partes?.getOrNull(1)?.toIntOrNull() ?: 0
        TimePickerDialog(
            contexto,
            { _, hh, mm -> hora = Fechas.hora(hh, mm) },
            h,
            m,
            true
        ).show()
    }

    fun guardar() {
        if (nombre.isBlank()) {
            mostrarError = true
            return
        }
        vm.guardarTarea(id, nombre, fecha, hora, esfuerzoSel)
        onVolver()
    }

    Column(Modifier.fillMaxSize().background(FondoApp).imePadding()) {
        EncabezadoAtras(if (id == 0L) "Nueva tarea" else "Editar tarea", onVolver)

        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            Column(
                Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Tarjeta {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(FondoSuave),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.EditNote, contentDescription = null, tint = AzulPrimario)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "¿Qué necesitas hacer?",
                                style = MaterialTheme.typography.titleMedium,
                                color = AzulTexto,
                                modifier = Modifier.semantics { heading() }
                            )
                            Text(
                                "Agrega los detalles para organizarla según tu energía.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextoSecundario
                            )
                        }
                    }
                    Spacer(Modifier.height(20.dp))

                    CampoTexto(
                        etiqueta = "Nombre de la tarea *",
                        valor = nombre,
                        onCambio = { nombre = it; mostrarError = false },
                        placeholder = "Ej. Preparar exposición",
                        imeAction = ImeAction.Done,
                        isError = mostrarError && nombre.isBlank()
                    )
                    if (mostrarError && nombre.isBlank()) {
                        Spacer(Modifier.height(8.dp))
                        MensajeError("Escribe un nombre para la tarea")
                    }
                    Spacer(Modifier.height(16.dp))

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CampoSelector(
                            etiqueta = "Fecha",
                            valor = Fechas.dmY(fecha),
                            esVacio = false,
                            icono = Icons.Outlined.CalendarMonth,
                            onClick = { elegirFecha() },
                            modifier = Modifier.weight(1f)
                        )
                        CampoSelector(
                            etiqueta = "Hora (opcional)",
                            valor = hora ?: "--:--",
                            esVacio = hora == null,
                            icono = Icons.Outlined.Schedule,
                            onClick = { elegirHora() },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (hora != null) {
                        TextButton(onClick = { hora = null }) {
                            Text("Quitar hora", color = AzulPrimario, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    Text("Esfuerzo", style = MaterialTheme.typography.labelMedium, color = AzulTexto)
                    Text(
                        "¿Cuánta energía requiere?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario
                    )
                    Spacer(Modifier.height(8.dp))
                    Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Esfuerzo.values().forEach { opcion ->
                            OpcionEsfuerzo(
                                esfuerzo = opcion,
                                seleccionada = opcion == esfuerzoSel,
                                onClick = { esfuerzo = opcion.name }
                            )
                        }
                    }
                }
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BotonSecundario("Cancelar", onVolver, Modifier.weight(1f))
            BotonPrincipal("Guardar", onClick = { guardar() }, modifier = Modifier.weight(1f), icono = Icons.Outlined.Check)
        }
    }
}

// Campo que abre un selector de fecha u hora (no se escribe a mano).
@Composable
private fun CampoSelector(
    etiqueta: String,
    valor: String,
    esVacio: Boolean,
    icono: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val forma = RoundedCornerShape(14.dp)
    Column(modifier) {
        Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = AzulTexto)
        Spacer(Modifier.height(8.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .clip(forma)
                .background(Color.White, forma)
                .border(1.dp, BordeCampo, forma)
                .clickable(role = Role.Button, onClickLabel = "Cambiar " + etiqueta.lowercase(), onClick = onClick)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                valor,
                style = MaterialTheme.typography.bodyLarge,
                color = if (esVacio) TextoSecundario else AzulTexto,
                modifier = Modifier.weight(1f)
            )
            Icon(icono, contentDescription = null, tint = AzulPrimario)
        }
    }
}

@Composable
private fun OpcionEsfuerzo(esfuerzo: Esfuerzo, seleccionada: Boolean, onClick: () -> Unit) {
    val forma = RoundedCornerShape(14.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .clip(forma)
            .background(if (seleccionada) ChipFondo else Color.White, forma)
            .border(
                width = if (seleccionada) 2.dp else 1.dp,
                color = if (seleccionada) ChipBorde else BordeCampo,
                shape = forma
            )
            .selectable(selected = seleccionada, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconoEsfuerzo(esfuerzo.nivel)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                esfuerzo.etiqueta,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = AzulTexto
            )
            Text(esfuerzo.descripcion, style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
        }
        if (seleccionada) {
            Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = AzulTexto)
        }
    }
}
