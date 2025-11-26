@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.safetyfirst.ui.pantallas

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.safetyfirst.ubicacion.UbicacionVM
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.safetyfirst.R
import com.safetyfirst.datos.FirebaseRepositorio
import com.safetyfirst.modelo.Usuario
import com.safetyfirst.ui.Rutas
import com.safetyfirst.ui.sensors.NoiseMeter
import com.safetyfirst.ui.sensors.rememberAltitudeMeters
import com.safetyfirst.ui.sensors.rememberFallDetector
import com.safetyfirst.ui.sensors.rememberNoiseMeter
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun PantHomeObrero(nav: NavController, repo: FirebaseRepositorio = FirebaseRepositorio()) {
    val brandGreen = Color(0xFF0B5F2A)
    val currentUid = repo.usuarioActual()?.uid
    val zonas by repo.flujoZonas().collectAsState(initial = emptyList())
    val usuario by produceState<Usuario?>(initialValue = null, currentUid) {
        value = currentUid?.let { repo.obtenerUsuario(it) }
    }

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val noiseMeter = rememberNoiseMeter()
    var showNoiseDialog by remember { mutableStateOf(false) }
    var fallAlert by remember { mutableStateOf(false) }
    val altitude by rememberAltitudeMeters()

    //  Enviar ubicación del obrero a Firebase para que el supervisor lo vea en el mapa
    val ubicacionVM: UbicacionVM = viewModel()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            currentUid?.let { uid ->
                ubicacionVM.empezarRastreo(uid)
            }
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    "Se necesita acceso a la ubicación para reportar tu posición al supervisor."
                )
            }
        }
    }

    LaunchedEffect(currentUid) {
        currentUid?.let { uid ->
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Ya tiene permiso → empieza a mandar posiciones a /posiciones/{uid}
                ubicacionVM.empezarRastreo(uid)
            } else {
                // Pide el permiso de ubicación
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }


    rememberFallDetector {
        fallAlert = true
    }

    LaunchedEffect(fallAlert) {
        if (fallAlert) {
            snackbarHostState.showSnackbar("Posible caída detectada. Confirma tu estado.")
        }
    }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            showNoiseDialog = true
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Se necesita acceso al micrófono para medir el nivel de ruido.")
            }
        }
    }

    val zonasAsignadasIds = usuario?.zonasAsignadas.orEmpty()
    val zonasAsignadas = zonas.filter { it.id in zonasAsignadasIds }

    val pendientesInspeccion = zonasAsignadas.count { it.tipos.isEmpty() }
    val conEvidencia = zonasAsignadas.count { it.fotoUrl.isNotBlank() }

    val altitudeStat = altitude?.let { String.format(Locale.US, "%.1f", it) } ?: "--"

    val statCards = listOf(
        DashboardStat(zonasAsignadas.size.toString(), "Zonas asignadas", highlight = true),
        DashboardStat(pendientesInspeccion.toString(), "Pendientes por revisar", highlight = false),
        DashboardStat(conEvidencia.toString(), "Con evidencia registrada", highlight = false),
        DashboardStat(altitudeStat, "Altitud actual (m)", highlight = false)
    )

    val alertCards = zonasAsignadas
        .sortedByDescending { it.radioMetros }
        .take(5)
        .map {
            val resumen = buildString {
                append(it.descripcion.ifBlank { "Sin descripción" }.take(70))
                if (it.descripcion.length > 70) append("…")
                append(" • Radio ${it.radioMetros} m")
            }
            DashboardAlert(
                id = it.id,
                title = it.titulo.ifBlank { "Zona por nombrar" },
                subtitle = resumen
            )
        }

    val quickActions = listOf(
        WorkerQuickAction(
            icon = Icons.Outlined.Add,
            title = "Reportar hallazgo",
            description = "Captura un nuevo riesgo con fotos y descripción detallada."
        ) { nav.navigate(Rutas.Zonas.ruta) },
        WorkerQuickAction(
            icon = Icons.Outlined.Map,
            title = "Ver mapa asignado",
            description = "Consulta los límites y puntos críticos de tu jornada."
        ) { nav.navigate(Rutas.MapaSupervisor.ruta) },
        WorkerQuickAction(
            icon = Icons.Outlined.ChatBubble,
            title = "Contactar supervisor",
            description = "Envía una actualización o solicita apoyo de inmediato."
        ) { nav.navigate(Rutas.ChatUsuarios.ruta) },
        WorkerQuickAction(
            icon = Icons.AutoMirrored.Outlined.VolumeUp,
            title = "Medir nivel de ruido",
            description = "Analiza el ruido ambiente y detecta niveles peligrosos."
        ) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                showNoiseDialog = true
            } else {
                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    )

    Scaffold(
        containerColor = Color.White,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = { WorkerBottomBar(nav, brandGreen) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            WorkerDashboardHeader(usuario, brandGreen, nav)

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                statCards.forEach { stat -> StatisticCard(stat, brandGreen) }
            }

            WorkerQuickActionsSection(quickActions, brandGreen)

            WorkerAlertSection(
                alerts = alertCards,
                onAlertClick = { id -> nav.navigate(Rutas.RiesgoDetalle.ruta + "/$id") }
            )

            Spacer(Modifier.height(90.dp))
        }
    }

    if (fallAlert) {
        AlertDialog(
            onDismissRequest = { fallAlert = false },
            title = { Text("Posible caída detectada") },
            text = {
                Text("Registramos un movimiento brusco. Si necesitas asistencia, contacta a tu supervisor o marca una alerta.")
            },
            confirmButton = {
                TextButton(onClick = { fallAlert = false }) {
                    Text("Estoy bien")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    fallAlert = false
                    nav.navigate(Rutas.ChatUsuarios.ruta)
                }) {
                    Text("Pedir ayuda")
                }
            }
        )
    }

    if (showNoiseDialog) {
        NoiseMeasurementDialog(
            noiseMeter = noiseMeter,
            onDismiss = { showNoiseDialog = false }
        )
    }
}

