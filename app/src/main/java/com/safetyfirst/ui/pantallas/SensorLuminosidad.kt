package com.safetyfirst.ui.pantallas

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SensorLuminosidad(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensorLuz: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    // StateFlow para emitir el valor de la luz (Lux)
    private val _lux = MutableStateFlow(0f)
    val lux: StateFlow<Float> = _lux.asStateFlow()

    // Inicia la escucha del sensor
    fun iniciarEscucha() {
        // Solo registrar si el sensor existe
        sensorLuz?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    // Detiene la escucha del sensor
    fun detenerEscucha() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            // El valor de la luminosidad está en event.values[0]
            _lux.value = event.values[0]
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No se requiere lógica específica para la precisión de la luz por ahora
    }
}
