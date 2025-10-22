package com.safetyfirst.modelo

// ────────────────────────
// Modelos de dominio
// ────────────────────────

enum class Rol { OBRERO, SUPERVISOR }

enum class TipoRiesgo(val etiqueta: String) {
    CAIDA("Caída"),
    MAQUINARIA("Maquinaria pesada"),
    SUPERFICIE("Superficie inestable"),
    ALTO_VOLTAGE("Alto voltaje"),
    INFLAMABLE("Inflamable"),
    PISO_RESBALOSO("Piso resbaloso");
}

data class Usuario(
    val uid: String = "",
    val nombre: String = "",
    val correo: String = "",
    val rol: Rol = Rol.OBRERO,
    val tokenFcm: String = "",
    // Campos extra para mockups de perfil
    val documentoTipo: String = "",
    val documentoNumero: String = "",
    val telefono: String = "",
    val fotoUrl: String = "",
    val permisos: List<String> = emptyList(),
    val zonasAsignadas: List<String> = emptyList(),
    val actividadesAsignadas: List<String> = emptyList()
)

data class Posicion(
    val uid: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val tiempo: Long = 0L
)

data class ZonaRiesgo(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val radioMetros: Double = 20.0,
    val creadorUid: String = "",
    val tipos: List<String> = emptyList(),
    val fotoUrl: String = ""
)

data class Mensaje(
    val id: String = "",
    val conversacionId: String = "",
    val emisorUid: String = "",
    val receptorUid: String = "",
    val texto: String = "",
    val tiempo: Long = 0L
)

sealed class TipoAlerta(
    val titulo: String,
    val mensajeDisplay: String,
    val textoBoton: String,
    // Se usa el nombre de la clase como identificador para la navegación (alerta.name)
    val name: String = this::class.simpleName ?: "Desconocido"
) {
    // Estado por defecto: No hay alerta activa
    object Ninguna : TipoAlerta("", "", "")

    // Alerta 1: Luminosidad
    object PocaLuz : TipoAlerta(
        titulo = "¡ALERTA! Zona Con Poca Luz",
        mensajeDisplay = "¿Desea enviar advertencia de esta zona al supervisor?",
        textoBoton = "Advertir"
    )

    // Alerta 2: Micrófono / Ruido
    object RuidoAlto : TipoAlerta(
        titulo = "¡ALERTA! Ruido muy alto",
        mensajeDisplay = "¿Desea enviar advertencia de esta zona al supervisor?",
        textoBoton = "Notificar"
    )

    // Alerta 3: Ubicación / Geofencing
    object AlturaRiesgosa : TipoAlerta(
        titulo = "¡ALERTA! Altura riesgosa",
        mensajeDisplay = "Ha entrado en una zona marcada como riesgo de altura.",
        textoBoton = "Advertir"
    )

    // Alerta 4: Acelerómetro / Caída (FOD)
    object CaidaDetectada : TipoAlerta(
        titulo = "¡ALERTA DE CAÍDA!",
        mensajeDisplay = "¿Ha sufrido el obrero una caída abrupta? Pida ayuda de emergencia.",
        textoBoton = "Pedir Ayuda"
    )

    /**
     * Función estática para obtener el objeto TipoAlerta a partir de su nombre (String).
     * Esto resuelve el error 'fromString'.
     */
    companion object {
        fun fromString(name: String?): TipoAlerta = when (name) {
            PocaLuz.name -> PocaLuz
            RuidoAlto.name -> RuidoAlto
            AlturaRiesgosa.name -> AlturaRiesgosa
            CaidaDetectada.name -> CaidaDetectada
            else -> Ninguna
        }
    }
}