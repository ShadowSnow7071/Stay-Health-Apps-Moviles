package com.avance_proyecto.bienestar_salud.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Tamaño mínimo de 14 sp en cuerpo y 16 sp en botones (criterio de accesibilidad).
// Para usar la tipografía redondeada del diseño (Nunito), agrega los .ttf en res/font
// y cambia FamiliaApp por FontFamily(Font(R.font.nunito_regular), Font(R.font.nunito_bold, FontWeight.Bold)).
val FamiliaApp: FontFamily = FontFamily.Default

val Typography = Typography(
    headlineLarge = TextStyle(fontFamily = FamiliaApp, fontWeight = FontWeight.ExtraBold, fontSize = 40.sp, lineHeight = 46.sp),
    headlineMedium = TextStyle(fontFamily = FamiliaApp, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 34.sp),
    titleLarge = TextStyle(fontFamily = FamiliaApp, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 30.sp),
    titleMedium = TextStyle(fontFamily = FamiliaApp, fontWeight = FontWeight.Bold, fontSize = 18.sp, lineHeight = 24.sp),
    bodyLarge = TextStyle(fontFamily = FamiliaApp, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = FamiliaApp, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontFamily = FamiliaApp, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 22.sp),
    labelMedium = TextStyle(fontFamily = FamiliaApp, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.5.sp),
    labelSmall = TextStyle(fontFamily = FamiliaApp, fontWeight = FontWeight.Medium, fontSize = 13.sp, lineHeight = 18.sp)
)
