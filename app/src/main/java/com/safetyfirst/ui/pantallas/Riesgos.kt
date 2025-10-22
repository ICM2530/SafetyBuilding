@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.foundation.layout.ExperimentalLayoutApi::class
)

package com.safetyfirst.ui.pantallas

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.safetyfirst.ui.sensors.rememberAltitudeMeters
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.safetyfirst.R
import com.safetyfirst.datos.FirebaseRepositorio
import com.safetyfirst.modelo.TipoRiesgo
import com.safetyfirst.modelo.ZonaRiesgo
import com.safetyfirst.ui.Rutas
import java.io.File
import java.io.InputStream
import java.util.Locale
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun PantRiesgoFormulario(nav: NavController, repo: FirebaseRepositorio = FirebaseRepositorio()) {
    val brandGreen = Color(0xFF0B5F2A)
    val alertRed = Color(0xFFD74C3D)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHost = remember { SnackbarHostState() }

    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    val tipos = remember { TipoRiesgo.values().toList() }
    val seleccionados = remember {
        mutableStateMapOf<TipoRiesgo, Boolean>().apply { tipos.forEach { this[it] = false } }
    }

    val defaultLatLng = LatLng(4.6486, -74.2479)
    var latText by remember { mutableStateOf(formatCoordinate(defaultLatLng.latitude)) }
    var lonText by remember { mutableStateOf(formatCoordinate(defaultLatLng.longitude)) }
    var altText by remember { mutableStateOf("-") }
    var selectedLat by remember { mutableStateOf<Double?>(defaultLatLng.latitude) }
    var selectedLon by remember { mutableStateOf<Double?>(defaultLatLng.longitude) }
    var altitude by remember { mutableStateOf<Double?>(null) }

    var guardando by remember { mutableStateOf(false) }

    val altitudeSensor by rememberAltitudeMeters()
    LaunchedEffect(altitudeSensor) {
        altitudeSensor?.let {
            altitude = it
            if (altText == "-" || altText.isBlank()) {
                altText = formatAltitudeValue(it)
            }
        }
    }

    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var photoBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            tempCameraUri?.let { uri ->
                photoUri = uri
                photoBitmap = decodeBitmapFromUri(context, uri)
            }
        }
    }

    val pickMediaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            photoUri = uri
            photoBitmap = decodeBitmapFromUri(context, uri)
        }
    }

    fun capturePhoto() {
        val uri = createImageUri(context)
        tempCameraUri = uri
        takePictureLauncher.launch(uri)
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            capturePhoto()
        } else {
            scope.launch {
                snackbarHost.showSnackbar("Se requiere el permiso de cámara para tomar fotografías.")
            }
        }
    }

    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val hasLocationPermission = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLatLng, 17f)
    }
    val markerState = remember { MarkerState(defaultLatLng) }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            try {
                val current = fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
                current?.let {
                    selectedLat = it.latitude
                    selectedLon = it.longitude
                    latText = formatCoordinate(it.latitude)
                    lonText = formatCoordinate(it.longitude)
                    if (it.hasAltitude()) {
                        altitude = it.altitude
                        altText = formatAltitude(it.altitude)
                    } else {
                        altitude = null
                        altText = "-"
                    }
                    val latLng = LatLng(it.latitude, it.longitude)
                    markerState.position = latLng
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
                }
            } catch (_: Exception) { /* Ignorar fallos de sensor */ }
        }
    }

    Scaffold(
        containerColor = Color.White,
        snackbarHost = { SnackbarHost(snackbarHost) },
        bottomBar = { SupervisorBottomBar(nav, brandGreen) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            HeaderReportar(brandGreen) { nav.navigate(Rutas.Perfil.ruta) }

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Título:", fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Descripción breve") },
                    shape = RoundedCornerShape(50),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = brandGreen,
                        unfocusedBorderColor = Color(0xFFD9D9D9)
                    )
                )

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp),
                    placeholder = { Text("Describe el riesgo encontrado") },
                    shape = RoundedCornerShape(30),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = brandGreen,
                        unfocusedBorderColor = Color(0xFFD9D9D9)
                    )
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Evidencia fotográfica", fontWeight = FontWeight.SemiBold)
                    if (photoBitmap != null) {
                        Image(
                            bitmap = photoBitmap!!.asImageBitmap(),
                            contentDescription = "Evidencia fotográfica",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(24.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp),
                            shape = RoundedCornerShape(30),
                            color = Color(0xFFD9D9D9)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "Aún no has añadido evidencia fotográfica",
                                    textAlign = TextAlign.Center,
                                    color = Color(0xFF555555)
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                    capturePhoto()
                                } else {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Outlined.PhotoCamera, contentDescription = null, tint = brandGreen)
                            Spacer(Modifier.width(6.dp))
                            Text("Tomar foto")
                        }
                        OutlinedButton(
                            onClick = {
                                pickMediaLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Outlined.Collections, contentDescription = null, tint = brandGreen)
                            Spacer(Modifier.width(6.dp))
                            Text("Galería")
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Clasificación del riesgo", fontWeight = FontWeight.SemiBold)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    tipos.forEach { tipo ->
                        val isSelected = seleccionados[tipo] == true
                        AssistChip(
                            onClick = { seleccionados[tipo] = !isSelected },
                            label = { Text(tipo.etiqueta) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (isSelected) alertRed else Color(0xFFE3E3E3),
                                labelColor = if (isSelected) Color.White else Color.Black
                            )
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Coordenadas", fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CoordinateField(
                        label = "Lat",
                        value = latText,
                        onValueChange = {
                            latText = it
                            val lat = it.toDoubleOrNull()
                            val lon = lonText.toDoubleOrNull()
                            if (lat != null) {
                                selectedLat = lat
                                if (lon != null) {
                                    selectedLon = lon
                                    val latLng = LatLng(lat, lon)
                                    markerState.position = latLng
                                }
                            }
                        }
                    )
                    CoordinateField(
                        label = "Lon",
                        value = lonText,
                        onValueChange = {
                            lonText = it
                            val lon = it.toDoubleOrNull()
                            val lat = latText.toDoubleOrNull()
                            if (lon != null) {
                                selectedLon = lon
                                if (lat != null) {
                                    selectedLat = lat
                                    val latLng = LatLng(lat, lon)
                                    markerState.position = latLng
                                }
                            }
                        }
                    )
                    CoordinateField(
                        label = "Alt",
                        value = altText,
                        onValueChange = { altText = it },
                        readOnly = altitude != null,
                        placeholder = if (altitude == null) "0.0" else null
                    )
                }
            }

            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    compassEnabled = true
                ),
                onMapClick = { latLng ->
                    selectedLat = latLng.latitude
                    selectedLon = latLng.longitude
                    latText = formatCoordinate(latLng.latitude)
                    lonText = formatCoordinate(latLng.longitude)
                    markerState.position = latLng
                    scope.launch {
                        cameraPositionState.animate(CameraUpdateFactory.newLatLng(latLng))
                    }
                }
            ) {
                Marker(
                    state = markerState,
                    title = if (titulo.isBlank()) "Riesgo reportado" else titulo,
                    snippet = descripcion.take(60)
                )
            }

            Button(
                onClick = {
                    val lat = selectedLat ?: latText.toDoubleOrNull()
                    val lon = selectedLon ?: lonText.toDoubleOrNull()
                    val tiposSeleccionados = seleccionados.filter { it.value }.keys.map { it.etiqueta }

                    if (titulo.isBlank() || descripcion.isBlank() || lat == null || lon == null) {
                        scope.launch { snackbarHost.showSnackbar("Completa título, descripción y ubicación.") }
                        return@Button
                    }

                    guardando = true
                    scope.launch {
                        try {
                            val uid = repo.usuarioActual()?.uid.orEmpty()
                            val id = "zona_${System.currentTimeMillis()}"
                            repo.agregarZona(
                                ZonaRiesgo(
                                    id = id,
                                    titulo = titulo,
                                    descripcion = descripcion,
                                    lat = lat,
                                    lon = lon,
                                    radioMetros = 20.0,
                                    creadorUid = uid,
                                    tipos = tiposSeleccionados,
                                    fotoUrl = photoUri?.toString() ?: "",
                                    altitud = altitude ?: altText.toDoubleOrNull() ?: 0.0
                                )
                            )
                            snackbarHost.showSnackbar("Riesgo reportado.")
                            titulo = ""
                            descripcion = ""
                            seleccionados.keys.forEach { seleccionados[it] = false }
                            photoUri = null
                            photoBitmap = null
                            tempCameraUri = null
                        } catch (e: Exception) {
                            snackbarHost.showSnackbar("No se pudo guardar: ${e.message ?: "error"}")
                        } finally {
                            guardando = false
                        }
                    }
                },
                enabled = !guardando,
                colors = ButtonDefaults.buttonColors(containerColor = brandGreen),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50)
            ) {
                Text("Reportar riesgo", color = Color.White)
            }

            if (guardando) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun HeaderReportar(brandGreen: Color, onProfileClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.auth_logo),
                contentDescription = "Logo SafetyFirst",
                modifier = Modifier.size(40.dp),
                colorFilter = ColorFilter.tint(brandGreen)
            )
            Spacer(Modifier.width(12.dp))
            Text("Reportar", fontSize = MaterialTheme.typography.titleLarge.fontSize, fontWeight = FontWeight.SemiBold)
        }
        IconButton(onClick = onProfileClick) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "Mi perfil",
                tint = brandGreen
            )
        }
    }
}

