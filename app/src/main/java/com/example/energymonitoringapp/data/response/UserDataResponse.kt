package com.example.energymonitoringapp.data.response

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequest(
	val name: String? = null,
	val email: String? = null,
	val nip: String? = null
)

data class ChangePasswordRequest(
	@SerializedName("current_password")          val currentPassword: String,
	@SerializedName("new_password")              val newPassword: String,
	@SerializedName("new_password_confirmation") val newPasswordConfirmation: String
)

data class UserData(
	val id: Int? = null,
	val name: String? = null,
	val email: String? = null,
	val nip: String? = null,
	val role: String? = null,
	@SerializedName("created_at") val createdAt: String? = null
)

data class UserDataResponse(
	val success: Boolean? = null,
	val data: UserData? = null
)

data class UpdateProfileResponse(
	val success: Boolean? = null,
	val message: String? = null,
	val data: UserData? = null
)

data class ChangePasswordResponse(
	val success: Boolean? = null,
	val message: String? = null,
	val token: String? = null
)

data class ValidationErrorResponse(
	val success: Boolean? = null,
	val message: String? = null,
	val errors: ValidationErrors? = null
)

data class ValidationErrors(
	val name: List<String>? = null,
	val email: List<String>? = null,
	val nip: List<String>? = null,
	@SerializedName("current_password")          val currentPassword: List<String>? = null,
	@SerializedName("new_password")              val newPassword: List<String>? = null,
	@SerializedName("new_password_confirmation") val newPasswordConfirmation: List<String>? = null
)
