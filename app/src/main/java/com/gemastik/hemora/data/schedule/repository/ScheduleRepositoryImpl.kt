package com.gemastik.hemora.data.schedule.repository

import com.gemastik.hemora.data.schedule.dto.TtdScheduleDto
import com.gemastik.hemora.data.schedule.dto.toDto
import com.gemastik.hemora.domain.schedule.model.TtdSchedule
import com.gemastik.hemora.domain.schedule.repository.ScheduleRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ScheduleRepositoryImpl(
    private val firestore: FirebaseFirestore
) : ScheduleRepository {

    private val schedulesCollection = firestore.collection("ttdSchedules")

    override fun getSchedules(schoolId: String): Flow<Result<List<TtdSchedule>>> = flow {
        try {
            val snapshot = schedulesCollection
                .whereEqualTo("schoolId", schoolId)
                .get()
                .await()
            val schedules = snapshot.documents.mapNotNull { doc ->
                doc.toObject(TtdScheduleDto::class.java)?.toDomain(doc.id)
            }.sortedByDescending { it.createdAt } // Order by newest first
            emit(Result.success(schedules))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun addSchedule(schedule: TtdSchedule): Flow<Result<Unit>> = flow {
        try {
            val docRef = if (schedule.scheduleId.isEmpty()) {
                schedulesCollection.document() // Auto-generate ID
            } else {
                schedulesCollection.document(schedule.scheduleId)
            }
            docRef.set(schedule.toDto()).await()
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun updateSchedule(schedule: TtdSchedule): Flow<Result<Unit>> = flow {
        try {
            schedulesCollection.document(schedule.scheduleId)
                .set(schedule.toDto())
                .await()
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun deleteSchedule(scheduleId: String): Flow<Result<Unit>> = flow {
        try {
            schedulesCollection.document(scheduleId).delete().await()
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
