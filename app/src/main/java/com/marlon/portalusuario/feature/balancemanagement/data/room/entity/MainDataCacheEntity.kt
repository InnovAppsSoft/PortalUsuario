package com.marlon.portalusuario.feature.balancemanagement.data.room.entity

data class MainDataCacheEntity(
    val usageBasedPricing: Boolean,
    val mainDataCount: Double?,
    val mainDataLteCount: Double?,
    val mainDataRemainingDays: Int?
)