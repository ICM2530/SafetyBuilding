package com.example.main.Screens.DrawerScreens

import Navigation.BottomDestination
import Navigation.buildBottomItems
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.main.CompReusable.ReusableTextField
import com.example.main.CompReusable.ReusableTopAppBar
import com.example.main.CompReusable.SafetyBottomBar
import com.example.main.utils.theme.*
import com.safetyfirst.modelo.Mensaje
import com.safetyfirst.modelo.Usuario
import com.safetyfirst.ui.pantallas.ChatViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel = viewModel()
) {
    val bottomBarItems = buildBottomItems(BottomDestination.Team, navController)
    val uiState by viewModel.uiState.collectAsState()
    var usuarioSeleccionado by remember { mutableStateOf<Usuario?>(null) }

    if (usuarioSeleccionado == null) {
        ListaUsuariosScreen(
            usuarios = uiState.usuarios,
            cargando = uiState.cargando,
            onUsuarioClick = { usuario ->
                usuarioSeleccionado = usuario
                viewModel.iniciarConversacion(usuario.uid)
            },
            navController = navController
        )
    } else {
        ConversacionScreen(
            usuario = usuarioSeleccionado!!,
            mensajes = uiState.mensajes,
            onEnviarMensaje = { texto ->
                viewModel.enviarMensaje(texto, usuarioSeleccionado!!.uid)
            },
            onVolver = {
                usuarioSeleccionado = null
            },
            navController = navController
        )
    }
    
    // Mostrar error si existe
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            kotlinx.coroutines.delay(3000)
            viewModel.limpiarError()
        }
        Snackbar(
            modifier = Modifier.padding(16.dp),
            containerColor = MaterialTheme.colorScheme.error
        ) {
            Text(error)
        }
    }
}

@Composable
private fun ListaUsuariosScreen(
    usuarios: List<Usuario>,
    cargando: Boolean,
    onUsuarioClick: (Usuario) -> Unit,
    navController: NavController
) {
    val bottomBarItems = buildBottomItems(BottomDestination.Team, navController)
    Scaffold(
        containerColor = White,
        topBar = {
            ReusableTopAppBar(
                title = "Mensajes",
                showDivider = true
            )
        },
        bottomBar = { SafetyBottomBar(items = bottomBarItems) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(White)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            if (cargando) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SafetyGreenPrimary)
                }
            } else if (usuarios.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay usuarios disponibles",
                        style = TextStyle(
                            color = SafetyTextSecondary,
                            fontSize = 16.sp
                        )
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(usuarios) { usuario ->
                        UsuarioItem(
                            usuario = usuario,
                            onClick = { onUsuarioClick(usuario) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UsuarioItem(
    usuario: Usuario,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = SafetySurfaceAlt),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(SafetyGreenPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = usuario.nombre.firstOrNull()?.uppercase() ?: "?",
                    style = TextStyle(
                        color = White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            
            // Info usuario
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = usuario.nombre,
                    style = TextStyle(
                        color = SafetyTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    text = usuario.rol.name,
                    style = TextStyle(
                        color = SafetyTextSecondary,
                        fontSize = 14.sp
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConversacionScreen(
    usuario: Usuario,
    mensajes: List<Mensaje>,
    onEnviarMensaje: (String) -> Unit,
    onVolver: () -> Unit,
    navController: NavController
) {
    val bottomBarItems = buildBottomItems(BottomDestination.Team, navController)
    var nuevoMensaje by remember { mutableStateOf("") }

    Scaffold(
        containerColor = White,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SafetyGreenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = usuario.nombre.firstOrNull()?.uppercase() ?: "?",
                                style = TextStyle(
                                    color = White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Column {
                            Text(
                                text = usuario.nombre,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Text(
                                text = usuario.rol.name,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = SafetyTextSecondary
                                )
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        bottomBar = { SafetyBottomBar(items = bottomBarItems) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(White)
        ) {
            // Lista de mensajes
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mensajes) { mensaje ->
                    MessageBubble(
                        mensaje = mensaje,
                        nombreReceptor = usuario.nombre
                    )
                }
            }

            // Campo de entrada
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ReusableTextField(
                    contenido = "Escribe un mensaje",
                    value = nuevoMensaje,
                    onValueChange = { nuevoMensaje = it },
                    modifier = Modifier.weight(1f)
                )
                Card(
                    colors = CardDefaults.cardColors(containerColor = SafetyGreenPrimary),
                    shape = RoundedCornerShape(20.dp),
                ) {
                    IconButton(
                        onClick = {
                            if (nuevoMensaje.isNotBlank()) {
                                onEnviarMensaje(nuevoMensaje)
                                nuevoMensaje = ""
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Send,
                            contentDescription = "Enviar mensaje",
                            tint = White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(
    mensaje: Mensaje,
    nombreReceptor: String
) {
    val esPropio = mensaje.emisorUid == com.safetyfirst.datos.FirebaseRepositorio().usuarioActual()?.uid
    val formato = SimpleDateFormat("HH:mm", Locale.getDefault())
    val hora = formato.format(Date(mensaje.tiempo))
    
    Column(
        horizontalAlignment = if (esPropio) Alignment.End else Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            colors = if (esPropio) {
                CardDefaults.cardColors(containerColor = SafetyGreenPrimary)
            } else {
                CardDefaults.cardColors(containerColor = SafetySurface)
            },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .align(if (esPropio) Alignment.End else Alignment.Start)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (!esPropio) {
                    Text(
                        text = nombreReceptor,
                        style = TextStyle(
                            color = SafetyTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
                Text(
                    text = mensaje.texto,
                    style = TextStyle(
                        color = if (esPropio) White else SafetyTextSecondary,
                        fontSize = 14.sp
                    )
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = hora,
            style = TextStyle(
                color = SafetyTextSecondary,
                fontSize = 12.sp
            )
        )
    }
}
