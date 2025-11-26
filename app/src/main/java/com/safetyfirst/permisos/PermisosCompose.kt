package com.safetyfirst.permisos

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.safetyfirst.datos.FirebaseRepositorio
import com.safetyfirst.modelo.Rol
import com.safetyfirst.ui.Rutas
import com.safetyfirst.ubicacion.UbicacionVM
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

private fun permisoConcedido(permiso: String, actividad: Activity): Boolean =
    ContextCompat.checkSelfPermission(actividad, permiso) == PackageManager.PERMISSION_GRANTED

@Composable
fun PantPermisos(nav: NavController, repo: FirebaseRepositorio = FirebaseRepositorio()) {
    val activity = LocalContext.current as Activity
    val scope = rememberCoroutineScope()
    val brandGreen = Color(0xFF0B5F2A)
    val ubicacionVM: UbicacionVM = viewModel()

    var locFinaOk by remember { mutableStateOf(permisoConcedido(Manifest.permission.ACCESS_FINE_LOCATION, activity)) }
    var locGruesaOk by remember { mutableStateOf(permisoConcedido(Manifest.permission.ACCESS_COARSE_LOCATION, activity)) }
    var notifOk by remember {
        mutableStateOf(Build.VERSION.SDK_INT < 33 || permisoConcedido(Manifest.permission.POST_NOTIFICATIONS, activity))
    }

    val pedirLocFina = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { locFinaOk = it }
    val pedirLocGruesa = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { locGruesaOk = it }
    val pedirNotif = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { notifOk = it }

    fun justificar(permiso: String, mensaje: String) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permiso)) {
            Toast.makeText(activity, mensaje, Toast.LENGTH_LONG).show()
        }
    }

    val totalRequeridos = remember(locFinaOk, locGruesaOk, notifOk) {
        buildList {
            add(locFinaOk)
            add(locGruesaOk)
            if (Build.VERSION.SDK_INT >= 33) add(notifOk)
        }
    }
    val concedidos = totalRequeridos.count { it }
    val progreso = if (totalRequeridos.isEmpty()) 1f else concedidos / totalRequeridos.size.toFloat()
    val todosConcedidos = locFinaOk && locGruesaOk && notifOk

    LaunchedEffect(locFinaOk, locGruesaOk, notifOk) {
        if (todosConcedidos) {
            val uid = repo.usuarioActual()?.uid
            if (uid != null) {
                // Iniciar rastreo de ubicación AQUÍ, después de tener los permisos
                ubicacionVM.empezarRastreo(uid)
                
                val usuario = repo.obtenerUsuario(uid)
                when (usuario?.rol) {
                    Rol.SUPERVISOR -> nav.navigate(Rutas.HomeSupervisor.ruta) { popUpTo(Rutas.Permisos.ruta) { inclusive = true } }
                    else -> nav.navigate(Rutas.HomeObrero.ruta) { popUpTo(Rutas.Permisos.ruta) { inclusive = true } }
                }
            }
        }
    }

    Scaffold(containerColor = Color(0xFFF6F7F6)) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Configura tu experiencia", style = MaterialTheme.typography.titleLarge)
                Text(
                    "Activaremos alerta en tiempo real cuando nos compartas tu ubicación y podamos enviarte notificaciones.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "$concedidos de ${totalRequeridos.size} permisos concedidos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF4A4A4A)
                    )
                    LinearProgressIndicator(
                        progress = { progreso },
                        modifier = Modifier.fillMaxWidth(),
                        trackColor = Color(0xFFE3EAE4),
                        color = brandGreen
                    )
                }
            }

            PermissionCard(
                icon = Icons.Outlined.LocationOn,
                titulo = "Ubicación en tiempo real",
                descripcion = "Necesitamos saber dónde estás para activar zonas de riesgo cercanas y guiarte hasta las salidas seguras.",
                concedido = locFinaOk && locGruesaOk,
                botonTexto = when {
                    !locFinaOk -> "Permitir ubicación precisa"
                    !locGruesaOk -> "Permitir ubicación aproximada"
                    else -> "Permiso activo"
                },
                botonHabilitado = !(locFinaOk && locGruesaOk),
                onSolicitar = {
                    when {
                        !locFinaOk -> {
                            justificar(Manifest.permission.ACCESS_FINE_LOCATION, "Tu ubicación exacta permite activarte alarmas a tiempo.")
                            pedirLocFina.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                        !locGruesaOk -> {
                            justificar(Manifest.permission.ACCESS_COARSE_LOCATION, "La ubicación aproximada nos ayuda a mostrar tu progreso en el mapa.")
                            pedirLocGruesa.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                        }
                    }
                }
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PermissionStatusChip("Precisa", locFinaOk, brandGreen)
                    PermissionStatusChip("Aproximada", locGruesaOk, brandGreen)
                }
            }

            if (Build.VERSION.SDK_INT >= 33) {
                PermissionCard(
                    icon = Icons.Outlined.NotificationsActive,
                    titulo = "Notificaciones críticas",
                    descripcion = "Te avisaremos al instante cuando se creen nuevas zonas o debas evacuar un área.",
                    concedido = notifOk,
                    botonTexto = if (notifOk) "Permiso activo" else "Permitir notificaciones",
                    botonHabilitado = !notifOk,
                    onSolicitar = { pedirNotif.launch(Manifest.permission.POST_NOTIFICATIONS) }
                )
            }

            Spacer(Modifier.weight(1f, fill = true))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Puedes modificar estos permisos más tarde desde los ajustes de tu dispositivo.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF5F5F5F)
                )
                Button(
                    onClick = {
                        if (todosConcedidos) {
                            scope.launch {
                                val uid = repo.usuarioActual()?.uid
                                val usuario = uid?.let { repo.obtenerUsuario(it) }
                                when (usuario?.rol) {
                                    Rol.SUPERVISOR -> nav.navigate(Rutas.HomeSupervisor.ruta) { popUpTo(Rutas.Permisos.ruta) { inclusive = true } }
                                    else -> nav.navigate(Rutas.HomeObrero.ruta) { popUpTo(Rutas.Permisos.ruta) { inclusive = true } }
                                }
                            }
                        }
                    },
                    enabled = todosConcedidos,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = brandGreen,
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFBFCFC5),
                        disabledContentColor = Color.White
                    )
                ) {
                    Text(if (todosConcedidos) "Continuar" else "Concede todos los permisos")
                }
            }
        }
    }
}

