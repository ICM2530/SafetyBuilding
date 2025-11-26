package com.safetyfirst.notificaciones

import android.app.PendingIntent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.safetyfirst.R
import com.safetyfirst.ui.MainActivity
import com.safetyfirst.base.MiAplicacion
import android.content.Intent
import android.os.Build
import androidx.core.net.toUri

class ServicioMensajeriaFirebase : FirebaseMessagingService() {
    // Usamos un contador estático para asegurar IDs únicos aunque el servicio se reinicie.
    companion object { private var contador = 0 }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        FirebaseAuth.getInstance().currentUser?.uid?.let { uid ->
            FirebaseDatabase.getInstance().reference
                .child("usuarios").child(uid).child("tokenFcm").setValue(token)
        }
    }

    override fun onMessageReceived(m: RemoteMessage) {
        super.onMessageReceived(m)
        Log.i("SafetyFirst", "FCM recibido. Datos: ${m.data}")

        if (m.data.isEmpty()) {
            // Mensaje de notificación de Firebase Console.
            m.notification?.let {
                val titulo = it.title ?: "Alerta SafetyFirst"
                val cuerpo = it.body ?: "Nueva notificación"
                mostrarNotificacion(titulo, cuerpo, null)
            }
            return
        }

        val tipo = m.data["tipo"] ?: "generico"
        val titulo = m.data["titulo"] ?: "Alerta SafetyFirst"
        val cuerpo = m.data["cuerpo"] ?: "Nueva notificación"
        var pendingIntent: PendingIntent? = null

        val pendingIntentFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        when (tipo) {
            "chat" -> {
                val uidOtro = m.data["uidOtro"]
                if (uidOtro != null) {
                    val deepLinkIntent = Intent(
                        Intent.ACTION_VIEW,
                        "safetyfirst://chat/$uidOtro".toUri(),
                        this,
                        MainActivity::class.java
                    )
                    pendingIntent = PendingIntent.getActivity(this, 0, deepLinkIntent, pendingIntentFlag)
                }
            }
            "riesgo" -> {
                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("tipoNotificacion", "riesgo")
                }
                pendingIntent = PendingIntent.getActivity(this, 0, intent, pendingIntentFlag)
            }
            "accidente" -> {
                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("tipoNotificacion", "accidente")
                }
                pendingIntent = PendingIntent.getActivity(this, 0, intent, pendingIntentFlag)
            }
            else -> { // genérico
                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                pendingIntent = PendingIntent.getActivity(this, 0, intent, pendingIntentFlag)
            }
        }
        mostrarNotificacion(titulo, cuerpo, pendingIntent)
    }

    private fun mostrarNotificacion(titulo: String, cuerpo: String, pendingIntent: PendingIntent?) {
        val n = NotificationCompat.Builder(this, MiAplicacion.ID_CANAL_NOTIFICACIONES)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(titulo)
            .setContentText(cuerpo)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Importante para que aparezca
            .also { if (pendingIntent != null) it.setContentIntent(pendingIntent) }
            .setAutoCancel(true)
            .build()

        try { NotificationManagerCompat.from(this).notify(++contador, n) } catch (_: SecurityException) { Log.w("SafetyFirst", "Falta el permiso POST_NOTIFICATIONS") }
    }
}
