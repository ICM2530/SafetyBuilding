package com.example.main.Screens.RecoverPassword

import Navigation.AppScreens
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.main.CompReusable.ReusableButton
import com.example.main.CompReusable.ReusableTextField
import com.example.main.CompReusable.ReusableTopAppBar
import com.example.main.utils.theme.Blue
import com.example.main.utils.theme.Red

@Composable
fun ChangePassWordScreen (navController: NavController){
    var password by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }


    Scaffold (
        topBar = { ReusableTopAppBar(
            modifier = Modifier.background(
                brush = Brush.horizontalGradient(
                    colorStops = arrayOf(
                        0.1f to Red,
                        0.3f to Blue
                    )
                )
            ).fillMaxWidth(),
            icon = Icons.Default.ArrowBack,
            contentDescription = "Volver a inicio de sesión",
            onClick = {navController.navigate(AppScreens.LogInScreen.name)},
            title = "Recuperar contraseña"
        ) }
    ){innerPadding->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ReusableTextField(
                onValueChange = {password = it},
                value = password,
                contenido = "Nueva contraseña"
            )
            ReusableTextField(
                onValueChange = {newPassword = it},
                value = newPassword,
                contenido = "Confirmación contraseña"
            )
            Spacer(Modifier.height(20.dp))
            ReusableButton(
                label = "Cambiar",
                onClick = {
                    navController.navigate(AppScreens.LogInScreen.name)
                }
            )



        }
    }
}