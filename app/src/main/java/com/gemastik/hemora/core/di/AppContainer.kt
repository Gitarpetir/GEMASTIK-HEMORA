package com.gemastik.hemora.core.di

import com.gemastik.hemora.data.auth.repository.AuthRepositoryImpl
import com.gemastik.hemora.domain.auth.repository.AuthRepository
import com.gemastik.hemora.domain.auth.usecase.LoginUseCase
import com.gemastik.hemora.domain.auth.usecase.RegisterRemajaPutriUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

interface AppContainer {
    val authRepository: AuthRepository
    val loginUseCase: LoginUseCase
    val registerRemajaPutriUseCase: RegisterRemajaPutriUseCase
}

class DefaultAppContainer : AppContainer {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(firebaseAuth, firestore)
    }

    override val loginUseCase: LoginUseCase by lazy {
        LoginUseCase(authRepository)
    }

    override val registerRemajaPutriUseCase: RegisterRemajaPutriUseCase by lazy {
        RegisterRemajaPutriUseCase(authRepository)
    }
}
