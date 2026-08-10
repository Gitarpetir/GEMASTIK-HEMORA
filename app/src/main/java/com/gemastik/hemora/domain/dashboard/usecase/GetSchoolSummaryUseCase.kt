package com.gemastik.hemora.domain.dashboard.usecase

import com.gemastik.hemora.domain.consumption.repository.ConsumptionRepository
import com.gemastik.hemora.domain.dashboard.model.DashboardSummaryModel
import com.gemastik.hemora.domain.model.ConsumptionStatus
import com.gemastik.hemora.domain.monitoring.repository.MonitoringRepository
import com.gemastik.hemora.domain.schedule.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class GetSchoolSummaryUseCase(
    private val monitoringRepository: MonitoringRepository,
    private val scheduleRepository: ScheduleRepository,
    private val consumptionRepository: ConsumptionRepository
) {
    operator fun invoke(schoolId: String): Flow<Result<DashboardSummaryModel>> {
        val studentsFlow = monitoringRepository.getStudentsBySchool(schoolId)
        val schedulesFlow = scheduleRepository.getSchedules(schoolId)

        return combine(studentsFlow, schedulesFlow) { studentsResult, schedulesResult ->
            if (studentsResult.isFailure) {
                Result.failure<Pair<List<com.gemastik.hemora.domain.model.User>, List<com.gemastik.hemora.domain.model.TtdSchedule>>>(
                    studentsResult.exceptionOrNull() ?: Exception("Unknown error")
                )
            } else if (schedulesResult.isFailure) {
                Result.failure<Pair<List<com.gemastik.hemora.domain.model.User>, List<com.gemastik.hemora.domain.model.TtdSchedule>>>(
                    schedulesResult.exceptionOrNull() ?: Exception("Unknown error")
                )
            } else {
                val students = studentsResult.getOrNull() ?: emptyList()
                val schedules = schedulesResult.getOrNull() ?: emptyList()
                Result.success(Pair(students, schedules))
            }
        }.flatMapLatest { resultPair ->
            if (resultPair.isFailure) {
                flowOf(Result.failure<DashboardSummaryModel>(resultPair.exceptionOrNull() ?: Exception("Unknown error")))
            } else {
                val pair = resultPair.getOrNull()
                val students = pair?.first ?: emptyList()
                val schedules = pair?.second ?: emptyList()
                
                if (students.isEmpty()) {
                    flowOf(
                        Result.success(
                            DashboardSummaryModel(
                                totalStudents = 0,
                                totalSchedules = schedules.size,
                                totalConfirmed = 0,
                                totalUnconfirmed = 0
                            )
                        )
                    )
                } else {
                    val consumptionFlows = students.map { student ->
                        consumptionRepository.getConsumptionHistory(student.userId)
                    }

                    combine(consumptionFlows) { consumptionsResultsArray ->
                        val allConsumptions = mutableListOf<com.gemastik.hemora.domain.model.TtdConsumption>()
                        for (res in consumptionsResultsArray) {
                            @Suppress("UNCHECKED_CAST")
                            val result = res as? Result<List<com.gemastik.hemora.domain.model.TtdConsumption>>
                            val list = result?.getOrNull()
                            if (list != null) {
                                allConsumptions.addAll(list)
                            }
                        }

                        val confirmedConsumptions = allConsumptions.filter { it.status == ConsumptionStatus.SUDAH_KONSUMSI }
                        val uniqueConfirmed = confirmedConsumptions.distinctBy { it.userId + "_" + it.scheduleId }

                        val totalStudents = students.size
                        val totalSchedules = schedules.size
                        val totalConfirmed = uniqueConfirmed.size
                        val totalUnconfirmed = (totalStudents * totalSchedules) - totalConfirmed

                        Result.success(
                            DashboardSummaryModel(
                                totalStudents = totalStudents,
                                totalSchedules = totalSchedules,
                                totalConfirmed = totalConfirmed,
                                totalUnconfirmed = if (totalUnconfirmed < 0) 0 else totalUnconfirmed
                            )
                        )
                    }
                }
            }
        }
    }
}
