package com.gemastik.hemora.domain.model

import java.util.Date

data class TtdSchedule(
    val scheduleId: String,
    val schoolId: String,
    val date: Date,
    val time: String
)
