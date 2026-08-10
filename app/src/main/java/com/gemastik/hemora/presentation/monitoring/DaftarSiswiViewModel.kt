package com.gemastik.hemora.presentation.monitoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gemastik.hemora.domain.auth.repository.AuthRepository
import com.gemastik.hemora.domain.monitoring.usecase.GetStudentsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DaftarSiswiViewModel(
    private val authRepository: AuthRepository,
    private val getStudentsUseCase: GetStudentsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DaftarSiswiState>(DaftarSiswiState.Loading)
    val uiState: StateFlow<DaftarSiswiState> = _uiState

    init {
        loadStudents()
    }

    private fun loadStudents() {
        viewModelScope.launch {
            _uiState.value = DaftarSiswiState.Loading
            
            authRepository.getCurrentUser().collect { authResult ->
                val user = authResult.getOrNull()
                if (user != null && user.role == "UKS") {
                    val schoolId = user.schoolId
                    getStudentsUseCase(schoolId).collect { studentsResult ->
                        val students = studentsResult.getOrNull()
                        if (studentsResult.isSuccess && students != null) {
                            _uiState.value = DaftarSiswiState.Success(students)
                        } else {
                            _uiState.value = DaftarSiswiState.Error(studentsResult.exceptionOrNull()?.message ?: "Gagal memuat daftar siswi.")
                        }
                    }
                } else if (authResult.isFailure) {
                    _uiState.value = DaftarSiswiState.Error(authResult.exceptionOrNull()?.message ?: "Gagal memverifikasi akses pengguna.")
                } else {
                    _uiState.value = DaftarSiswiState.Error("Akses ditolak: Hanya UKS yang dapat mengakses halaman ini.")
                }
            }
        }
    }
}
