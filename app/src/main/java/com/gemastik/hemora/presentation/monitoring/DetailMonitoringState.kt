package com.gemastik.hemora.presentation.monitoring

import com.gemastik.hemora.domain.monitoring.model.StudentMonitoringItem

sealed class DetailMonitoringState {
    object Loading : DetailMonitoringState()
    data class Success(val items: List<StudentMonitoringItem>) : DetailMonitoringState()
    data class Error(val message: String) : DetailMonitoringState()
}
