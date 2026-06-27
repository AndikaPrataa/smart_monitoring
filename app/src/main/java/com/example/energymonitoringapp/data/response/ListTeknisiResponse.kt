package com.example.energymonitoringapp.data.response

import com.google.gson.annotations.SerializedName

data class ListTeknisiResponse(
	@SerializedName("success") val success: Boolean? = null,
	@SerializedName("message") val message: String? = null,
	@SerializedName("total") val total: Int? = null,
	@SerializedName("data") val data: List<TeknisiItem?>? = null
)

data class TeknisiItem(
	@SerializedName("id") val id: Int? = null,
	@SerializedName("name") val name: String? = null,
	@SerializedName("email") val email: String? = null,
	@SerializedName("role") val role: String? = null,
	@SerializedName("nip") val nip: String? = null,
	@SerializedName("status_teknisi") val statusTeknisi: String? = null,
	@SerializedName("can_receive_task") val canReceiveTask: Boolean? = null,
	@SerializedName("active_task_count") val activeTaskCount: Int? = null,
	@SerializedName("total_assigned_task_count") val totalAssignedTaskCount: Int? = null,
	@SerializedName("created_at") val createdAt: String? = null,
	@SerializedName("updated_at") val updatedAt: String? = null
)
