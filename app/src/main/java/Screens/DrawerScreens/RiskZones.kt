package com.example.main.Screens.DrawerScreens

import Navigation.BottomDestination
import Navigation.buildBottomItems
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.main.CompReusable.ReusableTopAppBar
import com.example.main.CompReusable.SafetyBottomBar
import com.example.main.utils.theme.SafetyGreenPrimary
import com.example.main.utils.theme.SafetySurface
import com.example.main.utils.theme.SafetySurfaceAlt
import com.example.main.utils.theme.SafetyTextPrimary
import com.example.main.utils.theme.SafetyTextSecondary
import com.example.main.utils.theme.White
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

private data class RiskZone(
    val title: String,
    val description: String,
    val level: String,
    val location: LatLng
)

@Composable
fun RiskZones(navController: NavController) {
    val bottomBarItems = buildBottomItems(BottomDestination.Map, navController)
    val riskZones = listOf(
        RiskZone(
            title = "Planta de mezclado",
            description = "Riesgo eléctrico por cableado expuesto",
            level = "Alto",
            location = LatLng(4.711, -74.0721)
        ),
        RiskZone(
            title = "Zona de bodegas",
            description = "Almacenamiento de materiales pesados",
            level = "Medio",
            location = LatLng(4.712, -74.0715)
        ),
        RiskZone(
            title = "Acceso principal",
            description = "Ingreso de maquinaria pesada",
            level = "Alto",
            location = LatLng(4.7105, -74.073)
        )
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(riskZones.first().location, 15f)
    }

    Scaffold(
        containerColor = White,
        topBar = {
            ReusableTopAppBar(
                title = "Mapa de riesgos",
                trailingIcon = Icons.Outlined.Map,
                trailingContentDescription = "Vista general",
                onTrailingClick = { /* TODO filtros */ },
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
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SafetySurfaceAlt),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    riskZones.forEach { zone ->
                        Marker(
                            state = MarkerState(position = zone.location),
                            title = zone.title,
                            snippet = zone.description
                        )
                    }
                }
            }

            riskZones.forEach { zone ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = SafetySurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = zone.title,
                            style = TextStyle(
                                color = SafetyTextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(
                            text = zone.description,
                            style = TextStyle(
                                color = SafetyTextSecondary,
                                fontSize = 14.sp
                            )
                        )
                        Text(
                            text = "Nivel de riesgo: ${zone.level}",
                            style = TextStyle(
                                color = SafetyGreenPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
