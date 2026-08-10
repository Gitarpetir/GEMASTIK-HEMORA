package com.gemastik.hemora.presentation.consumption.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gemastik.hemora.domain.auth.repository.AuthRepository
import com.gemastik.hemora.domain.consumption.usecase.GetConsumptionHistoryUseCase
import com.gemastik.hemora.domain.model.TtdConsumption
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

data class HistoryUiState(
    val isLoading: Boolean = true,
    val history: List<TtdConsumption> = emptyList(),
    val error: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModel(
    private val authRepository: AuthRepository,
    private val getConsumptionHistoryUseCase: GetConsumptionHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            authRepository.getCurrentUser().flatMapLatest { userResult ->
                val user = userResult.getOrNull()
                if (user != null) {
                    getConsumptionHistoryUseCase(user.userId)
                } else {
                    kotlinx.coroutines.flow.flowOf(Result.failure(Exception("Sesi tidak valid")))
                }
            }.collectLatest { historyResult ->
                if (historyResult.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        history = historyResult.getOrNull() ?: emptyList()
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = historyResult.exceptionOrNull()?.message
                    )
                }
            }
        }
    }
}
