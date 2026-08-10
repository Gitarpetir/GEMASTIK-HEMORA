package com.gemastik.hemora.domain.monitoring.usecase

import com.gemastik.hemora.domain.consumption.repository.ConsumptionRepository
import com.gemastik.hemora.domain.monitoring.model.StudentMonitoringItem
import com.gemastik.hemora.domain.schedule.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetStudentMonitoringUseCase(
    private val scheduleRepository: ScheduleRepository,
    private val consumptionRepository: ConsumptionRepository
) {
    operator fun invoke(schoolId: String, studentId: String): Flow<Result<List<StudentMonitoringItem>>> {
        val schedulesFlow = scheduleRepository.getSchedules(schoolId)
        val consumptionsFlow = consumptionRepository.getConsumptionHistory(studentId)

        return combine(schedulesFlow, consumptionsFlow) { schedulesResult, consumptionsResult ->
            if (schedulesResult.isFailure) {
                return@combine Result.failure(schedulesResult.exceptionOrNull() ?: Exception("Gagal memuat jadwal"))
            }
            if (consumptionsResult.isFailure) {
                return@combine Result.failure(consumptionsResult.exceptionOrNull() ?: Exception("Gagal memuat histori konsumsi"))
            }

            val schedules = schedulesResult.getOrNull() ?: emptyList()
            val consumptions = consumptionsResult.getOrNull() ?: emptyList()

            // Sort schedules by date descending (latest first)
            val sortedSchedules = schedules.sortedByDescending { it.date }

            val monitoringItems = sortedSchedules.map { schedule ->
                val matchingConsumption = consumptions.find { it.scheduleId == schedule.scheduleId }
                StudentMonitoringItem(schedule, matchingConsumption)
            }

            Result.success(monitoringItems)
        }
    }
}
