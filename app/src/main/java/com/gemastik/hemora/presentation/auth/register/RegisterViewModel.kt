package com.gemastik.hemora.presentation.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gemastik.hemora.domain.auth.usecase.RegisterRemajaPutriUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUseCase: RegisterRemajaPutriUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun register(name: String, email: String, password: String, schoolCode: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank() || schoolCode.isBlank()) {
            _uiState.value = RegisterUiState.Error("Semua data harus diisi")
            return
        }

        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading
            registerUseCase(name, email, password, schoolCode).collect { result ->
                result.onSuccess {
                    _uiState.value = RegisterUiState.Success
                }.onFailure { error ->
                    _uiState.value = RegisterUiState.Error(error.message ?: "Terjadi kesalahan")
                }
            }
        }
    }
}

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    object Success : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}
