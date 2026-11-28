package com.avance_proyecto.bienestar_salud.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.avance_proyecto.bienestar_salud.AppViewModel
import com.avance_proyecto.bienestar_salud.data.Fechas
import com.avance_proyecto.bienestar_salud.model.Animo
import com.avance_proyecto.bienestar_salud.model.Energia
import com.avance_proyecto.bienestar_salud.model.Sueno
import com.avance_proyecto.bienestar_salud.model.comoEnum
import com.avance_proyecto.bienestar_salud.ui.components.BotonIconoCuadrado
import com.avance_proyecto.bienestar_salud.ui.components.BotonPrincipal
import com.avance_proyecto.bienestar_salud.ui.components.ChipOpcion
import com.avance_proyecto.bienestar_salud.ui.components.EncabezadoTab
import com.avance_proyecto.bienestar_salud.ui.components.Tarjeta
import com.avance_proyecto.bienestar_salud.ui.components.esPantallaAncha
import com.avance_proyecto.bienestar_salud.ui.theme.AzulPrimario
import com.avance_proyecto.bienestar_salud.ui.theme.AzulTexto
import com.avance_proyecto.bienestar_salud.ui.theme.FondoApp
import com.avance_proyecto.bienestar_salud.ui.theme.TextoSecundario

@Composable
fun PantallaCheckin(
    vm: AppViewModel,
    esEdicion: Boolean,
    onAjustes: () -> Unit,
    onTerminar: () -> Unit
) {
    val usuario by vm.usuario.collectAsState()
    val registro by vm.registroHoy.collectAsState()

    // rememberSaveable: la selección sobrevive al girar el teléfono.
    var sueno by rememberSaveable(registro?.sueno) { mutableStateOf(registro?.sueno) }
    var energia by rememberSaveable(registro?.energia) { mutableStateOf(registro?.energia) }
    var animo by rememberSaveable(registro?.animo) { mutableStateOf(registro?.animo) }

    val suenoSel: Sueno? = sueno.comoEnum<Sueno>()
    val energiaSel: Energia? = energia.comoEnum<Energia>()
    val animoSel: Animo? = animo.comoEnum<Animo>()
    val completo = suenoSel != null && energiaSel != null && animoSel != null

    val tarjetaSueno: @Composable () -> Unit = {
        TarjetaPregunta(
            icono = Icons.Outlined.Bedtime,
            pregunta = "¿Cuánto dormiste?",
            opciones = Sueno.values().toList(),
            etiqueta = { it.etiqueta },
            seleccion = suenoSel,
            onSeleccion = { sueno = it.name }
        )
    }
    val tarjetaEnergia: @Composable () -> Unit = {
        TarjetaPregunta(
            icono = Icons.Outlined.Bolt,
            pregunta = "¿Cómo está tu energía?",
            opciones = Energia.values().toList(),
            etiqueta = { it.etiqueta },
            seleccion = energiaSel,
            onSeleccion = { energia = it.name }
        )
    }
    val tarjetaAnimo: @Composable () -> Unit = {
        TarjetaPregunta(
            icono = Icons.Outlined.SentimentSatisfied,
            pregunta = "¿Cuál es tu estado de ánimo?",
            opciones = Animo.values().toList(),
            etiqueta = { it.etiqueta },
            seleccion = animoSel,
            onSeleccion = { animo = it.name }
        )
    }

    Column(Modifier.fillMaxSize().background(FondoApp)) {
        EncabezadoTab(
            etiqueta = Fechas.encabezado(Fechas.hoy()),
            titulo = Fechas.saludo() + ", " + (usuario?.nombreUsuario ?: ""),
            subtitulo = "¿Cómo te sientes hoy?",
            accion = {
                BotonIconoCuadrado(
                    icono = Icons.Outlined.Settings,
                    descripcion = "Ajustes",
                    onClick = onAjustes
                )
            }
        )

        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            if (esPantallaAncha()) {
                Row(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        tarjetaSueno()
                        tarjetaEnergia()
                    }
                    Column(Modifier.weight(1f)) {
                        tarjetaAnimo()
                    }
                }
            } else {
                Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    tarjetaSueno()
                    tarjetaEnergia()
                    tarjetaAnimo()
                }
            }
        }

        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(
                Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BotonPrincipal(
                    texto = if (esEdicion) "Guardar cambios" else "Empezar mi día",
                    enabled = completo,
                    icono = Icons.AutoMirrored.Outlined.ArrowForward,
                    onClick = {
                        if (suenoSel != null && energiaSel != null && animoSel != null) {
                            vm.guardarCheckin(suenoSel, energiaSel, animoSel)
                            onTerminar()
                        }
                    }
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = if (completo) "Todo listo para comenzar" else "Completa las 3 preguntas para continuar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun <T> TarjetaPregunta(
    icono: ImageVector,
    pregunta: String,
    opciones: List<T>,
    etiqueta: (T) -> String,
    seleccion: T?,
    onSeleccion: (T) -> Unit
) {
    Tarjeta {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icono, contentDescription = null, tint = AzulPrimario, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                pregunta,
                style = MaterialTheme.typography.titleMedium,
                color = AzulTexto,
                modifier = Modifier.semantics { heading() }
            )
        }
        Spacer(Modifier.height(12.dp))
        FlowRow(
            Modifier.fillMaxWidth().selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            opciones.forEach { opcion ->
                ChipOpcion(
                    texto = etiqueta(opcion),
                    seleccionado = opcion == seleccion,
                    onClick = { onSeleccion(opcion) }
                )
            }
        }
    }
}
