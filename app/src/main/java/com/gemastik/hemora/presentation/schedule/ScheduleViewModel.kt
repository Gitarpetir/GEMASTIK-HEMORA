package com.gemastik.hemora.presentation.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gemastik.hemora.domain.auth.repository.AuthRepository
import com.gemastik.hemora.domain.model.TtdSchedule
import com.gemastik.hemora.domain.schedule.usecase.ManageScheduleUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

class ScheduleViewModel(
    private val authRepository: AuthRepository,
    private val useCases: ManageScheduleUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScheduleUiState>(ScheduleUiState.Loading)
    val uiState: StateFlow<ScheduleUiState> = _uiState

    private var currentSchoolId: String = ""
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

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

    fun addSchedule(dateStr: String, time: String) {
        viewModelScope.launch {
            val dateObj = try { dateFormat.parse(dateStr) ?: Date() } catch (e: Exception) { Date() }
            val newSchedule = TtdSchedule(
                scheduleId = "",
                schoolId = currentSchoolId,
                date = dateObj,
                time = time
            )
            useCases.addSchedule(newSchedule).collect { result ->
                result.onSuccess {
                    loadSchedules()
                }
            }
        }
    }

    fun updateSchedule(scheduleId: String, dateStr: String, time: String) {
        viewModelScope.launch {
            val dateObj = try { dateFormat.parse(dateStr) ?: Date() } catch (e: Exception) { Date() }
            val updatedSchedule = TtdSchedule(
                scheduleId = scheduleId,
                schoolId = currentSchoolId,
                date = dateObj,
                time = time
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
