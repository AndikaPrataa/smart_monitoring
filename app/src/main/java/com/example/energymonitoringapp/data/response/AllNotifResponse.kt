package com.example.energymonitoringapp.data.response

import com.google.gson.annotations.SerializedName

data class AllNotifResponseItem(
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
    @SerializedName("assigned_to") val assignedTo: Any? = null,
    @SerializedName("assigned_at") val assignedAt: Any? = null,
    @SerializedName("completed_at") val completedAt: Any? = null,
    @SerializedName("field_photo") val fieldPhoto: Any? = null,
    @SerializedName("action_taken") val actionTaken: Any? = null,
    @SerializedName("additional_note") val additionalNote: Any? = null,
    @SerializedName("field_photo_url") val fieldPhotoUrl: Any? = null,
    @SerializedName("timestamp") val timestamp: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)
