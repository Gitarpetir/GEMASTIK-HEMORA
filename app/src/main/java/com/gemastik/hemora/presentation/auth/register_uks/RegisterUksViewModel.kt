package com.gemastik.hemora.presentation.auth.register_uks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gemastik.hemora.domain.auth.usecase.RegisterUksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterUksViewModel(
    private val registerUksUseCase: RegisterUksUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUksUiState>(RegisterUksUiState.Idle)
    val uiState: StateFlow<RegisterUksUiState> = _uiState

    fun register(name: String, email: String, password: String, schoolName: String, activationCode: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank() || schoolName.isBlank() || activationCode.isBlank()) {
            _uiState.value = RegisterUksUiState.Error("Semua data harus diisi")
            return
        }

        viewModelScope.launch {
            _uiState.value = RegisterUksUiState.Loading
            registerUksUseCase(name, email, password, schoolName, activationCode).collect { result ->
                result.onSuccess {
                    _uiState.value = RegisterUksUiState.Success
                }.onFailure { error ->
                    _uiState.value = RegisterUksUiState.Error(error.message ?: "Terjadi kesalahan")
                }
            }
        }
    }
}

sealed class RegisterUksUiState {
    object Idle : RegisterUksUiState()
    object Loading : RegisterUksUiState()
    object Success : RegisterUksUiState()
    data class Error(val message: String) : RegisterUksUiState()
}
