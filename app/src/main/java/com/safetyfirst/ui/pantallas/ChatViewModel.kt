package com.safetyfirst.ui.pantallas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safetyfirst.datos.FirebaseRepositorio
import com.safetyfirst.modelo.Mensaje
import com.safetyfirst.modelo.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatUiState(
    val usuarios: List<Usuario> = emptyList(),
    val mensajes: List<Mensaje> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null
)

class ChatViewModel : ViewModel() {
    private val repo = FirebaseRepositorio()
    
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    
    private var conversacionActual: String? = null
    
    init {
        cargarUsuarios()
    }
    
    private fun cargarUsuarios() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(cargando = true)
            try {
                repo.flujoUsuarios().collect { usuarios ->
                    val usuarioActual = repo.usuarioActual()
                    // Filtrar el usuario actual de la lista
                    val usuariosFiltrados = usuarios.filter { it.uid != usuarioActual?.uid }
                    _uiState.value = _uiState.value.copy(
                        usuarios = usuariosFiltrados,
                        cargando = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    cargando = false,
                    error = "Error al cargar usuarios: ${e.message}"
                )
            }
        }
    }
    
    fun iniciarConversacion(receptorUid: String) {
        val emisorUid = repo.usuarioActual()?.uid ?: return
        conversacionActual = repo.construirConversacionId(emisorUid, receptorUid)
        
        viewModelScope.launch {
            try {
                repo.flujoMensajes(conversacionActual!!).collect { mensajes ->
                    _uiState.value = _uiState.value.copy(
                        mensajes = mensajes,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al cargar mensajes: ${e.message}"
                )
            }
        }
    }
    
    fun enviarMensaje(texto: String, receptorUid: String) {
        if (texto.isBlank()) return
        
        val emisorUid = repo.usuarioActual()?.uid ?: return
        val conversacionId = conversacionActual ?: repo.construirConversacionId(emisorUid, receptorUid)
        
        viewModelScope.launch {
            try {
                val mensaje = Mensaje(
                    conversacionId = conversacionId,
                    emisorUid = emisorUid,
                    receptorUid = receptorUid,
                    texto = texto,
                    tiempo = System.currentTimeMillis()
                )
                repo.enviarMensaje(mensaje)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al enviar mensaje: ${e.message}"
                )
            }
        }
    }
    
    fun limpiarError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
