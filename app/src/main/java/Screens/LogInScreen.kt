package Screens

import Navigation.AppScreens
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.main.CompReusable.ReusableButton
import com.example.main.utils.theme.Blue
import com.example.practica.R

@Composable
fun LogInScreen (controller: NavController){
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Scaffold (
        modifier = Modifier.padding()
    ){ innerPadding ->

        Column (

            modifier = Modifier
                .padding(horizontal = 22.dp)
                .padding(innerPadding)
                .clickable (
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }

                ){
                    focusManager.clearFocus()
                },

            horizontalAlignment = Alignment.CenterHorizontally

        ){

            Image(
                modifier = Modifier.padding(top = 63.dp ).width(244.dp).height(279.dp),
                painter = painterResource(id = R.drawable.log_life_srtyle),
                contentDescription = "Logo de safety life",
            )
            Text(
                "SAFETY LIFE",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                )
            )
            Spacer(Modifier.height(60.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().height(60.dp),
                label = { Text("Userrname") },
                value = username,
                onValueChange = {username = it}
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().height(60.dp),
                label = { Text("Password") },
                value = password,
                onValueChange = {password = it},


            )
            Text(
                "Olvide la contraseña",

                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 11.dp)
                    .clickable {
                        controller.navigate(AppScreens.EmailVerificationScreen.name)
                    },
                style =TextStyle(
                    color = Blue,
                    fontSize = 12.sp,
                )

            )
            Spacer(Modifier.height(20.dp))
            ReusableButton(
                label = "Entrar",
                onClick = {
                    controller.navigate(AppScreens.RiskCodeScreen.name)
                }
            )

            Spacer(Modifier.height(10.dp))
            Row {
                Text(
                    "¿No tiene cuenta? ",
                    style = TextStyle(
                        fontSize = 12.sp,
                    )
                )
                Text(
                    "Registrarme",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Blue
                    ),
                    //hola
                    modifier = Modifier
                        .clickable {
                            controller.navigate(AppScreens.SignInScreen.name)
                        }
                )
            }

        }

    }
}

