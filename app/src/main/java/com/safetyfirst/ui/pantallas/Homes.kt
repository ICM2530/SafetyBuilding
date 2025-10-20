@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.safetyfirst.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.safetyfirst.ui.Rutas

@Composable
fun PantHomeObrero(nav: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("SafetyFirst · Obrero") }) },
        bottomBar = {
            BottomAppBar {
                Button(onClick = { nav.navigate(Rutas.Perfil.ruta) }) { Text("Perfil") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { nav.navigate(Rutas.ChatUsuarios.ruta) }) { Text("Mensajes") }
            }
        }
    ) { p ->
        Column(Modifier.padding(p).padding(16.dp)) {
            Text("Inicio", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))
            Button(onClick = { nav.navigate(Rutas.Zonas.ruta) }) { Text("Riesgos") }
        }
    }
}

@Composable
fun PantHomeSupervisor(nav: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("SafetyFirst · Supervisor") }) },
        bottomBar = {
            BottomAppBar {
                Button(onClick = { nav.navigate(Rutas.Perfil.ruta) }) { Text("Perfil") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { nav.navigate(Rutas.ChatUsuarios.ruta) }) { Text("Mensajes") }
            }
        }
    ) { p ->
        Column(Modifier.padding(p).padding(16.dp)) {
            Text("Panel", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))
            Button(onClick = { nav.navigate(Rutas.Operadores.ruta) }) { Text("Administrar operadores") }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { nav.navigate(Rutas.MapaSupervisor.ruta) }) { Text("Ver mapa de trabajadores") }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { nav.navigate(Rutas.RiesgosLista.ruta) }) { Text("Ver riesgos") }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { nav.navigate(Rutas.Zonas.ruta) }) { Text("Agregar zona de riesgo") }
        }
    }
}
