package com.gemastik.hemora.domain.auth.usecase

import com.gemastik.hemora.domain.auth.repository.AuthRepository
import com.gemastik.hemora.domain.model.User
import kotlinx.coroutines.flow.Flow

class LoginUseCase(private val repository: AuthRepository) {
    operator fun invoke(email: String, password: String): Flow<Result<User>> {
        return repository.login(email, password)
    }
}
