package com.gemastik.hemora.domain.consumption.usecase

import com.gemastik.hemora.domain.consumption.repository.ConsumptionRepository
import com.gemastik.hemora.domain.model.ConsumptionStatus

class ConfirmTtdConsumptionUseCase(
    private val consumptionRepository: ConsumptionRepository
) {
    suspend operator fun invoke(userId: String, scheduleId: String): Result<Unit> {
        return consumptionRepository.confirmConsumption(userId, scheduleId, ConsumptionStatus.SUDAH_KONSUMSI)
    }
}
