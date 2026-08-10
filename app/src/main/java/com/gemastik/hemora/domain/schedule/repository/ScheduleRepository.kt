package com.gemastik.hemora.domain.schedule.repository

import com.gemastik.hemora.domain.schedule.model.TtdSchedule
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    fun getSchedules(schoolId: String): Flow<Result<List<TtdSchedule>>>
    fun addSchedule(schedule: TtdSchedule): Flow<Result<Unit>>
    fun updateSchedule(schedule: TtdSchedule): Flow<Result<Unit>>
    fun deleteSchedule(scheduleId: String): Flow<Result<Unit>>
}
