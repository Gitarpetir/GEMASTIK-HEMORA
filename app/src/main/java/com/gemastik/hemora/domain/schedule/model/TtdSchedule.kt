package com.gemastik.hemora.domain.schedule.model

data class TtdSchedule(
    val scheduleId: String,
    val schoolId: String,
    val date: String, // Format: YYYY-MM-DD
    val time: String, // Format: HH:MM
    val createdAt: Long
)
