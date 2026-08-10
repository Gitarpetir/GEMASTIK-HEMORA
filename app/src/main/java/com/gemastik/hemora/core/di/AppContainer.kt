package com.gemastik.hemora.core.di

import com.gemastik.hemora.data.auth.repository.AuthRepositoryImpl
import com.gemastik.hemora.data.school.repository.SchoolRepositoryImpl
import com.gemastik.hemora.data.schedule.repository.ScheduleRepositoryImpl
import com.gemastik.hemora.domain.auth.repository.AuthRepository
import com.gemastik.hemora.domain.auth.usecase.LoginUseCase
import com.gemastik.hemora.domain.auth.usecase.RegisterRemajaPutriUseCase
import com.gemastik.hemora.domain.auth.usecase.RegisterUksUseCase
import com.gemastik.hemora.domain.school.repository.SchoolRepository
import com.gemastik.hemora.domain.school.usecase.GetSchoolInfoUseCase
import com.gemastik.hemora.domain.schedule.repository.ScheduleRepository
import com.gemastik.hemora.domain.schedule.usecase.AddScheduleUseCase
import com.gemastik.hemora.domain.schedule.usecase.DeleteScheduleUseCase
import com.gemastik.hemora.domain.schedule.usecase.GetSchedulesUseCase
import com.gemastik.hemora.domain.schedule.usecase.ManageScheduleUseCases
import com.gemastik.hemora.domain.schedule.usecase.UpdateScheduleUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

interface AppContainer {
    val authRepository: AuthRepository
    val schoolRepository: SchoolRepository
    val scheduleRepository: ScheduleRepository
    val loginUseCase: LoginUseCase
    val registerRemajaPutriUseCase: RegisterRemajaPutriUseCase
    val registerUksUseCase: RegisterUksUseCase
    val getSchoolInfoUseCase: GetSchoolInfoUseCase
    val manageScheduleUseCases: ManageScheduleUseCases
}

class DefaultAppContainer : AppContainer {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(firebaseAuth, firestore)
    }

    override val schoolRepository: SchoolRepository by lazy {
        SchoolRepositoryImpl(firestore)
    }

    override val scheduleRepository: ScheduleRepository by lazy {
        ScheduleRepositoryImpl(firestore)
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

    override val getSchoolInfoUseCase: GetSchoolInfoUseCase by lazy {
        GetSchoolInfoUseCase(schoolRepository)
    }

    override val manageScheduleUseCases: ManageScheduleUseCases by lazy {
        ManageScheduleUseCases(
            getSchedules = GetSchedulesUseCase(scheduleRepository),
            addSchedule = AddScheduleUseCase(scheduleRepository),
            updateSchedule = UpdateScheduleUseCase(scheduleRepository),
            deleteSchedule = DeleteScheduleUseCase(scheduleRepository)
        )
    }
}
