package com.safetyfirst.ui.tema

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Colores = lightColorScheme(
    primary = Color(0xFF006E5E),
    secondary = Color(0xFF4E5BA6),
    background = Color(0xFFF6F6F8),
    surface = Color.White
)

private val Tipos = Typography(
    titleLarge = androidx.compose.ui.text.TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold),
    headlineSmall = androidx.compose.ui.text.TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
)

@Composable
fun SafetyTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Colores, typography = Tipos, content = content)
}
