package com.safetyfirst.ui.pantallas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.safetyfirst.datos.FirebaseResultado
import com.safetyfirst.datos.FirebaseRepositorio
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun PantMapaSupervisor(nav: NavController, repo: FirebaseRepositorio = FirebaseRepositorio()) {
    val estado by repo.flujoUltimaPosicionTodos().collectAsState(initial = FirebaseResultado.Cargando)

    when (val actual = estado) {
        FirebaseResultado.Cargando -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is FirebaseResultado.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(actual.mensaje)
            }
        }
        is FirebaseResultado.Exito -> {
            val posiciones = actual.datos
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        controller.setZoom(16.0)
                        controller.setCenter(GeoPoint(4.6486, -74.2479)) // Bogotá por defecto
                    }
                },
                update = { map ->
                    map.overlays.clear()
                    posiciones.values.forEach { p ->
                        val m = Marker(map)
                        m.position = GeoPoint(p.lat, p.lon)
                        m.title = p.uid
                        map.overlays.add(m)
                    }
                    map.invalidate()
                },
                modifier = Modifier
            )
        }
    }
}
