package com.safetyfirst.base

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import org.osmdroid.config.Configuration

class MiAplicacion : Application() {
    companion object { const val ID_CANAL_NOTIFICACIONES = "canal_fcm_safetyfirst" }

    override fun onCreate() {
        super.onCreate()
        crearCanalNotificaciones()
        Configuration.getInstance().userAgentValue = "SafetyFirst-OSM"

        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            Log.i("SafetyFirst", "Token FCM: $token")
        }
    }

    private fun crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                ID_CANAL_NOTIFICACIONES,
                "Notificaciones FCM SafetyFirst",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Canal para notificaciones FCM y alertas de zona" }
            getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
        }
    }
}