@Composable
private fun RowScope.CoordinateField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    readOnly: Boolean = false,
    placeholder: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.weight(1f),
        label = { Text(label) },
        readOnly = readOnly,
        placeholder = { placeholder?.let { Text(it) } },
        shape = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF0B5F2A),
            unfocusedBorderColor = Color(0xFFD9D9D9)
        ),
        singleLine = true
    )
}

private fun formatCoordinate(value: Double): String = String.format("%.6f", value)

private fun createImageUri(context: Context): Uri {
    val imagesDir = File(context.cacheDir, "images").apply { mkdirs() }
    val imageFile = File(imagesDir, "capture_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)
}

private fun decodeBitmapFromUri(context: Context, uri: Uri): Bitmap? =
    try {
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream)
        }
    } catch (e: Exception) {
        null
    }

private fun formatAltitudeValue(value: Double): String = String.format(Locale.US, "%.1f", value)
private fun formatAltitude(value: Double): String = String.format("%.1f", value)

@Composable
fun PantRiesgosListaSupervisor(nav: NavController, repo: FirebaseRepositorio = FirebaseRepositorio()) {
    val zonas by repo.flujoZonas().collectAsState(initial = emptyList())
    Scaffold(topBar = { TopAppBar(title = { Text("Riesgos") }) }) { p ->
        LazyColumn(Modifier.padding(p)) {
            items(zonas) { z ->
                Card(Modifier.padding(12.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(z.titulo, style = MaterialTheme.typography.titleMedium)
                        Text(z.descripcion)
                        Spacer(Modifier.height(6.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            z.tipos.forEach { t -> AssistChip(onClick = {}, label = { Text(t) }) }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Lat ${z.lat}, Lon ${z.lon}, Alt ${z.altitud} m • r=${z.radioMetros}m",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PantRiesgoDetalle(
    nav: NavController,
    zonaId: String,
    repo: FirebaseRepositorio = FirebaseRepositorio()
) {
    var zona by remember { mutableStateOf<ZonaRiesgo?>(null) }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(zonaId) {
        try {
            zona = repo.obtenerZona(zonaId)
            if (zona == null) error = "No se encontró la zona solicitada."
        } catch (e: Exception) {
            error = e.message
        } finally {
            cargando = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del riesgo") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        when {
            cargando -> {
                Box(
                    Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            zona == null -> {
                Box(
                    Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(error ?: "No hay información disponible.")
                }
            }

            else -> {
                val z = zona!!
                Column(
                    Modifier
                        .padding(padding)
                        .padding(20.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(z.titulo.ifBlank { "Zona sin nombre" }, style = MaterialTheme.typography.headlineSmall)
                    Text(z.descripcion.ifBlank { "Sin descripción" })
                    Text("Ubicación aproximada:", fontWeight = FontWeight.SemiBold)
                    Text("Lat: ${z.lat}  •  Lon: ${z.lon}")
                    Text("Altitud: ${z.altitud} m")
                    Text("Radio de cobertura: ${z.radioMetros} metros")

                    if (z.tipos.isNotEmpty()) {
                        Text("Tipos de riesgo asignados:", fontWeight = FontWeight.SemiBold)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            z.tipos.forEach { t ->
                                AssistChip(onClick = {}, label = { Text(t) })
                            }
                        }
                    }

                    if (z.fotoUrl.isNotBlank()) {
                        Text("Evidencia fotográfica:", fontWeight = FontWeight.SemiBold)
                        Text(z.fotoUrl) // Placeholder; indicar dónde colocar imagen si se desea mostrar.
                    }
                }
            }
        }
    }
}
