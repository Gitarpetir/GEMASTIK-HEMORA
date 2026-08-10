package com.gemastik.hemora.core.di

import com.gemastik.hemora.data.auth.repository.AuthRepositoryImpl
import com.gemastik.hemora.domain.auth.repository.AuthRepository
import com.gemastik.hemora.domain.auth.usecase.LoginUseCase
import com.gemastik.hemora.domain.auth.usecase.RegisterRemajaPutriUseCase
import com.gemastik.hemora.domain.auth.usecase.RegisterUksUseCase
import com.gemastik.hemora.data.schedule.repository.ScheduleRepositoryImpl
import com.gemastik.hemora.data.consumption.repository.ConsumptionRepositoryImpl
import com.gemastik.hemora.domain.schedule.repository.ScheduleRepository
import com.gemastik.hemora.domain.consumption.repository.ConsumptionRepository
import com.gemastik.hemora.domain.consumption.usecase.GetActiveReminderUseCase
import com.gemastik.hemora.domain.consumption.usecase.ConfirmTtdConsumptionUseCase
import com.gemastik.hemora.domain.consumption.usecase.GetConsumptionHistoryUseCase
import com.gemastik.hemora.domain.consumption.usecase.GetUserStatisticsUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
interface AppContainer {
    val authRepository: AuthRepository
    val loginUseCase: LoginUseCase
    val registerRemajaPutriUseCase: RegisterRemajaPutriUseCase
    val registerUksUseCase: RegisterUksUseCase
    val scheduleRepository: ScheduleRepository
    val consumptionRepository: ConsumptionRepository
    val getActiveReminderUseCase: GetActiveReminderUseCase
    val confirmTtdConsumptionUseCase: ConfirmTtdConsumptionUseCase
    val getConsumptionHistoryUseCase: GetConsumptionHistoryUseCase
    val getUserStatisticsUseCase: GetUserStatisticsUseCase
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

    override val registerUksUseCase: RegisterUksUseCase by lazy {
        RegisterUksUseCase(authRepository)
    }

    override val scheduleRepository: ScheduleRepository by lazy {
        ScheduleRepositoryImpl(firestore)
    }

    override val consumptionRepository: ConsumptionRepository by lazy {
        ConsumptionRepositoryImpl(firestore)
    }

    override val getActiveReminderUseCase: GetActiveReminderUseCase by lazy {
        GetActiveReminderUseCase(scheduleRepository, consumptionRepository)
    }

    override val confirmTtdConsumptionUseCase: ConfirmTtdConsumptionUseCase by lazy {
        ConfirmTtdConsumptionUseCase(consumptionRepository)
    }

    override val getConsumptionHistoryUseCase: GetConsumptionHistoryUseCase by lazy {
        GetConsumptionHistoryUseCase(consumptionRepository)
    }

    override val getUserStatisticsUseCase: GetUserStatisticsUseCase by lazy {
        GetUserStatisticsUseCase(consumptionRepository)
    }
}
