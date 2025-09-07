package com.example.main.CompReusable

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true, name = "Alerta flotante")
@Composable
private fun Preview_AlertaFlotante() {
    MaterialTheme {
        AlertaFlotante(
            visible = true,
            titulo = "¡ALERTA!",
            subtitulo = "Zona con Poca Luz",
            mensaje = "¿Desea enviar advertencia de esta zona al supervisor?"
        )
    }
}
