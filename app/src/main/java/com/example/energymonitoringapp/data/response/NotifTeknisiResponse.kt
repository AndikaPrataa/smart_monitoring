package com.example.energymonitoringapp.data.response

import com.google.gson.annotations.SerializedName

data class NotifTeknisiResponse(
	@SerializedName("success") val success: Boolean? = null,
	@SerializedName("message") val message: String? = null,
	@SerializedName("total") val total: Int? = null,
	@SerializedName("current_page") val currentPage: Int? = null,
	@SerializedName("last_page")    val lastPage: Int? = null,
	@SerializedName("per_page")     val perPage: Int? = null,
	@SerializedName("data") val data: List<NotifTeknisiItem?>? = null
)

data class NotifTeknisiItem(
	@SerializedName("id") val id: Int? = null,
	@SerializedName("kategori") val kategori: String? = null,
	@SerializedName("sensor") val sensor: String? = null,
	@SerializedName("title") val title: String? = null,
	@SerializedName("level") val level: String? = null,
	@SerializedName("value") val value: Double? = null,
	@SerializedName("unit") val unit: String? = null,
	@SerializedName("message") val message: String? = null,
	@SerializedName("lokasi") val lokasi: String? = null,
	@SerializedName("status") val status: String? = null,
	@SerializedName("assigned_to") val assignedTo: Int? = null,
	@SerializedName("assigned_at") val assignedAt: String? = null,
	@SerializedName("completed_at") val completedAt: String? = null,
	@SerializedName("field_photo") val fieldPhoto: String? = null,
	@SerializedName("action_taken") val actionTaken: String? = null,
	@SerializedName("additional_note") val additionalNote: String? = null,
	@SerializedName("field_photo_url") val fieldPhotoUrl: String? = null,
	@SerializedName("timestamp") val timestamp: String? = null,
	@SerializedName("created_at") val createdAt: String? = null,
	@SerializedName("updated_at") val updatedAt: String? = null
)

data class DetailNotifTeknisiResponse(
	@SerializedName("success") val success: Boolean? = null,
	@SerializedName("message") val message: String? = null,
	@SerializedName("data") val data: NotifTeknisiItem? = null
)
