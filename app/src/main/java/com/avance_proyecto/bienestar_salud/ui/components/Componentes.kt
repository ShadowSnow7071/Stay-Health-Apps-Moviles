package com.avance_proyecto.bienestar_salud.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.avance_proyecto.bienestar_salud.data.Fechas
import com.avance_proyecto.bienestar_salud.data.Tarea
import com.avance_proyecto.bienestar_salud.model.Esfuerzo
import com.avance_proyecto.bienestar_salud.ui.theme.AzulEncabezado
import com.avance_proyecto.bienestar_salud.ui.theme.AzulPrimario
import com.avance_proyecto.bienestar_salud.ui.theme.AzulTexto
import com.avance_proyecto.bienestar_salud.ui.theme.BordeCampo
import com.avance_proyecto.bienestar_salud.ui.theme.BordeSuave
import com.avance_proyecto.bienestar_salud.ui.theme.BotonDeshabilitado
import com.avance_proyecto.bienestar_salud.ui.theme.ChipBorde
import com.avance_proyecto.bienestar_salud.ui.theme.ChipFondo
import com.avance_proyecto.bienestar_salud.ui.theme.RojoEliminar
import com.avance_proyecto.bienestar_salud.ui.theme.TextoDeshabilitado
import com.avance_proyecto.bienestar_salud.ui.theme.TextoSecundario

// En horizontal (o tablet) el ancho pasa de 600 dp: se usan dos columnas y riel lateral.
@Composable
fun esPantallaAncha(): Boolean = LocalConfiguration.current.screenWidthDp >= 600

// En horizontal en teléfono la altura es poca: se compactan los encabezados.
@Composable
fun esPantallaBaja(): Boolean = LocalConfiguration.current.screenHeightDp < 480

// ---------- Encabezados ----------

@Composable
fun EncabezadoTab(
    etiqueta: String,
    titulo: String,
    modifier: Modifier = Modifier,
    subtitulo: String? = null,
    accion: (@Composable () -> Unit)? = null
) {
    val vertical = if (esPantallaBaja()) 10.dp else 20.dp
    Box(modifier.fillMaxWidth().background(AzulEncabezado)) {
        Row(
            Modifier
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = vertical),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = TextoSecundario)
                Spacer(Modifier.height(2.dp))
                Text(
                    titulo,
                    style = MaterialTheme.typography.headlineMedium,
                    color = AzulTexto,
                    modifier = Modifier.semantics { heading() }
                )
                if (subtitulo != null) {
                    Spacer(Modifier.height(2.dp))
                    Text(subtitulo, style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
                }
            }
            if (accion != null) {
                Spacer(Modifier.width(12.dp))
                accion()
            }
        }
    }
}

@Composable
fun EncabezadoAtras(titulo: String, onAtras: () -> Unit) {
    Box(Modifier.fillMaxWidth().background(AzulEncabezado)) {
        Box(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            BotonIconoCuadrado(
                icono = Icons.AutoMirrored.Outlined.ArrowBack,
                descripcion = "Atrás",
                onClick = onAtras,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Text(
                titulo,
                style = MaterialTheme.typography.titleMedium,
                color = AzulTexto,
                modifier = Modifier
                    .align(Alignment.Center)
                    .semantics { heading() }
            )
        }
    }
}

// ---------- Botones ----------

@Composable
fun BotonIconoCuadrado(
    icono: ImageVector,
    descripcion: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fondo: Color = Color.White,
    tinte: Color = AzulPrimario,
    borde: Color? = null,
    tamano: Dp = 48.dp
) {
    val forma = RoundedCornerShape(14.dp)
    var base = modifier.size(tamano).clip(forma).background(fondo, forma)
    if (borde != null) base = base.border(1.5.dp, borde, forma)
    Box(
        base.clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icono,
            contentDescription = descripcion,
            tint = if (enabled) tinte else TextoDeshabilitado.copy(alpha = 0.5f),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icono: ImageVector? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().heightIn(min = 56.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AzulPrimario,
            contentColor = Color.White,
            disabledContainerColor = BotonDeshabilitado,
            disabledContentColor = TextoDeshabilitado
        )
    ) {
        Text(texto, style = MaterialTheme.typography.labelLarge)
        if (icono != null) {
            Spacer(Modifier.width(8.dp))
            Icon(icono, contentDescription = null, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun BotonSecundario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null,
    color: Color = AzulPrimario
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().heightIn(min = 56.dp),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.5.dp, color),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = color)
    ) {
        if (icono != null) {
            Icon(icono, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(texto, style = MaterialTheme.typography.labelLarge)
    }
}

// ---------- Contenedores ----------

@Composable
fun Tarjeta(
    modifier: Modifier = Modifier,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp), content = contenido)
    }
}

@Composable
fun IconoMarca(tamano: Dp) {
    Box(
        Modifier
            .size(tamano)
            .clip(RoundedCornerShape(tamano * 0.28f))
            .background(AzulEncabezado),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Outlined.Spa,
            contentDescription = null,
            tint = AzulPrimario,
            modifier = Modifier.size(tamano * 0.5f)
        )
    }
}

