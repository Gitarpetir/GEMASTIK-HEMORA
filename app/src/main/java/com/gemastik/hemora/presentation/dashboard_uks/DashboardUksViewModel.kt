package com.gemastik.hemora.presentation.dashboard_uks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gemastik.hemora.domain.auth.repository.AuthRepository
import com.gemastik.hemora.domain.dashboard.model.DashboardSummaryModel
import com.gemastik.hemora.domain.school.usecase.GetSchoolInfoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class DashboardUksViewModel(
    private val authRepository: AuthRepository,
    private val getSchoolInfoUseCase: GetSchoolInfoUseCase,
    private val getSchoolSummaryUseCase: com.gemastik.hemora.domain.dashboard.usecase.GetSchoolSummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUksUiState>(DashboardUksUiState.Loading)
    val uiState: StateFlow<DashboardUksUiState> = _uiState

    init {
        loadSchoolInfo()
    }

    private fun loadSchoolInfo() {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { result ->
                result.onSuccess { user ->
                    if (user != null) {
                        val schoolInfoFlow = getSchoolInfoUseCase(user.schoolId)
                        val summaryFlow = getSchoolSummaryUseCase(user.schoolId)

                        combine(schoolInfoFlow, summaryFlow) { schoolResult, summaryResult ->
                            if (schoolResult.isSuccess) {
                                val school = schoolResult.getOrNull()!!
                                val summary = summaryResult.getOrNull()
                                DashboardUksUiState.Success(
                                    schoolName = school.schoolName,
                                    schoolCode = school.schoolCode,
                                    summary = summary
                                )
                            } else {
                                DashboardUksUiState.Error(schoolResult.exceptionOrNull()?.message ?: "Gagal memuat data sekolah")
                            }
                        }.collect { combinedState ->
                            _uiState.value = combinedState
                        }
                    } else {
                        _uiState.value = DashboardUksUiState.Error("Sesi telah berakhir")
                    }
                }.onFailure {
                    _uiState.value = DashboardUksUiState.Error(it.message ?: "Gagal mendapatkan sesi user")
                }
            }
        }
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onLogoutSuccess()
        }
    }
}

sealed class DashboardUksUiState {
    object Loading : DashboardUksUiState()
    data class Success(
        val schoolName: String, 
        val schoolCode: String,
        val summary: DashboardSummaryModel? = null
    ) : DashboardUksUiState()
    data class Error(val message: String) : DashboardUksUiState()
}
