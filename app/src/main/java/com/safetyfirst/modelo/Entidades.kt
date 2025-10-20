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
