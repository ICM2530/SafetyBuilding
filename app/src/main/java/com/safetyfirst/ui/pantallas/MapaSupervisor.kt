package com.safetyfirst.ui.pantallas

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.safetyfirst.R
import com.safetyfirst.datos.FirebaseResultado
import com.safetyfirst.datos.FirebaseRepositorio
import com.safetyfirst.modelo.Posicion
import com.safetyfirst.modelo.Rol
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantMapaSupervisor(
    nav: NavController,
    repo: FirebaseRepositorio = FirebaseRepositorio()
) {
    // Estado remoto
    val posicionesEstado by repo.flujoUltimaPosicionTodos()
        .collectAsState(initial = FirebaseResultado.Cargando)
    val zonas by repo.flujoZonas().collectAsState(initial = emptyList())
    val usuarioActual by repo.flujoUsuarioActual().collectAsState(initial = null)

    // Estado local
    val snackbarHostState = remember { SnackbarHostState() }
    val routeResult = remember { mutableStateOf<RouteResult?>(null) }
    val selectedWorker = remember { mutableStateOf<Posicion?>(null) }

    val currentUserUid = usuarioActual?.uid
    val esSupervisor = usuarioActual?.rol == Rol.SUPERVISOR

    val mapView = rememberMapViewWithLifecycle()

    val posiciones = when (val estado = posicionesEstado) {
        FirebaseResultado.Cargando -> null
        is FirebaseResultado.Error -> {
            LaunchedEffect(estado.mensaje) {
                snackbarHostState.showSnackbar(estado.mensaje)
            }
            null
        }

        is FirebaseResultado.Exito -> estado.datos
    }

    // Centro inicial del mapa:
    // 1) posición del usuario actual (si existe)
    // 2) primera zona de riesgo
    // 3) centro aproximado PUJ Bogotá
    val currentStart: GeoPoint = when {
        posiciones != null && currentUserUid != null && posiciones[currentUserUid] != null -> {
            val p = posiciones[currentUserUid]!!
            GeoPoint(p.lat, p.lon)
        }
        zonas.isNotEmpty() -> {
            GeoPoint(zonas.first().lat, zonas.first().lon)
        }
        else -> {
            GeoPoint(4.6287, -74.0636)   // centro aproximado PUJ Bogotá
        }
    }

    /**
     * Cálculo de ruta: SOLO supervisor, desde su posición hacia el obrero seleccionado
     */
    LaunchedEffect(selectedWorker.value, posiciones, zonas, esSupervisor) {
        if (!esSupervisor) {
            routeResult.value = null
            return@LaunchedEffect
        }

        val worker = selectedWorker.value
        if (worker == null || posiciones == null || currentUserUid == null) {
            routeResult.value = null
            return@LaunchedEffect
        }

        val startPoint = posiciones[currentUserUid]?.let {
            GeoPoint(it.lat, it.lon)
        } ?: currentStart

        val workerPoint = GeoPoint(worker.lat, worker.lon)

        val nuevaRuta = calculateSafeRouteToPoint(
            start = startPoint,
            target = workerPoint,
            zonasRiesgo = zonas
        )

        if (nuevaRuta == null) {
            routeResult.value = null
            snackbarHostState.showSnackbar(
                "No se encontró una ruta segura hasta el trabajador seleccionado."
            )
        } else {
            routeResult.value = nuevaRuta
        }
    }

    /**
     * Dibujo en el mapa: obra, zonas de riesgo, posiciones y ruta (si existe)
     */
    LaunchedEffect(mapView, posiciones, zonas, routeResult.value, esSupervisor) {
        mapView.overlays.clear()
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        // Zoom un poco más cercano por defecto
        mapView.controller.setZoom(18.0)
        mapView.controller.setCenter(currentStart)

        mapView.addBlueprintOverlay()

        // Zonas de riesgo (círculo + icono pequeño)
        zonas.forEach { zona ->
            mapView.addRiskZoneOverlay(zona)

            val hazardIcon = mapView.smallIcon(R.drawable.ic_marcador_zona_riesgo, factor = 0.6f)
                ?: ContextCompat.getDrawable(mapView.context, R.drawable.ic_marcador_zona_riesgo)

            val hazardMarker = Marker(mapView).apply {
                position = GeoPoint(zona.lat, zona.lon)
                title = zona.titulo.ifBlank { "Zona de riesgo" }
                snippet = zona.descripcion
                icon = hazardIcon
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }
            mapView.overlays.add(hazardMarker)
        }

        // Posiciones de usuarios
        if (posiciones != null && currentUserUid != null) {
            if (esSupervisor) {
                // Supervisor: ve a todos y puede tocarlos para trazar ruta
                posiciones.values.forEach { pos ->
                    val workerIcon =
                        mapView.smallIcon(R.drawable.ic_marcador_obrero, factor = 0.6f)
                            ?: ContextCompat.getDrawable(
                                mapView.context,
                                R.drawable.ic_marcador_obrero
                            )

                    val marker = Marker(mapView).apply {
                        position = GeoPoint(pos.lat, pos.lon)
                        title = if (pos.uid == currentUserUid) "Tu ubicación" else pos.uid
                        icon = workerIcon
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                        setOnMarkerClickListener { _, _ ->
                            if (pos.uid != currentUserUid) {
                                selectedWorker.value = pos
                            }
                            true
                        }
                    }
                    mapView.overlays.add(marker)
                }
            } else {
                // OBRERO: solo se ve a sí mismo con su marcador
                posiciones[currentUserUid]?.let { pos ->
                    val workerIcon =
                        mapView.smallIcon(R.drawable.ic_marcador_obrero, factor = 0.6f)
                            ?: ContextCompat.getDrawable(
                                mapView.context,
                                R.drawable.ic_marcador_obrero
                            )

                    val marker = Marker(mapView).apply {
                        position = GeoPoint(pos.lat, pos.lon)
                        title = "Tu ubicación"
                        icon = workerIcon
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    }
                    mapView.overlays.add(marker)
                }
            }
        }

        // Ruta y marcador de destino (obrero seleccionado) + AUTO-ZOOM a la ruta
        routeResult.value?.let { ruta ->
            val destIcon = mapView.smallIcon(android.R.drawable.star_on, factor = 0.7f)
                ?: ContextCompat.getDrawable(mapView.context, android.R.drawable.star_on)

            val destinationMarker = Marker(mapView).apply {
                position = ruta.path.last()
                title = selectedWorker.value?.uid ?: "Trabajador seleccionado"
                icon = destIcon
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            }
            mapView.overlays.add(destinationMarker)
            mapView.addRouteOverlay(ruta.path)

            // AUTO-ZOOM: ajustar vista para que toda la ruta quede visible
            if (ruta.path.isNotEmpty()) {
                val bbox: BoundingBox = BoundingBox.fromGeoPoints(ruta.path)
                mapView.zoomToBoundingBox(bbox, true)
            }
        }

        mapView.invalidate()
    }

    /**
     * UI sobre el mapa
     */
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        )

        // Supervisor: solo ve el resumen de la ruta (sin barra de búsqueda)
        if (esSupervisor) {
            routeResult.value?.let { result ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.85f),
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(16.dp)
                        .align(Alignment.TopCenter)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE7EFE4)
                        )
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(
                                "Ruta encontrada",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                "Distancia aproximada: ${
                                    "%.1f".format(result.distanceMeters / 1000)
                                } km",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF4A4A4A)
                            )
                        }
                    }
                }
            }
        }

        // Overlay de cargando
        if (posicionesEstado == FirebaseResultado.Cargando) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

/**
 * Helper para crear íconos pequeños escalados desde drawables
 */
private fun MapView.smallIcon(drawableRes: Int, factor: Float = 0.5f): BitmapDrawable? {
    val res = context.resources
    val bmp = BitmapFactory.decodeResource(res, drawableRes) ?: return null
    val w = (bmp.width * factor).toInt().coerceAtLeast(1)
    val h = (bmp.height * factor).toInt().coerceAtLeast(1)
    val scaled: Bitmap = Bitmap.createScaledBitmap(bmp, w, h, true)
    return BitmapDrawable(res, scaled)
}

@SuppressLint("ClickableViewAccessibility")
@Composable
private fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            controller.setZoom(18.0) // un poco más cerca por defecto
        }
    }

    val lifecycleObserver = rememberMapLifecycleObserver(mapView)
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    return mapView
}

@Composable
private fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver {
    return remember(mapView) {
        LifecycleEventObserver { _, event ->
            when (event) {
                androidx.lifecycle.Lifecycle.Event.ON_RESUME -> mapView.onResume()
                androidx.lifecycle.Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                androidx.lifecycle.Lifecycle.Event.ON_DESTROY -> mapView.onDetach()
                else -> Unit
            }
        }
    }
}
