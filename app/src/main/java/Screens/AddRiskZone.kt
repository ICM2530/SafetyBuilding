package com.example.main.Screens

import Navigation.AppScreens
import Navigation.BottomDestination
import Navigation.buildBottomItems
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.main.CompReusable.ReusableButton
import com.example.main.CompReusable.ReusableTextField
import com.example.main.CompReusable.ReusableTopAppBar
import com.example.main.CompReusable.SafetyBottomBar
import com.example.main.utils.theme.SafetyGreenPrimary
import com.example.main.utils.theme.SafetyNeutralLight
import com.example.main.utils.theme.SafetySurface
import com.example.main.utils.theme.SafetySurfaceAlt
import com.example.main.utils.theme.SafetyTextPrimary
import com.example.main.utils.theme.SafetyTextSecondary
import com.example.main.utils.theme.White
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Surface

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddRiskZone(navController: NavController) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var latitud by remember { mutableStateOf("") }
    var longitud by remember { mutableStateOf("") }
    var altitud by remember { mutableStateOf("") }

    val riskOptions = listOf(
        "Caída de altura",
        "Atrapamiento",
        "Eléctrico",
        "Carga suspendida",
        "Colapso",
        "Incendio",
        "Ruido",
        "Vibraciones",
        "Sustancias químicas",
        "Sobreesfuerzo"
    )
    val criticalRisks = setOf("Eléctrico", "Incendio", "Colapso")
    var selectedRisks by remember { mutableStateOf(setOf<String>()) }

    val bottomBarItems = buildBottomItems(BottomDestination.Report, navController)

    Scaffold(
        containerColor = White,
        topBar = {
            ReusableTopAppBar(
                title = "Reportar riesgo",
                leadingIcon = Icons.AutoMirrored.Filled.ArrowBack,
                leadingContentDescription = "Volver a Home",
                onLeadingClick = { navController.navigate(AppScreens.HomeScreen.name) },
                trailingIcon = Icons.Outlined.Person,
                onTrailingClick = { navController.navigate(AppScreens.ProfileScreen.name) },
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Completa la información para notificar al equipo de seguridad.",
                style = TextStyle(
                    color = SafetyTextSecondary,
                    fontSize = 15.sp
                )
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = SafetySurfaceAlt),
                shape = RoundedCornerShape(26.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ReusableTextField(
                        contenido = "Título del riesgo",
                        value = titulo,
                        onValueChange = { titulo = it }
                    )

                    ReusableTextField(
                        contenido = "Descripción del riesgo",
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        singleLine = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    )

                    Text(
                        text = "Clasificación",
                        style = TextStyle(
                            color = SafetyTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        riskOptions.forEach { risk ->
                            val isSelected = risk in selectedRisks
                            val isCritical = risk in criticalRisks
                            RiskCategoryChip(
                                label = risk,
                                selected = isSelected,
                                highlight = isCritical,
                                onClick = {
                                    selectedRisks = selectedRisks.toMutableSet().also { set ->
                                        if (isSelected) set.remove(risk) else set.add(risk)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = SafetySurfaceAlt),
                shape = RoundedCornerShape(26.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Ubicación del riesgo",
                        style = TextStyle(
                            color = SafetyTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ReusableTextField(
                            contenido = "Latitud",
                            value = latitud,
                            onValueChange = { latitud = it },
                            modifier = Modifier.weight(1f)
                        )
                        ReusableTextField(
                            contenido = "Longitud",
                            value = longitud,
                            onValueChange = { longitud = it },
                            modifier = Modifier.weight(1f)
                        )
                        ReusableTextField(
                            contenido = "Altitud",
                            value = altitud,
                            onValueChange = { altitud = it },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(SafetyNeutralLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = null,
                                tint = SafetyGreenPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = "Vista previa del mapa",
                                style = TextStyle(
                                    color = SafetyTextSecondary,
                                    fontSize = 14.sp
                                )
                            )
                            Text(
                                text = "Conecta tu ubicación para visualizarlo en el mapa.",
                                style = TextStyle(
                                    color = SafetyTextSecondary,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            ReusableButton(
                label = "Reportar riesgo",
                onClick = {
                    navController.navigate(AppScreens.HomeScreen.name)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            )
        }
    }
}

@Composable
private fun RiskCategoryChip(
    label: String,
    selected: Boolean,
    highlight: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        selected -> SafetyGreenPrimary
        highlight -> Color(0xFFE35B4F)
        else -> SafetySurface
    }
    val contentColor = when {
        selected || highlight -> White
        else -> SafetyTextSecondary
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(24.dp)
    ) {
        Text(
            text = label,
            style = TextStyle(
                color = contentColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier
                .clickable { onClick() }
                .padding(horizontal = 18.dp, vertical = 10.dp)
        )
    }
}
