package com.safetyfirst.datos

import com.safetyfirst.modelo.Mensaje
import com.safetyfirst.modelo.Rol
import com.safetyfirst.modelo.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Fuente de datos en memoria para probar Operadores y Chat sin RTDB.
 * No interfiere con autenticación Firebase (se usa el UID real para
 * filtrar listas, pero los datos de usuarios y mensajes son locales).
 */
object MockInMemory {
    // Lista fija de participantes demo
    private val demoUsuarios = listOf(
        Usuario(uid = "demo_supervisor", nombre = "Supervisor Demo", correo = "supervisor@safety.demo", rol = Rol.SUPERVISOR, telefono = "+57 310 000 0000"),
        Usuario(uid = "u_laura", nombre = "Laura Méndez", correo = "laura@safety.demo", rol = Rol.OBRERO, telefono = "+57 311 111 1111"),
        Usuario(uid = "u_andres", nombre = "Andrés Rojas", correo = "andres@safety.demo", rol = Rol.OBRERO, telefono = "+57 312 222 2222"),
        Usuario(uid = "u_paola", nombre = "Paola Nieto", correo = "paola@safety.demo", rol = Rol.OBRERO, telefono = "+57 313 333 3333"),
    )

    fun usuarios(): List<Usuario> = demoUsuarios
    fun usuariosPorRol(rol: Rol): List<Usuario> = demoUsuarios.filter { it.rol == rol }
    fun usuarioPorUid(uid: String): Usuario? = demoUsuarios.find { it.uid == uid }
    fun usuariosExcept(uid: String): List<Usuario> = demoUsuarios.filter { it.uid != uid }

    // Mensajes en memoria por conversación (convId = sorted(uidA, uidB).joinToString("_"))
    private val mensajesPorConv = mutableMapOf<String, MutableStateFlow<List<Mensaje>>>()

    private fun ensure(convId: String): MutableStateFlow<List<Mensaje>> =
        mensajesPorConv.getOrPut(convId) { MutableStateFlow(seed(convId)) }

    private fun seed(convId: String): List<Mensaje> {
        // Una semilla simple con un saludo inicial
        val (a, b) = convId.split("_").let { it.first() to it.last() }
        return listOf(
            Mensaje(
                conversacionId = convId,
                emisorUid = a,
                receptorUid = b,
                texto = "Hola, ¿cómo va el frente de trabajo?",
                tiempo = System.currentTimeMillis() - 5 * 60 * 1000
            ),
            Mensaje(
                conversacionId = convId,
                emisorUid = b,
                receptorUid = a,
                texto = "Todo en orden. Enviando reporte de ruido.",
                tiempo = System.currentTimeMillis() - 4 * 60 * 1000
            ),
        )
    }

    fun flujoMensajes(convId: String): Flow<List<Mensaje>> = ensure(convId)

    suspend fun enviarMensajeLocal(m: Mensaje) {
        val flujo = ensure(m.conversacionId)
        flujo.value = flujo.value + m
    }
}

