package com.avance_proyecto.bienestar_salud.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.avance_proyecto.bienestar_salud.ui.theme.AzulPrimario
import com.avance_proyecto.bienestar_salud.ui.theme.AzulTexto
import com.avance_proyecto.bienestar_salud.ui.theme.BordeSuave
import com.avance_proyecto.bienestar_salud.ui.theme.FondoSuave
import com.avance_proyecto.bienestar_salud.ui.theme.TextoSecundario

// Mosaico pequeño con ícono, etiqueta y valor (resumen del check-in, detalle del día, totales).
@Composable
fun MosaicoDato(icono: ImageVector, etiqueta: String, valor: String, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .background(FondoSuave)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icono, contentDescription = null, tint = AzulPrimario, modifier = Modifier.size(24.dp))
        Spacer(Modifier.height(4.dp))
        Text(etiqueta, style = MaterialTheme.typography.bodyMedium, color = TextoSecundario, textAlign = TextAlign.Center)
        Spacer(Modifier.height(2.dp))
        Text(
            valor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = AzulTexto,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun BarraProgreso(fraccion: Float, descripcion: String, modifier: Modifier = Modifier) {
    val f = fraccion.coerceIn(0f, 1f)
    Box(
        modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(BordeSuave)
            .semantics(mergeDescendants = true) {
                contentDescription = descripcion
                progressBarRangeInfo = ProgressBarRangeInfo(f, 0f..1f)
            }
    ) {
        Box(Modifier.fillMaxWidth(f).fillMaxHeight().background(AzulPrimario))
    }
}

// Círculo para marcar una tarea como hecha (área táctil de 48 dp).
@Composable
fun BotonCirculo(hecha: Boolean, descripcion: String, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = if (hecha) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
            contentDescription = descripcion,
            tint = AzulPrimario,
            modifier = Modifier.size(28.dp)
        )
    }
}
