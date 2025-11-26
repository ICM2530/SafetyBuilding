package com.safetyfirst.ubicacion

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.safetyfirst.datos.FirebaseRepositorio
import com.safetyfirst.modelo.Posicion
import kotlinx.coroutines.launch

class UbicacionVM(
    app: Application,
    private val repo: FirebaseRepositorio
) : AndroidViewModel(app) {

    // 🔹 Este constructor es el que necesita la AndroidViewModelFactory
    constructor(app: Application) : this(app, FirebaseRepositorio())

    private val cliente = LocationServices.getFusedLocationProviderClient(app)

    private var callback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    fun empezarRastreo(uid: String) {
        val req = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10_000L          // intervalo máximo entre updates
        ).setMinUpdateIntervalMillis(5_000L) // intervalo mínimo
            .build()

        val nuevoCallback = object : LocationCallback() {
            override fun onLocationResult(res: LocationResult) {
                res.lastLocation?.let { guardar(uid, it) }
            }
        }

        callback = nuevoCallback
        cliente.requestLocationUpdates(
            req,
            nuevoCallback,
            getApplication<Application>().mainLooper
        )
    }

    fun detenerRastreo() {
        callback?.let { cliente.removeLocationUpdates(it) }
        callback = null
    }

    private fun guardar(uid: String, loc: Location) = viewModelScope.launch {
        repo.publicarPosicion(
            Posicion(
                uid = uid,
                lat = loc.latitude,
                lon = loc.longitude,
                tiempo = System.currentTimeMillis()
            )
        )
    }
}
