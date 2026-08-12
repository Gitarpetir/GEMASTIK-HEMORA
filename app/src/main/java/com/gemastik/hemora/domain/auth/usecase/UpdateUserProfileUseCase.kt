package com.gemastik.hemora.domain.auth.usecase

import com.gemastik.hemora.domain.auth.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class UpdateUserProfileUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(userId: String, name: String): Flow<Result<Unit>> {
        return authRepository.updateProfile(userId, name)
    }
}
