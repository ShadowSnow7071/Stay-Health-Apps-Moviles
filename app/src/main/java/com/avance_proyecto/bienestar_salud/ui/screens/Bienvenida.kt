package com.avance_proyecto.bienestar_salud.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avance_proyecto.bienestar_salud.ui.components.BotonPrincipal
import com.avance_proyecto.bienestar_salud.ui.components.BotonSecundario
import com.avance_proyecto.bienestar_salud.ui.components.IconoMarca
import com.avance_proyecto.bienestar_salud.ui.components.esPantallaAncha
import com.avance_proyecto.bienestar_salud.ui.theme.AzulPrimario
import com.avance_proyecto.bienestar_salud.ui.theme.AzulTexto
import com.avance_proyecto.bienestar_salud.ui.theme.FondoApp
import com.avance_proyecto.bienestar_salud.ui.theme.FondoBienvenida
import com.avance_proyecto.bienestar_salud.ui.theme.TextoSecundario

@Composable
fun PantallaBienvenida(onIniciarSesion: () -> Unit, onCrearCuenta: () -> Unit) {
    if (esPantallaAncha()) {
        // Horizontal / tablet: marca a la izquierda, botones a la derecha.
        Row(Modifier.fillMaxSize().background(FondoApp)) {
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(FondoBienvenida)
            ) {
                Column(
                    Modifier
                        .align(Alignment.Center)
                        .verticalScroll(rememberScrollState())
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ContenidoMarca()
                }
            }
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(horizontal = 48.dp),
                verticalArrangement = Arrangement.Center
            ) {
                BotonPrincipal("Iniciar sesión", onIniciarSesion)
                Spacer(Modifier.height(12.dp))
                BotonSecundario("Crear cuenta", onCrearCuenta)
            }
        }
    } else {
        Box(Modifier.fillMaxSize().background(FondoApp)) {
            // Fondo azul claro con corte diagonal, como en el diseño.
            Canvas(Modifier.fillMaxSize()) {
                val forma = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width, size.height * 0.42f)
                    lineTo(0f, size.height * 0.55f)
                    close()
                }
                drawPath(forma, FondoBienvenida)
            }
            Column(Modifier.fillMaxSize().statusBarsPadding()) {
                Column(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(24.dp))
                    ContenidoMarca()
                }
                Column(
                    Modifier
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                        .navigationBarsPadding()
                ) {
                    BotonPrincipal("Iniciar sesión", onIniciarSesion)
                    Spacer(Modifier.height(12.dp))
                    BotonSecundario("Crear cuenta", onCrearCuenta)
                }
            }
        }
    }
}

@Composable
private fun ContenidoMarca() {
    IconoMarca(88.dp)
    Spacer(Modifier.height(20.dp))
    Text(
        "TU BIENESTAR, CADA DÍA",
        style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
        color = TextoSecundario,
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = buildAnnotatedString {
            append("Stay\n")
            withStyle(SpanStyle(color = AzulPrimario)) { append("Healthy") }
        },
        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 44.sp, lineHeight = 48.sp),
        color = AzulTexto,
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(20.dp))
    Text(
        "Tu rutina diaria,\nmás consciente",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
        color = AzulTexto,
        textAlign = TextAlign.Center
    )
    Spacer(Modifier.height(12.dp))
    Text(
        "Registra cómo te sientes, organiza tus tareas según tu energía y celebra tu progreso.",
        style = MaterialTheme.typography.bodyLarge,
        color = TextoSecundario,
        textAlign = TextAlign.Center
    )
}
