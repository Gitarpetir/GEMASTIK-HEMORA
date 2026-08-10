package com.gemastik.hemora.presentation.school

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gemastik.hemora.domain.auth.repository.AuthRepository
import com.gemastik.hemora.domain.school.usecase.GetSchoolInfoUseCase
import com.gemastik.hemora.domain.school.usecase.RegenerateSchoolCodeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SchoolViewModel(
    private val authRepository: AuthRepository,
    private val getSchoolInfoUseCase: GetSchoolInfoUseCase,
    private val regenerateSchoolCodeUseCase: RegenerateSchoolCodeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SchoolState())
    val uiState: StateFlow<SchoolState> = _uiState

    private var currentSchoolId: String? = null

    init {
        loadSchoolInfo()
    }

    fun loadSchoolInfo() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { result ->
                result.onSuccess { user ->
                    if (user != null) {
                        currentSchoolId = user.schoolId
                        getSchoolInfoUseCase(user.schoolId).collect { schoolResult ->
                            schoolResult.onSuccess { school ->
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        schoolName = school.schoolName,
                                        schoolCode = school.schoolCode
                                    )
                                }
                            }.onFailure { error ->
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        error = error.message ?: "Gagal memuat data sekolah"
                                    )
                                }
                            }
                        }
                    } else {
                        _uiState.update {
                            it.copy(isLoading = false, error = "Sesi telah berakhir")
                        }
                    }
                }.onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "Gagal mendapatkan sesi user")
                    }
                }
            }
        }
    }

    fun regenerateCode() {
        val schoolId = currentSchoolId ?: return
        
        _uiState.update { it.copy(isRegenerating = true, error = null) }
        viewModelScope.launch {
            regenerateSchoolCodeUseCase(schoolId).collect { result ->
                result.onSuccess { newCode ->
                    _uiState.update {
                        it.copy(
                            isRegenerating = false,
                            schoolCode = newCode
                        )
                    }
                }.onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isRegenerating = false,
                            error = error.message ?: "Gagal memperbarui kode"
                        )
                    }
                }
            }
        }
    }
}
