package com.gemastik.hemora.data.monitoring.repository

import android.util.Log
import com.gemastik.hemora.data.auth.dto.UserDto
import com.gemastik.hemora.domain.model.User
import com.gemastik.hemora.domain.monitoring.repository.MonitoringRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class MonitoringRepositoryImpl(
    private val firestore: FirebaseFirestore
) : MonitoringRepository {

    override fun getStudentsBySchool(schoolId: String): Flow<Result<List<User>>> = callbackFlow {
        val listener = firestore.collection("users")
            .whereEqualTo("role", "REMAJA_PUTRI")
            .whereEqualTo("schoolId", schoolId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FR15_AUDIT", "Firestore Query Failed! Code: ${error.code}, Message: ${error.message}", error)
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(UserDto::class.java)?.toDomain(doc.id)
                    }.sortedBy { it.name }
                    
                    trySend(Result.success(list))
                }
            }

        awaitClose { listener.remove() }
    }
}
