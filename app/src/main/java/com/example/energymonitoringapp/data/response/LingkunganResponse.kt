package com.example.energymonitoringapp.data.response

import com.google.gson.annotations.SerializedName

data class LingkunganResponse(
	@SerializedName("success") val success: Boolean? = null,
	@SerializedName("message") val message: String? = null,
	@SerializedName("data") val data: LingkunganData? = null,
	@SerializedName("ieq") val ieq: Ieq? = null,
	@SerializedName("tvoc_average_8_hours") val tvocAverage8Hours: Double? = null
)

data class LingkunganData(
	@SerializedName("id") val id: Int? = null,
	@SerializedName("suhu") val suhu: Double? = null,
	@SerializedName("kelembapan") val kelembapan: Double? = null,
	@SerializedName("pm25") val pm25: Double? = null,
	@SerializedName("pm10") val pm10: Double? = null,
	@SerializedName("gas_co") val gasCo: Double? = null,
	@SerializedName("gas_co2") val gasCo2: Double? = null,
	@SerializedName("tvoc") val tvoc: Double? = null,
	@SerializedName("cahaya") val cahaya: Double? = null,
	@SerializedName("kebisingan") val kebisingan: Double? = null,
	@SerializedName("lokasi") val lokasi: String? = null,
	@SerializedName("waktu") val waktu: String? = null,
	@SerializedName("created_at") val createdAt: Any? = null,
	@SerializedName("updated_at") val updatedAt: Any? = null
)

data class Ieq(
	@SerializedName("score") val score: Double? = null,
	@SerializedName("status") val status: String? = null,
	@SerializedName("description") val description: String? = null,
	@SerializedName("details") val details: IeqDetails? = null
)

data class IeqDetails(
	@SerializedName("suhu") val suhu: Double? = null,
	@SerializedName("kelembapan") val kelembapan: Double? = null,
	@SerializedName("pm25") val pm25: Double? = null,
	@SerializedName("pm10") val pm10: Double? = null,
	@SerializedName("gas_co") val gasCo: Double? = null,
	@SerializedName("gas_co2") val gasCo2: Double? = null,
	@SerializedName("tvoc") val tvoc: Double? = null,
	@SerializedName("cahaya") val cahaya: Double? = null,
	@SerializedName("kebisingan") val kebisingan: Double? = null
)

