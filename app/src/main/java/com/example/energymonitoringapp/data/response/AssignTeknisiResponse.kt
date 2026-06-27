package com.example.energymonitoringapp.data.response

import com.google.gson.annotations.SerializedName

data class AssignTeknisiRequest(
    @SerializedName("assigned_to") val assignedTo: Int
)

data class AssignTeknisiResponse(
    @SerializedName("success") val success: Boolean? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: NotifAdminItem? = null,
    @SerializedName("technician_status") val technicianStatus: TechnicianStatusItem? = null
)

data class TechnicianStatusItem(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("nip") val nip: String? = null,
    @SerializedName("status_teknisi") val statusTeknisi: String? = null,
    @SerializedName("active_task_count") val activeTaskCount: Int? = null,
    @SerializedName("can_receive_task") val canReceiveTask: Boolean? = null
)