// ---------- Selección ----------

// Opción de una sola respuesta: se distingue por color, contorno grueso y palomita (no solo color).
@Composable
fun ChipOpcion(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    val forma = RoundedCornerShape(24.dp)
    Row(
        Modifier
            .heightIn(min = 48.dp)
            .clip(forma)
            .background(if (seleccionado) ChipFondo else Color.White, forma)
            .border(
                width = if (seleccionado) 2.dp else 1.dp,
                color = if (seleccionado) ChipBorde else BordeCampo,
                shape = forma
            )
            .selectable(selected = seleccionado, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (seleccionado) {
            Icon(Icons.Outlined.Check, contentDescription = null, tint = AzulTexto, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
        }
        Text(
            texto,
            style = MaterialTheme.typography.labelMedium,
            color = AzulTexto,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium
        )
    }
}

// ---------- Esfuerzo (texto + ícono de 1, 2 o 3 barras, nunca solo color) ----------

@Composable
fun IconoEsfuerzo(nivel: Int, modifier: Modifier = Modifier) {
    Canvas(modifier.size(width = 20.dp, height = 16.dp).clearAndSetSemantics { }) {
        val ancho = size.width * 0.26f
        val hueco = (size.width - 3 * ancho) / 2f
        val radio = CornerRadius(2.dp.toPx())
        for (i in 0..2) {
            val alto = size.height * (0.4f + 0.3f * i)
            val izquierda = i * (ancho + hueco)
            val arriba = size.height - alto
            if (i < nivel) {
                drawRoundRect(
                    color = AzulPrimario,
                    topLeft = Offset(izquierda, arriba),
                    size = Size(ancho, alto),
                    cornerRadius = radio
                )
            } else {
                drawRoundRect(
                    color = AzulPrimario,
                    topLeft = Offset(izquierda + 0.75.dp.toPx(), arriba + 0.75.dp.toPx()),
                    size = Size(ancho - 1.5.dp.toPx(), alto - 1.5.dp.toPx()),
                    cornerRadius = radio,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
        }
    }
}

@Composable
fun EtiquetaEsfuerzo(esfuerzo: Esfuerzo) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconoEsfuerzo(esfuerzo.nivel)
        Spacer(Modifier.width(6.dp))
        Text(esfuerzo.etiqueta, style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
    }
}

@Composable
fun FilaFechaTarea(tarea: Tarea, hoy: String) {
    val texto = Fechas.etiqueta(tarea.fecha, hoy) + " · " + (tarea.hora ?: "Sin hora")
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Outlined.CalendarMonth,
            contentDescription = null,
            tint = TextoSecundario,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(texto, style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
    }
}

// ---------- Formularios ----------

@Composable
fun CampoTexto(
    etiqueta: String,
    valor: String,
    onCambio: (String) -> Unit,
    icono: ImageVector? = null,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    esPassword: Boolean = false,
    imeAction: ImeAction = ImeAction.Next,
    isError: Boolean = false
) {
    var visible by rememberSaveable { mutableStateOf(false) }
    Column(modifier) {
        Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = AzulTexto)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = valor,
            onValueChange = onCambio,
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            singleLine = true,
            isError = isError,
            leadingIcon = if (icono != null) {
                { Icon(icono, contentDescription = null) }
            } else null,
            placeholder = if (placeholder.isNotEmpty()) {
                { Text(placeholder) }
            } else null,
            trailingIcon = if (esPassword) {
                {
                    IconButton(onClick = { visible = !visible }) {
                        Icon(
                            if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = if (visible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                }
            } else null,
            visualTransformation = if (esPassword && !visible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (esPassword) KeyboardType.Password else KeyboardType.Text,
                imeAction = imeAction
            ),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor = Color.White,
                focusedBorderColor = AzulPrimario,
                unfocusedBorderColor = BordeCampo,
                errorBorderColor = RojoEliminar,
                focusedTextColor = AzulTexto,
                unfocusedTextColor = AzulTexto,
                focusedLeadingIconColor = AzulPrimario,
                unfocusedLeadingIconColor = TextoSecundario,
                unfocusedPlaceholderColor = TextoSecundario,
                focusedPlaceholderColor = TextoSecundario
            )
        )
    }
}

// Error con ícono y texto (no solo color rojo); TalkBack lo anuncia al aparecer.
@Composable
fun MensajeError(texto: String, modifier: Modifier = Modifier) {
    Row(
        modifier.semantics { liveRegion = LiveRegionMode.Polite },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.ErrorOutline, contentDescription = null, tint = RojoEliminar, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(texto, style = MaterialTheme.typography.bodyMedium, color = RojoEliminar)
    }
}

@Composable
fun SeparadorSuave() {
    Box(Modifier.fillMaxWidth().height(1.dp).background(BordeSuave))
}
