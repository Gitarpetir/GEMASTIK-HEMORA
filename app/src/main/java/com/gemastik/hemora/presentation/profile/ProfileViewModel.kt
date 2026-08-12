package com.gemastik.hemora.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gemastik.hemora.domain.auth.repository.AuthRepository
import com.gemastik.hemora.domain.auth.usecase.GetUserProfileUseCase
import com.gemastik.hemora.domain.auth.usecase.UpdateUserProfileUseCase
import com.gemastik.hemora.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState

    private val _updateState = MutableStateFlow<ProfileUpdateState>(ProfileUpdateState.Idle)
    val updateState: StateFlow<ProfileUpdateState> = _updateState

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            getUserProfileUseCase().collect { result ->
                if (result.isSuccess) {
                    val user = result.getOrNull()
                    if (user != null) {
                        _uiState.value = ProfileUiState.Success(user)
                    } else {
                        _uiState.value = ProfileUiState.Error("Sesi tidak valid")
                    }
                } else {
                    _uiState.value = ProfileUiState.Error(result.exceptionOrNull()?.message ?: "Gagal memuat profil")
                }
            }
        }
    }

    fun updateName(userId: String, newName: String) {
        if (newName.isBlank()) {
            _updateState.value = ProfileUpdateState.Error("Nama tidak boleh kosong")
            return
        }
        
        _updateState.value = ProfileUpdateState.Loading
        viewModelScope.launch {
            updateUserProfileUseCase(userId, newName).collect { result ->
                if (result.isSuccess) {
                    _updateState.value = ProfileUpdateState.Success
                    // Profile will be auto-updated because of the snapshot listener in getCurrentUser
                } else {
                    _updateState.value = ProfileUpdateState.Error(result.exceptionOrNull()?.message ?: "Gagal memperbarui profil")
                }
            }
        }
    }

    fun resetUpdateState() {
        _updateState.value = ProfileUpdateState.Idle
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onLogoutSuccess()
        }
    }
}

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val user: User) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

sealed class ProfileUpdateState {
    object Idle : ProfileUpdateState()
    object Loading : ProfileUpdateState()
    object Success : ProfileUpdateState()
    data class Error(val message: String) : ProfileUpdateState()
}
