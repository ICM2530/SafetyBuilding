/*import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource // Para usar imágenes de recursos
import com.example.proyectoparte1.R // Asegúrate de que este sea tu paquete de recursos

@Composable
fun ProfileScreen(
    nombre: String,
    rol: String,
    cedula: String,
    idTrabajador: String,
    celular: String,
    correo: String,
    contactoEmergencia: String,
    onEditClick: () -> Unit,
    onPasswordClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.avatarface),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 24.dp)
        )

        Text(text = "Nombre: $nombre", modifier = Modifier.padding(vertical = 4.dp))
        Text(text = "Rol: $rol", modifier = Modifier.padding(vertical = 4.dp))
        Text(text = "Cédula: $cedula", modifier = Modifier.padding(vertical = 4.dp))
        Text(text = "ID Trabajador: $idTrabajador", modifier = Modifier.padding(vertical = 4.dp))
        Text(text = "Celular: $celular", modifier = Modifier.padding(vertical = 4.dp))
        Text(text = "Correo electrónico: $correo", modifier = Modifier.padding(vertical = 4.dp))
        Text(text = "Contacto de emergencia: $contactoEmergencia", modifier = Modifier.padding(vertical = 4.dp))

        Spacer(modifier = Modifier.height(32.dp))

        // Botones de acción
        Button(
            onClick = onEditClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Editar datos personales")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onPasswordClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Cambiar contraseña")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Cerrar sesión")
        }
    }
}
*/