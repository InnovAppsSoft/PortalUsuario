package com.marlon.portalusuario.domain.model

import com.google.gson.annotations.SerializedName

data class DataSession(
    @SerializedName("phone_number") val phoneNumber: String,
    val password: String,
    @SerializedName("captcha_code") val captchaCode: String,
    @SerializedName("id_request") val idRequest: String,
    @SerializedName("auth_token") val authToken: String,
    @SerializedName("last_update") val lastUpdate: String,
    @SerializedName("portal_user") val portalUser: String,
    @SerializedName("updated_services") val updatedServices: Boolean,
)
