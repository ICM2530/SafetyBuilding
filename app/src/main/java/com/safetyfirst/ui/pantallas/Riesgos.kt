@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.foundation.layout.ExperimentalLayoutApi::class
)

package com.safetyfirst.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.safetyfirst.datos.FirebaseRepositorio
import com.safetyfirst.modelo.TipoRiesgo
import com.safetyfirst.modelo.ZonaRiesgo
import kotlinx.coroutines.launch

@Composable
fun PantRiesgoFormulario(nav: NavController, repo: FirebaseRepositorio = FirebaseRepositorio()) {
    val scope = rememberCoroutineScope()
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    val tipos = remember { TipoRiesgo.values().toList() }
    var seleccionados by remember { mutableStateOf(tipos.associate { it to true }.toMutableMap()) }
    var ubicacion by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize().background(Color(0xFF47434C)).padding(12.dp)) {
        TopAppBar(
            title = { Text("Agregue una nueva zona de riesgo", color = Color.White) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
        )
        Spacer(Modifier.height(8.dp))

        Column(
            Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .background(Color(0xFF5A5660), shape = MaterialTheme.shapes.medium)
                .padding(12.dp)
        ) {
            Etiqueta("Nombre")
            OutlinedTextField(
                titulo, { titulo = it }, modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFBFC3C8))
            )
            Spacer(Modifier.height(8.dp))
            Etiqueta("Descripción")
            OutlinedTextField(
                descripcion, { descripcion = it }, modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFBFC3C8))
            )
            Spacer(Modifier.height(8.dp))
            Text("Tipo de riesgos:", color = Color.White, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(8.dp)) {
                    tipos.forEach { t ->
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            AssistChip(onClick = {}, label = { Text("A") })
                            Spacer(Modifier.width(8.dp))
                            Text(t.etiqueta, modifier = Modifier.weight(1f))
                            Checkbox(
                                checked = seleccionados[t] == true,
                                onCheckedChange = { seleccionados[t] = it }
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Etiqueta("Ubicación:")
            OutlinedTextField(
                ubicacion, { ubicacion = it }, modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFBFC3C8))
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { /* TODO: tomar foto */ }, modifier = Modifier.weight(1f)) {
                    Text("Tomar\nfoto")
                }
                Button(
                    onClick = {
                        scope.launch {
                            val uid = repo.usuarioActual()?.uid.orEmpty()
                            val id = "zona_${System.currentTimeMillis()}"
                            val (lat, lon) = parseLatLon(ubicacion)
                            val tiposSel = seleccionados.filter { it.value }.keys.map { it.etiqueta }
                            repo.agregarZona(
                                ZonaRiesgo(id, titulo, descripcion, lat, lon, 20.0, uid, tiposSel, "")
                            )
                            titulo = ""; descripcion = ""; ubicacion = ""
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Agregar") }
            }
        }
    }
}

private fun parseLatLon(texto: String): Pair<Double, Double> = try {
    val p = texto.split(",").map { it.trim() }
    Pair(p[0].toDouble(), p[1].toDouble())
} catch (_: Exception) { 0.0 to 0.0 }

@Composable private fun Etiqueta(txt: String) {
    Text(txt, color = Color.White, fontWeight = FontWeight.SemiBold)
}

@Composable
fun PantRiesgosListaSupervisor(nav: NavController, repo: FirebaseRepositorio = FirebaseRepositorio()) {
    val zonas by repo.flujoZonas().collectAsState(initial = emptyList())
    Scaffold(topBar = { TopAppBar(title = { Text("Riesgos") }) }) { p ->
        LazyColumn(Modifier.padding(p)) {
            items(zonas) { z ->
                Card(Modifier.padding(12.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(z.titulo, style = MaterialTheme.typography.titleMedium)
                        Text(z.descripcion)
                        Spacer(Modifier.height(6.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            z.tipos.forEach { t -> AssistChip(onClick = {}, label = { Text(t) }) }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text("(${z.lat}, ${z.lon}) r=${z.radioMetros}m", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}