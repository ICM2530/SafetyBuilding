import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UpdateAccount(onSaveClick: (
    String, String, String, String, String, String, String
) -> Unit) {
    var nombre by remember { mutableStateOf("Nombre completo") }
    var rol by remember { mutableStateOf("Rol") }
    var cedula by remember { mutableStateOf("Cédula") }
    var idTrabajador by remember { mutableStateOf("ID Trabajador") }
    var celular by remember { mutableStateOf("Celular") }
    var correo by remember { mutableStateOf("Correo electrónico") }
    var contactoEmergencia by remember { mutableStateOf("Contacto de emergencia") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Campos de texto editables
        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre completo") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = rol,
            onValueChange = { rol = it },
            label = { Text("Rol") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = cedula,
            onValueChange = { cedula = it },
            label = { Text("Cédula") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = idTrabajador,
            onValueChange = { idTrabajador = it },
            label = { Text("ID Trabajador") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = celular,
            onValueChange = { celular = it },
            label = { Text("Celular") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = contactoEmergencia,
            onValueChange = { contactoEmergencia = it },
            label = { Text("Contacto de emergencia") }
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onSaveClick(nombre, rol, cedula, idTrabajador, celular, correo, contactoEmergencia)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Guardar datos personales")
        }
    }
}