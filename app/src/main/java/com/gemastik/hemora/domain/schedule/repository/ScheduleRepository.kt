package com.gemastik.hemora.domain.schedule.repository

import com.gemastik.hemora.domain.model.TtdSchedule
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface ScheduleRepository {
    fun getScheduleByDate(schoolId: String, date: Date): Flow<Result<TtdSchedule?>>
    suspend fun createSchedule(schedule: TtdSchedule): Result<Unit>
}
