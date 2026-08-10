package com.gemastik.hemora.domain.schedule.usecase

import com.gemastik.hemora.domain.model.TtdSchedule
import com.gemastik.hemora.domain.schedule.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow

class GetSchedulesUseCase(private val repository: ScheduleRepository) {
    operator fun invoke(schoolId: String): Flow<Result<List<TtdSchedule>>> {
        return repository.getSchedules(schoolId)
    }
}

class AddScheduleUseCase(private val repository: ScheduleRepository) {
    operator fun invoke(schedule: TtdSchedule): Flow<Result<Unit>> {
        return repository.addSchedule(schedule)
    }
}

class UpdateScheduleUseCase(private val repository: ScheduleRepository) {
    operator fun invoke(schedule: TtdSchedule): Flow<Result<Unit>> {
        return repository.updateSchedule(schedule)
    }
}

class DeleteScheduleUseCase(private val repository: ScheduleRepository) {
    operator fun invoke(scheduleId: String): Flow<Result<Unit>> {
        return repository.deleteSchedule(scheduleId)
    }
}

data class ManageScheduleUseCases(
    val getSchedules: GetSchedulesUseCase,
    val addSchedule: AddScheduleUseCase,
    val updateSchedule: UpdateScheduleUseCase,
    val deleteSchedule: DeleteScheduleUseCase
)
