package com.gemastik.hemora.domain.consumption.usecase

import com.gemastik.hemora.domain.consumption.repository.ConsumptionRepository
import com.gemastik.hemora.domain.model.ComplianceStatistics
import com.gemastik.hemora.domain.model.ConsumptionStatus
import com.gemastik.hemora.domain.model.TtdSchedule
import com.gemastik.hemora.domain.model.TtdConsumption
import com.gemastik.hemora.domain.schedule.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

class GetUserStatisticsUseCase(
    private val scheduleRepository: ScheduleRepository,
    private val consumptionRepository: ConsumptionRepository
) {
    operator fun invoke(userId: String, schoolId: String): Flow<Result<ComplianceStatistics>> {
        val schedulesFlow = scheduleRepository.getSchedules(schoolId)
        val consumptionFlow = consumptionRepository.getConsumptionHistory(userId)

        return combine(schedulesFlow, consumptionFlow) { schedulesResult, consumptionResult ->
            if (schedulesResult.isFailure) {
                return@combine Result.failure<ComplianceStatistics>(
                    schedulesResult.exceptionOrNull() ?: Exception("Failed to get schedules")
                )
            }
            if (consumptionResult.isFailure) {
                return@combine Result.failure<ComplianceStatistics>(
                    consumptionResult.exceptionOrNull() ?: Exception("Failed to get consumptions")
                )
            }

            val schedules = schedulesResult.getOrNull() ?: emptyList()
            val consumptions = consumptionResult.getOrNull() ?: emptyList()

            val totalSchedules = schedules.size
            val confirmedConsumptions = consumptions.filter { it.status == ConsumptionStatus.SUDAH_KONSUMSI }
            val uniqueConfirmed = confirmedConsumptions.distinctBy { it.scheduleId }
            val totalConsumed = uniqueConfirmed.size
            val totalMissed = totalSchedules - totalConsumed

            val percentage = if (totalSchedules > 0) {
                (totalConsumed.toFloat() / totalSchedules.toFloat()) * 100f
            } else 0f

            Result.success(
                ComplianceStatistics(
                    totalSchedules = totalSchedules,
                    totalConsumed = totalConsumed,
                    totalMissed = if (totalMissed < 0) 0 else totalMissed,
                    compliancePercentage = percentage
                )
            )
        }
    }
}
