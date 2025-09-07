package com.example.main.CompReusable

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Preview(showBackground = true, name = "BotonAdvertir / BotonCerrar")
@Composable
private fun Preview_Botones_Individuales() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BotonesAlerta.BotonAdvertir()
            BotonesAlerta.BotonCerrar()
            BotonesAlerta.BotonPeligro()
        }
    }
}

@Preview(showBackground = true, name = "Grupo Advertir/Cerrar (interactivo)")
@Composable
private fun Preview_Grupo_Advertir_Cerrar() {
    MaterialTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Pulsa los botones para ver el comportamiento en Preview (Interactive).")
                BotonesAlerta.BotonesAdvertirCerrar(
                    onAdvertir = {
                        scope.launch { snackbarHostState.showSnackbar("Advertencia enviada (preview)") }
                    },
                    onCerrar = {
                        scope.launch { snackbarHostState.showSnackbar("Cerrado (preview)") }
                    },
                    modifier = Modifier.fillMaxWidth(0.9f)
                )
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}
