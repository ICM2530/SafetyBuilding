@file:OptIn(ExperimentalMaterial3Api::class)

package com.safetyfirst.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.safetyfirst.modelo.TipoAlerta
import com.safetyfirst.ubicacion.UbicacionVM

@Composable
fun AlertaActiva(
    nav: NavController,
    alertaTipo: String, // Recibe el nombre del objeto TipoAlerta como String
    miUid: String, // ID del obrero (usuario actual)
    supervisorUid: String, // ID del supervisor
    vm: UbicacionVM = viewModel()
) {
    // 1. Obtener el objeto TipoAlerta a partir del nombre (String)
    val alerta = remember(alertaTipo) {
        TipoAlerta.fromString(alertaTipo)
    }

    val ColorAlertaFondo = Color(0xFFE0F7FA)
    val ColorBotonPrimario = Color(0xFF00AEEF)
    val ColorCaida = Color(0xFFDC3545)

    // Determinar el icono y color de acción
    val icono = when (alerta) {
        is TipoAlerta.PocaLuz -> Icons.Default.Lightbulb // Luz
        is TipoAlerta.RuidoAlto -> Icons.Default.VolumeUp // Ruido
        is TipoAlerta.AlturaRiesgosa -> Icons.Default.VerticalAlignTop // Altura/Riesgo
        is TipoAlerta.CaidaDetectada -> Icons.Default.CrisisAlert // Caída/Emergencia
        else -> Icons.Default.Warning // Default
    }

    val colorAccion = if (alerta is TipoAlerta.CaidaDetectada) ColorCaida else ColorBotonPrimario

    // Estructura de pantalla completa
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { p ->
        // Contenedor principal para centrar la alerta
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(p)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ColorAlertaFondo),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        alerta.titulo,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Icon(
                        imageVector = icono,
                        contentDescription = alerta.titulo,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(80.dp).padding(bottom = 16.dp)
                    )

                    Text(
                        alerta.mensajeDisplay,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                vm.enviarAdvertencia(alerta, miUid, supervisorUid)
                                vm.limpiarAlertaMostrada() // Limpia el estado de alerta en el VM
                                nav.popBackStack() // Regresar a la pantalla Home
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = colorAccion),
                            modifier = Modifier.weight(1f).padding(end = 8.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(alerta.textoBoton, fontWeight = FontWeight.SemiBold)
                        }

                        OutlinedButton(
                            onClick = {
                                vm.limpiarAlertaMostrada() // Limpia el estado de alerta en el VM
                                nav.popBackStack() // Regresar a la pantalla Home
                            },
                            modifier = Modifier.weight(1f).padding(start = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp)
                        ) {
                            Text("Cerrar", color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }
    }
}