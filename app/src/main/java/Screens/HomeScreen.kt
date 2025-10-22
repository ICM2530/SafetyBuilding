package com.example.main.Screens

import Navigation.AppScreens
import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.example.main.CompReusable.ReusableTopAppBar
import com.example.main.utils.theme.Blue
import com.example.main.utils.theme.Red
import com.example.main.utils.theme.White
import com.safetyfirst.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen (navController: NavController){

    var showNoiseAlert by remember { mutableStateOf(false) }

    var hablar by remember { mutableStateOf(false) }
    var drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate(AppScreens.ProfileScreen.name)
                    }
                ) {
                    Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start,modifier = Modifier.fillMaxWidth(),){
                        Icon(
                            imageVector = Icons.Default.Person ,
                            contentDescription = "boton de perfil",
                            tint = Color.Black
                        )
                        Text("Perfil", modifier = Modifier.padding(horizontal =  5.dp), color = Color.Black)
                    }
                }
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate(AppScreens.ChatScreen.name)
                    }
                ) {
                    Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth(),){
                        Icon(
                            imageVector = Icons.Default.Email ,
                            contentDescription = "boton de chat",
                            tint = Color.Black
                        )
                        Text("Mensajes", modifier = Modifier.padding(horizontal =  5.dp),color = Color.Black)
                    }
                }
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate(AppScreens.RiskZones.name)
                    }
                ) {
                    Row (verticalAlignment = Alignment.CenterVertically,  horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth(),){
                        Icon(
                            imageVector = Icons.Default.Warning ,
                            contentDescription = "boton de zonas de riesgo",
                            tint = Color.Black
                        )
                        Text("Zonas de riesgo", modifier = Modifier.padding(horizontal =  5.dp),color = Color.Black)
                    }
                }
                TextButton(

                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate(AppScreens.LogInScreen.name)
                    }
                ) {
                    Row (verticalAlignment = Alignment.CenterVertically,  horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth(),){
                        Icon(
                            imageVector = Icons.Default.ExitToApp ,
                            contentDescription = "cerrar secion",
                            tint = Color.Red
                        )
                        Text("Cerrar sesion", modifier = Modifier.padding(horizontal =  5.dp),
                            color = Red)
                    }
                }
            }

        }
    )
    {
        Scaffold(
            topBar = {
                ReusableTopAppBar(
                    title = "Safety First",
                    icon = Default.Menu,
                    modifier = Modifier.background(color = Blue).fillMaxWidth(),
                    onClick = {

                            scope.launch {
                                if (drawerState.isClosed) {
                                    drawerState.open()
                                } else {
                                    drawerState.close()
                                }
                            }

                    },
                    contentDescription = "Menu",
                    textAlign = TextAlign.Center,
                    textStyle = TextStyle(
                        fontSize = 36.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    containerColor = Red,
                    onClick = { navController.navigate(AppScreens.AddZoneRisk.name) }
                ) {
                    Icon(
                        imageVector = Default.Add,
                        tint = White,
                        contentDescription = "Agregar zona de riesgo"
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding).fillMaxWidth().fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Image(
                    painter = if (!hablar) {
                        painterResource(R.drawable.boton_rojo_2)
                    } else {
                        painterResource(R.drawable.boton_verde)
                    },
                    contentDescription = "Boton para hablar",
                    modifier = Modifier.clickable {
                        hablar = !hablar
                    }
                        .width(315.dp).height(315.dp)

                )
                TextButton(onClick = { showNoiseAlert = true }) {
                    Text("Botón para alerta de ruido")
                }
            }

            // Mostramos la alerta como modal:
            if (showNoiseAlert) {
                Dialog(onDismissRequest = { showNoiseAlert = false }) {
                    HighNoiseAlert(
                        onNotify = {
                            showNoiseAlert = false
                        },
                        onClose = { showNoiseAlert = false }
                    )
                }
            }

            }

        }
    }




@Composable
fun HighNoiseAlert(
    onNotify: () -> Unit,
    onClose: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD8E9F7)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¡ALERTA!",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Ruido muy alto",
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ruido),
                    contentDescription = "Icono de ruido alto",
                    modifier = Modifier.size(150.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "¿Desea enviar advertencia de esta\nzona al supervisor?",
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(18.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onNotify,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                ) { Text("Notificar") }

                Button(
                    onClick = onClose,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                ) { Text("Cerrar") }
            }
        }
    }
}