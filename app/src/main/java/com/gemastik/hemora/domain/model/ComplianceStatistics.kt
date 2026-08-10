package com.gemastik.hemora.domain.model

data class ComplianceStatistics(
    val totalSchedules: Int,
    val totalConsumed: Int,
    val totalMissed: Int,
    val compliancePercentage: Float
)
