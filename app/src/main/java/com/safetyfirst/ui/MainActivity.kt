package com.safetyfirst.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.safetyfirst.permisos.PantPermisos
import com.safetyfirst.ui.pantallas.*
import com.safetyfirst.ui.tema.SafetyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SafetyTheme { AppRaiz(intent) } }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Sobrescribimos el intent para que Compose lo reciba
        setIntent(intent)
    }
}

@Composable
fun AppRaiz(intent: Intent?) {
    val nav = rememberNavController()

    LaunchedEffect(key1 = intent) {
        if (intent?.action == Intent.ACTION_VIEW && intent.data != null) {
            nav.handleDeepLink(intent)
        }
    }

    NavHost(navController = nav, startDestination = Rutas.Autenticacion.ruta) {
        // El `handleDeepLink` se encarga de navegar a la ruta correcta, p.ej. "chat/some_user_id"
        // por lo que no se necesita lógica adicional aquí.
        composable(Rutas.Autenticacion.ruta) { PantAutenticacion(nav) }
        composable(Rutas.Permisos.ruta) { PantPermisos(nav) }
        composable(Rutas.HomeObrero.ruta) { PantHomeObrero(nav) }
        composable(Rutas.HomeSupervisor.ruta) { PantHomeSupervisor(nav) }
        composable(Rutas.MapaSupervisor.ruta) { PantMapaSupervisor(nav) }

        // Riesgos
        composable(Rutas.Zonas.ruta) { PantRiesgoFormulario(nav) }
        composable(Rutas.RiesgosLista.ruta) { PantRiesgosListaSupervisor(nav) }
        composable(Rutas.RiesgoDetalle.ruta + "/{zonaId}") { back ->
            PantRiesgoDetalle(nav, back.arguments?.getString("zonaId").orEmpty())
        }

        // Operadores
        composable(Rutas.Operadores.ruta) { PantOperadoresLista(nav) }
        composable(Rutas.OperadorPerfil.ruta + "/{uid}") { back ->
            PantOperadorPerfil(nav, back.arguments?.getString("uid").orEmpty())
        }

        // Chat
        composable(Rutas.ChatUsuarios.ruta) { PantUsuariosChatLista(nav) }
        composable(Rutas.Chat.ruta + "/{uidOtro}") { back ->
            PantChat(nav, back.arguments?.getString("uidOtro").orEmpty())
        }

        // Perfil propio
        composable(Rutas.Perfil.ruta) { PantPerfil(nav) }
    }
}

object Rutas {
    data class Ruta(val ruta: String)
    val Autenticacion = Ruta("auth")
    val Permisos = Ruta("permisos")
    val HomeObrero = Ruta("home_obrero")
    val HomeSupervisor = Ruta("home_supervisor")
    val MapaSupervisor = Ruta("mapa_supervisor")

    val Zonas = Ruta("zonas_form")
    val RiesgosLista = Ruta("riesgos_lista")
    val RiesgoDetalle = Ruta("riesgo_detalle")

    val Operadores = Ruta("operadores")
    val OperadorPerfil = Ruta("operador_perfil")

    val ChatUsuarios = Ruta("chat_usuarios")
    val Chat = Ruta("chat")

    val Perfil = Ruta("perfil")
}
