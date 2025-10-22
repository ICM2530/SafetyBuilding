package com.safetyfirst.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.safetyfirst.datos.FirebaseRepositorio
import com.safetyfirst.modelo.ZonaRiesgo
import kotlinx.coroutines.launch

@Composable
fun PantZonas(nav: NavController, repo: FirebaseRepositorio = FirebaseRepositorio()) {
    val scope = rememberCoroutineScope()
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf("0.0") }
    var lon by remember { mutableStateOf("0.0") }
    var radio by remember { mutableStateOf("20.0") }
    val zonas by repo.flujoZonas().collectAsState(initial = emptyList())

    Column(Modifier.padding(16.dp)) {
        Text("Zonas de riesgo", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(titulo, { titulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(descripcion, { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
        Row {
            OutlinedTextField(lat, { lat = it }, label = { Text("Lat") }, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(lon, { lon = it }, label = { Text("Lon") }, modifier = Modifier.weight(1f))
        }
        OutlinedTextField(radio, { radio = it }, label = { Text("Radio (m)") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            scope.launch {
                val id = "zona_${System.currentTimeMillis()}"
                repo.agregarZona(
                    ZonaRiesgo(
                        id = id, titulo = titulo, descripcion = descripcion,
                        lat = lat.toDoubleOrNull() ?: 0.0, lon = lon.toDoubleOrNull() ?: 0.0,
                        radioMetros = radio.toDoubleOrNull() ?: 20.0, altitud = 0.0
                    )
                )
                titulo = ""; descripcion = ""; lat = "0.0"; lon = "0.0"; radio = "20.0"
            }
        }) { Text("Agregar zona") }

        Spacer(Modifier.height(16.dp))
        LazyColumn {
            items(zonas) { z ->
                ListItem(
                    headlineContent = { Text(z.titulo) },
                    supportingContent = { Text("${z.descripcion} • r=${z.radioMetros}m  [${z.lat}, ${z.lon}]") }
                )
                Divider()
            }
        }
    }
}
