package com.safetyfirst.ui.pantallas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.NoDrinks
import androidx.compose.material.icons.filled.RunningWithErrors
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.safetyfirst.modelo.TipoAlerta
import com.safetyfirst.ui.tema.SafetyTheme

// ====================================================================
// 1. COMPOSABLE DE DIÁLOGO UNIFICADO
// ====================================================================

/**
 * Muestra el diálogo de alerta para cualquiera de los tipos de riesgo.
 *
 * @param alerta El TipoAlerta actual (PocaLuz, RuidoAlto, etc.).
 * @param onAdvertirClick Callback cuando se presiona el botón principal (Advertir/Notificar/Pedir Ayuda).
 * @param onCerrarClick Callback cuando se presiona el botón secundario (Cerrar).
 */
@Composable
fun DialogoAlerta(
    alerta: TipoAlerta,
    onAdvertirClick: () -> Unit,
    onCerrarClick: () -> Unit
) {
    // Definición del icono basado en el tipo de alerta
    val icono = when (alerta) {
        is TipoAlerta.PocaLuz -> Icons.Default.Lightbulb // Icono para Baja Luz
        is TipoAlerta.RuidoAlto -> Icons.Default.VolumeUp // Icono para Ruido Alto
        is TipoAlerta.AlturaRiesgosa -> Icons.Default.Height // Icono para Altura Riesgosa
        is TipoAlerta.CaidaDetectada -> Icons.Default.RunningWithErrors // Icono para Inmovilidad/Emergencia
        else -> Icons.Default.NoDrinks // Icono de fallback, aunque no debería usarse
    }

    // Color del botón principal (rojo para inmovilidad, azul para el resto)
    val colorBoton = if (alerta is TipoAlerta.CaidaDetectada) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    // Implementación del diálogo de alerta
    AlertDialog(
        onDismissRequest = onCerrarClick,
        title = {
            Text(alerta.titulo, style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = icono,
                    contentDescription = alerta.titulo,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(alerta.mensajeDisplay, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
            }
        },
        confirmButton = {
            Button(onClick = onAdvertirClick, colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = colorBoton)) {
                Text(alerta.textoBoton) // Mostrar el texto correcto: Advertir, Notificar o Pedir Ayuda
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onCerrarClick) {
                Text("Cerrar")
            }
        }
    )
}

// ====================================================================
// 2. FUNCIONES PREVIEW PARA CADA ESTADO DE SENSOR
// ====================================================================

@Preview(showBackground = true, name = "1. Preview Poca Luz")
@Composable
fun PreviewAlertaPocaLuz() {
    SafetyTheme {
        Surface {
            DialogoAlerta(
                alerta = TipoAlerta.PocaLuz,
                onAdvertirClick = {},
                onCerrarClick = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "2. Preview Ruido Alto")
@Composable
fun PreviewAlertaRuidoAlto() {
    SafetyTheme {
        Surface {
            DialogoAlerta(
                alerta = TipoAlerta.RuidoAlto,
                onAdvertirClick = {},
                onCerrarClick = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "3. Preview Altura Riesgosa")
@Composable
fun PreviewAlertaAlturaRiesgosa() {
    SafetyTheme {
        Surface {
            DialogoAlerta(
                alerta = TipoAlerta.AlturaRiesgosa,
                onAdvertirClick = {},
                onCerrarClick = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "4. Preview Caida")
@Composable
fun PreviewAlarmaInmovilidad() {
    SafetyTheme {
        Surface {
            DialogoAlerta(
                alerta = TipoAlerta.CaidaDetectada,
                onAdvertirClick = {},
                onCerrarClick = {}
            )
        }
    }
}
