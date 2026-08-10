package com.gemastik.hemora.data.schedule.repository

import com.gemastik.hemora.data.schedule.dto.TtdScheduleDto
import com.gemastik.hemora.data.schedule.dto.toDto
import com.gemastik.hemora.domain.model.TtdSchedule
import com.gemastik.hemora.domain.schedule.repository.ScheduleRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.Calendar

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
            }.sortedByDescending { it.date }
            emit(Result.success(schedules))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun addSchedule(schedule: TtdSchedule): Flow<Result<Unit>> = flow {
        try {
            val docRef = if (schedule.scheduleId.isEmpty()) {
                schedulesCollection.document()
            } else {
                schedulesCollection.document(schedule.scheduleId)
            }
            docRef.set(schedule.copy(scheduleId = docRef.id).toDto()).await()
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

    override fun getScheduleByDate(schoolId: String, date: Date): Flow<Result<TtdSchedule?>> = callbackFlow {
        val listener = schedulesCollection
            .whereEqualTo("schoolId", schoolId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val calendar = Calendar.getInstance()
                    calendar.time = date
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val day = calendar.get(Calendar.DAY_OF_MONTH)

                    var foundSchedule: TtdSchedule? = null
                    
                    for (doc in snapshot.documents) {
                        val dto = doc.toObject(TtdScheduleDto::class.java)
                        if (dto != null && dto.date != null) {
                            val schedCal = Calendar.getInstance()
                            schedCal.time = dto.date.toDate()
                            
                            if (schedCal.get(Calendar.YEAR) == year &&
                                schedCal.get(Calendar.MONTH) == month &&
                                schedCal.get(Calendar.DAY_OF_MONTH) == day) {
                                foundSchedule = dto.toDomain(doc.id)
                                break
                            }
                        }
                    }
                    trySend(Result.success(foundSchedule))
                } else {
                    trySend(Result.success(null))
                }
            }

        awaitClose { listener.remove() }
    }

    override suspend fun createSchedule(schedule: TtdSchedule): Result<Unit> {
        return try {
            val docRef = if (schedule.scheduleId.isNotEmpty()) {
                schedulesCollection.document(schedule.scheduleId)
            } else {
                schedulesCollection.document()
            }
            
            docRef.set(schedule.copy(scheduleId = docRef.id).toDto()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
