package com.gemastik.hemora.presentation.consumption.tracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gemastik.hemora.domain.auth.repository.AuthRepository
import com.gemastik.hemora.domain.consumption.usecase.ConfirmTtdConsumptionUseCase
import com.gemastik.hemora.domain.consumption.usecase.GetActiveReminderUseCase
import com.gemastik.hemora.domain.model.TtdSchedule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

data class TrackerUiState(
    val isLoading: Boolean = true,
    val activeSchedule: TtdSchedule? = null,
    val error: String? = null,
    val confirmSuccess: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class TrackerViewModel(
    private val authRepository: AuthRepository,
    private val getActiveReminderUseCase: GetActiveReminderUseCase,
    private val confirmTtdConsumptionUseCase: ConfirmTtdConsumptionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrackerUiState())
    val uiState: StateFlow<TrackerUiState> = _uiState.asStateFlow()
    
    private var currentUserId: String? = null

    init {
        loadActiveReminder()
    }

    private fun loadActiveReminder() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            authRepository.getCurrentUser().flatMapLatest { userResult ->
                val user = userResult.getOrNull()
                if (user != null) {
                    currentUserId = user.userId
                    getActiveReminderUseCase(schoolId = user.schoolId, userId = user.userId)
                } else {
                    kotlinx.coroutines.flow.flowOf(Result.failure(Exception("Sesi pengguna tidak valid")))
                }
            }.collectLatest { reminderResult ->
                if (reminderResult.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        activeSchedule = reminderResult.getOrNull()
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = reminderResult.exceptionOrNull()?.message
                    )
                }
            }
        }
    }

    fun confirmConsumption() {
        val schedule = _uiState.value.activeSchedule ?: return
        val userId = currentUserId ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = confirmTtdConsumptionUseCase(userId, schedule.scheduleId)
            
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    confirmSuccess = true,
                    activeSchedule = null
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Gagal mengonfirmasi"
                )
            }
        }
    }
}
