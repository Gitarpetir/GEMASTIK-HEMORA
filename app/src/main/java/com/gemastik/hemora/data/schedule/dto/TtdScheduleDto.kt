package com.gemastik.hemora.data.schedule.dto

import com.gemastik.hemora.domain.schedule.model.TtdSchedule

data class TtdScheduleDto(
    val schoolId: String = "",
    val date: String = "",
    val time: String = "",
    val createdAt: Long = 0L
) {
    fun toDomain(scheduleId: String): TtdSchedule {
        return TtdSchedule(
            scheduleId = scheduleId,
            schoolId = schoolId,
            date = date,
            time = time,
            createdAt = createdAt
        )
    }
}

fun TtdSchedule.toDto(): TtdScheduleDto {
    return TtdScheduleDto(
        schoolId = schoolId,
        date = date,
        time = time,
        createdAt = createdAt
    )
}
