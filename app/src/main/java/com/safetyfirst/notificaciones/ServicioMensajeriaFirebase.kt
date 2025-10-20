package com.safetyfirst.notificaciones

import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.safetyfirst.R
import com.safetyfirst.base.MiAplicacion

class ServicioMensajeriaFirebase : FirebaseMessagingService() {
    private var contador = 0

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        FirebaseAuth.getInstance().currentUser?.uid?.let { uid ->
            FirebaseDatabase.getInstance().reference
                .child("usuarios").child(uid).child("tokenFcm").setValue(token)
        }
    }

    override fun onMessageReceived(m: RemoteMessage) {
        Log.i("SafetyFirst", "FCM recibido")
        val titulo = m.notification?.title ?: "Alerta SafetyFirst"
        val cuerpo = m.notification?.body ?: "Nueva notificación"
        val n = NotificationCompat.Builder(this, MiAplicacion.ID_CANAL_NOTIFICACIONES)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(titulo)
            .setContentText(cuerpo)
            .setAutoCancel(true)
            .build()
        try { NotificationManagerCompat.from(this).notify(++contador, n) }
        catch (_: SecurityException) { Log.w("SafetyFirst", "Falta POST_NOTIFICATIONS") }
    }
}
