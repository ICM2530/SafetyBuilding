package com.safetyfirst.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.messaging.FirebaseMessaging
import com.safetyfirst.datos.FirebaseRepositorio
import com.safetyfirst.modelo.Rol
import com.safetyfirst.modelo.Usuario
import com.safetyfirst.ui.Rutas
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun PantSeleccionRol(nav: NavController, repo: FirebaseRepositorio = FirebaseRepositorio()) {
    val scope = rememberCoroutineScope()
    var nombre by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf(Rol.OBRERO) }
    val uid = repo.usuarioActual()?.uid.orEmpty()
    val correo = repo.usuarioActual()?.correo.orEmpty()

    Column(Modifier.padding(16.dp)) {
        Text("Completar perfil", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Row {
            Text("Rol:"); Spacer(Modifier.width(8.dp))
            AssistChip(onClick = { rol = Rol.OBRERO }, label = { Text("Obrero") })
            Spacer(Modifier.width(8.dp))
            AssistChip(onClick = { rol = Rol.SUPERVISOR }, label = { Text("Supervisor") })
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            scope.launch {
                val token = FirebaseMessaging.getInstance().token.await()
                repo.guardarUsuario(Usuario(uid, nombre, correo, rol, token))
                // Evita el 'inclusive' problemático: primero navega y luego limpia back stack
                nav.navigate(Rutas.Permisos.ruta)
            }
        }, modifier = Modifier.fillMaxWidth()) { Text("Guardar y continuar") }
    }
}

@Composable
fun PantPerfil(nav: NavController, repo: FirebaseRepositorio = FirebaseRepositorio()) {
    val uid = repo.usuarioActual()?.uid.orEmpty()
    var u by remember { mutableStateOf<com.safetyfirst.modelo.Usuario?>(null) }
    LaunchedEffect(uid) { if (uid.isNotEmpty()) u = repo.obtenerUsuario(uid) }

    Column(Modifier.padding(16.dp)) {
        Text("Mi perfil", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text("UID: ${u?.uid ?: "-"}")
        Text("Correo: ${u?.correo ?: "-"}")
        Text("Nombre: ${u?.nombre ?: "-"}")
        Text("Rol: ${u?.rol ?: "-"}")
    }
}