@Composable
private fun PermissionCard(
    icon: ImageVector,
    titulo: String,
    descripcion: String,
    concedido: Boolean,
    botonTexto: String,
    botonHabilitado: Boolean,
    onSolicitar: () -> Unit,
    extraContenido: @Composable ColumnScope.() -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    color = Color(0xFFEAF2EC),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color(0xFF0B5F2A),
                        modifier = Modifier
                            .padding(12.dp)
                            .size(28.dp)
                    )
                }
                Column {
                    Text(titulo, style = MaterialTheme.typography.titleMedium)
                    Text(
                        descripcion,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF5F5F5F)
                    )
                }
            }

            extraContenido()

            Button(
                onClick = onSolicitar,
                enabled = botonHabilitado,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0B5F2A),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFBFCFC5),
                    disabledContentColor = Color.White
                ),
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(botonTexto)
            }
        }
    }
}

@Composable
private fun PermissionStatusChip(label: String, activo: Boolean, brandGreen: Color) {
    val chipColor = if (activo) brandGreen.copy(alpha = 0.12f) else Color(0xFFF1F1F1)
    val textColor = if (activo) brandGreen else Color(0xFF6B6B6B)
    Surface(
        color = chipColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = if (activo) Icons.Outlined.CheckCircle else Icons.Outlined.Info,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(18.dp)
            )
            Text(label, color = textColor, style = MaterialTheme.typography.bodySmall)
        }
    }
}
