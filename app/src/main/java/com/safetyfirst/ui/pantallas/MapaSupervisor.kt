package com.safetyfirst.ui.pantallas

import android.annotation.SuppressLint
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import com.example.main.CompReusable.ReusableTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.safetyfirst.datos.FirebaseResultado
import com.safetyfirst.datos.FirebaseRepositorio
import com.safetyfirst.modelo.Usuario
import com.safetyfirst.modelo.Rol
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.produceState
import androidx.core.content.ContextCompat
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantMapaSupervisor(nav: NavController, repo: FirebaseRepositorio = FirebaseRepositorio()) {
    val posicionesEstado by repo.flujoUltimaPosicionTodos().collectAsState(initial = FirebaseResultado.Cargando)
    val zonas by repo.flujoZonas().collectAsState(initial = emptyList())
    val snackbarHostState = remember { SnackbarHostState() }
    val searchQuery = rememberSaveable { mutableStateOf("") }
    val showSuggestions = remember { mutableStateOf(false) }
    val selectedDestination = remember { mutableStateOf<ObraDestination?>(null) }
    val routeResult = remember { mutableStateOf<RouteResult?>(null) }
    val currentUserUid = remember { repo.usuarioActual()?.uid }
    
    val currentUser by produceState<Usuario?>(initialValue = null, currentUserUid) {
        value = currentUserUid?.let { repo.obtenerUsuario(it) }
    }
    
    val allUsers by produceState<List<Usuario>>(initialValue = emptyList()) {
        value = repo.obtenerTodosUsuarios()
    }

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

    val currentStart = posiciones?.get(currentUserUid ?: "")?.let { GeoPoint(it.lat, it.lon) }
        ?: GeoPoint(4.6486, -74.2479)

    LaunchedEffect(selectedDestination.value, posiciones, zonas) {
        val destino = selectedDestination.value
        if (destino != null && posiciones != null) {
            val startPoint = currentStart
            val nuevaRuta = calculateSafeRoute(startPoint, destino.nodeId, zonas)
            if (nuevaRuta == null) {
                routeResult.value = null
                snackbarHostState.showSnackbar("No se encontró una ruta segura hasta ${destino.name}.")
            } else {
                routeResult.value = nuevaRuta
            }
        } else {
            routeResult.value = null
        }
    }

    LaunchedEffect(mapView, posiciones, zonas, routeResult.value) {
        mapView.overlays.clear()
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.controller.setZoom(16.0)
        mapView.controller.setCenter(currentStart)

        mapView.addBlueprintOverlay()

        zonas.forEach { zona ->
            mapView.addRiskZoneOverlay(zona)
            val hazardMarker = Marker(mapView).apply {
                position = GeoPoint(zona.lat, zona.lon)
                title = zona.titulo.ifBlank { "Zona de riesgo" }
                snippet = zona.descripcion
                icon = ContextCompat.getDrawable(mapView.context, android.R.drawable.presence_busy)
            }
            mapView.overlays.add(hazardMarker)
        }

        // Mostrar marcadores según el rol del usuario
        if (currentUser?.rol == Rol.SUPERVISOR) {
            // El supervisor ve a todos los usuarios
            posiciones?.values?.forEach { pos ->
                val usuario = allUsers.find { it.uid == pos.uid }
                val marker = Marker(mapView).apply {
                    position = GeoPoint(pos.lat, pos.lon)
                    title = if (pos.uid == currentUserUid) {
                        "Tu ubicación (Supervisor)"
                    } else {
                        usuario?.nombre ?: pos.uid
                    }
                    snippet = when {
                        pos.uid == currentUserUid -> "Lat: ${"%+.5f".format(pos.lat)}, Lon: ${"%+.5f".format(pos.lon)}"
                        usuario != null -> "${usuario.rol} - Lat: ${"%+.5f".format(pos.lat)}, Lon: ${"%+.5f".format(pos.lon)}"
                        else -> "Lat: ${"%+.5f".format(pos.lat)}, Lon: ${"%+.5f".format(pos.lon)}"
                    }
                    icon = if (pos.uid == currentUserUid) {
                        ContextCompat.getDrawable(mapView.context, android.R.drawable.ic_menu_mylocation)
                    } else if (usuario?.rol == Rol.SUPERVISOR) {
                        ContextCompat.getDrawable(mapView.context, android.R.drawable.ic_menu_compass)
                    } else {
                        ContextCompat.getDrawable(mapView.context, android.R.drawable.ic_menu_myplaces)
                    }
                }
                mapView.overlays.add(marker)
            }
        } else {
            // El obrero solo ve su propia ubicación
            posiciones?.get(currentUserUid ?: "")?.let { pos ->
                val marker = Marker(mapView).apply {
                    position = GeoPoint(pos.lat, pos.lon)
                    title = "Tu ubicación (${currentUser?.nombre ?: "Obrero"})"
                    snippet = "Lat: ${"%+.5f".format(pos.lat)}, Lon: ${"%+.5f".format(pos.lon)}"
                    icon = ContextCompat.getDrawable(mapView.context, android.R.drawable.ic_menu_mylocation)
                }
                mapView.overlays.add(marker)
            }
        }

        routeResult.value?.let { ruta ->
            val destinationMarker = Marker(mapView).apply {
                position = ruta.path.last()
                title = selectedDestination.value?.name ?: "Destino"
                icon = ContextCompat.getDrawable(mapView.context, android.R.drawable.star_on)
            }
            mapView.overlays.add(destinationMarker)
            mapView.addRouteOverlay(ruta.path)
        }

        mapView.invalidate()
    }

    Scaffold(
        topBar = {
            ReusableTopAppBar(
                title = "Mapa",
                leadingIcon = Icons.AutoMirrored.Filled.ArrowBack,
                leadingContentDescription = "Volver",
                onLeadingClick = { nav.popBackStack() },
                trailingIcon = null,
                showDivider = true
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.White.copy(alpha = 0.85f), shape = MaterialTheme.shapes.medium)
                    .padding(16.dp)
            ) {
            OutlinedTextField(
                value = searchQuery.value,
                onValueChange = {
                    searchQuery.value = it
                    showSuggestions.value = it.isNotBlank()
                },
                placeholder = { Text("Buscar destino dentro de la obra") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                modifier = Modifier.fillMaxWidth()
            )

            val filteredDestinations = remember(searchQuery.value) {
                obraDestinations.filter { dest ->
                    dest.name.contains(searchQuery.value, ignoreCase = true)
                }
            }

            if (showSuggestions.value && filteredDestinations.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 180.dp)
                        .padding(top = 8.dp)
                ) {
                    items(filteredDestinations) { dest ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F7F6))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(Icons.Outlined.Place, contentDescription = null, tint = Color(0xFF0B5F2A))
                                Column(Modifier.weight(1f)) {
                                    Text(dest.name, style = MaterialTheme.typography.titleMedium)
                                    Text(dest.description, style = MaterialTheme.typography.bodySmall, color = Color(0xFF606060))
                                }
                                IconButton(onClick = {
                                    selectedDestination.value = dest
                                    searchQuery.value = dest.name
                                    showSuggestions.value = false
                                }) {
                                    Icon(Icons.Outlined.Route, contentDescription = "Calcular ruta")
                                }
                            }
                        }
                    }
                }
            }

            routeResult.value?.let { result ->
                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE7EFE4))
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Ruta encontrada", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Distancia aproximada: ${"%.1f".format(result.distanceMeters / 1000)} km",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF4A4A4A)
                        )
                    }
                }
            }
        }

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

        }
    }
}

@SuppressLint("ClickableViewAccessibility")
@Composable
private fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            controller.setZoom(16.0)
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
