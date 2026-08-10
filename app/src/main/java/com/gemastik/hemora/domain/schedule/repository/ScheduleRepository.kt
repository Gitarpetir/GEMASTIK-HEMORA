package com.gemastik.hemora.domain.schedule.repository

import com.gemastik.hemora.domain.model.TtdSchedule
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface ScheduleRepository {
    fun getSchedules(schoolId: String): Flow<Result<List<TtdSchedule>>>
    fun addSchedule(schedule: TtdSchedule): Flow<Result<Unit>>
    fun updateSchedule(schedule: TtdSchedule): Flow<Result<Unit>>
    fun deleteSchedule(scheduleId: String): Flow<Result<Unit>>
    
    fun getScheduleByDate(schoolId: String, date: Date): Flow<Result<TtdSchedule?>>
    suspend fun createSchedule(schedule: TtdSchedule): Result<Unit>
}
