package com.gemastik.hemora.domain.dashboard.model

data class DashboardSummaryModel(
    val totalStudents: Int,
    val totalSchedules: Int,
    val totalConfirmed: Int,
    val totalUnconfirmed: Int
)
