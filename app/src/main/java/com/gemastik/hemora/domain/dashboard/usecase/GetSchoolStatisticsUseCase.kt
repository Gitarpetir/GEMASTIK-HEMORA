package com.gemastik.hemora.domain.dashboard.usecase

import com.gemastik.hemora.domain.consumption.repository.ConsumptionRepository
import com.gemastik.hemora.domain.dashboard.model.ScheduleComplianceModel
import com.gemastik.hemora.domain.model.ConsumptionStatus
import com.gemastik.hemora.domain.model.TtdConsumption
import com.gemastik.hemora.domain.monitoring.repository.MonitoringRepository
import com.gemastik.hemora.domain.schedule.repository.ScheduleRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class GetSchoolStatisticsUseCase(
    private val monitoringRepository: MonitoringRepository,
    private val scheduleRepository: ScheduleRepository,
    private val consumptionRepository: ConsumptionRepository
) {
    private suspend fun <T> Flow<T>.safeFirst(): T = coroutineScope {
        var result: Any? = null
        val job = launch {
            collect {
                result = it
                cancel()
            }
        }
        job.join()
        @Suppress("UNCHECKED_CAST")
        result as T
    }

    operator fun invoke(schoolId: String): Flow<Result<List<ScheduleComplianceModel>>> = flow {
        try {
            val studentsResult = monitoringRepository.getStudentsBySchool(schoolId).safeFirst()
            if (studentsResult.isFailure) {
                emit(Result.failure(studentsResult.exceptionOrNull() ?: Exception("Gagal memuat data siswi")))
                return@flow
            }

            val schedulesResult = scheduleRepository.getSchedules(schoolId).safeFirst()
            if (schedulesResult.isFailure) {
                emit(Result.failure(schedulesResult.exceptionOrNull() ?: Exception("Gagal memuat jadwal sekolah")))
                return@flow
            }

            val students = studentsResult.getOrNull() ?: emptyList()
            val schedules = schedulesResult.getOrNull() ?: emptyList()

            if (students.isEmpty() || schedules.isEmpty()) {
                val emptyStats = schedules.map { schedule ->
                    ScheduleComplianceModel(
                        schedule = schedule,
                        consumedCount = 0,
                        totalStudents = students.size,
                        percentage = 0f
                    )
                }.sortedByDescending { it.schedule.date }
                emit(Result.success(emptyStats))
                return@flow
            }

            val allConsumptions = mutableListOf<TtdConsumption>()
            for (student in students) {
                val consumptionResult = consumptionRepository.getConsumptionHistory(student.userId).safeFirst()
                if (consumptionResult.isSuccess) {
                    allConsumptions.addAll(consumptionResult.getOrNull() ?: emptyList())
                }
            }

            val confirmedConsumptions = allConsumptions.filter { it.status == ConsumptionStatus.SUDAH_KONSUMSI }

            val scheduleStats = schedules.map { schedule ->
                val consumptionsForSchedule = confirmedConsumptions.filter { it.scheduleId == schedule.scheduleId }
                val uniqueConsumers = consumptionsForSchedule.distinctBy { it.userId }

                val consumedCount = uniqueConsumers.size
                val percentage = if (students.isNotEmpty()) {
                    (consumedCount.toFloat() / students.size.toFloat()) * 100f
                } else {
                    0f
                }

                ScheduleComplianceModel(
                    schedule = schedule,
                    consumedCount = consumedCount,
                    totalStudents = students.size,
                    percentage = percentage
                )
            }.sortedByDescending { it.schedule.date }

            emit(Result.success(scheduleStats))

        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
