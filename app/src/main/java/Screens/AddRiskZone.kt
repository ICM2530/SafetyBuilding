package com.example.main.Screens

import Navigation.AppScreens
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.main.CompReusable.ReusableButton
import com.example.main.CompReusable.ReusableTextField
import com.example.main.CompReusable.ReusableTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRiskZone(navController: NavController){

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") } // seguramente esto cambie

    var tipoRiesgoList = listOf(
        "Caidas de altura",
        "Golpes por objetos (movimiento/caida)",
        "Atrapamiento (maquinaria/excavacion/estructuras)",
        "Sobreesfuerzo (cargar peso)",
        "Ruido excesivo",
        "Vibraciones",
        "Radicación solar (calor extremo)",
        "Inhalación de polvo (cemento)",
        "Exposición a solventes, pinturas o adhesivos",
        "Contacto con sustancias corrosivas",
        "Picaduras de insectos o mordeduras de animales en campo",
        "Moho o bacterias en ambientes cerrados y húmedos",
        "Contacto con líneas eléctricas aéreas o subterráneas",
        "Caída de materiales mal almacenados",
        "Mala iluminación en zonas de trabajo",
        "Lluvias en la obra"
    )

    Scaffold (
        topBar = {
            ReusableTopAppBar(
                title = "Agregar nueva zona de riesgo",
                icon = Icons.Default.ArrowBack,
                onClick = {
                    navController.navigate(AppScreens.HomeScreen.name)
                },
                contentDescription = "Volver a home"
            )
        }
    ) {innerPadding->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 40.dp, vertical = 20.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),


        ) {
            Text("Nombre:")
            ReusableTextField(
                value = nombre,
                onValueChange = {nombre = it},
                contenido = ""
            )
            Text("Descripción del riesgo:",
                modifier = Modifier.padding(top = 18.dp))
            ReusableTextField(
                value = descripcion,
                onValueChange = {descripcion = it},
                contenido = ""
            )
            Text("Tipo de riesgo:",
                modifier = Modifier.padding(top = 18.dp, bottom = 3.dp),
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                )
            )
            LazyColumn (
                modifier = Modifier.background(color = Color(0xffCCEAFF))
                    .height(350.dp)
            ){

                items(tipoRiesgoList){ tipoRiesgo->
                    var checked by remember { mutableStateOf(false) }
                    Row (
                        Modifier.fillMaxWidth()
                            .border(BorderStroke(width = 1.dp, color = Color.Black)),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,

                    ) {
                        Text(tipoRiesgo,
                            modifier = Modifier.padding(3.dp)
                                .width(265.dp)
                        )
                        Checkbox(
                            checked = checked,
                            onCheckedChange = {checked = it},
                            modifier =  Modifier.padding(3.dp)
                        )
                    }

                }

            }
            Text("Ubicación:",
                modifier = Modifier.padding(top = 18.dp))
            ReusableTextField(
                value = ubicacion,
                onValueChange = {ubicacion = it},
                contenido = ""
            )
            Spacer(Modifier.height(15.dp))

            Box(
                modifier =  Modifier.border(
                    width = 1.dp,
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black,
                )
                    .align(Alignment.CenterHorizontally)
            ){
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                        Text("Foto de perfil", Modifier.padding(3.dp))
                        IconButton(
                            onClick = {

                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ExitToApp,
                                contentDescription = "Tomarse una foto de perfil"
                            )

                        }

                }
            }
            Spacer(Modifier.height(35.dp))
            ReusableButton(
                onClick = {
                    navController.navigate(AppScreens.HomeScreen.name)
                },
                label = "Registrar",
                modifier = Modifier.align(Alignment.CenterHorizontally)

            )

        }

    }
}