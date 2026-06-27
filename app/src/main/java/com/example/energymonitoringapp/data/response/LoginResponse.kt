package com.example.energymonitoringapp.data.response

import com.google.gson.annotations.SerializedName

data class LoginRequest(
	@SerializedName("email") val email: String,
	@SerializedName("password") val password: String
)

data class LoginResponse(
	@SerializedName("success") val success: Boolean? = null,
	@SerializedName("message") val message: String? = null,
	@SerializedName("data") val data: LoginData? = null
)

data class LoginData(
	@SerializedName("id") val id: Int? = null,
	@SerializedName("name") val name: String? = null,
	@SerializedName("email") val email: String? = null,
	@SerializedName("role") val role: String? = null,
	@SerializedName("token") val token: String? = null,
	@SerializedName("token_type") val tokenType: String? = null
)