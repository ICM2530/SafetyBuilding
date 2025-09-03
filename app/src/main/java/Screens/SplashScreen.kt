package Screens

import Navigation.AppScreens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.main.utils.theme.Blue
import com.example.main.utils.theme.Red
import com.example.main.utils.theme.White


@Composable
fun SplashScreen (controller: NavController) {
    Box(
        modifier = Modifier.
        fillMaxSize().
        background(
            brush = Brush.verticalGradient(
                colors = listOf(Red, Blue)
            )
        ),
        contentAlignment = Alignment.Center,
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "SAFETY FIRST",
                modifier = Modifier.fillMaxWidth(),
                style = TextStyle(
                    color = White,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                    ),
            )
            Spacer(
                modifier = Modifier.height(height = 40.dp)
            )
            Button(
                onClick = {
                    controller.navigate(AppScreens.LogInScreen.name)
                },
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Red,
                ),
            ) {
                Text("Empezar",
                    style = TextStyle(
                        fontSize = 16.sp,
                    )
                )
            }
        }
    }

}