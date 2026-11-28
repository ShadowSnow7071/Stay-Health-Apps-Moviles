package com.avance_proyecto.bienestar_salud.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.avance_proyecto.bienestar_salud.AppViewModel
import com.avance_proyecto.bienestar_salud.data.Fechas
import com.avance_proyecto.bienestar_salud.data.Tarea
import com.avance_proyecto.bienestar_salud.model.Energia
import com.avance_proyecto.bienestar_salud.model.Esfuerzo
import com.avance_proyecto.bienestar_salud.model.comoEnum
import com.avance_proyecto.bienestar_salud.ui.components.BotonCirculo
import com.avance_proyecto.bienestar_salud.ui.components.BotonIconoCuadrado
import com.avance_proyecto.bienestar_salud.ui.components.BotonPrincipal
import com.avance_proyecto.bienestar_salud.ui.components.EncabezadoTab
import com.avance_proyecto.bienestar_salud.ui.components.EtiquetaEsfuerzo
import com.avance_proyecto.bienestar_salud.ui.components.FilaFechaTarea
import com.avance_proyecto.bienestar_salud.ui.components.Tarjeta
import com.avance_proyecto.bienestar_salud.ui.theme.AzulPrimario
import com.avance_proyecto.bienestar_salud.ui.theme.AzulTexto
import com.avance_proyecto.bienestar_salud.ui.theme.BordeSuave
import com.avance_proyecto.bienestar_salud.ui.theme.FondoApp
import com.avance_proyecto.bienestar_salud.ui.theme.FondoBanner
import com.avance_proyecto.bienestar_salud.ui.theme.FondoSuave
import com.avance_proyecto.bienestar_salud.ui.theme.RojoEliminar
import com.avance_proyecto.bienestar_salud.ui.theme.TextoSecundario

@Composable
fun PantallaTareas(
    vm: AppViewModel,
    onAgregar: () -> Unit,
    onEditar: (Long) -> Unit
) {
    val registro by vm.registroHoy.collectAsState()
    val tareas by vm.tareas.collectAsState()
    val hoy = Fechas.hoy()
    val energia: Energia? = registro?.energia.comoEnum<Energia>()

    // Pendientes ordenadas según la energía del día y, al final, las que ya están hechas.
    val ordenadas = remember(tareas, energia) {
        ordenarPendientes(tareas, energia) + tareas.filter { it.hecha }.sortedBy { it.fecha }
    }
    var porEliminar by remember { mutableStateOf<Tarea?>(null) }

    Box(Modifier.fillMaxSize().background(FondoApp)) {
        Column(Modifier.fillMaxSize()) {
            EncabezadoTab(
                etiqueta = "ORGANIZA SEGÚN TU ENERGÍA",
                titulo = "Mis tareas",
                accion = { ChipEnergia(energia) { vm.cambiarEnergia(it) } }
            )
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                if (tareas.isEmpty()) {
                    EstadoVacio(onAgregar)
                } else {
                    LazyColumn(
                        Modifier.widthIn(max = 720.dp).fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 104.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item { InfoOrden(energia) }
                        items(ordenadas, key = { it.id }) { tarea ->
                            TarjetaTarea(
                                tarea = tarea,
                                hoy = hoy,
                                onAlternar = { vm.alternarTarea(tarea) },
                                onEditar = { onEditar(tarea.id) },
                                onEliminar = { porEliminar = tarea }
                            )
                        }
                    }
                }
            }
        }

        if (tareas.isNotEmpty()) {
            ExtendedFloatingActionButton(
                onClick = onAgregar,
                containerColor = AzulPrimario,
                contentColor = Color.White,
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                icon = { Icon(Icons.Outlined.Add, contentDescription = null) },
                text = { Text("Agregar tarea", style = MaterialTheme.typography.labelLarge) }
            )
        }
    }

    val pendiente = porEliminar
    if (pendiente != null) {
        AlertDialog(
            onDismissRequest = { porEliminar = null },
            containerColor = Color.White,
            title = { Text("Eliminar tarea", color = AzulTexto) },
            text = {
                Text(
                    "¿Seguro que quieres eliminar «" + pendiente.nombre + "»? Esta acción no se puede deshacer.",
                    color = TextoSecundario
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.eliminarTarea(pendiente)
                    porEliminar = null
                }) { Text("Eliminar", color = RojoEliminar, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { porEliminar = null }) {
                    Text("Cancelar", color = AzulPrimario, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// Botón del encabezado: muestra la energía de hoy y permite cambiarla.
@Composable
private fun ChipEnergia(energia: Energia?, onElegir: (Energia) -> Unit) {
    var abierto by remember { mutableStateOf(false) }
    val forma = RoundedCornerShape(24.dp)
    Box {
        Row(
            Modifier
                .heightIn(min = 48.dp)
                .clip(forma)
                .background(Color.White, forma)
                .border(1.5.dp, AzulPrimario, forma)
                .clickable(role = Role.Button, onClickLabel = "Cambiar nivel de energía") { abierto = true }
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Bolt, contentDescription = null, tint = AzulPrimario, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(
                "Energía: " + (energia?.etiqueta ?: "sin registrar"),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = AzulTexto
            )
            Icon(Icons.Outlined.ArrowDropDown, contentDescription = null, tint = AzulTexto)
        }
        DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            Energia.values().forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion.etiqueta) },
                    onClick = {
                        onElegir(opcion)
                        abierto = false
                    }
                )
            }
        }
    }
}

