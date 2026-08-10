package com.gemastik.hemora.presentation.dashboard_uks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gemastik.hemora.domain.auth.repository.AuthRepository
import com.gemastik.hemora.domain.school.usecase.GetSchoolInfoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardUksViewModel(
    private val authRepository: AuthRepository,
    private val getSchoolInfoUseCase: GetSchoolInfoUseCase
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
                        getSchoolInfoUseCase(user.schoolId).collect { schoolResult ->
                            schoolResult.onSuccess { school ->
                                _uiState.value = DashboardUksUiState.Success(
                                    schoolName = school.schoolName,
                                    schoolCode = school.schoolCode
                                )
                            }.onFailure { error ->
                                _uiState.value = DashboardUksUiState.Error(error.message ?: "Gagal memuat data sekolah")
                            }
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
}

sealed class DashboardUksUiState {
    object Loading : DashboardUksUiState()
    data class Success(val schoolName: String, val schoolCode: String) : DashboardUksUiState()
    data class Error(val message: String) : DashboardUksUiState()
}