@Composable
fun PantHomeSupervisor(nav: NavController, repo: FirebaseRepositorio = FirebaseRepositorio()) {
    val brandGreen = Color(0xFF0B5F2A)
    val zonas by repo.flujoZonas().collectAsState(initial = emptyList())

    val totalReportados = zonas.size
    val conTipos = zonas.count { it.tipos.isNotEmpty() }
    val conFoto = zonas.count { it.fotoUrl.isNotBlank() }

    val statCards = listOf(
        DashboardStat(totalReportados.toString(), "Riesgos reportados", highlight = true),
        DashboardStat(conTipos.toString(), "Zonas con tipificación", highlight = false),
        DashboardStat(conFoto.toString(), "Con evidencia fotográfica", highlight = false)
    )

    val alertCards = zonas.filter { it.id.isNotBlank() }.take(5).map {
        val subtitulo = buildString {
            append(it.descripcion.ifBlank { "Sin descripción" }.take(60))
            if (it.descripcion.length > 60) append("…")
            append(" • Radio ${it.radioMetros}m")
        }
        DashboardAlert(
            id = it.id,
            title = it.titulo.ifBlank { "Zona sin nombre" },
            subtitle = subtitulo
        )
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = { SupervisorBottomBar(nav, brandGreen) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            HeaderSupervisorDashboard(nav, brandGreen)

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                statCards.forEach { stat ->
                    StatisticCard(stat, brandGreen)
                }
            }

            if (alertCards.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD9D9D9)),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp, horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No hay alertas recientes", color = Color(0xFF4A4A4A))
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    alertCards.forEach { alert ->
                        AlertCard(alert) {
                            nav.navigate(Rutas.RiesgoDetalle.ruta + "/${alert.id}")
                        }
                    }
                }
            }

            Spacer(Modifier.height(90.dp)) // espacio adicional para que no tape la barra inferior
        }
    }
}

@Composable
private fun HeaderSupervisorDashboard(nav: NavController, brandGreen: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = androidx.compose.ui.res.painterResource(id = R.drawable.auth_logo),
                contentDescription = "Logo SafetyFirst",
                modifier = Modifier.size(40.dp),
                colorFilter = ColorFilter.tint(brandGreen)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Dashboard",
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        IconButton(onClick = { nav.navigate(Rutas.Perfil.ruta) }) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "Ir a mi perfil",
                tint = brandGreen,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun StatisticCard(stat: DashboardStat, brandGreen: Color) {
    val background = if (stat.highlight) 
        androidx.compose.ui.graphics.Brush.verticalGradient(
            colors = listOf(Color(0xFFD5E6D4), Color(0xFFE8F1E8))
        ) 
    else 
        androidx.compose.ui.graphics.Brush.verticalGradient(
            colors = listOf(Color(0xFFF0F5F0), Color(0xFFF8FBF8))
        )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (stat.highlight) 4.dp else 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(background)
                .padding(vertical = 28.dp, horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = stat.value,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = brandGreen
                    )
                    Text(
                        text = stat.description,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2D3D2D)
                    )
                }
                if (stat.highlight) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(brandGreen.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = brandGreen,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkerDashboardHeader(usuario: Usuario?, brandGreen: Color, nav: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FBF8)),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(brandGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = usuario?.nombre?.firstOrNull()?.uppercase() ?: "O",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = brandGreen
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = if (!usuario?.nombre.isNullOrBlank()) "Hola, ${usuario.nombre?.trim()?.split(' ')?.firstOrNull() ?: usuario.nombre}" else "Hola, Obrero",
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Mantén actualizada tu jornada",
                        color = Color(0xFF6B7B69),
                        fontSize = 13.sp
                    )
                }
            }
            IconButton(
                onClick = { nav.navigate(Rutas.Perfil.ruta) },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(brandGreen.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Ir a mi perfil",
                    tint = brandGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun WorkerQuickActionsSection(
    actions: List<WorkerQuickAction>,
    brandGreen: Color
) {
    if (actions.isEmpty()) return

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(brandGreen)
            )
            Text(
                text = "Acciones rápidas",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        actions.forEach { action ->
            WorkerQuickActionCard(action, brandGreen)
        }
    }
}

@Composable
private fun WorkerQuickActionCard(action: WorkerQuickAction, brandGreen: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = action.onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(brandGreen.copy(alpha = 0.15f), brandGreen.copy(alpha = 0.25f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = null,
                    tint = brandGreen,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = action.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = action.description,
                    fontSize = 13.sp,
                    color = Color(0xFF6B7B69),
                    lineHeight = 18.sp
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = brandGreen.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun WorkerAlertSection(alerts: List<DashboardAlert>, onAlertClick: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Alertas asignadas",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )

        if (alerts.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F1F1)),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tu jornada está al día",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Cuando un supervisor te asigne una zona, aparecerá aquí.",
                        fontSize = 13.sp,
                        color = Color(0xFF5F5F5F)
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                alerts.forEach { alert ->
                    AlertCard(alert) { onAlertClick(alert.id) }
                }
            }
        }
    }
}

