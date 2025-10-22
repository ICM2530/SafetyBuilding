package com.safetyfirst.ui.pantallas

import java.util.PriorityQueue
import kotlin.math.*
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Polyline
import com.safetyfirst.modelo.ZonaRiesgo

private data class GraphNode(
    val id: String,
    val name: String? = null,
    val point: GeoPoint,
    val neighbors: List<String>
)

data class ObraDestination(
    val name: String,
    val nodeId: String,
    val description: String
)

private val blueprintPolygonPoints = listOf(
    GeoPoint(4.64890, -74.24820),
    GeoPoint(4.64920, -74.24720),
    GeoPoint(4.64810, -74.24670),
    GeoPoint(4.64770, -74.24760)
)

private val obraGraph: Map<String, GraphNode> = listOf(
    GraphNode(
        id = "A",
        name = "Acceso principal",
        point = GeoPoint(4.64905, -74.24805),
        neighbors = listOf("B", "E")
    ),
    GraphNode(
        id = "B",
        point = GeoPoint(4.64890, -74.24770),
        neighbors = listOf("A", "C", "F")
    ),
    GraphNode(
        id = "C",
        name = "Zona de materiales",
        point = GeoPoint(4.64880, -74.24720),
        neighbors = listOf("B", "D", "G")
    ),
    GraphNode(
        id = "D",
        point = GeoPoint(4.64845, -74.24690),
        neighbors = listOf("C")
    ),
    GraphNode(
        id = "E",
        point = GeoPoint(4.64850, -74.24830),
        neighbors = listOf("A", "F", "I")
    ),
    GraphNode(
        id = "F",
        name = "Zona de ensamblaje",
        point = GeoPoint(4.64830, -74.24775),
        neighbors = listOf("B", "E", "G", "J")
    ),
    GraphNode(
        id = "G",
        point = GeoPoint(4.64815, -74.24725),
        neighbors = listOf("C", "F", "H", "K")
    ),
    GraphNode(
        id = "H",
        name = "Área administrativa",
        point = GeoPoint(4.64795, -74.24695),
        neighbors = listOf("G")
    ),
    GraphNode(
        id = "I",
        point = GeoPoint(4.64795, -74.24810),
        neighbors = listOf("E", "J")
    ),
    GraphNode(
        id = "J",
        name = "Punto de encuentro",
        point = GeoPoint(4.64780, -74.24760),
        neighbors = listOf("F", "I", "K")
    ),
    GraphNode(
        id = "K",
        point = GeoPoint(4.64770, -74.24715),
        neighbors = listOf("G", "J")
    )
).associateBy { it.id }

val obraDestinations: List<ObraDestination> = obraGraph.values
    .filter { !it.name.isNullOrBlank() }
    .map { node ->
        ObraDestination(
            name = node.name!!,
            nodeId = node.id,
            description = "Coordenadas ${"%.5f".format(node.point.latitude)}, ${"%.5f".format(node.point.longitude)}"
        )
    }

fun MapView.addBlueprintOverlay() {
    val polygon = Polygon().apply {
        setPoints(ArrayList(blueprintPolygonPoints))
        fillColor = 0x22A2D2FF
        outlinePaint.color = 0xFF2F7AFF.toInt()
        outlinePaint.strokeWidth = 4f
    }
    overlays.add(polygon)
}

fun MapView.addRiskZoneOverlay(zona: ZonaRiesgo) {
    val polygon = Polygon().apply {
        val center = GeoPoint(zona.lat, zona.lon)
        setPoints(generateCircle(center, zona.radioMetros, 32))
        fillColor = 0x33FF4C3C
        outlinePaint.color = 0xFFFF4C3C.toInt()
        outlinePaint.strokeWidth = 3f
    }
    overlays.add(polygon)
}

fun MapView.addRouteOverlay(points: List<GeoPoint>): Polyline {
    val polyline = Polyline(this).apply {
        setPoints(ArrayList(points))
        outlinePaint.color = 0xFF0B5F2A.toInt()
        outlinePaint.strokeWidth = 8f
    }
    overlays.add(polyline)
    return polyline
}

private fun generateCircle(center: GeoPoint, radius: Double, segments: Int): ArrayList<GeoPoint> {
    val result = ArrayList<GeoPoint>()
    val earthRadius = 6371000.0
    val lat = Math.toRadians(center.latitude)
    val lon = Math.toRadians(center.longitude)
    for (i in 0..segments) {
        val angle = 2 * Math.PI * i / segments
        val pointLat = asin(
            sin(lat) * cos(radius / earthRadius) + cos(lat) * sin(radius / earthRadius) * cos(angle)
        )
        val pointLon = lon + atan2(
            sin(angle) * sin(radius / earthRadius) * cos(lat),
            cos(radius / earthRadius) - sin(lat) * sin(pointLat)
        )
        result.add(GeoPoint(Math.toDegrees(pointLat), Math.toDegrees(pointLon)))
    }
    return result
}

