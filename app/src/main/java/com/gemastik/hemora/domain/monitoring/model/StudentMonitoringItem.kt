package com.gemastik.hemora.domain.monitoring.model

import com.gemastik.hemora.domain.model.TtdConsumption
import com.gemastik.hemora.domain.model.TtdSchedule

data class StudentMonitoringItem(
    val schedule: TtdSchedule,
    val consumption: TtdConsumption?
)
