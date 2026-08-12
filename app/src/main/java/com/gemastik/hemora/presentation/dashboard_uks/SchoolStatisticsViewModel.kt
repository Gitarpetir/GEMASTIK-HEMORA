package com.gemastik.hemora.presentation.dashboard_uks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gemastik.hemora.domain.auth.repository.AuthRepository
import com.gemastik.hemora.domain.dashboard.model.ScheduleComplianceModel
import com.gemastik.hemora.domain.dashboard.usecase.GetSchoolStatisticsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

sealed class SchoolStatisticsUiState {
    object Loading : SchoolStatisticsUiState()
    data class Success(val stats: List<ScheduleComplianceModel>) : SchoolStatisticsUiState()
    data class Error(val message: String) : SchoolStatisticsUiState()
}

class SchoolStatisticsViewModel(
    private val authRepository: AuthRepository,
    private val getSchoolStatisticsUseCase: GetSchoolStatisticsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SchoolStatisticsUiState>(SchoolStatisticsUiState.Loading)
    val uiState: StateFlow<SchoolStatisticsUiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    fun refreshStatistics() {
        _uiState.value = SchoolStatisticsUiState.Loading
        loadStatistics()
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            authRepository.getCurrentUser().flatMapLatest { userResult ->
                val user = userResult.getOrNull()
                if (user != null) {
                    getSchoolStatisticsUseCase(user.schoolId)
                } else {
                    kotlinx.coroutines.flow.flowOf(Result.failure(Exception("Sesi tidak valid")))
                }
            }.collectLatest { result ->
                if (result.isSuccess) {
                    _uiState.value = SchoolStatisticsUiState.Success(result.getOrNull() ?: emptyList())
                } else {
                    _uiState.value = SchoolStatisticsUiState.Error(result.exceptionOrNull()?.message ?: "Gagal memuat statistik")
                }
            }
        }
    }
}
