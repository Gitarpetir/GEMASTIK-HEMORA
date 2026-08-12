package com.gemastik.hemora.domain.auth.repository

import com.gemastik.hemora.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(email: String, password: String): Flow<Result<User>>
    fun registerRemajaPutri(name: String, email: String, password: String, schoolCode: String): Flow<Result<User>>
    fun registerUks(name: String, email: String, password: String, schoolName: String, activationCode: String): Flow<Result<User>>
    fun getCurrentUser(): Flow<Result<User?>>
    fun updateProfile(userId: String, name: String): Flow<Result<Unit>>
    suspend fun logout()
}
