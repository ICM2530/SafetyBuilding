package com.safetyfirst.ui.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.*

private const val SEA_LEVEL_PRESSURE_HPA = 1013.25f

@Composable
fun rememberAltitudeMeters(): State<Double?> {
    val context = LocalContext.current
    val altitudeState = remember { mutableStateOf<Double?>(null) }

    DisposableEffect(context) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val barometer = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
        if (barometer == null) {
            altitudeState.value = null
            onDispose { }
        } else {
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val pressure = event.values.firstOrNull() ?: return
                    val altitude = SensorManager.getAltitude(SEA_LEVEL_PRESSURE_HPA, pressure)
                    altitudeState.value = altitude.toDouble()
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
            }
            sensorManager.registerListener(listener, barometer, SensorManager.SENSOR_DELAY_NORMAL)
            onDispose {
                sensorManager.unregisterListener(listener)
            }
        }
    }

    return altitudeState
}

@Composable
fun rememberFallDetector(onFallDetected: () -> Unit) {
    val context = LocalContext.current
    val currentCallback by rememberUpdatedState(onFallDetected)

    DisposableEffect(context) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometer == null) {
            onDispose { }
        } else {
            var lastLowTimestamp = 0L
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]
                    val magnitude = sqrt(x * x + y * y + z * z)
                    val now = System.currentTimeMillis()

                    if (magnitude < 2f) {
                        lastLowTimestamp = now
                    }

                    if (lastLowTimestamp != 0L && now - lastLowTimestamp < 1500 && magnitude > 25f) {
                        lastLowTimestamp = 0L
                        currentCallback()
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
            }
            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
            onDispose {
                sensorManager.unregisterListener(listener)
            }
        }
    }
}

class NoiseMeter(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private val _decibels = mutableStateOf(0.0)
    val decibels: State<Double> get() = _decibels

    private var audioRecord: AudioRecord? = null
    private var samplingJob: Job? = null

    fun start() {
        if (audioRecord != null) return

        val minBufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        if (minBufferSize == AudioRecord.ERROR || minBufferSize == AudioRecord.ERROR_BAD_VALUE) {
            return
        }

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            minBufferSize
        )

        audioRecord?.startRecording()
        samplingJob = scope.launch(Dispatchers.Default) {
            val buffer = ShortArray(minBufferSize)
            while (isActive) {
                val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (read > 0) {
                    var sum = 0.0
                    buffer.forEach { sample ->
                        sum += (sample / MAX_AMPLITUDE).pow(2)
                    }
                    val rms = sqrt(sum / read)
                    val decibel = 20 * log10(rms / REFERENCE_PRESSURE)
                    withContext(Dispatchers.Main) {
                        _decibels.value = decibel.coerceIn(-10.0, 120.0)
                    }
                }
            }
        }
    }

    fun stop() {
        samplingJob?.cancel()
        samplingJob = null
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }

    companion object {
        private const val SAMPLE_RATE = 44100
        private const val MAX_AMPLITUDE = 32768.0
        private const val REFERENCE_PRESSURE = 0.00002
    }
}

@Composable
fun rememberNoiseMeter(): NoiseMeter {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val meter = remember { NoiseMeter(context, scope) }
    DisposableEffect(Unit) {
        onDispose {
            meter.stop()
        }
    }
    return meter
}
