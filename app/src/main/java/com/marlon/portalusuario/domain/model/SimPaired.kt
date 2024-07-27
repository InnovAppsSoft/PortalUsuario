package com.marlon.portalusuario.domain.model

import com.google.gson.annotations.SerializedName

data class SimPaired(
    @SerializedName("sim_id") val simId: String,
    @SerializedName("service_id") val serviceId: String
)