@Composable
private fun AlertCard(alert: DashboardAlert, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD9D9D9)),
        shape = RoundedCornerShape(32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp, horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = alert.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = alert.subtitle,
                fontSize = 14.sp,
                color = Color(0xFF4A4A4A)
            )
        }
    }
}

@Composable
fun SupervisorBottomBar(nav: NavController, brandGreen: Color) {
    val items = listOf(
        BottomBarItem(Icons.Filled.Home, "Inicio", Rutas.HomeSupervisor.ruta),
        BottomBarItem(Icons.Outlined.ChatBubble, "Mensajes", Rutas.ChatUsuarios.ruta),
        BottomBarItem(Icons.Outlined.Map, "Mapa", Rutas.MapaSupervisor.ruta),
        BottomBarItem(Icons.Outlined.Group, "Operadores", Rutas.Operadores.ruta),
        BottomBarItem(Icons.Filled.Add, "Nueva zona", Rutas.Zonas.ruta)
    )

    RoundedBottomNavigation(nav, brandGreen, items)
}

@Composable
fun WorkerBottomBar(nav: NavController, brandGreen: Color) {
    val items = listOf(
        BottomBarItem(Icons.Filled.Home, "Inicio", Rutas.HomeObrero.ruta),
        BottomBarItem(Icons.Outlined.Add, "Reportar", Rutas.Zonas.ruta),
        BottomBarItem(Icons.Outlined.Map, "Mapa", Rutas.MapaSupervisor.ruta),
        BottomBarItem(Icons.Outlined.ChatBubble, "Chat", Rutas.ChatUsuarios.ruta),
        BottomBarItem(Icons.Outlined.Person, "Perfil", Rutas.Perfil.ruta)
    )

    RoundedBottomNavigation(nav, brandGreen, items)
}

@Composable
private fun RoundedBottomNavigation(
    nav: NavController,
    brandGreen: Color,
    items: List<BottomBarItem>
) {
    val navBackStackEntry by nav.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route.orEmpty()

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = brandGreen,
            shape = RoundedCornerShape(40.dp),
            tonalElevation = 4.dp,
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = currentRoute.startsWith(item.route)
                    IconButton(onClick = {
                        if (!isSelected) {
                            nav.navigate(item.route) {
                                launchSingleTop = true
                            }
                        }
                    }) {
                        val background = if (isSelected) Color(0xFFAED4B3) else Color.Transparent
                        Surface(
                            color = background,
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.contentDescription,
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class DashboardStat(
    val value: String,
    val description: String,
    val highlight: Boolean
)

private data class DashboardAlert(
    val id: String,
    val title: String,
    val subtitle: String
)

private data class BottomBarItem(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val contentDescription: String,
    val route: String
)

private data class WorkerQuickAction(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val description: String,
    val onClick: () -> Unit
)

@Composable
private fun NoiseMeasurementDialog(
    noiseMeter: NoiseMeter,
    onDismiss: () -> Unit
) {
    val decibels by noiseMeter.decibels

    LaunchedEffect(Unit) {
        noiseMeter.start()
    }

    DisposableEffect(Unit) {
        onDispose { noiseMeter.stop() }
    }

    val dbValue = decibels.coerceAtLeast(0.0)
    val status = when {
        dbValue >= 85 -> "Riesgo alto"
        dbValue >= 65 -> "Precaución"
        else -> "Nivel seguro"
    }
    val statusColor = when {
        dbValue >= 85 -> Color(0xFFD74C3D)
        dbValue >= 65 -> Color(0xFFF3A03A)
        else -> Color(0xFF0B5F2A)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Medición de ruido") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "${dbValue.roundToInt()} dB",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = status,
                    color = statusColor,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Se recomienda protección auditiva cuando superas los 85 dB.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        }
    )
}
