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
import com.safetyfirst.datos.MockInMemory
import com.safetyfirst.modelo.Mensaje
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────
// Lista de usuarios para iniciar conversación
// ─────────────────────────────────────────────
@Composable
fun PantUsuariosChatLista(nav: NavController, repo: FirebaseRepositorio = FirebaseRepositorio()) {
    val yo = repo.usuarioActual()?.uid.orEmpty()
    val usuariosRemotos by repo.flujoUsuarios().collectAsState(initial = emptyList())
    val usuarios = if (usuariosRemotos.isNotEmpty()) usuariosRemotos else MockInMemory.usuariosExcept(yo)

    Scaffold(topBar = { TopAppBar(title = { Text("Conversaciones") }) }) { p ->
        LazyColumn(Modifier.padding(p)) {
            items(usuarios.filter { it.uid != yo }) { u ->
                ListItem(
                    headlineContent = { Text(u.nombre.ifEmpty { u.correo }) },
                    leadingContent = { Icon(Icons.Filled.Person, contentDescription = null) },
                    trailingContent = { Icon(Icons.Filled.ChevronRight, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .clickable { nav.navigate(com.safetyfirst.ui.Rutas.Chat.ruta + "/" + u.uid) }
                )
                Divider()
            }
        }
    }
}

// ─────────────────────────────────────────────
// Conversación (burbujas izquierda/derecha)
// ─────────────────────────────────────────────
@Composable
fun PantChat(
    nav: NavController,
    uidOtro: String,
    repo: FirebaseRepositorio = FirebaseRepositorio()
) {
    val yo = repo.usuarioActual()?.uid.orEmpty()
    val convId = remember(uidOtro, yo) { repo.construirConversacionId(yo, uidOtro) }
    val mensajesRemotos by repo.flujoMensajes(convId).collectAsState(initial = emptyList())
    val mensajesMock by MockInMemory.flujoMensajes(convId).collectAsState(initial = emptyList())
    val useMock = mensajesRemotos.isEmpty()
    val mensajes = if (useMock) mensajesMock else mensajesRemotos
    var texto by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Scaffold(topBar = { TopAppBar(title = { Text("Chat") }) }) { p ->
        Column(Modifier.padding(p).fillMaxSize()) {

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(12.dp)
            ) {
                items(mensajes) { m ->
                    val soyYo = m.emisorUid == yo
                    val bg = if (soyYo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                    val fg = if (soyYo) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

                    // En lugar de .align(Alignment...), usamos Row con Arrangement
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = if (soyYo) Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            color = bg,
                            tonalElevation = 2.dp,
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(m.texto, color = fg, modifier = Modifier.padding(10.dp))
                        }
                    }
                }
            }

            Row(
                Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Escribe un mensaje...") }
                )
                Button(onClick = {
                    if (texto.isNotBlank()) {
                        scope.launch {
                            val nuevo = Mensaje(
                                conversacionId = convId,
                                emisorUid = yo,
                                receptorUid = uidOtro,
                                texto = texto.trim(),
                                tiempo = System.currentTimeMillis()
                            )
                            if (useMock) {
                                MockInMemory.enviarMensajeLocal(nuevo)
                            } else {
                                repo.enviarMensaje(nuevo)
                            }
                            texto = ""
                        }
                    }
                }) { Text("Enviar") }
            }
        }
    }
}
