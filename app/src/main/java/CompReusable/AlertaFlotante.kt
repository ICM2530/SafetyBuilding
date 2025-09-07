package com.example.main.CompReusable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

// Colores solicitados
private val AndroidBlue = Color(0xFF0095FF)
private val PopupBg = Color(0xFFC8DFEF)

@Composable
fun AlertaFlotante(
    visible: Boolean,
    titulo: String = "¡ALERTA!",
    subtitulo: String = "Zona con Poca Luz",
    mensaje: String = "¿Desea enviar advertencia de esta zona al supervisor?",
    // Usa UNO: icon (ImageVector) o iconPainter (Painter)
    icon: ImageVector? = null,
    iconPainter: Painter? = null,
    onAdvertir: () -> Unit = {},
    onCerrar: () -> Unit = {},
    colorBotones: Color = AndroidBlue,
    onDismissRequest: () -> Unit = onCerrar
) {
    if (!visible) return

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = PopupBg) // Fondo #C8DFEF
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subtitulo,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(10.dp))

                when {
                    iconPainter != null -> Icon(
                        painter = iconPainter,
                        contentDescription = null,
                        tint = colorBotones,
                        modifier = Modifier.size(64.dp)
                    )
                    icon != null -> Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = colorBotones,
                        modifier = Modifier.size(64.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))
                Text(
                    text = mensaje,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))

                BotonesAlerta.BotonesAdvertirCerrar(
                    onAdvertir = onAdvertir,
                    onCerrar = onCerrar,
                    color = colorBotones, // Botones #0095FF
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
