package com.gemastik.hemora.data.schedule.dto

import com.gemastik.hemora.domain.model.TtdSchedule
import com.google.firebase.Timestamp
import java.util.Date

data class TtdScheduleDto(
    val scheduleId: String = "",
    val schoolId: String = "",
    val date: Timestamp? = null,
    val time: String = ""
) {
    fun toDomain(): TtdSchedule {
        return TtdSchedule(
            scheduleId = scheduleId,
            schoolId = schoolId,
            date = date?.toDate() ?: Date(),
            time = time
        )
    }
}
