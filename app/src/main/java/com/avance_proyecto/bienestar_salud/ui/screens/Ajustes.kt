package com.avance_proyecto.bienestar_salud.ui.screens

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.avance_proyecto.bienestar_salud.AppViewModel
import com.avance_proyecto.bienestar_salud.model.NOMBRE_APP
import com.avance_proyecto.bienestar_salud.recordatorios.Recordatorios
import com.avance_proyecto.bienestar_salud.ui.components.BotonSecundario
import com.avance_proyecto.bienestar_salud.ui.components.ChipOpcion
import com.avance_proyecto.bienestar_salud.ui.components.EncabezadoAtras
import com.avance_proyecto.bienestar_salud.ui.components.SeparadorSuave
import com.avance_proyecto.bienestar_salud.ui.components.Tarjeta
import com.avance_proyecto.bienestar_salud.ui.theme.AzulPrimario
import com.avance_proyecto.bienestar_salud.ui.theme.AzulTexto
import com.avance_proyecto.bienestar_salud.ui.theme.BordeCampo
import com.avance_proyecto.bienestar_salud.ui.theme.FondoApp
import com.avance_proyecto.bienestar_salud.ui.theme.RojoEliminar
import com.avance_proyecto.bienestar_salud.ui.theme.TextoSecundario

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PantallaAjustes(vm: AppViewModel, onAtras: () -> Unit, onSesionCerrada: () -> Unit) {
    val contexto = LocalContext.current
    val usuario by vm.usuario.collectAsState()
    val prefs = vm.prefs

    var aguaActiva by remember { mutableStateOf(prefs.recordarAgua) }
    var pausasActivas by remember { mutableStateOf(prefs.recordarPausas) }
    var horasAgua by remember { mutableIntStateOf(prefs.horasEntreAgua) }
    var confirmarEliminar by remember { mutableStateOf(false) }
    // Qué recordatorio esperaba el permiso de notificaciones ("agua" o "pausas").
    var esperandoPermiso by remember { mutableStateOf<String?>(null) }

    fun activarAgua() {
        aguaActiva = true
        prefs.recordarAgua = true
        Recordatorios.programarAgua(contexto, horasAgua)
    }

    fun activarPausas() {
        pausasActivas = true
        prefs.recordarPausas = true
        Recordatorios.programarPausas(contexto)
    }

    val pedirPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        val cual = esperandoPermiso
        esperandoPermiso = null
        if (concedido) {
            if (cual == "agua") activarAgua()
            if (cual == "pausas") activarPausas()
        } else {
            Toast.makeText(
                contexto,
                "Sin permiso de notificaciones no podemos mostrar los recordatorios.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun solicitar(tipo: String) {
        if (Recordatorios.tienePermiso(contexto)) {
            if (tipo == "agua") activarAgua() else activarPausas()
        } else if (Build.VERSION.SDK_INT >= 33) {
            esperandoPermiso = tipo
            pedirPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Column(Modifier.fillMaxSize().background(FondoApp)) {
        EncabezadoAtras("Ajustes", onAtras)

        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            Column(
                Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ---- Cuenta ----
                Tarjeta {
                    Text(
                        "Mi cuenta",
                        style = MaterialTheme.typography.titleMedium,
                        color = AzulTexto,
                        modifier = Modifier.semantics { heading() }
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Person, contentDescription = null, tint = AzulPrimario)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Usuario", style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
                            Text(
                                usuario?.nombreUsuario ?: "",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = AzulTexto
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    BotonSecundario(
                        texto = "Cerrar sesión",
                        onClick = {
                            vm.cerrarSesion()
                            onSesionCerrada()
                        },
                        icono = Icons.AutoMirrored.Outlined.Logout
                    )
                    Spacer(Modifier.height(8.dp))
                    TextButton(
                        onClick = { confirmarEliminar = true },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                    ) {
                        Icon(Icons.Outlined.DeleteForever, contentDescription = null, tint = RojoEliminar)
                        Spacer(Modifier.width(8.dp))
                        Text("Eliminar cuenta y datos", color = RojoEliminar, fontWeight = FontWeight.Bold)
                    }
                }

                // ---- Recordatorios ----
                Tarjeta {
                    Text(
                        "Recordatorios",
                        style = MaterialTheme.typography.titleMedium,
                        color = AzulTexto,
                        modifier = Modifier.semantics { heading() }
                    )
                    Spacer(Modifier.height(4.dp))
                    FilaInterruptor(
                        icono = Icons.Outlined.WaterDrop,
                        titulo = "Recordatorio de agua",
                        detalle = "Te avisa para tomar un vaso (de 8:00 a 22:00).",
                        activo = aguaActiva,
                        onCambio = { quiere ->
                            if (quiere) {
                                solicitar("agua")
                            } else {
                                aguaActiva = false
                                prefs.recordarAgua = false
                                Recordatorios.cancelarAgua(contexto)
                            }
                        }
                    )
                    if (aguaActiva) {
                        Text(
                            "Frecuencia",
                            style = MaterialTheme.typography.labelMedium,
                            color = AzulTexto,
                            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                        )
                        FlowRow(
                            Modifier.fillMaxWidth().selectableGroup(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(1, 2, 3).forEach { horas ->
                                ChipOpcion(
                                    texto = "Cada $horas h",
                                    seleccionado = horasAgua == horas,
                                    onClick = {
                                        horasAgua = horas
                                        prefs.horasEntreAgua = horas
                                        Recordatorios.programarAgua(contexto, horas)
                                    }
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                    SeparadorSuave()
                    FilaInterruptor(
                        icono = Icons.Outlined.SelfImprovement,
                        titulo = "Pausa activa",
                        detalle = "Un aviso cada 2 horas para estirarte y descansar la vista.",
                        activo = pausasActivas,
                        onCambio = { quiere ->
                            if (quiere) {
                                solicitar("pausas")
                            } else {
                                pausasActivas = false
                                prefs.recordarPausas = false
                                Recordatorios.cancelarPausas(contexto)
                            }
                        }
                    )
                }

                Text(
                    "$NOMBRE_APP · versión 1.0",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (confirmarEliminar) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            containerColor = Color.White,
            title = { Text("Eliminar cuenta y datos", color = AzulTexto) },
            text = {
                Text(
                    "Se borrarán tu cuenta, tus registros de bienestar y tus tareas de este teléfono. Esta acción no se puede deshacer.",
                    color = TextoSecundario
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    confirmarEliminar = false
                    vm.eliminarCuenta()
                    onSesionCerrada()
                }) { Text("Eliminar todo", color = RojoEliminar, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { confirmarEliminar = false }) {
                    Text("Cancelar", color = AzulPrimario, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun FilaInterruptor(
    icono: ImageVector,
    titulo: String,
    detalle: String,
    activo: Boolean,
    onCambio: (Boolean) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .toggleable(value = activo, role = Role.Switch, onValueChange = onCambio)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, contentDescription = null, tint = AzulPrimario)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(titulo, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = AzulTexto)
            Text(detalle, style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
        }
        Spacer(Modifier.width(8.dp))
        Switch(
            checked = activo,
            onCheckedChange = null,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AzulPrimario,
                uncheckedThumbColor = BordeCampo,
                uncheckedTrackColor = Color.White,
                uncheckedBorderColor = BordeCampo
            )
        )
    }
}
