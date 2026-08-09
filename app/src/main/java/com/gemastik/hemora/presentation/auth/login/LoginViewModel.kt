package com.gemastik.hemora.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gemastik.hemora.domain.auth.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("Email dan password tidak boleh kosong")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            loginUseCase(email, password).collect { result ->
                result.onSuccess { user ->
                    _uiState.value = LoginUiState.Success(user.name)
                }.onFailure { error ->
                    _uiState.value = LoginUiState.Error(error.message ?: "Terjadi kesalahan")
                }
            }
        }
    }
}

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val userName: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
