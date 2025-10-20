package com.safetyfirst.ui.pantallas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.safetyfirst.datos.FirebaseRepositorio
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun PantMapaSupervisor(nav: NavController, repo: FirebaseRepositorio = FirebaseRepositorio()) {
    val posiciones by repo.flujoUltimaPosicionTodos().collectAsState(initial = emptyMap())
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
