package com.gemastik.hemora.domain.consumption.usecase

import com.gemastik.hemora.domain.consumption.repository.ConsumptionRepository
import com.gemastik.hemora.domain.model.TtdSchedule
import com.gemastik.hemora.domain.schedule.repository.ScheduleRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class GetActiveReminderUseCase(
    private val scheduleRepository: ScheduleRepository,
    private val consumptionRepository: ConsumptionRepository
) {
    operator fun invoke(schoolId: String, userId: String, date: Date = Date()): Flow<Result<TtdSchedule?>> {
        return scheduleRepository.getScheduleByDate(schoolId, date).flatMapLatest { scheduleResult ->
            if (scheduleResult.isSuccess) {
                val schedule = scheduleResult.getOrNull()
                if (schedule != null) {
                    consumptionRepository.getConsumptionBySchedule(userId, schedule.scheduleId).map { consumptionResult ->
                        if (consumptionResult.isSuccess) {
                            val consumption = consumptionResult.getOrNull()
                            if (consumption == null) {
                                Result.success(schedule)
                            } else {
                                Result.success(null)
                            }
                        } else {
                            Result.failure(consumptionResult.exceptionOrNull() ?: Exception("Unknown error"))
                        }
                    }
                } else {
                    flowOf(Result.success(null))
                }
            } else {
                flowOf(Result.failure(scheduleResult.exceptionOrNull() ?: Exception("Unknown error")))
            }
        }
    }
}
