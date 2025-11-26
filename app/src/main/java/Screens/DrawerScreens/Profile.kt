package com.example.main.Screens.DrawerScreens

import Navigation.AppScreens
import Navigation.BottomDestination
import Navigation.buildBottomItems
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.main.CompReusable.ReusableButton
import com.example.main.CompReusable.ReusableTopAppBar
import com.example.main.CompReusable.SafetyBottomBar
import com.example.main.utils.theme.SafetyGreenPrimary
import com.example.main.utils.theme.SafetyNeutralLight
import com.example.main.utils.theme.SafetySurface
import com.example.main.utils.theme.SafetySurfaceAlt
import com.example.main.utils.theme.SafetyTextPrimary
import com.example.main.utils.theme.SafetyTextSecondary
import com.example.main.utils.theme.White

@Composable
fun ProfileScreen(navController: NavController) {
    val bottomBarItems = buildBottomItems(BottomDestination.Team, navController)

    val infoItems = listOf(
        "Nombre completo" to "Laura Méndez",
        "Rol" to "Supervisora de seguridad",
        "Identificación" to "CC 1023456789",
        "Empresa" to "Safety Building S.A.S.",
        "Teléfono" to "+57 310 456 7890",
        "Correo" to "laura.mendez@safetybuilding.com"
    )

    Scaffold(
        containerColor = White,
        topBar = {
            ReusableTopAppBar(
                title = "Perfil",
                leadingIcon = Icons.AutoMirrored.Filled.ArrowBack,
                leadingContentDescription = "Volver a Home",
                onLeadingClick = { navController.navigate(AppScreens.HomeScreen.name) },
                trailingIcon = Icons.Outlined.Edit,
                trailingContentDescription = "Editar información",
                onTrailingClick = { /* TODO: editar datos */ },
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SafetySurfaceAlt),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = SafetyGreenPrimary,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(SafetyNeutralLight)
                            .padding(12.dp)
                    )
                    Text(
                        text = "Laura Méndez",
                        style = TextStyle(
                            color = SafetyTextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "Supervisora de seguridad",
                        style = TextStyle(
                            color = SafetyTextSecondary,
                            fontSize = 14.sp
                        )
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = SafetySurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    infoItems.forEachIndexed { index, (label, value) ->
                        ProfileInfoRow(label = label, value = value)
                        if (index != infoItems.lastIndex) {
                            HorizontalDivider(color = SafetyNeutralLight, thickness = 1.dp)
                        }
                    }
                }
            }

            ReusableButton(
                label = "Actualizar contraseña",
                onClick = { navController.navigate(AppScreens.ChangePassWordScreen.name) },
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = SafetySurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                            contentDescription = null,
                            tint = SafetyGreenPrimary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Cerrar sesión",
                            style = TextStyle(
                                color = SafetyTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                    IconButton(onClick = {
                        navController.navigate(AppScreens.LogInScreen.name)
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = SafetyGreenPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = TextStyle(
                color = SafetyTextSecondary,
                fontSize = 12.sp
            )
        )
        Text(
            text = value,
            style = TextStyle(
                color = SafetyTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}
