package com.gemastik.hemora.presentation.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gemastik.hemora.domain.auth.repository.AuthRepository
import com.gemastik.hemora.domain.schedule.model.TtdSchedule
import com.gemastik.hemora.domain.schedule.usecase.ManageScheduleUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ScheduleViewModel(
    private val authRepository: AuthRepository,
    private val useCases: ManageScheduleUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScheduleUiState>(ScheduleUiState.Loading)
    val uiState: StateFlow<ScheduleUiState> = _uiState

    private var currentSchoolId: String = ""

    init {
        loadSchedules()
    }

    fun loadSchedules() {
        viewModelScope.launch {
            _uiState.value = ScheduleUiState.Loading
            authRepository.getCurrentUser().collect { authResult ->
                authResult.onSuccess { user ->
                    if (user != null) {
                        currentSchoolId = user.schoolId
                        useCases.getSchedules(currentSchoolId).collect { scheduleResult ->
                            scheduleResult.onSuccess { schedules ->
                                _uiState.value = ScheduleUiState.Success(schedules)
                            }.onFailure { error ->
                                _uiState.value = ScheduleUiState.Error(error.message ?: "Gagal memuat jadwal")
                            }
                        }
                    } else {
                        _uiState.value = ScheduleUiState.Error("Sesi berakhir")
                    }
                }.onFailure {
                    _uiState.value = ScheduleUiState.Error("Gagal memuat data user")
                }
            }
        }
    }

    fun addSchedule(date: String, time: String) {
        viewModelScope.launch {
            val newSchedule = TtdSchedule(
                scheduleId = "",
                schoolId = currentSchoolId,
                date = date,
                time = time,
                createdAt = System.currentTimeMillis()
            )
            useCases.addSchedule(newSchedule).collect { result ->
                result.onSuccess {
                    loadSchedules()
                }
            }
        }
    }

    fun updateSchedule(scheduleId: String, date: String, time: String) {
        viewModelScope.launch {
            val updatedSchedule = TtdSchedule(
                scheduleId = scheduleId,
                schoolId = currentSchoolId,
                date = date,
                time = time,
                createdAt = System.currentTimeMillis()
            )
            useCases.updateSchedule(updatedSchedule).collect { result ->
                result.onSuccess {
                    loadSchedules()
                }
            }
        }
    }

    fun deleteSchedule(scheduleId: String) {
        viewModelScope.launch {
            useCases.deleteSchedule(scheduleId).collect { result ->
                result.onSuccess {
                    loadSchedules()
                }
            }
        }
    }
}

sealed class ScheduleUiState {
    object Loading : ScheduleUiState()
    data class Success(val schedules: List<TtdSchedule>) : ScheduleUiState()
    data class Error(val message: String) : ScheduleUiState()
}
