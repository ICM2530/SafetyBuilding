package com.safetyfirst.permisos

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.safetyfirst.datos.FirebaseRepositorio
import com.safetyfirst.modelo.Rol
import com.safetyfirst.ui.Rutas
import kotlinx.coroutines.launch

private fun permisoConcedido(permiso: String, actividad: Activity): Boolean =
    ContextCompat.checkSelfPermission(actividad, permiso) == PackageManager.PERMISSION_GRANTED

@Composable
fun PantPermisos(nav: NavController, repo: FirebaseRepositorio = FirebaseRepositorio()) {
    val act = LocalContext.current as Activity
    val scope = rememberCoroutineScope()

    var locFinaOk by remember { mutableStateOf(permisoConcedido(Manifest.permission.ACCESS_FINE_LOCATION, act)) }
    var locGruesaOk by remember { mutableStateOf(permisoConcedido(Manifest.permission.ACCESS_COARSE_LOCATION, act)) }
    var microOk by remember { mutableStateOf(permisoConcedido(Manifest.permission.RECORD_AUDIO, act)) }
    var notifOk by remember { mutableStateOf(Build.VERSION.SDK_INT < 33 || permisoConcedido(Manifest.permission.POST_NOTIFICATIONS, act)) }

    val pedirLocFina = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { locFinaOk = it }
    val pedirLocGruesa = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { locGruesaOk = it }
    val pedirMicro = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { microOk = it }
    val pedirNotif = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { notifOk = it }

    fun justificar(permiso: String, msg: String) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(act, permiso)) {
            Toast.makeText(act, msg, Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(locFinaOk, locGruesaOk, microOk, notifOk) {
        if (locFinaOk && locGruesaOk && microOk && notifOk) {
            val uid = repo.usuarioActual()?.uid
            if (uid != null) {
                val u = repo.obtenerUsuario(uid)
                when (u?.rol) {
                    Rol.SUPERVISOR -> nav.navigate(Rutas.HomeSupervisor.ruta) { popUpTo(Rutas.Permisos.ruta) { inclusive = true } }
                    else -> nav.navigate(Rutas.HomeObrero.ruta) { popUpTo(Rutas.Permisos.ruta) { inclusive = true } }
                }
            }
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Permisos requeridos", style = MaterialTheme.typography.titleLarge)

        Card { Column(Modifier.padding(12.dp)) {
            Text("Ubicación precisa")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    justificar(Manifest.permission.ACCESS_FINE_LOCATION, "Necesitamos ubicación precisa para alertas de riesgo (altura).")
                    pedirLocFina.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }) { Text(if (locFinaOk) "Concedido" else "Solicitar") }

                Button(onClick = {
                    justificar(Manifest.permission.ACCESS_COARSE_LOCATION, "Necesitamos ubicación aproximada.")
                    pedirLocGruesa.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                }) { Text(if (locGruesaOk) "Concedido" else "Solicitar") }
            }
        } }

        Card { Column(Modifier.padding(12.dp)) {
            Text("Micrófono (Ruido)")
            Button(onClick = {
                justificar(Manifest.permission.RECORD_AUDIO, "Necesitamos acceso al micrófono para detectar niveles de ruido peligroso.")
                pedirMicro.launch(Manifest.permission.RECORD_AUDIO)
            }) {
                Text(if (microOk) "Concedido" else "Solicitar")
            }
        } }


        if (Build.VERSION.SDK_INT >= 33) {
            Card { Column(Modifier.padding(12.dp)) {
                Text("Notificaciones")
                Button(onClick = { pedirNotif.launch(Manifest.permission.POST_NOTIFICATIONS) }) {
                    Text(if (notifOk) "Concedido" else "Solicitar")
                }
            } }
        }

        Spacer(Modifier.height(12.dp))
        Text("Una vez otorgados, avanzarás automáticamente al Home por tu rol.")
    }
}
