package com.gemastik.hemora.data.consumption.repository

import com.gemastik.hemora.data.consumption.dto.TtdConsumptionDto
import com.gemastik.hemora.domain.consumption.repository.ConsumptionRepository
import com.gemastik.hemora.domain.model.ComplianceStatistics
import com.gemastik.hemora.domain.model.ConsumptionStatus
import com.gemastik.hemora.domain.model.TtdConsumption
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ConsumptionRepositoryImpl(
    private val firestore: FirebaseFirestore
) : ConsumptionRepository {

    override fun getConsumptionHistory(userId: String): Flow<Result<List<TtdConsumption>>> = callbackFlow {
        val listener = firestore.collection("ttdConsumptions")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(TtdConsumptionDto::class.java)?.copy(consumptionId = doc.id)?.toDomain()
                    }.sortedByDescending { it.confirmedAt } 
                    
                    trySend(Result.success(list))
                }
            }

        awaitClose { listener.remove() }
    }

    override fun getConsumptionBySchedule(
        userId: String,
        scheduleId: String
    ): Flow<Result<TtdConsumption?>> = callbackFlow {
        val listener = firestore.collection("ttdConsumptions")
            .whereEqualTo("userId", userId)
            .whereEqualTo("scheduleId", scheduleId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val doc = snapshot.documents.first()
                    val dto = doc.toObject(TtdConsumptionDto::class.java)?.copy(consumptionId = doc.id)
                    trySend(Result.success(dto?.toDomain()))
                } else {
                    trySend(Result.success(null))
                }
            }

        awaitClose { listener.remove() }
    }

    override suspend fun confirmConsumption(
        userId: String,
        scheduleId: String,
        status: ConsumptionStatus
    ): Result<Unit> {
        return try {
            val snapshot = firestore.collection("ttdConsumptions")
                .whereEqualTo("userId", userId)
                .whereEqualTo("scheduleId", scheduleId)
                .get()
                .await()
                
            if (!snapshot.isEmpty) {
                val docId = snapshot.documents.first().id
                firestore.collection("ttdConsumptions").document(docId)
                    .update(
                        mapOf(
                            "status" to status.name,
                            "confirmedAt" to com.google.firebase.Timestamp.now()
                        )
                    ).await()
            } else {
                val docRef = firestore.collection("ttdConsumptions").document()
                val dto = TtdConsumptionDto(
                    consumptionId = docRef.id,
                    userId = userId,
                    scheduleId = scheduleId,
                    status = status.name,
                    confirmedAt = com.google.firebase.Timestamp.now()
                )
                docRef.set(dto).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getComplianceStatistics(userId: String): Flow<Result<ComplianceStatistics>> = callbackFlow {
        val listener = firestore.collection("ttdConsumptions")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val consumptions = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(TtdConsumptionDto::class.java)?.toDomain()
                    }
                    val total = consumptions.size
                    val consumed = consumptions.count { it.status == ConsumptionStatus.SUDAH_KONSUMSI }
                    val missed = consumptions.count { it.status == ConsumptionStatus.TERLEWAT }
                    
                    val percentage = if (total > 0) {
                        (consumed.toFloat() / total.toFloat()) * 100f
                    } else 0f
                    
                    val stats = ComplianceStatistics(
                        totalSchedules = total,
                        totalConsumed = consumed,
                        totalMissed = missed,
                        compliancePercentage = percentage
                    )
                    
                    trySend(Result.success(stats))
                }
            }
            
        awaitClose { listener.remove() }
    }
}
