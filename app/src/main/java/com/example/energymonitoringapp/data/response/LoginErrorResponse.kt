package com.example.energymonitoringapp.data.response

import com.google.gson.annotations.SerializedName

data class LoginErrorResponse(
	@SerializedName("success") val success: Boolean? = null,
	@SerializedName("message") val message: String? = null,
	@SerializedName("errors") val errors: LoginErrors? = null
)

data class LoginErrors(
	@SerializedName("email") val email: List<String?>? = null,
	@SerializedName("password") val password: List<String?>? = null
)
