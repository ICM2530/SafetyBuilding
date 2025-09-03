package Navigation

import Screens.LogInScreen
import Screens.RiskCodeScreen
import Screens.SignInScreen
import Screens.SplashScreen
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.main.Screens.AddRiskZone
import com.example.main.Screens.DrawerScreens.ChatScreen
import com.example.main.Screens.DrawerScreens.ProfileScreen
import com.example.main.Screens.DrawerScreens.RiskZones
import com.example.main.Screens.HomeScreen
import com.example.main.Screens.RecoverPassword.ChangePassWordScreen
import com.example.main.Screens.RecoverPassword.CodeVerificationScreen
import com.example.main.Screens.RecoverPassword.EmailVerificationScreen

@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    NavHost(navController= navController, startDestination = AppScreens.SplashScreen.name){
        composable(route = AppScreens.SplashScreen.name){
            SplashScreen(navController)
        }
        composable(route = AppScreens.LogInScreen.name){
            LogInScreen(navController)
        }
        composable(route = AppScreens.SignInScreen.name){
            SignInScreen(navController)
        }
        composable(route = AppScreens.RiskCodeScreen.name){
            RiskCodeScreen(navController)
        }
        composable(route = AppScreens.EmailVerificationScreen.name){
            EmailVerificationScreen(navController)
        }
        composable(route = AppScreens.HomeScreen.name){
            HomeScreen(navController)
        }
        composable(route = AppScreens.AddZoneRisk.name){
            AddRiskZone(navController)
        }
        composable(route = AppScreens.CodeVerificationScreen.name){
            CodeVerificationScreen(navController)
        }
        composable(route = AppScreens.ChangePassWordScreen.name){
            ChangePassWordScreen(navController)
        }
        composable(route = AppScreens.ProfileScreen.name){
            ProfileScreen(navController)
        }
     /*  composable(route = AppScreens.UpdateAccountScreen.name){
            EditProfileScreen(onSaveClick = { navController.popBackStack() } )
            }*/
        composable(route = AppScreens.ChatScreen.name){
            ChatScreen(navController)
        }
        composable(route = AppScreens.RiskZones.name){
            RiskZones(navController)
        }
    }
}

