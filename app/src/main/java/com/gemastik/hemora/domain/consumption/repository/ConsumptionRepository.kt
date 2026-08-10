package com.gemastik.hemora.domain.consumption.repository

import com.gemastik.hemora.domain.model.ComplianceStatistics
import com.gemastik.hemora.domain.model.ConsumptionStatus
import com.gemastik.hemora.domain.model.TtdConsumption
import kotlinx.coroutines.flow.Flow

interface ConsumptionRepository {
    fun getConsumptionHistory(userId: String): Flow<Result<List<TtdConsumption>>>
    fun getConsumptionBySchedule(userId: String, scheduleId: String): Flow<Result<TtdConsumption?>>
    suspend fun confirmConsumption(userId: String, scheduleId: String, status: ConsumptionStatus): Result<Unit>
}