@Composable
private fun InfoOrden(energia: Energia?) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(FondoBanner)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.Sort, contentDescription = null, tint = AzulPrimario, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text("Ordenadas para ti", style = MaterialTheme.typography.titleMedium, color = AzulTexto)
            Text(
                energia?.orden ?: "Registra tu energía para ordenar tus tareas.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )
        }
    }
}

@Composable
private fun TarjetaTarea(
    tarea: Tarea,
    hoy: String,
    onAlternar: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    val esfuerzo: Esfuerzo = tarea.esfuerzo.comoEnum<Esfuerzo>() ?: Esfuerzo.MEDIA
    Tarjeta {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BotonCirculo(
                hecha = tarea.hecha,
                descripcion = if (tarea.hecha) "Marcar como pendiente: " + tarea.nombre
                else "Marcar como hecha: " + tarea.nombre,
                onClick = onAlternar
            )
            Spacer(Modifier.width(4.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                // Sin límite de líneas: el nombre nunca se corta con "...".
                Text(
                    tarea.nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (tarea.hecha) TextoSecundario else AzulTexto,
                    textDecoration = if (tarea.hecha) TextDecoration.LineThrough else TextDecoration.None
                )
                if (tarea.hecha) {
                    Text("Hecha", style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
                } else {
                    EtiquetaEsfuerzo(esfuerzo)
                    FilaFechaTarea(tarea, hoy)
                }
            }
            Spacer(Modifier.width(8.dp))
            BotonIconoCuadrado(
                icono = Icons.Outlined.Edit,
                descripcion = "Editar tarea: " + tarea.nombre,
                onClick = onEditar,
                fondo = FondoSuave,
                borde = BordeSuave
            )
            Spacer(Modifier.width(8.dp))
            BotonIconoCuadrado(
                icono = Icons.Outlined.Delete,
                descripcion = "Eliminar tarea: " + tarea.nombre,
                onClick = onEliminar,
                fondo = FondoSuave,
                tinte = RojoEliminar,
                borde = BordeSuave
            )
        }
    }
}

@Composable
private fun EstadoVacio(onAgregar: () -> Unit) {
    Column(
        Modifier.widthIn(max = 420.dp).fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Outlined.TaskAlt, contentDescription = null, tint = AzulPrimario, modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(16.dp))
        Text(
            "Aún no tienes tareas",
            style = MaterialTheme.typography.titleLarge,
            color = AzulTexto,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Agrega la primera y la organizamos según tu energía de hoy.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextoSecundario,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        BotonPrincipal("Agregar tarea", onAgregar, icono = Icons.Outlined.Add)
    }
}
