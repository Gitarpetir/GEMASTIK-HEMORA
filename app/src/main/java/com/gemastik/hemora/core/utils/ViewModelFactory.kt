package com.gemastik.hemora.core.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.gemastik.hemora.HemoraApplication
import com.gemastik.hemora.presentation.auth.login.LoginViewModel
import com.gemastik.hemora.presentation.auth.register.RegisterViewModel

object ViewModelFactory {
    val Factory = viewModelFactory {
        initializer {
            val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HemoraApplication)
            LoginViewModel(application.container.loginUseCase)
        }
        initializer {
            val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HemoraApplication)
            RegisterViewModel(application.container.registerRemajaPutriUseCase)
        }
    }
}
