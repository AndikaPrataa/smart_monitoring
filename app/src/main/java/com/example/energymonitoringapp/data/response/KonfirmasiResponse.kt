package com.example.energymonitoringapp.data.response

import com.google.gson.annotations.SerializedName

data class KonfirmasiResponse(
    @SerializedName("success") val success: Boolean? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: NotifTeknisiItem? = null
)