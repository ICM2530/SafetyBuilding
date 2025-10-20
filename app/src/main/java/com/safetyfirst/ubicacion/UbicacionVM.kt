package com.safetyfirst.ubicacion

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.*
import com.safetyfirst.datos.FirebaseRepositorio
import com.safetyfirst.modelo.Posicion
import kotlinx.coroutines.launch

class UbicacionVM(app: Application, private val repo: FirebaseRepositorio = FirebaseRepositorio())
    : AndroidViewModel(app) {

    private val cliente = LocationServices.getFusedLocationProviderClient(app)

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
}
