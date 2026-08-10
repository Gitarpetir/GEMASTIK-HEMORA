package com.gemastik.hemora.domain.consumption.usecase

import com.gemastik.hemora.domain.consumption.repository.ConsumptionRepository
import com.gemastik.hemora.domain.model.TtdConsumption
import kotlinx.coroutines.flow.Flow

class GetConsumptionHistoryUseCase(
    private val consumptionRepository: ConsumptionRepository
) {
    operator fun invoke(userId: String): Flow<Result<List<TtdConsumption>>> {
        return consumptionRepository.getConsumptionHistory(userId)
    }
}