data class RouteResult(
    val start: GeoPoint,
    val path: List<GeoPoint>,
    val distanceMeters: Double
)

fun calculateSafeRoute(
    start: GeoPoint,
    destinationNodeId: String,
    zonasRiesgo: List<ZonaRiesgo>
): RouteResult? {
    val nodes = obraGraph
    val destination = nodes[destinationNodeId] ?: return null

    val startNode = nodes.values
        .filterNot { it.inHazard(zonasRiesgo) }
        .minByOrNull { distanceMeters(start, it.point) } ?: return null

    val visited = mutableSetOf<String>()
    val distances = mutableMapOf<String, Double>().withDefault { Double.POSITIVE_INFINITY }
    val previous = mutableMapOf<String, String>()
    val queue = PriorityQueue(compareBy<Pair<String, Double>> { it.second })

    distances[startNode.id] = 0.0
    queue.add(startNode.id to 0.0)

    while (queue.isNotEmpty()) {
        val entry = queue.poll() ?: break
        val currentId = entry.first
        if (!visited.add(currentId)) continue
        if (currentId == destinationNodeId) break

        val currentNode = nodes[currentId] ?: continue
        currentNode.neighbors.forEach { neighborId ->
            val neighbor = nodes[neighborId] ?: return@forEach
            if (neighbor.inHazard(zonasRiesgo)) return@forEach

            val segmentUnsafe = zonasRiesgo.any { zona ->
                segmentCrossesHazard(currentNode.point, neighbor.point, zona)
            }
            if (segmentUnsafe) return@forEach

            val weight = distanceMeters(currentNode.point, neighbor.point)
            val newDist = distances.getValue(currentId) + weight
            if (newDist < distances.getValue(neighborId)) {
                distances[neighborId] = newDist
                previous[neighborId] = currentId
                queue.add(neighborId to newDist)
            }
        }
    }

    if (!previous.containsKey(destinationNodeId) && startNode.id != destinationNodeId) return null

    val pathNodes = mutableListOf<GeoPoint>()
    var current = destinationNodeId
    pathNodes.add(destination.point)
    while (previous.containsKey(current)) {
        current = previous[current]!!
        pathNodes.add(nodes[current]?.point ?: break)
    }
    if (pathNodes.last() != startNode.point) {
        pathNodes.add(startNode.point)
    }
    pathNodes.reverse()

    val fullPath = listOf(start) + pathNodes
    val totalDistance = fullPath.zipWithNext { a, b -> distanceMeters(a, b) }.sum()

    return RouteResult(
        start = start,
        path = fullPath,
        distanceMeters = totalDistance
    )
}

private fun GraphNode.inHazard(zonas: List<ZonaRiesgo>): Boolean =
    zonas.any { distanceMeters(point, GeoPoint(it.lat, it.lon)) <= it.radioMetros }

private fun segmentCrossesHazard(start: GeoPoint, end: GeoPoint, zona: ZonaRiesgo): Boolean {
    val center = GeoPoint(zona.lat, zona.lon)
    val distanceToSegment = distancePointToSegmentMeters(center, start, end)
    return distanceToSegment <= zona.radioMetros
}

private fun distanceMeters(a: GeoPoint, b: GeoPoint): Double = distanceMeters(
    a.latitude, a.longitude, b.latitude, b.longitude
)

private fun distanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadius = 6371000.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val startLat = Math.toRadians(lat1)
    val endLat = Math.toRadians(lat2)
    val a = sin(dLat / 2).pow(2) + cos(startLat) * cos(endLat) * sin(dLon / 2).pow(2)
    return 2 * earthRadius * atan2(sqrt(a), sqrt(1 - a))
}

private fun distancePointToSegmentMeters(point: GeoPoint, a: GeoPoint, b: GeoPoint): Double {
    val lat1 = Math.toRadians(a.latitude)
    val lon1 = Math.toRadians(a.longitude)
    val lat2 = Math.toRadians(b.latitude)
    val lon2 = Math.toRadians(b.longitude)
    val latP = Math.toRadians(point.latitude)
    val lonP = Math.toRadians(point.longitude)

    val d13 = distanceMeters(a.latitude, a.longitude, point.latitude, point.longitude)
    val bearing13 = bearing(lat1, lon1, latP, lonP)
    val bearing12 = bearing(lat1, lon1, lat2, lon2)
    val crossTrack = asin(sin(d13 / 6371000.0) * sin(bearing13 - bearing12)) * 6371000.0

    val d12 = distanceMeters(a.latitude, a.longitude, b.latitude, b.longitude)
    val alongTrack = acos(cos(d13 / 6371000.0) / cos(crossTrack / 6371000.0)) * 6371000.0

    return when {
        alongTrack < 0 -> distanceMeters(point, a)
        alongTrack > d12 -> distanceMeters(point, b)
        else -> abs(crossTrack)
    }
}

private fun bearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val y = sin(lon2 - lon1) * cos(lat2)
    val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(lon2 - lon1)
    return atan2(y, x)
}
