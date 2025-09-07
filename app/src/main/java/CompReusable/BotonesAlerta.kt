package com.example.main.CompReusable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Colores solicitados
private val AndroidBlue = Color(0xFF0095FF)

object BotonesAlerta {

    @Composable
    fun BotonAdvertir(
        text: String = "Advertir",
        onClick: () -> Unit = {},
        modifier: Modifier = Modifier.height(44.dp),
        color: Color = AndroidBlue
    ) {
        Button(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = color,
                contentColor = Color.White,
                disabledContainerColor = color.copy(alpha = 0.4f),
                disabledContentColor = Color.White.copy(alpha = 0.8f)
            )
        ) {
            Text(text, fontSize = 16.sp)
        }
    }

    @Composable
    fun BotonCerrar(
        text: String = "Cerrar",
        onClick: () -> Unit = {},
        modifier: Modifier = Modifier.height(44.dp),
        color: Color = AndroidBlue
    ) {
        // Mismo estilo que BotonAdvertir
        Button(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = color,
                contentColor = Color.White,
                disabledContainerColor = color.copy(alpha = 0.4f),
                disabledContentColor = Color.White.copy(alpha = 0.8f)
            )
        ) {
            Text(text, fontSize = 16.sp)
        }
    }

    @Composable
    fun BotonPeligro(
        text: String = "Paro de emergencia",
        onClick: () -> Unit = {},
        modifier: Modifier = Modifier.height(44.dp),
        color: Color = MaterialTheme.colorScheme.error
    ) {
        Button(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = color,
                contentColor = Color.White
            )
        ) {
            Text(text, fontSize = 16.sp)
        }
    }

    @Composable
    fun BotonesAdvertirCerrar(
        onAdvertir: () -> Unit = {},
        onCerrar: () -> Unit = {},
        modifier: Modifier = Modifier,
        color: Color = AndroidBlue
    ) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BotonAdvertir(onClick = onAdvertir, modifier = Modifier.weight(1f), color = color)
            BotonCerrar(onClick = onCerrar, modifier = Modifier.weight(1f), color = color)
        }
    }
}
