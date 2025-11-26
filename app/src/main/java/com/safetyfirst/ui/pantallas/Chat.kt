@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.safetyfirst.ui.pantallas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.safetyfirst.datos.FirebaseRepositorio
import com.safetyfirst.datos.MockInMemory
import com.safetyfirst.modelo.Mensaje
import kotlinx.coroutines.launch

@Composable
fun PantUsuariosChatLista(nav: NavController, repo: FirebaseRepositorio = FirebaseRepositorio()) {
    val yo = repo.usuarioActual()?.uid.orEmpty()
    val usuariosRemotos by repo.flujoUsuarios().collectAsState(initial = emptyList())
    val autenticado = yo.isNotBlank()
    val usuarios = when {
        usuariosRemotos.isNotEmpty() -> usuariosRemotos
        autenticado -> emptyList() // si ya hay sesion real mostramos lista vacia en lugar del demo
        else -> MockInMemory.usuariosExcept(yo)
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Conversaciones") }) }) { p ->
        LazyColumn(Modifier.padding(p)) {
            if (usuarios.isEmpty()) {
                item {
                    ListItem(
                        headlineContent = { Text("Aun no hay usuarios para conversar") },
                        supportingContent = { Text("Cuando haya otros perfiles registrados aqui podras iniciar un chat.") },
                        leadingContent = { Icon(Icons.Filled.Person, contentDescription = null) }
                    )
                }
            } else {
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
}

@Composable
fun PantChat(
    nav: NavController,
    uidOtro: String,
    repo: FirebaseRepositorio = FirebaseRepositorio()
) {
    val yo = repo.usuarioActual()?.uid.orEmpty()
    val autenticado = yo.isNotBlank()

    if (!autenticado || uidOtro.isBlank()) {
        Scaffold(topBar = { TopAppBar(title = { Text("Chat") }) }) { p ->
            Column(
                modifier = Modifier
                    .padding(p)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "No pudimos iniciar el chat. Verifica que hayas iniciado sesion y que el usuario sea valido.",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        return
    }

    val convId = remember(uidOtro, yo) { repo.construirConversacionId(yo, uidOtro) }
    val mensajesRemotos by repo.flujoMensajes(convId).collectAsState(initial = emptyList())
    val mensajesMock by MockInMemory.flujoMensajes(convId).collectAsState(initial = emptyList())
    val mensajes = if (autenticado) mensajesRemotos else mensajesMock
    var texto by remember { mutableStateOf("") }
    var enviando by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(topBar = { TopAppBar(title = { Text("Chat") }) }) { p ->
        Column(
            modifier = Modifier
                .padding(p)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(12.dp)
            ) {
                items(mensajes) { m ->
                    val soyYo = m.emisorUid == yo
                    val bg = if (soyYo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                    val fg = if (soyYo) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Escribe un mensaje...") }
                )
                Button(
                    enabled = texto.isNotBlank() && !enviando,
                    onClick = {
                        scope.launch {
                            enviando = true
                            try {
                                val nuevo = Mensaje(
                                    conversacionId = convId,
                                    emisorUid = yo,
                                    receptorUid = uidOtro,
                                    texto = texto.trim(),
                                    tiempo = System.currentTimeMillis()
                                )
                                repo.enviarMensaje(nuevo)
                                texto = ""
                            } catch (_: Exception) {
                                // Si Firebase falla, mantenemos la UX con datos locales
                                val fallback = Mensaje(
                                    conversacionId = convId,
                                    emisorUid = yo,
                                    receptorUid = uidOtro,
                                    texto = texto.trim(),
                                    tiempo = System.currentTimeMillis()
                                )
                                MockInMemory.enviarMensajeLocal(fallback)
                                texto = ""
                            } finally {
                                enviando = false
                            }
                        }
                    }
                ) { Text(if (enviando) "Enviando..." else "Enviar") }
            }
        }
    }
}
