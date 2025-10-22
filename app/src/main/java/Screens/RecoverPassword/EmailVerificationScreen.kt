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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.MarkEmailUnread
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.main.CompReusable.ReusableButton
import com.example.main.CompReusable.ReusableTextField
import com.example.main.CompReusable.ReusableTopAppBar
import com.example.main.utils.theme.SafetySurfaceAlt
import com.example.main.utils.theme.SafetyTextPrimary
import com.example.main.utils.theme.SafetyTextSecondary
import com.example.main.utils.theme.White

@Composable
fun EmailVerificationScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }

    Scaffold(
        containerColor = White,
        topBar = {
            ReusableTopAppBar(
                title = "Recuperar contraseña",
                leadingIcon = Icons.AutoMirrored.Outlined.ArrowBack,
                onLeadingClick = { navController.popBackStack() },
                trailingIcon = null,
                showDivider = true
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(White)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SafetySurfaceAlt),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MarkEmailUnread,
                        contentDescription = null,
                        tint = SafetyTextPrimary
                    )
                    Text(
                        text = "Reestablece tu acceso",
                        style = TextStyle(
                            color = SafetyTextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "Ingresa el correo registrado y te enviaremos un código para que puedas recuperar tu contraseña.",
                        style = TextStyle(
                            color = SafetyTextSecondary,
                            fontSize = 14.sp
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    ReusableTextField(
                        onValueChange = { email = it },
                        value = email,
                        contenido = "Correo electrónico"
                    )
                    ReusableButton(
                        label = "Enviar código",
                        onClick = {
                            navController.navigate(AppScreens.CodeVerificationScreen.name)
                        }
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
            Text(
                text = "¿Recuerdas tu contraseña? Inicia sesión nuevamente.",
                style = TextStyle(
                    color = SafetyTextSecondary,
                    fontSize = 13.sp
                )
            )
        }
    }
}
