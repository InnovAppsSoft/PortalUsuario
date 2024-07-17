package com.marlon.portalusuario.domain.model

import com.google.gson.annotations.SerializedName

data class DataSession(
    @SerializedName("phone_number") val phoneNumber: String? = null,
    val password: String? = null,
    @SerializedName("captcha_code") val captchaCode: String? = null,
    @SerializedName("id_request") val idRequest: String? = null,
    @SerializedName("auth_token") val authToken: String? = null,
    @SerializedName("last_update") val lastUpdate: String? = null,
    @SerializedName("portal_user") val portalUser: String? = null
)
