@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.safetyfirst.ui.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.messaging.FirebaseMessaging
import com.safetyfirst.R
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
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Row {
            Text("Rol:")
            Spacer(Modifier.width(8.dp))
            AssistChip(onClick = { rol = Rol.OBRERO }, label = { Text("Obrero") })
            Spacer(Modifier.width(8.dp))
            AssistChip(onClick = { rol = Rol.SUPERVISOR }, label = { Text("Supervisor") })
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                scope.launch {
                    val token = FirebaseMessaging.getInstance().token.await()
                    repo.guardarUsuario(Usuario(uid, nombre, correo, rol, token))
                    nav.navigate(Rutas.Permisos.ruta)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Guardar y continuar") }
    }
}

@Composable
fun PantPerfil(nav: NavController, repo: FirebaseRepositorio = FirebaseRepositorio()) {
    val uid = repo.usuarioActual()?.uid.orEmpty()
    val brandGreen = Color(0xFF0B5F2A)
    val cardBackground = Color(0xFFE8F1E8)
    val infoSurface = Color(0xFFF6F8F4)
    val snackbarHost = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var perfil by remember { mutableStateOf<Usuario?>(null) }
    var cargando by remember { mutableStateOf(true) }

    val recargarPerfil: () -> Unit = remember(uid) {
        {
            scope.launch {
                cargando = true
                try {
                    perfil = if (uid.isNotBlank()) repo.obtenerUsuario(uid) else null
                } catch (e: Exception) {
                    snackbarHost.showSnackbar("No se pudo cargar el perfil: ${e.message ?: "error"}")
                    perfil = null
                } finally {
                    cargando = false
                }
            }
        }
    }

    LaunchedEffect(uid) { recargarPerfil() }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Surface(
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = brandGreen
                        )
                    }
                    Text(
                        text = "Mi perfil",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { padding ->
        when {
            cargando -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = brandGreen) }
            }

            perfil == null -> {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No pudimos obtener tu información.", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { recargarPerfil() },
                        colors = ButtonDefaults.buttonColors(containerColor = brandGreen)
                    ) {
                        Text("Reintentar", color = Color.White)
                    }
                }
            }

            else -> {
                val user = perfil!!
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.auth_logo),
                                contentDescription = "Logo SafetyFirst",
                                modifier = Modifier.size(42.dp),
                                colorFilter = ColorFilter.tint(brandGreen)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Mi perfil",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black
                                )
                                Text(
                                    if (user.rol == Rol.SUPERVISOR) "Supervisor SafetyFirst" else "Colaborador",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        IconButton(onClick = {
                            scope.launch {
                                repo.cerrarSesion()
                                snackbarHost.showSnackbar("Sesión cerrada.")
                                nav.navigate(Rutas.Autenticacion.ruta) {
                                    popUpTo(Rutas.Autenticacion.ruta) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Logout,
                                contentDescription = "Cerrar sesión",
                                tint = brandGreen
                            )
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBackground),
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(84.dp)
                                    .clip(CircleShape)
                                    .background(brandGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Person,
                                    contentDescription = null,
                                    tint = brandGreen,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    user.nombre.ifBlank { "Supervisor sin nombre" },
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(user.correo.ifBlank { "Correo no registrado" }, color = Color.DarkGray)
                                AssistChip(
                                    onClick = {},
                                    label = { Text(if (user.rol == Rol.SUPERVISOR) "Supervisor" else "Obrero") },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = brandGreen,
                                        labelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    Surface(
                        color = infoSurface,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ProfileInfoRow(Icons.Outlined.Badge, "Identificador", user.uid.takeLast(8).uppercase())
                            HorizontalDivider(thickness = 1.dp, color = Color(0xFFD0D7CE))
                            ProfileInfoRow(Icons.Outlined.Email, "Correo electrónico", user.correo.ifBlank { "-" })
                            HorizontalDivider(thickness = 1.dp, color = Color(0xFFD0D7CE))
                            ProfileInfoRow(
                                Icons.Outlined.LocationOn,
                                "Documento",
                                if (user.documentoNumero.isNotBlank()) user.documentoNumero else "Sin registrar"
                            )
                            HorizontalDivider(thickness = 1.dp, color = Color(0xFFD0D7CE))
                            ProfileInfoRow(
                                Icons.Outlined.Phone,
                                "Teléfono",
                                if (user.telefono.isNotBlank()) user.telefono else "Sin registrar"
                            )
                        }
                    }

                    if (user.permisos.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Permisos y certificaciones", fontWeight = FontWeight.SemiBold)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                user.permisos.forEach { permiso ->
                                    AssistChip(onClick = {}, label = { Text(permiso) })
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { scope.launch { snackbarHost.showSnackbar("Pronto podrás editar tu perfil desde aquí.") } },
                        colors = ButtonDefaults.buttonColors(containerColor = brandGreen),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(28.dp)
                    ) { Text("Editar información", color = Color.White) }
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, etiqueta: String, valor: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF0B5F2A))
        Column {
            Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = Color(0xFF526150))
            Text(
                valor,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
