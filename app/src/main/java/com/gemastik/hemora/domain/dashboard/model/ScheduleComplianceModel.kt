package com.gemastik.hemora.domain.dashboard.model

import com.gemastik.hemora.domain.model.TtdSchedule

data class ScheduleComplianceModel(
    val schedule: TtdSchedule,
    val consumedCount: Int,
    val totalStudents: Int,
    val percentage: Float
)
