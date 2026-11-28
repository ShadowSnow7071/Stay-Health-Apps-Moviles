package com.avance_proyecto.bienestar_salud.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.avance_proyecto.bienestar_salud.AppViewModel
import com.avance_proyecto.bienestar_salud.data.ValidadorRegistro
import com.avance_proyecto.bienestar_salud.ui.components.BotonPrincipal
import com.avance_proyecto.bienestar_salud.ui.components.CampoTexto
import com.avance_proyecto.bienestar_salud.ui.components.EncabezadoAtras
import com.avance_proyecto.bienestar_salud.ui.components.MensajeError
import com.avance_proyecto.bienestar_salud.ui.theme.AzulPrimario
import com.avance_proyecto.bienestar_salud.ui.theme.AzulTexto
import com.avance_proyecto.bienestar_salud.ui.theme.FondoApp
import com.avance_proyecto.bienestar_salud.ui.theme.TextoSecundario
import com.avance_proyecto.bienestar_salud.ui.theme.VerdeExito

@Composable
fun PantallaRegistro(
    vm: AppViewModel,
    onAtras: () -> Unit,
    onIniciarSesion: () -> Unit,
    onExito: (String) -> Unit
) {
    var usuario by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmar by rememberSaveable { mutableStateOf("") }
    var errorServidor by rememberSaveable { mutableStateOf<String?>(null) }
    var enProceso by remember { mutableStateOf(false) }

    // Validación de usuario en tiempo real
    val usuarioLimpio = usuario.trim()
    val errorUsuario = if (usuario.isNotEmpty()) ValidadorRegistro.validarNombreUsuario(usuario) else null
    val tieneNombreValido = usuarioLimpio.length >= 3 && errorUsuario == null

    // Requisitos de contraseña en tiempo real
    val tieneMinimoLength = password.length >= 6
    val tieneMayuscula = password.any { it.isUpperCase() }
    val tieneNumero = password.any { it.isDigit() }
    val tieneSimbolo = password.any { !it.isLetterOrDigit() }
    val passwordEsValido = ValidadorRegistro.validarPassword(password).isEmpty()

    val noCoinciden = confirmar.isNotEmpty() && confirmar != password
    val puedeEnviar = tieneNombreValido && passwordEsValido && confirmar == password && !enProceso

    fun enviar() {
        if (!puedeEnviar) return
        enProceso = true
        errorServidor = null
        vm.registrarse(usuario, password) { resultado ->
            enProceso = false
            if (resultado.error != null) errorServidor = resultado.error
            else resultado.destino?.let(onExito)
        }
    }

    Column(Modifier.fillMaxSize().background(FondoApp).imePadding()) {
        EncabezadoAtras("Crear cuenta", onAtras)

        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            Column(
                Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Comencemos", style = MaterialTheme.typography.titleLarge, color = AzulTexto)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Crea tu espacio personal de bienestar.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))

                CampoTexto(
                    etiqueta = "Nombre de usuario",
                    valor = usuario,
                    onCambio = { usuario = it; errorServidor = null },
                    icono = Icons.Outlined.Person,
                    isError = errorUsuario != null
                )
                if (errorUsuario != null) {
                    Spacer(Modifier.height(4.dp))
                    MensajeError(errorUsuario, Modifier.fillMaxWidth())
                }

                Spacer(Modifier.height(16.dp))
                CampoTexto(
                    etiqueta = "Contraseña",
                    valor = password,
                    onCambio = { password = it; errorServidor = null },
                    icono = Icons.Outlined.Lock,
                    esPassword = true,
                    isError = password.isNotEmpty() && !passwordEsValido
                )

                Spacer(Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = "La contraseña debe contener:",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextoSecundario,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    ItemRequisito(cumplido = tieneMinimoLength, texto = "Mínimo 6 caracteres")
                    ItemRequisito(cumplido = tieneMayuscula, texto = "Al menos una letra mayúscula")
                    ItemRequisito(cumplido = tieneNumero, texto = "Al menos un número")
                    ItemRequisito(cumplido = tieneSimbolo, texto = "Al menos un símbolo (ej: !@#$)")
                }

                Spacer(Modifier.height(16.dp))
                CampoTexto(
                    etiqueta = "Confirmar contraseña",
                    valor = confirmar,
                    onCambio = { confirmar = it; errorServidor = null },
                    icono = Icons.Outlined.LockReset,
                    esPassword = true,
                    imeAction = ImeAction.Done,
                    isError = noCoinciden
                )

                if (noCoinciden) {
                    Spacer(Modifier.height(8.dp))
                    MensajeError("Las contraseñas no coinciden", Modifier.fillMaxWidth())
                }
                val mensaje = errorServidor
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
                BotonPrincipal("Crear cuenta", onClick = { enviar() }, enabled = puedeEnviar)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("¿Ya tienes cuenta?", style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
                    TextButton(onClick = onIniciarSesion) {
                        Text("Iniciar sesión", fontWeight = FontWeight.Bold, color = AzulPrimario)
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemRequisito(cumplido: Boolean, texto: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(
            imageVector = if (cumplido) Icons.Outlined.CheckCircle else Icons.Outlined.Cancel,
            contentDescription = null,
            tint = if (cumplido) VerdeExito else TextoSecundario,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = texto,
            style = MaterialTheme.typography.bodySmall,
            color = if (cumplido) VerdeExito else TextoSecundario
        )
    }
}
