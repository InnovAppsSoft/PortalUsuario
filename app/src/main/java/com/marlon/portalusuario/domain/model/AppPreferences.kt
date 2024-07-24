package com.marlon.portalusuario.domain.model

data class AppPreferences(
    val dataSession: DataSession? = null,
    val mssId: String? = null, // MobileServiceSelectedId
)
