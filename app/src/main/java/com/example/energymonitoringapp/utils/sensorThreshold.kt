package com.example.energymonitoringapp.utils

import com.example.energymonitoringapp.view.theme.GreenLight
import com.example.energymonitoringapp.view.theme.GreenTeal
import com.example.energymonitoringapp.view.theme.OrangeLight
import com.example.energymonitoringapp.view.theme.OrangePrimary
import com.example.energymonitoringapp.view.theme.RedLight
import com.example.energymonitoringapp.view.theme.RedPrimary

object SensorThresholds {

    data class Threshold(
        val normalMin  : Double,
        val normalMax  : Double,
        val waspadaMin : Double,
        val waspadaMax : Double,
        val dangerMax  : Double? = null, // untuk sensor yg bahaya di ATAS waspadaMax (e.g. cahaya > 200)
        val unit       : String,
        val namaLabel  : String
    )

    val map: Map<String, Threshold> = mapOf(
        "suhu" to Threshold(
            normalMin  = 20.0,
            normalMax  = 30.0,
            waspadaMin = 16.0,
            waspadaMax = 35.0,
            unit       = "°C",
            namaLabel  = "Suhu"
        ),
        "kelembapan" to Threshold(
            normalMin  = 40.0,
            normalMax  = 60.0,
            waspadaMin = 30.0,
            waspadaMax = 80.0,
            unit       = "%",
            namaLabel  = "Kelembapan"
        ),
        "gas_co2" to Threshold(
            normalMin  = 0.0,
            normalMax  = 1000.0,
            waspadaMin = 0.0,
            waspadaMax = 2000.0,
            unit       = "ppm",
            namaLabel  = "Gas CO₂"
        ),
        "tvoc" to Threshold(
            normalMin  = 0.0,
            normalMax  = 1000.0,
            waspadaMin = 0.0,
            waspadaMax = 3000.0,
            unit       = "ppb",
            namaLabel  = "TVOC"
        ),
        "cahaya" to Threshold(
            normalMin  = 100.0,
            normalMax  = 200.0,
            waspadaMin = 60.0,
            waspadaMax = 200.0,
            dangerMax  = 200.0, // nilai > 200 langsung BAHAYA
            unit       = "lux",
            namaLabel  = "Cahaya"
        ),
        "pm25" to Threshold(
            normalMin  = 0.0,
            normalMax  = 25.0,
            waspadaMin = 0.0,
            waspadaMax = 55.0,
            unit       = "µg/m³",
            namaLabel  = "PM2.5"
        ),
        "pm10" to Threshold(
            normalMin  = 0.0,
            normalMax  = 70.0,
            waspadaMin = 0.0,
            waspadaMax = 150.0,
            unit       = "µg/m³",
            namaLabel  = "PM10"
        ),
        "co" to Threshold(
            normalMin  = 0.0,
            normalMax  = 24.0,
            waspadaMin = 0.0,
            waspadaMax = 50.0,
            unit       = "ppm",
            namaLabel  = "CO"
        ),
        "kebisingan" to Threshold(
            normalMin  = 0.0,
            normalMax  = 50.0,
            waspadaMin = 0.0,
            waspadaMax = 65.0,
            unit       = "dBA",
            namaLabel  = "Kebisingan"
        )
    )

    fun get(sensor: String?): Threshold? = map[sensor?.lowercase()]
}

// ─────────────────────────────────────────────────────────────────────────────
// ENUM STATUS + HELPER WARNA
// ─────────────────────────────────────────────────────────────────────────────

enum class SensorStatus { NORMAL, WASPADA, BAHAYA }

data class SensorStatusResult(
    val status     : SensorStatus,
    val label      : String,
    val keterangan : String
)

fun SensorStatus.toColor() = when (this) {
    SensorStatus.NORMAL  -> GreenTeal
    SensorStatus.WASPADA -> OrangePrimary
    SensorStatus.BAHAYA  -> RedPrimary
}

fun SensorStatus.toBgColor() = when (this) {
    SensorStatus.NORMAL  -> GreenLight
    SensorStatus.WASPADA -> OrangeLight
    SensorStatus.BAHAYA  -> RedLight
}

// ─────────────────────────────────────────────────────────────────────────────
// DETEKSI STATUS NILAI SENSOR
// ─────────────────────────────────────────────────────────────────────────────

fun getSensorStatus(sensor: String?, value: Double?): SensorStatusResult {
    val threshold = SensorThresholds.get(sensor)
    val nama      = threshold?.namaLabel ?: sensor ?: "Sensor"
    val unit      = threshold?.unit ?: ""

    if (value == null || threshold == null) {
        return SensorStatusResult(
            status     = SensorStatus.NORMAL,
            label      = "Normal",
            keterangan = "-"
        )
    }

    // Bahaya jika:
    // 1. di bawah waspadaMin, ATAU
    // 2. di atas waspadaMax (untuk sensor tanpa dangerMax), ATAU
    // 3. di atas dangerMax (khusus sensor seperti cahaya yg punya batas bahaya atas)
    val isOutsideWaspada = value < threshold.waspadaMin ||
            (threshold.dangerMax != null && value > threshold.dangerMax) ||
            (threshold.dangerMax == null && value > threshold.waspadaMax)

    val isOutsideNormal = value < threshold.normalMin || value > threshold.normalMax

    return when {
        isOutsideWaspada -> SensorStatusResult(
            status     = SensorStatus.BAHAYA,
            label      = "Bahaya",
            keterangan = "$nama di luar batas aman " +
                    "(${threshold.waspadaMin.fmt()}–${threshold.waspadaMax.fmt()} $unit)"
        )
        isOutsideNormal -> SensorStatusResult(
            status     = SensorStatus.WASPADA,
            label      = "Waspada",
            keterangan = "$nama mendekati batas " +
                    "(${threshold.normalMin.fmt()}–${threshold.normalMax.fmt()} $unit)"
        )
        else -> SensorStatusResult(
            status     = SensorStatus.NORMAL,
            label      = "Normal",
            keterangan = "$nama dalam batas normal " +
                    "(${threshold.normalMin.fmt()}–${threshold.normalMax.fmt()} $unit)"
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TEKS BATAS NORMAL UNTUK DETAIL NOTIF
// ─────────────────────────────────────────────────────────────────────────────

fun getBatasNormal(sensor: String?): String {
    val threshold = SensorThresholds.get(sensor) ?: return "-"
    return "${threshold.normalMin.fmt()}–${threshold.normalMax.fmt()} ${threshold.unit}"
}

// ─────────────────────────────────────────────────────────────────────────────
// HELPER FORMAT ANGKA (private extension)
// ─────────────────────────────────────────────────────────────────────────────

fun Double.fmt(): String =
    if (this == kotlin.math.floor(this)) this.toInt().toString()
    else "%.1f".format(this)