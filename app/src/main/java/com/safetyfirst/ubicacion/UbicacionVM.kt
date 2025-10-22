package com.safetyfirst.ubicacion

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.*
import com.safetyfirst.datos.FirebaseRepositorio
import com.safetyfirst.modelo.Posicion
import com.safetyfirst.modelo.Mensaje
import com.safetyfirst.modelo.TipoAlerta
import com.safetyfirst.modelo.ZonaRiesgo
import com.safetyfirst.ui.pantallas.SensorLuminosidad
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class UbicacionVM(app: Application, private val repo: FirebaseRepositorio = FirebaseRepositorio())
    : AndroidViewModel(app) {

    private val cliente = LocationServices.getFusedLocationProviderClient(app)

    private val sensorLuminosidad = SensorLuminosidad(app.applicationContext)

    // Channel para emitir eventos de navegación de alerta (one-shot events)
    private val _eventosNavegacion = Channel<TipoAlerta>(Channel.BUFFERED)
    val eventosNavegacion: Flow<TipoAlerta> = _eventosNavegacion.receiveAsFlow()

    // Umbral: Menos de 10 Lux es luz muy baja para una obra
    private val UMBRAL_LUZ_BAJA = 10f

    // Estado para saber si una alerta de luz está activamente siendo detectada
    private var alertaLuzActiva = false

    init {
        // Iniciar la escucha del sensor (se detiene en onCleared)
        sensorLuminosidad.iniciarEscucha()

        // Observar el flujo de luminosidad y actualizar el estado
        sensorLuminosidad.lux
            .onEach { luxValue ->
                manejarAlertaLuminosidad(luxValue)
            }
            .launchIn(viewModelScope)
    }

    private fun manejarAlertaLuminosidad(luxValue: Float) {
        if (luxValue < UMBRAL_LUZ_BAJA) {
            // Activa la alerta de Poca Luz
            if (!alertaLuzActiva) {
                alertaLuzActiva = true
                _eventosNavegacion.trySend(TipoAlerta.PocaLuz)
            }
        } else if (alertaLuzActiva) {
            // Si la condición se resuelve mientras la alerta está activa, la desactivamos internamente.
            alertaLuzActiva = false
        }
    }

    fun limpiarAlertaMostrada() {
        //
    }

    /** * Envía un mensaje estructurado de alerta al supervisor via Firebase.
     * @param alerta: El tipo de alerta (ej. PocaLuz) que se va a enviar.
     * @param miUid: El UID del obrero que genera la alerta.
     * @param supervisorUid: El UID del supervisor al que se envía el mensaje.
     */
    fun enviarAdvertencia(alerta: TipoAlerta, miUid: String, supervisorUid: String) = viewModelScope.launch {
        if (alerta !is TipoAlerta.Ninguna) {
            // Lógica para determinar el ID de la conversación (orden alfabético de UIDs)
            val conversacionId = if (miUid < supervisorUid) "chat_${miUid}_${supervisorUid}" else "chat_${supervisorUid}_${miUid}"

            // Usamos el título de la alerta como texto del mensaje
            val nuevoMensaje = Mensaje(
                conversacionId = conversacionId,
                emisorUid = miUid,
                receptorUid = supervisorUid,
                texto = alerta.titulo,
                tiempo = System.currentTimeMillis()
            )

            // Asumiendo que repo.enviarMensaje está definido y envía el Mensaje a Firestore/RTDB
            repo.enviarMensaje(nuevoMensaje)

            // Si la alerta enviada era de luz, podemos resetear el estado interno para permitir otra alerta
            if (alerta is TipoAlerta.PocaLuz) {
                alertaLuzActiva = false
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun empezarRastreo(uid: String) {
        val req = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10_000L)
            .setMinUpdateIntervalMillis(5_000L).build()
        val callback = object: LocationCallback() {
            override fun onLocationResult(res: LocationResult) {
                res.lastLocation?.let { guardar(uid, it) }
            }
        }
        cliente.requestLocationUpdates(req, callback, getApplication<Application>().mainLooper)
    }

    private fun guardar(uid: String, loc: Location) = viewModelScope.launch {
        repo.publicarPosicion(Posicion(uid, loc.latitude, loc.longitude, System.currentTimeMillis()))
    }

    override fun onCleared() {
        super.onCleared()
        sensorLuminosidad.detenerEscucha()
    }

    // Ejemplo de Zona de Riesgo (para futura implementación de Geofencing)
    private val zonasDeRiesgoMock = listOf(
        ZonaRiesgo(
            id = "Z-ALT1",
            titulo = "Zona de Andamios Altos",
            descripcion = "Área con riesgo de caída de altura",
            lat = 4.654,
            lon = -74.058,
            radioMetros = 50.0,
            tipos = listOf("ALTURA", "CAIDA")
        )
    )
}