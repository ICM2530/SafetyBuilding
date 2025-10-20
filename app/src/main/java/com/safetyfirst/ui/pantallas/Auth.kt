package com.safetyfirst.ui.pantallas

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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

    Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { p ->
        Column(Modifier.padding(p).padding(16.dp)) {
            Text(if (esRegistro) "Registro" else "Inicio de sesión", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(12.dp))
            if (esRegistro) {
                OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
            }
            OutlinedTextField(correo, { correo = it }, label = { Text("Correo") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                clave, { clave = it }, label = { Text("Clave") },
                visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth()
            )
            if (esRegistro) {
                Spacer(Modifier.height(8.dp))
                Row {
                    Text("Rol:"); Spacer(Modifier.width(8.dp))
                    AssistChip(onClick = { rol = Rol.OBRERO }, label = { Text("Obrero") })
                    Spacer(Modifier.width(8.dp))
                    AssistChip(onClick = { rol = Rol.SUPERVISOR }, label = { Text("Supervisor") })
                }
            }
            Spacer(Modifier.height(12.dp))
            Row {
                Button(enabled = !cargando, onClick = {
                    if (!hayInternet()) { scope.launch { snackbar.showSnackbar("Sin conexión a Internet.") }; return@Button }
                    cargando = true
                    scope.launch {
                        try {
                            if (esRegistro) repo.registrarUsuario(correo, clave, nombre, rol)
                            else {
                                val (usuario, existePerfil) = repo.iniciarSesionConEstado(correo, clave)
                                if (!existePerfil) {
                                    // Para cuentas creadas en consola: crear ficha mínima
                                    repo.guardarUsuario(usuario.copy(nombre = usuario.nombre.ifEmpty { "Usuario" }, rol = Rol.OBRERO))
                                }
                            }
                            nav.navigate(Rutas.Permisos.ruta) { popUpTo(Rutas.Autenticacion.ruta) { inclusive = true } }
                        } catch (e: com.google.firebase.FirebaseNetworkException) {
                            snackbar.showSnackbar("Error de red con Firebase (reCAPTCHA).")
                        } catch (e: Exception) {
                            snackbar.showSnackbar("No se pudo autenticar: ${e.message ?: "error desconocido"}")
                        } finally { cargando = false }
                    }
                }) { Text(if (esRegistro) "Crear cuenta" else "Entrar") }

                Spacer(Modifier.width(8.dp))
                TextButton(onClick = { esRegistro = !esRegistro }) {
                    Text(if (esRegistro) "¿Ya tienes cuenta? Inicia sesión" else "Crear cuenta nueva")
                }
            }
            if (cargando) { Spacer(Modifier.height(16.dp)); LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
        }
    }
}
