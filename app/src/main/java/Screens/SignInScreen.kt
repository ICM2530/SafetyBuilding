package Screens

import Navigation.AppScreens
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.main.CompReusable.ReusableButton
import com.example.main.CompReusable.ReusableTextField
import com.example.main.CompReusable.ReusableTopAppBar
import com.example.main.utils.theme.SafetyGreenPrimary
import com.example.main.utils.theme.SafetyNeutralLight
import com.example.main.utils.theme.SafetySurfaceAlt
import com.example.main.utils.theme.SafetyTextPrimary
import com.example.main.utils.theme.SafetyTextSecondary
import com.example.main.utils.theme.White

@Composable
fun SignInScreen(controller: NavController) {

    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var cedula by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var celular by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmacionContrasena by remember { mutableStateOf("") }
    val opciones = listOf("C.C.", "Pasaporte", "C.E.")
    var tipoDoc by remember { mutableStateOf(opciones.first()) }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = White,
        topBar = {
            ReusableTopAppBar(
                title = "Crear cuenta",
                leadingIcon = Icons.AutoMirrored.Outlined.ArrowBack,
                onLeadingClick = {
                    controller.popBackStack()
                },
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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Completa tus datos para que podamos identificarte en el sitio de trabajo.",
                style = TextStyle(
                    color = SafetyTextSecondary,
                    fontSize = 15.sp
                )
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = SafetySurfaceAlt),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SectionTitle("Datos personales")

                    ReusableTextField(
                        contenido = "Nombre(s)",
                        value = nombre,
                        onValueChange = { nombre = it }
                    )

                    ReusableTextField(
                        contenido = "Apellido(s)",
                        value = apellidos,
                        onValueChange = { apellidos = it }
                    )

                    Box {
                        ReusableTextField(
                            contenido = "Tipo de documento",
                            value = tipoDoc,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.ArrowDropDown,
                                    contentDescription = null,
                                    tint = SafetyGreenPrimary
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = !expanded }
                        )

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            opciones.forEach { opcion ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = opcion,
                                            style = TextStyle(color = SafetyTextPrimary)
                                        )
                                    },
                                    onClick = {
                                        tipoDoc = opcion
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    ReusableTextField(
                        contenido = "Número de documento",
                        value = cedula,
                        onValueChange = { cedula = it }
                    )

                    ReusableTextField(
                        contenido = "Correo electrónico",
                        value = email,
                        onValueChange = { email = it }
                    )

                    ReusableTextField(
                        contenido = "Número de contacto",
                        value = celular,
                        onValueChange = { celular = it }
                    )

                    SectionTitle("Acceso a la plataforma")

                    ReusableTextField(
                        contenido = "Usuario",
                        value = usuario,
                        onValueChange = { usuario = it }
                    )

                    ReusableTextField(
                        contenido = "Contraseña",
                        value = contrasena,
                        onValueChange = { contrasena = it }
                    )

                    ReusableTextField(
                        contenido = "Confirmar contraseña",
                        value = confirmacionContrasena,
                        onValueChange = { confirmacionContrasena = it }
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = SafetySurfaceAlt),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SectionTitle("Foto de perfil")

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(SafetyNeutralLight)
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = null,
                                tint = SafetyGreenPrimary,
                                modifier = Modifier.size(56.dp)
                            )
                            Text(
                                text = "Agrega una foto para que te identifiquen en campo.",
                                style = TextStyle(
                                    color = SafetyTextSecondary,
                                    fontSize = 14.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                            IconButton(onClick = { /* TODO: implementar carga */ }) {
                                Icon(
                                    imageVector = Icons.Outlined.CameraAlt,
                                    contentDescription = "Agregar foto",
                                    tint = SafetyGreenPrimary
                                )
                            }
                        }
                    }
                }
            }

            ReusableButton(
                label = "Finalizar registro",
                onClick = {
                    controller.navigate(AppScreens.RiskCodeScreen.name)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = TextStyle(
            color = SafetyTextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    )
}
