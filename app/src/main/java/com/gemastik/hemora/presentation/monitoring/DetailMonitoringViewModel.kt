package com.gemastik.hemora.presentation.monitoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gemastik.hemora.domain.auth.repository.AuthRepository
import com.gemastik.hemora.domain.monitoring.usecase.GetStudentMonitoringUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailMonitoringViewModel(
    private val authRepository: AuthRepository,
    private val getStudentMonitoringUseCase: GetStudentMonitoringUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailMonitoringState>(DetailMonitoringState.Loading)
    val uiState: StateFlow<DetailMonitoringState> = _uiState

    fun loadStudentData(studentId: String) {
        viewModelScope.launch {
            _uiState.value = DetailMonitoringState.Loading
            
            authRepository.getCurrentUser().collect { authResult ->
                val user = authResult.getOrNull()
                if (user != null && user.role == "UKS") {
                    val schoolId = user.schoolId
                    getStudentMonitoringUseCase(schoolId, studentId).collect { monitoringResult ->
                        val items = monitoringResult.getOrNull()
                        if (monitoringResult.isSuccess && items != null) {
                            _uiState.value = DetailMonitoringState.Success(items)
                        } else {
                            _uiState.value = DetailMonitoringState.Error(
                                monitoringResult.exceptionOrNull()?.message ?: "Gagal memuat detail monitoring."
                            )
                        }
                    }
                } else if (authResult.isFailure) {
                    _uiState.value = DetailMonitoringState.Error(
                        authResult.exceptionOrNull()?.message ?: "Gagal memverifikasi sesi."
                    )
                } else {
                    _uiState.value = DetailMonitoringState.Error("Akses ditolak: Hanya UKS yang dapat mengakses halaman ini.")
                }
            }
        }
    }
}
