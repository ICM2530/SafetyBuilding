package com.example.main.Screens

import Navigation.AppScreens
import Navigation.BottomDestination
import Navigation.buildBottomItems
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.main.CompReusable.ReusableTopAppBar
import com.example.main.CompReusable.SafetyBottomBar
import com.example.main.CompReusable.ReusableButton
import com.example.main.utils.theme.SafetyGreenHighlight
import com.example.main.utils.theme.SafetyGreenPrimary
import com.example.main.utils.theme.SafetyNeutral
import com.example.main.utils.theme.SafetyNeutralLight
import com.example.main.utils.theme.SafetySurface
import com.example.main.utils.theme.SafetySurfaceAlt
import com.example.main.utils.theme.SafetyTextPrimary
import com.example.main.utils.theme.SafetyTextSecondary
import com.example.main.utils.theme.White

@Composable
fun HomeScreen(navController: NavController) {
    val bottomBarItems = buildBottomItems(BottomDestination.Home, navController)

    val updates = listOf(
        DashboardUpdate(
            title = "Alerta eléctrica en planta baja",
            description = "Se registró un corte de energía y exposición de cables  hace 12 minutos.",
            level = "Alta prioridad"
        ),
        DashboardUpdate(
            title = "Revisión de EPP",
            description = "El equipo de estructuras completó la inspección semanal de seguridad.",
            level = "Rutina"
        ),
        DashboardUpdate(
            title = "Nueva zona monitoreada",
            description = "Se agregó el cuarto de control como zona crítica para seguimiento.",
            level = "Actualización"
        )
    )

    Scaffold(
        containerColor = White,
        bottomBar = { SafetyBottomBar(items = bottomBarItems) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(White)
        ) {
            ReusableTopAppBar(
                title = "Dashboard",
                trailingIcon = null,
                actions = {
                    IconButton(onClick = { /* TODO: notificaciones */ }) {
                        Icon(
                            imageVector = Icons.Outlined.NotificationsNone,
                            contentDescription = "Notificaciones",
                            tint = SafetyTextPrimary
                        )
                    }
                    IconButton(onClick = {
                        navController.navigate(AppScreens.ProfileScreen.name)
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Perfil",
                            tint = SafetyTextPrimary
                        )
                    }
                },
                showDivider = true
            )

            val listState = rememberLazyListState()
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    DashboardSummaryCard(onCreateReport = {
                        navController.navigate(AppScreens.AddZoneRisk.name)
                    })
                }

                item {
                    QuickActionsRow(
                        actions = listOf(
                            QuickAction(
                                icon = Icons.AutoMirrored.Outlined.Article,
                                title = "Reportes",
                                description = "Historial de incidentes"
                            ) { navController.navigate(AppScreens.RiskCodeScreen.name) },
                            QuickAction(
                                icon = Icons.Outlined.Map,
                                title = "Mapa",
                                description = "Ubicación de zonas"
                            ) { navController.navigate(AppScreens.RiskZones.name) },
                            QuickAction(
                                icon = Icons.Outlined.Group,
                                title = "Equipo",
                                description = "Supervisores a cargo"
                            ) { navController.navigate(AppScreens.ChatScreen.name) }
                        )
                    )
                }

                item {
                    Text(
                        text = "Actualizaciones recientes",
                        style = TextStyle(
                            color = SafetyTextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                items(updates) { update ->
                    DashboardUpdateCard(update = update)
                }
            }
        }
    }
}

@Composable
private fun DashboardSummaryCard(onCreateReport: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SafetySurfaceAlt),
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "120",
                style = TextStyle(
                    color = SafetyGreenPrimary,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            )
            Text(
                text = "Riesgos reportados en la semana",
                style = TextStyle(
                    color = SafetyTextSecondary,
                    fontSize = 16.sp
                )
            )
            Surface(
                color = SafetyNeutralLight,
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "Última actualización - hace 8 minutos",
                    style = TextStyle(
                        color = SafetyTextSecondary,
                        fontSize = 13.sp
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
            ReusableButton(
                label = "Crear reporte",
                onClick = onCreateReport
            )
        }
    }
}

private data class QuickAction(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val onClick: () -> Unit
)

@Composable
private fun QuickActionsRow(actions: List<QuickAction>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        actions.forEach { action ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SafetySurface),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { action.onClick() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(SafetyNeutral),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = action.icon,
                            contentDescription = action.title,
                            tint = SafetyGreenHighlight
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = action.title,
                            style = TextStyle(
                                color = SafetyTextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(
                            text = action.description,
                            style = TextStyle(
                                color = SafetyTextSecondary,
                                fontSize = 14.sp
                            )
                        )
                    }
                    Icon(
                        imageVector = Icons.Outlined.ChevronRight,
                        contentDescription = null,
                        tint = SafetyGreenPrimary
                    )
                }
            }
        }
    }
}

private data class DashboardUpdate(
    val title: String,
    val description: String,
    val level: String
)

@Composable
private fun DashboardUpdateCard(update: DashboardUpdate) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SafetySurface),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = update.title,
                style = TextStyle(
                    color = SafetyTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = update.description,
                style = TextStyle(
                    color = SafetyTextSecondary,
                    fontSize = 14.sp
                )
            )
            Surface(
                color = SafetyNeutralLight,
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    text = update.level,
                    style = TextStyle(
                        color = SafetyGreenPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}
