package com.gemastik.hemora.core.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.gemastik.hemora.HemoraApplication
import com.gemastik.hemora.presentation.auth.login.LoginViewModel
import com.gemastik.hemora.presentation.auth.register.RegisterViewModel
import com.gemastik.hemora.presentation.consumption.history.HistoryViewModel
import com.gemastik.hemora.presentation.consumption.statistics.StatisticsViewModel
import com.gemastik.hemora.presentation.consumption.tracker.TrackerViewModel
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
        initializer {
            val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HemoraApplication)
            com.gemastik.hemora.presentation.auth.register_uks.RegisterUksViewModel(application.container.registerUksUseCase)
        }
        initializer {
            val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HemoraApplication)
            com.gemastik.hemora.presentation.dashboard_uks.DashboardUksViewModel(
                application.container.authRepository,
                application.container.getSchoolInfoUseCase
            )
        }
        initializer {
            val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HemoraApplication)
            TrackerViewModel(
                application.container.authRepository,
                application.container.getActiveReminderUseCase,
                application.container.confirmTtdConsumptionUseCase
            )
        }
        initializer {
            val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HemoraApplication)
            com.gemastik.hemora.presentation.schedule.ScheduleViewModel(
                application.container.authRepository,
                application.container.manageScheduleUseCases
            )
        }
        initializer {
            val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HemoraApplication)
            HistoryViewModel(
                application.container.authRepository,
                application.container.getConsumptionHistoryUseCase
            )
        }
        initializer {
            val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HemoraApplication)
            StatisticsViewModel(
                application.container.authRepository,
                application.container.getUserStatisticsUseCase
            )
        }
        initializer {
            val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HemoraApplication)
            com.gemastik.hemora.presentation.school.SchoolViewModel(
                application.container.authRepository,
                application.container.getSchoolInfoUseCase,
                application.container.regenerateSchoolCodeUseCase
            )
        }
    }
}
