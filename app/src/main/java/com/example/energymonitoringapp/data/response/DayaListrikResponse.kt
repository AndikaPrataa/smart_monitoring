package com.example.energymonitoringapp.data.response

import com.google.gson.annotations.SerializedName

data class DayaListrikResponse(
    @SerializedName("success") val success: Boolean? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: DayaData? = null,
    @SerializedName("total_active_power") val totalActivePower: TotalActivePower? = null,
    @SerializedName("biaya_realtime") val biayaRealtime: BiayaRealtime? = null
)

data class DayaData(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("active_power_r") val activePowerR: Double? = null,
    @SerializedName("active_power_s") val activePowerS: Double? = null,
    @SerializedName("active_power_t") val activePowerT: Double? = null,
    @SerializedName("reactive_power_r") val reactivePowerR: Double? = null,
    @SerializedName("reactive_power_s") val reactivePowerS: Double? = null,
    @SerializedName("reactive_power_t") val reactivePowerT: Double? = null,
    @SerializedName("apparent_power_r") val apparentPowerR: Double? = null,
    @SerializedName("apparent_power_s") val apparentPowerS: Double? = null,
    @SerializedName("apparent_power_t") val apparentPowerT: Double? = null,
    @SerializedName("time_stamp") val timeStamp: String? = null,
    @SerializedName("created_at") val createdAt: Any? = null,
    @SerializedName("updated_at") val updatedAt: Any? = null
)

data class TotalActivePower(
    @SerializedName("value") val value: Double? = null,
    @SerializedName("unit") val unit: String? = null
)

data class BiayaRealtime(
    @SerializedName("energy_kwh") val energyKwh: Double? = null,
    @SerializedName("tarif_per_kwh") val tarifPerKwh: Double? = null,
    @SerializedName("biaya") val biaya: Double? = null,
    @SerializedName("keterangan") val keterangan: String? = null
)