package com.gemastik.hemora.domain.consumption.usecase

import com.gemastik.hemora.domain.consumption.repository.ConsumptionRepository
import com.gemastik.hemora.domain.model.ComplianceStatistics
import kotlinx.coroutines.flow.Flow

class GetUserStatisticsUseCase(
    private val consumptionRepository: ConsumptionRepository
) {
    operator fun invoke(userId: String): Flow<Result<ComplianceStatistics>> {
        return consumptionRepository.getComplianceStatistics(userId)
    }
}
