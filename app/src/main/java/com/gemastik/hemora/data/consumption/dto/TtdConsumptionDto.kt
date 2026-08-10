package com.gemastik.hemora.data.consumption.dto

import com.gemastik.hemora.domain.model.ConsumptionStatus
import com.gemastik.hemora.domain.model.TtdConsumption
import com.google.firebase.Timestamp
import java.util.Date

data class TtdConsumptionDto(
    val consumptionId: String = "",
    val userId: String = "",
    val scheduleId: String = "",
    val status: String = ConsumptionStatus.BELUM_KONSUMSI.name,
    val confirmedAt: Timestamp? = null
) {
    fun toDomain(): TtdConsumption {
        return TtdConsumption(
            consumptionId = consumptionId,
            userId = userId,
            scheduleId = scheduleId,
            status = try {
                ConsumptionStatus.valueOf(status)
            } catch (e: Exception) {
                ConsumptionStatus.BELUM_KONSUMSI
            },
            confirmedAt = confirmedAt?.toDate()
        )
    }
}
