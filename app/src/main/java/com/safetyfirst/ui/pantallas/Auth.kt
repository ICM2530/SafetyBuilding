package com.safetyfirst.ui.pantallas

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.safetyfirst.R
import com.safetyfirst.datos.FirebaseRepositorio
import com.safetyfirst.modelo.Rol
import com.safetyfirst.ui.Rutas
import kotlinx.coroutines.launch

@Composable
fun PantAutenticacion(nav: NavController, repo: FirebaseRepositorio = FirebaseRepositorio()) {
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var esRegistro by remember { mutableStateOf(false) }
    var rol by remember { mutableStateOf(Rol.OBRERO) }
    var cargando by remember { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current

    fun hayInternet(): Boolean {
        val cm = ctx.getSystemService(ConnectivityManager::class.java)
        val net = cm?.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(net) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    val primaryGreen = androidx.compose.ui.graphics.Color(0xFF0B5F2A)

    Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
            ) {
                // Imagen de la maqueta. Coloca el recurso en res/drawable/auth_logo.png o svg y actualiza el id.
                ImageAuthLogo()

                Text(
                    text = if (esRegistro) "Registro" else "Inicio de sesión",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                if (esRegistro) {
                    AuthTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = "Nombre"
                    )
                }

                AuthTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = "Usuario"
                )

                AuthTextField(
                    value = clave,
                    onValueChange = { clave = it },
                    label = "Contraseña",
                    isPassword = true
                )

                if (esRegistro) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Rol:")
                        AssistChip(
                            onClick = { rol = Rol.OBRERO },
                            label = { Text("Obrero") },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (rol == Rol.OBRERO) primaryGreen else MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = if (rol == Rol.OBRERO) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        AssistChip(
                            onClick = { rol = Rol.SUPERVISOR },
                            label = { Text("Supervisor") },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (rol == Rol.SUPERVISOR) primaryGreen else MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = if (rol == Rol.SUPERVISOR) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Button(
                    enabled = !cargando,
                    onClick = {
                        if (!hayInternet()) {
                            scope.launch { snackbar.showSnackbar("Sin conexión a Internet.") }
                            return@Button
                        }
                        cargando = true
                        scope.launch {
                            try {
                                if (esRegistro) repo.registrarUsuario(correo, clave, nombre, rol) else {
                                    val (usuario, existePerfil) = repo.iniciarSesionConEstado(correo, clave)
                                    if (!existePerfil) {
                                        repo.guardarUsuario(usuario.copy(nombre = usuario.nombre.ifEmpty { "Usuario" }, rol = Rol.OBRERO))
                                    }
                                }
                                nav.navigate(Rutas.Permisos.ruta) { popUpTo(Rutas.Autenticacion.ruta) { inclusive = true } }
                            } catch (e: com.google.firebase.FirebaseNetworkException) {
                                snackbar.showSnackbar("Error de red con Firebase (reCAPTCHA).")
                            } catch (e: Exception) {
                                snackbar.showSnackbar("No se pudo autenticar: ${e.message ?: "error desconocido"}")
                            } finally {
                                cargando = false
                            }
                        }
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryGreen,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (esRegistro) "Registrar" else "LogIn")
                }

                TextButton(onClick = { esRegistro = !esRegistro }) {
                    Text(if (esRegistro) "¿Ya tienes cuenta? Inicia sesión" else "Crear cuenta nueva")
                }

                if (cargando) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false
) {
    androidx.compose.material3.TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(50),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            cursorColor = MaterialTheme.colorScheme.onSurface,
            focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
            unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
            disabledIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
        )
    )
}

@Composable
private fun ImageAuthLogo() {
    Image(
        painter = painterResource(id = R.drawable.auth_logo),
        contentDescription = "Logo SafetyFirst",
        modifier = Modifier.height(160.dp)
    )
}
