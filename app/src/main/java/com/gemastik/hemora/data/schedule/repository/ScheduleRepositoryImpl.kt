package com.gemastik.hemora.data.schedule.repository

import com.gemastik.hemora.data.schedule.dto.TtdScheduleDto
import com.gemastik.hemora.domain.model.TtdSchedule
import com.gemastik.hemora.domain.schedule.repository.ScheduleRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.Calendar

class ScheduleRepositoryImpl(
    private val firestore: FirebaseFirestore
) : ScheduleRepository {

    override fun getScheduleByDate(schoolId: String, date: Date): Flow<Result<TtdSchedule?>> = callbackFlow {
        val listener = firestore.collection("ttdSchedules")
            .whereEqualTo("schoolId", schoolId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // Filter locally by date
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
                                foundSchedule = dto.copy(scheduleId = doc.id).toDomain()
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
            val dto = TtdScheduleDto(
                scheduleId = schedule.scheduleId,
                schoolId = schedule.schoolId,
                date = com.google.firebase.Timestamp(schedule.date),
                time = schedule.time
            )
            val docRef = if (schedule.scheduleId.isNotEmpty()) {
                firestore.collection("ttdSchedules").document(schedule.scheduleId)
            } else {
                firestore.collection("ttdSchedules").document()
            }
            
            val finalDto = dto.copy(scheduleId = docRef.id)
            docRef.set(finalDto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
