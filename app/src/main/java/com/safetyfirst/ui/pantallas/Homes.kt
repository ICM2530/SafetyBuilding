@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.safetyfirst.ui.pantallas

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.safetyfirst.R
import com.safetyfirst.datos.FirebaseRepositorio
import com.safetyfirst.ui.Rutas
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun PantHomeObrero(nav: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("SafetyFirst · Obrero") }) },
        bottomBar = {
            BottomAppBar {
                Button(onClick = { nav.navigate(Rutas.Perfil.ruta) }) { Text("Perfil") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { nav.navigate(Rutas.ChatUsuarios.ruta) }) { Text("Mensajes") }
            }
        }
    ) { p ->
        Column(Modifier.padding(p).padding(16.dp)) {
            Text("Inicio", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))
            Button(onClick = { nav.navigate(Rutas.Zonas.ruta) }) { Text("Riesgos") }
        }
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
    val background = if (stat.highlight) Color(0xFFD5E6D4) else Color(0xFFE7EFE4)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = background),
        shape = RoundedCornerShape(32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stat.value,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = brandGreen
            )
            Text(
                text = stat.description,
                fontSize = 16.sp,
                color = Color.Black
            )
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
    val navBackStackEntry by nav.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route.orEmpty()

    val items = listOf(
        BottomBarItem(Icons.Filled.Home, "Inicio", Rutas.HomeSupervisor.ruta),
        BottomBarItem(Icons.Outlined.ChatBubble, "Mensajes", Rutas.ChatUsuarios.ruta),
        BottomBarItem(Icons.Outlined.Map, "Mapa", Rutas.MapaSupervisor.ruta),
        BottomBarItem(Icons.Outlined.Group, "Operadores", Rutas.Operadores.ruta),
        BottomBarItem(Icons.Filled.Add, "Nueva zona", Rutas.Zonas.ruta)
    )

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
