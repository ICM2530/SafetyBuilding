package com.safetyfirst.datos

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.safetyfirst.modelo.*
import com.google.firebase.database.DatabaseError
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

// ────────────────────────
// Acceso a Firebase Auth + RTDB
// ────────────────────────

sealed class FirebaseResultado<out T> {
    object Cargando : FirebaseResultado<Nothing>()
    data class Exito<T>(val datos: T) : FirebaseResultado<T>()
    data class Error(val mensaje: String, val causa: Throwable? = null) : FirebaseResultado<Nothing>()
}

class FirebaseRepositorio {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    private val R_USUARIOS = "usuarios"
    private val R_POSICIONES = "posiciones"   // posiciones/{uid}/{pushId}
    private val R_ZONAS = "zonas"
    private val R_MENSAJES = "mensajes"       // mensajes/{conversacionId}/{pushId}

    // Registro de usuario propio (no de terceros)
    suspend fun registrarUsuario(correo: String, clave: String, nombre: String, rol: Rol): Usuario {
        val cred = auth.createUserWithEmailAndPassword(correo, clave).await()
        val uid = cred.user!!.uid
        val token = FirebaseMessaging.getInstance().token.await()
        val usuario = Usuario(uid, nombre, correo, rol, token)
        db.child(R_USUARIOS).child(uid).setValue(usuario).await()
        return usuario
    }

    // Login y retorno de perfil (si no tiene perfil en RTDB, devolvemos mínimo UID/correo)
    suspend fun iniciarSesionConEstado(correo: String, clave: String): Pair<Usuario, Boolean> {
        val res = auth.signInWithEmailAndPassword(correo, clave).await()
        val uid = res.user!!.uid
        val snap = db.child(R_USUARIOS).child(uid).get().await()
        return if (snap.exists()) {
            snap.getValue(Usuario::class.java)!! to true
        } else {
            Usuario(uid = uid, correo = correo) to false
        }
    }

    suspend fun iniciarSesion(correo: String, clave: String): Usuario {
        val (u, _) = iniciarSesionConEstado(correo, clave)
        return u
    }

    fun usuarioActual(): Usuario? = auth.currentUser?.let { Usuario(uid = it.uid, correo = it.email ?: "") }

    suspend fun obtenerUsuario(uid: String): Usuario? =
        db.child(R_USUARIOS).child(uid).get().await().getValue(Usuario::class.java)

    suspend fun guardarUsuario(u: Usuario) {
        db.child(R_USUARIOS).child(u.uid).setValue(u).await()
    }

    suspend fun actualizarTokenFcm(uid: String, token: String) {
        db.child(R_USUARIOS).child(uid).child("tokenFcm").setValue(token).await()
    }

    suspend fun cerrarSesion() { auth.signOut() }

    // —— Usuarios
    fun flujoUsuarios(): Flow<List<Usuario>> = callbackFlow {
        val ref = db.child(R_USUARIOS)
        val l = object: com.google.firebase.database.ValueEventListener {
            override fun onDataChange(s: com.google.firebase.database.DataSnapshot) {
                trySend(s.children.mapNotNull { it.getValue(Usuario::class.java) })
            }
            override fun onCancelled(e: com.google.firebase.database.DatabaseError) { close(e.toException()) }
        }
        ref.addValueEventListener(l); awaitClose { ref.removeEventListener(l) }
    }

    fun flujoUsuariosPorRol(rol: Rol): Flow<FirebaseResultado<List<Usuario>>> = callbackFlow {
        val ref = db.child(R_USUARIOS)
        val l = object: com.google.firebase.database.ValueEventListener {
            override fun onDataChange(s: com.google.firebase.database.DataSnapshot) {
                val lista = s.children.mapNotNull { it.getValue(Usuario::class.java) }.filter { it.rol == rol }
                trySend(FirebaseResultado.Exito(lista))
            }
            override fun onCancelled(e: com.google.firebase.database.DatabaseError) {
                if (e.code == DatabaseError.PERMISSION_DENIED) {
                    trySend(FirebaseResultado.Error("No tienes permisos para ver los usuarios.", e.toException()))
                    close() // cerramos sin propagar excepción para no crashear la UI
                } else {
                    close(e.toException())
                }
            }
        }
        ref.addValueEventListener(l); awaitClose { ref.removeEventListener(l) }
    }

    // —— Zonas de riesgo
    fun flujoZonas(): Flow<List<ZonaRiesgo>> = callbackFlow {
        val ref = db.child(R_ZONAS)
        val l = object: com.google.firebase.database.ValueEventListener {
            override fun onDataChange(s: com.google.firebase.database.DataSnapshot) {
                trySend(s.children.mapNotNull { it.getValue(ZonaRiesgo::class.java) })
            }
            override fun onCancelled(e: com.google.firebase.database.DatabaseError) { close(e.toException()) }
        }
        ref.addValueEventListener(l); awaitClose { ref.removeEventListener(l) }
    }

    suspend fun agregarZona(z: ZonaRiesgo) {
        db.child(R_ZONAS).child(z.id).setValue(z).await()
    }

    // —— Posiciones (última por usuario para mapa)
    fun flujoUltimaPosicionTodos(): Flow<FirebaseResultado<Map<String, Posicion>>> = callbackFlow {
        val ref = db.child(R_POSICIONES)
        val l = object: com.google.firebase.database.ValueEventListener {
            override fun onDataChange(s: com.google.firebase.database.DataSnapshot) {
                val mapa = mutableMapOf<String, Posicion>()
                s.children.forEach { uidNode ->
                    val ultima = uidNode.children.maxByOrNull { it.child("tiempo").getValue(Long::class.java) ?: 0L }
                    ultima?.getValue(Posicion::class.java)?.let { mapa[uidNode.key!!] = it }
                }
                trySend(FirebaseResultado.Exito(mapa))
            }
            override fun onCancelled(e: com.google.firebase.database.DatabaseError) {
                if (e.code == DatabaseError.PERMISSION_DENIED) {
                    trySend(FirebaseResultado.Error("No tienes permisos para ver las posiciones.", e.toException()))
                    close()
                } else {
                    close(e.toException())
                }
            }
        }
        ref.addValueEventListener(l); awaitClose { ref.removeEventListener(l) }
    }

    suspend fun publicarPosicion(pos: Posicion) {
        db.child(R_POSICIONES).child(pos.uid).push().setValue(pos).await()
    }

    // —— Chat
    fun construirConversacionId(a: String, b: String): String = listOf(a, b).sorted().joinToString("_")

    fun flujoMensajes(conversacionId: String): Flow<List<Mensaje>> = callbackFlow {
        val ref = db.child(R_MENSAJES).child(conversacionId)
        val l = object: com.google.firebase.database.ValueEventListener {
            override fun onDataChange(s: com.google.firebase.database.DataSnapshot) {
                val lista = s.children
                    .filter { it.key != "participantes" }
                    .mapNotNull { it.getValue(Mensaje::class.java) }
                    .sortedBy { it.tiempo }
                trySend(lista)
            }
            override fun onCancelled(e: com.google.firebase.database.DatabaseError) { close(e.toException()) }
        }
        ref.addValueEventListener(l); awaitClose { ref.removeEventListener(l) }
    }

    suspend fun enviarMensaje(m: Mensaje) {
        val conv = db.child(R_MENSAJES).child(m.conversacionId)
        conv.child("participantes").child(m.emisorUid).setValue(true).await()
        conv.child("participantes").child(m.receptorUid).setValue(true).await()
        conv.push().setValue(m).await()
    }
}
