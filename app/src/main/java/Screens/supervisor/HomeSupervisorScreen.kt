package com.example.main.Screens.supervisor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.practica.R

data class SupervisorReport(val title: String, val subtitle: String, val dot: Color)

@Composable
fun HomeSupervisorScreen() {
    val bgTop = colorResource(R.color.sup_bg_top)
    val bgBottom = colorResource(R.color.sup_bg_bottom)
    val cardBg = colorResource(R.color.sup_card_bg)
    val textPrimary = colorResource(R.color.sup_text_primary)
    val textSecondary = colorResource(R.color.sup_text_secondary)
    val accent = colorResource(R.color.android_blue)

    // Fondo degradado
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(bgTop, bgBottom)))
            .padding(horizontal = 16.dp)
    ) {
        Column(Modifier.fillMaxSize()) {

            Spacer(Modifier.height(16.dp))
            Text("Dashboard", color = textPrimary, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))

            Text("Your Team’s Stats", color = textPrimary, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            SupervisorStatsCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                cardBg = cardBg,
                accent = accent
            )

            Spacer(Modifier.height(12.dp))
            Text("Last Reports", color = textPrimary, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            val reports = listOf(
                SupervisorReport("Riesgo de Carga Suspendida", "Zona B-121", Color(0xFFED7D31)),
                SupervisorReport("Alta Tensión", "Pasillo Zona A-51", Color(0xFFEE4B2B)),
                SupervisorReport("Riesgo de Derrumbe", "Zona A-107", Color(0xFF9B59B6)),
                SupervisorReport("Caída a Distinto Nivel", "Zona B-11", Color(0xFF2ECC71)),
                SupervisorReport("Atmósfera Inflamable", "Zona D-12", Color(0xFFFFC107)),
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(reports) { r ->
                    SupervisorReportRow(r, cardBg, textPrimary, textSecondary)
                }
            }
        }
    }
}

@Composable
private fun SupervisorReportRow(
    r: SupervisorReport,
    cardBg: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Surface(color = cardBg, shape = RoundedCornerShape(18.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(r.dot.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) { Box(Modifier.size(18.dp).clip(CircleShape).background(r.dot)) }

            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(r.title, color = textPrimary, style = MaterialTheme.typography.bodyLarge)
                Text(r.subtitle, color = textSecondary, style = MaterialTheme.typography.bodyMedium)
            }

            AssistChip(
                onClick = {},
                label = { Text("Nuevo", color = textSecondary, fontSize = 12.sp) }
            )
        }
    }
}

@Composable
private fun SupervisorStatsCard(
    modifier: Modifier,
    cardBg: Color,
    accent: Color
) {
    Surface(modifier = modifier, color = cardBg, shape = RoundedCornerShape(18.dp)) {
        Box(Modifier.padding(14.dp)) {
            SimpleLineChart(
                series = listOf(
                    sampleSeries(6, 0.2f, 0.9f),
                    sampleSeries(6, 0.1f, 0.7f),
                    sampleSeries(6, 0.3f, 0.8f),
                    sampleSeries(6, 0.4f, 0.6f)
                ),
                colors = listOf(
                    Color(0xFF6DD3FF),
                    Color(0xFFB388FF),
                    Color(0xFFFFAF7B),
                    accent
                )
            )
        }
    }
}

@Composable
private fun SimpleLineChart(series: List<List<Float>>, colors: List<Color>, stroke: Float = 3f) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val maxPoints = series.maxOf { it.size }
        val xGap = if (maxPoints > 1) w / (maxPoints - 1) else w

        series.forEachIndexed { idx, values ->
            val path = Path()
            values.forEachIndexed { i, v ->
                val x = i * xGap
                val y = h - (v.coerceIn(0f, 1f) * h)
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(path, color = colors[idx % colors.size], style = Stroke(width = stroke))
            values.forEachIndexed { i, v ->
                val x = i * xGap
                val y = h - (v.coerceIn(0f, 1f) * h)
                drawCircle(colors[idx % colors.size], radius = 3.5f, center = Offset(x, y))
            }
        }
    }
}

private fun sampleSeries(points: Int, min: Float, max: Float): List<Float> =
    List(points) { i ->
        val t = i / (points - 1f)
        val wave = 0.5f + 0.5f * kotlin.math.sin(6f * t)
        min + (max - min) * wave
    }
