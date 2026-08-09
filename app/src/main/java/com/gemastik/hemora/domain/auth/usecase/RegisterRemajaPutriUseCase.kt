package com.gemastik.hemora.domain.auth.usecase

import com.gemastik.hemora.domain.auth.repository.AuthRepository
import com.gemastik.hemora.domain.model.User
import kotlinx.coroutines.flow.Flow

class RegisterRemajaPutriUseCase(private val repository: AuthRepository) {
    operator fun invoke(name: String, email: String, password: String, schoolCode: String): Flow<Result<User>> {
        return repository.registerRemajaPutri(name, email, password, schoolCode)
    }
}
