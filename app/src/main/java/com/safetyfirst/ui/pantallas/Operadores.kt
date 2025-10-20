@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.safetyfirst.ui.pantallas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.safetyfirst.datos.FirebaseRepositorio
import com.safetyfirst.modelo.Rol
import com.safetyfirst.modelo.Usuario
import com.safetyfirst.ui.Rutas

@Composable
fun PantOperadoresLista(nav: NavController, repo: FirebaseRepositorio = FirebaseRepositorio()) {
    val obreros by repo.flujoUsuariosPorRol(Rol.OBRERO).collectAsState(initial = emptyList())

    Scaffold(topBar = { TopAppBar(title = { Text("Administrar operadores") }) }) { p ->
        LazyColumn(Modifier.padding(p).padding(8.dp)) {
            items(obreros) { u ->
                Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .clickable { nav.navigate(Rutas.OperadorPerfil.ruta + "/" + u.uid) },
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row {
                            Icon(Icons.Filled.Person, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(u.nombre.ifEmpty { u.correo })
                        }
                        Icon(Icons.Filled.ChevronRight, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
fun PantOperadorPerfil(nav: NavController, uid: String, repo: FirebaseRepositorio = FirebaseRepositorio()) {
    var u by remember { mutableStateOf<Usuario?>(null) }
    LaunchedEffect(uid) { u = repo.obtenerUsuario(uid) }

    Scaffold(topBar = { TopAppBar(title = { Text("Perfil operador") }) }) { p ->
        Column(Modifier.padding(p).padding(16.dp)) {
            Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(96.dp))
            Spacer(Modifier.height(8.dp))
            Text("Nombre:  ${u?.nombre ?: "-"}")
            Text("Rol:  ${u?.rol ?: "-"}")
            Text("Cédula:  ${u?.documentoNumero ?: "-"}")
            Text("ID Trabajador:  ID-${u?.uid?.takeLast(6) ?: "-"}")
            Text("Celular:  ${u?.telefono ?: "-"}")
            Text("Correo electrónico:  ${u?.correo ?: "-"}")
            Text("Contacto de emergencia:  -")
        }
    }
}
