package com.avance_proyecto.bienestar_salud.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Se desactiva el color dinámico de Android 12+ a propósito: con él, Material
// reemplazaría la paleta del diseño por los colores del fondo de pantalla del usuario.
private val EsquemaClaro = lightColorScheme(
    primary = AzulPrimario,
    onPrimary = Color.White,
    primaryContainer = ChipFondo,
    onPrimaryContainer = AzulTexto,
    secondary = AzulPrimario,
    onSecondary = Color.White,
    background = FondoApp,
    onBackground = AzulTexto,
    surface = Color.White,
    onSurface = AzulTexto,
    surfaceVariant = FondoSuave,
    onSurfaceVariant = TextoSecundario,
    outline = BordeCampo,
    outlineVariant = BordeSuave,
    error = RojoEliminar,
    onError = Color.White
)

@Composable
fun Bienestar_SaludTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaClaro,
        typography = Typography,
        content = content
    )
}
