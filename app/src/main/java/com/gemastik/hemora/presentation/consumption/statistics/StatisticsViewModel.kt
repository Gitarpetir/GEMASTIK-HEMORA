package com.gemastik.hemora.presentation.consumption.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gemastik.hemora.domain.auth.repository.AuthRepository
import com.gemastik.hemora.domain.consumption.usecase.GetUserStatisticsUseCase
import com.gemastik.hemora.domain.model.ComplianceStatistics
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

data class StatisticsUiState(
    val isLoading: Boolean = true,
    val stats: ComplianceStatistics? = null,
    val error: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class StatisticsViewModel(
    private val authRepository: AuthRepository,
    private val getUserStatisticsUseCase: GetUserStatisticsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            authRepository.getCurrentUser().flatMapLatest { userResult ->
                val user = userResult.getOrNull()
                if (user != null) {
                    getUserStatisticsUseCase(user.userId)
                } else {
                    kotlinx.coroutines.flow.flowOf(Result.failure(Exception("Sesi tidak valid")))
                }
            }.collectLatest { statsResult ->
                if (statsResult.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        stats = statsResult.getOrNull()
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = statsResult.exceptionOrNull()?.message
                    )
                }
            }
        }
    }
}
