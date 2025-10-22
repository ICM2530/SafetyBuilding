package Navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.example.main.CompReusable.SafetyBottomBarItem

enum class BottomDestination(
    val label: String,
    val icon: ImageVector,
    val screen: AppScreens
) {
    Home("Inicio", Icons.Outlined.Home, AppScreens.HomeScreen),
    Reports("Reportes", Icons.AutoMirrored.Outlined.Article, AppScreens.RiskCodeScreen),
    Map("Mapa", Icons.Outlined.Map, AppScreens.RiskZones),
    Team("Equipo", Icons.Outlined.Group, AppScreens.ChatScreen),
    Report("Reportar", Icons.Outlined.Add, AppScreens.AddZoneRisk)
}

fun buildBottomItems(
    current: BottomDestination,
    navController: NavController
): List<SafetyBottomBarItem> {
    return BottomDestination.entries.map { destination ->
        SafetyBottomBarItem(
            icon = destination.icon,
            label = destination.label,
            isSelected = destination == current,
            onClick = {
                if (destination == current) return@SafetyBottomBarItem
                navController.navigate(
                    destination.screen.name,
                    navOptions = navOptions {
                        launchSingleTop = true
                    }
                )
            }
        )
    }
}
