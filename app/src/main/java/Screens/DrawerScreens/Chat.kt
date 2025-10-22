package com.example.main.Screens.DrawerScreens

import Navigation.BottomDestination
import Navigation.buildBottomItems
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.main.CompReusable.ReusableTextField
import com.example.main.CompReusable.ReusableTopAppBar
import com.example.main.CompReusable.SafetyBottomBar
import com.example.main.utils.theme.SafetyGreenPrimary
import com.example.main.utils.theme.SafetySurface
import com.example.main.utils.theme.SafetySurfaceAlt
import com.example.main.utils.theme.SafetyTextPrimary
import com.example.main.utils.theme.SafetyTextSecondary
import com.example.main.utils.theme.White

private data class Message(
    val author: String,
    val text: String,
    val isUser: Boolean,
    val time: String
)

@Composable
fun ChatScreen(navController: NavController) {
    val bottomBarItems = buildBottomItems(BottomDestination.Team, navController)
    val messages = listOf(
        Message("Coordinación", "Recibimos la alerta eléctrica. El equipo de mantenimiento está en camino.", false, "08:45"),
        Message("Tú", "Perfecto, ya acordonamos la zona y detenemos el acceso.", true, "08:47"),
        Message("Coordinación", "Recuerden registrar fotografías de la intervención para el informe.", false, "08:48"),
        Message("Tú", "Las cargamos en cuanto finalice la inspección.", true, "08:50")
    )
    var newMessage by remember { mutableStateOf("") }

    Scaffold(
        containerColor = White,
        topBar = {
            ReusableTopAppBar(
                title = "Mensajes",
                trailingIcon = Icons.Outlined.ChatBubbleOutline,
                trailingContentDescription = "Nuevo chat",
                onTrailingClick = { /* TODO iniciar chat */ },
                showDivider = true
            )
        },
        bottomBar = { SafetyBottomBar(items = bottomBarItems) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(White)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SafetySurfaceAlt),
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messages) { message ->
                        MessageBubble(message = message)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ReusableTextField(
                    contenido = "Escribe un mensaje",
                    value = newMessage,
                    onValueChange = { newMessage = it },
                    modifier = Modifier.weight(1f)
                )
                Card(
                    colors = CardDefaults.cardColors(containerColor = SafetyGreenPrimary),
                    shape = RoundedCornerShape(20.dp),
                ) {
                    IconButton(
                        onClick = {
                            // TODO enviar mensaje
                            newMessage = ""
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Send,
                            contentDescription = "Enviar mensaje",
                            tint = White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(message: Message) {
    Column(
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            colors = if (message.isUser) {
                CardDefaults.cardColors(containerColor = SafetyGreenPrimary)
            } else {
                CardDefaults.cardColors(containerColor = SafetySurface)
            },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .align(if (message.isUser) Alignment.End else Alignment.Start)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (!message.isUser) {
                    Text(
                        text = message.author,
                        style = TextStyle(
                            color = SafetyTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
                Text(
                    text = message.text,
                    style = TextStyle(
                        color = if (message.isUser) White else SafetyTextSecondary,
                        fontSize = 14.sp
                    )
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = message.time,
            style = TextStyle(
                color = SafetyTextSecondary,
                fontSize = 12.sp
            )
        )
    }
}
