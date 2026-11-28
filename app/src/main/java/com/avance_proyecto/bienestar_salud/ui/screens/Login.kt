package com.avance_proyecto.bienestar_salud.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.avance_proyecto.bienestar_salud.AppViewModel
import com.avance_proyecto.bienestar_salud.ui.components.BotonPrincipal
import com.avance_proyecto.bienestar_salud.ui.components.CampoTexto
import com.avance_proyecto.bienestar_salud.ui.components.EncabezadoAtras
import com.avance_proyecto.bienestar_salud.ui.components.IconoMarca
import com.avance_proyecto.bienestar_salud.ui.components.MensajeError
import com.avance_proyecto.bienestar_salud.ui.theme.AzulPrimario
import com.avance_proyecto.bienestar_salud.ui.theme.AzulTexto
import com.avance_proyecto.bienestar_salud.ui.theme.FondoApp
import com.avance_proyecto.bienestar_salud.ui.theme.TextoSecundario

@Composable
fun PantallaLogin(
    vm: AppViewModel,
    onAtras: () -> Unit,
    onCrearCuenta: () -> Unit,
    onExito: (String) -> Unit
) {
    var usuario by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var mantener by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var enProceso by remember { mutableStateOf(false) }

    val puedeEnviar = usuario.isNotBlank() && password.isNotEmpty() && !enProceso

    fun enviar() {
        if (!puedeEnviar) return
        enProceso = true
        error = null
        vm.iniciarSesion(usuario, password, mantener) { resultado ->
            enProceso = false
            if (resultado.error != null) error = resultado.error
            else resultado.destino?.let(onExito)
        }
    }

    Column(Modifier.fillMaxSize().background(FondoApp).imePadding()) {
        EncabezadoAtras("Iniciar sesión", onAtras)

        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            Column(
                Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconoMarca(56.dp)
                Spacer(Modifier.height(12.dp))
                Text("Qué gusto verte", style = MaterialTheme.typography.titleLarge, color = AzulTexto)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Continúa cuidando de ti, un día a la vez.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))

                CampoTexto(
                    etiqueta = "Nombre de usuario",
                    valor = usuario,
                    onCambio = { usuario = it; error = null },
                    icono = Icons.Outlined.Person,
                    isError = error != null
                )
                Spacer(Modifier.height(16.dp))
                CampoTexto(
                    etiqueta = "Contraseña",
                    valor = password,
                    onCambio = { password = it; error = null },
                    icono = Icons.Outlined.Lock,
                    esPassword = true,
                    imeAction = ImeAction.Done,
                    isError = error != null
                )
                Spacer(Modifier.height(8.dp))

                Row(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .toggleable(value = mantener, role = Role.Checkbox, onValueChange = { mantener = it }),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = mantener,
                        onCheckedChange = null,
                        colors = CheckboxDefaults.colors(checkedColor = AzulPrimario)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Mantener sesión iniciada", style = MaterialTheme.typography.bodyLarge, color = AzulTexto)
                }

                val mensaje = error
                if (mensaje != null) {
                    Spacer(Modifier.height(8.dp))
                    MensajeError(mensaje, Modifier.fillMaxWidth())
                }
            }
        }

        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(
                Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BotonPrincipal("Iniciar sesión", onClick = { enviar() }, enabled = puedeEnviar)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("¿No tienes cuenta?", style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
                    TextButton(onClick = onCrearCuenta) {
                        Text("Crear cuenta", fontWeight = FontWeight.Bold, color = AzulPrimario)
                    }
                }
            }
        }
    }
}
