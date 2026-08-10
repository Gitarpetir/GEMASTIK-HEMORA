package com.gemastik.hemora.domain.model

import java.util.Date

data class TtdConsumption(
    val consumptionId: String,
    val userId: String,
    val scheduleId: String,
    val status: ConsumptionStatus,
    val confirmedAt: Date?
)
