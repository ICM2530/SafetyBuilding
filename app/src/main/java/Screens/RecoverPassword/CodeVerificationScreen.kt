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
import androidx.compose.material.icons.outlined.Verified
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.main.CompReusable.ReusableButton
import com.example.main.CompReusable.ReusableTextField
import com.example.main.CompReusable.ReusableTopAppBar
import com.example.main.utils.theme.SafetyGreenPrimary
import com.example.main.utils.theme.SafetySurfaceAlt
import com.example.main.utils.theme.SafetyTextPrimary
import com.example.main.utils.theme.SafetyTextSecondary
import com.example.main.utils.theme.White

@Composable
fun CodeVerificationScreen(navController: NavController) {
    var code by remember { mutableStateOf("") }

    Scaffold(
        containerColor = White,
        topBar = {
            ReusableTopAppBar(
                title = "Verificar código",
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
                        imageVector = Icons.Outlined.Verified,
                        contentDescription = null,
                        tint = SafetyGreenPrimary
                    )
                    Text(
                        text = "Ingresa el código enviado a tu correo",
                        style = TextStyle(
                            color = SafetyTextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Revisa tu bandeja de entrada (y spam). El código tiene vigencia de 10 minutos.",
                        style = TextStyle(
                            color = SafetyTextSecondary,
                            fontSize = 14.sp
                        )
                    )
                    ReusableTextField(
                        onValueChange = { code = it },
                        value = code,
                        contenido = "Código de 6 dígitos",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    ReusableButton(
                        label = "Verificar",
                        onClick = {
                            navController.navigate(AppScreens.ChangePassWordScreen.name)
                        }
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
            Text(
                text = "¿No recibiste el código? Reenvía en 58 segundos.",
                style = TextStyle(
                    color = SafetyTextSecondary,
                    fontSize = 13.sp
                )
            )
        }
    }
}
