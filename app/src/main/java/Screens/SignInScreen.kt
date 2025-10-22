package Screens

import Navigation.AppScreens
import android.graphics.drawable.Icon
import android.net.Uri
import android.util.Log
import android.widget.Space
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.main.CompReusable.ReusableButton
import com.example.main.CompReusable.ReusableTextField
import com.example.main.CompReusable.ReusableTopAppBar
import com.example.main.utils.theme.Blue
import com.example.main.utils.theme.Red
import com.example.main.utils.theme.White
// import kotlinx.serialization.descriptors.StructureKind
import java.lang.invoke.TypeDescriptor

@Composable
fun SignInScreen( controller: NavController){

    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var cedula by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var celular by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var contraseña by remember { mutableStateOf("") }
    var confirmacionContraseña by remember { mutableStateOf("") }
    var tipoDoc by remember { mutableStateOf(false) }
    val opciones = listOf("C.c", "Pasaporte")
    var seleccionado by remember { mutableStateOf(opciones[0]) }
    var image by remember { mutableStateOf<Uri?>(null) }



    Scaffold (
        topBar = { ReusableTopAppBar(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colorStops = arrayOf(
                            0.1f to Red,
                            0.3f to Blue
                        )
                    )
                )
                .fillMaxWidth(),
            icon = Icons.Default.ArrowBack,
            contentDescription = "Volver a inicio de sesión",
            onClick = {controller.navigate(AppScreens.LogInScreen.name)},
            title = "Registro de nuevo usuario"
        ) }
    ){ innerPaddig ->
        Column (
            modifier = Modifier
                .padding(innerPaddig)
                .padding(horizontal = 22.dp, vertical = 20.dp)
                ,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            ReusableTextField(
                contenido = "Nombre",
                value = nombre,
                onValueChange = {nombre = it}

            )
            ReusableTextField(
                contenido = "Nombre",
                value = apellidos,
                onValueChange = {apellidos = it}

            )
            Spacer(Modifier.height(10.dp))
            Row (
                verticalAlignment = Alignment.CenterVertically,
            ){
                Box(
                    

                    modifier = Modifier
                        .clickable { tipoDoc = true }
                        .border(
                            width = 1.dp, // grosor
                            color = Color.Black, // color del borde
                            shape = RoundedCornerShape(6.dp) // forma opcional
                        )
                        .height(56.dp)

                ){

                    Text(seleccionado, modifier = Modifier.padding(20.dp))
                    DropdownMenu(
                        modifier = Modifier.clip(
                            RoundedCornerShape(6.dp)
                        ),

                        expanded = tipoDoc,
                        onDismissRequest = {tipoDoc = false}
                    ) {
                        opciones.forEach{
                                opciones-> DropdownMenuItem(
                            text = { Text(opciones) },
                            onClick = {
                                seleccionado = opciones
                                tipoDoc = false
                            }
                        )
                        }
                    }
                }

                Spacer(Modifier.width(10.dp))
                ReusableTextField(
                    contenido = "Cédula",
                    value = cedula,
                    onValueChange = {cedula = it}

                )

            }

            ReusableTextField(
                contenido = "Cooreo electronico",
                value = email,
                onValueChange = {email = it}

            )
            ReusableTextField(
                contenido = "Celular",
                value = celular,
                onValueChange = {celular = it}

            )
            ReusableTextField(
                contenido = "Usuario",
                value = usuario,
                onValueChange = {usuario = it}

            )
            ReusableTextField(
                contenido = "Contraseña",
                value = contraseña,
                onValueChange = {contraseña = it}

            )
        ReusableTextField(
            contenido = "Confirmar contraseña",
            value = confirmacionContraseña,
            onValueChange = {confirmacionContraseña = it}

        )
            Spacer(Modifier.height(20.dp))
            Box(
               modifier =  Modifier.border(
                    width = 1.dp,
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black,
                )
            ){
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    if(image!= null){
                        //imagen perfil-Escogida desde galeria
                    }
                    else {
                        Text("Foto de perfil", Modifier.padding(3.dp))
                        IconButton(
                            onClick = {

                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Tomarse una foto de perfil"
                            )

                        }
                    }
                }
            }
            Spacer(Modifier.height(40.dp))
            ReusableButton(
                label = "Registrarme",
                onClick = {
                    controller.navigate(AppScreens.RiskCodeScreen.name)
                }

            )
        }

    }

}





