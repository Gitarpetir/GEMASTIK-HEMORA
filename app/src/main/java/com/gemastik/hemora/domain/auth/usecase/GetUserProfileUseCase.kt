package com.gemastik.hemora.domain.auth.usecase

import com.gemastik.hemora.domain.auth.repository.AuthRepository
import com.gemastik.hemora.domain.model.User
import kotlinx.coroutines.flow.Flow

class GetUserProfileUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<Result<User?>> {
        return authRepository.getCurrentUser()
    }
}
