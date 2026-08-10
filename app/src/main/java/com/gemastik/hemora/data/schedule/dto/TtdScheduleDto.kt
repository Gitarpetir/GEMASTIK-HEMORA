package com.gemastik.hemora.data.schedule.dto

import com.gemastik.hemora.domain.model.TtdSchedule
import com.google.firebase.Timestamp
import java.util.Date

data class TtdScheduleDto(
    val scheduleId: String = "",
    val schoolId: String = "",
    val date: Timestamp? = null,
    val time: String = "",
    val createdAt: Long = 0L
) {
    fun toDomain(docId: String = scheduleId): TtdSchedule {
        return TtdSchedule(
            scheduleId = if (docId.isNotEmpty()) docId else scheduleId,
            schoolId = schoolId,
            date = date?.toDate() ?: Date(),
            time = time
        )
    }
}

fun TtdSchedule.toDto(): TtdScheduleDto {
    return TtdScheduleDto(
        scheduleId = scheduleId,
        schoolId = schoolId,
        date = Timestamp(date),
        time = time,
        createdAt = System.currentTimeMillis()
    )
}
