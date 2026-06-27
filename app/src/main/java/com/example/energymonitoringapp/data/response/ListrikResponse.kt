package com.example.energymonitoringapp.data.response

import com.google.gson.annotations.SerializedName

data class ListrikResponse(
	@SerializedName("type") val type: String? = null,
	@SerializedName("timestamp") val timestamp: String? = null,
	@SerializedName("data") val data: ListrikData? = null,
	@SerializedName("total") val total: ListrikTotal? = null,
	@SerializedName("notifications") val notifications: List<ListrikNotifItem?>? = null
)

data class ListrikData(
	@SerializedName("id") val id: Int? = null,
	@SerializedName("voltage_l1l2") val voltageL1l2: Double? = null,
	@SerializedName("voltage_l2l3") val voltageL2l3: Double? = null,
	@SerializedName("voltage_l3l1") val voltageL3l1: Double? = null,
	@SerializedName("voltage_l1n") val voltageL1n: Double? = null,
	@SerializedName("voltage_l2n") val voltageL2n: Double? = null,
	@SerializedName("voltage_l3n") val voltageL3n: Double? = null,
	@SerializedName("current_l1") val currentL1: Double? = null,
	@SerializedName("current_l2") val currentL2:Double? = null,
	@SerializedName("current_l3") val currentL3: Double? = null,
	@SerializedName("current_n") val currentN: Double? = null,
	@SerializedName("frecuency") val frecuency: Double? = null,
	@SerializedName("power_factor") val powerFactor: Double? = null,
	@SerializedName("time_stamp") val timeStamp: String? = null,
	@SerializedName("created_at") val createdAt: Any? = null,
	@SerializedName("updated_at") val updatedAt: Any? = null
)

data class ListrikTotal(
	@SerializedName("total_voltage") val totalVoltage: Double? = null,
	@SerializedName("total_current") val totalCurrent: Double? = null
)

data class ListrikNotifItem(
	@SerializedName("sensor") val sensor: String? = null,
	@SerializedName("title") val title: String? = null,
	@SerializedName("level") val level: String? = null,
	@SerializedName("value") val value: Double? = null,
	@SerializedName("unit") val unit: String? = null,
	@SerializedName("message") val message: String? = null,
	@SerializedName("lokasi") val lokasi: String? = null,
	@SerializedName("kategori") val kategori: String? = null,
	@SerializedName("status") val status: String? = null,
	@SerializedName("timestamp") val timestamp: String? = null
)
