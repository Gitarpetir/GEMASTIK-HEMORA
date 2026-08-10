package com.gemastik.hemora.data.school.repository

import com.gemastik.hemora.data.school.dto.SchoolDto
import com.gemastik.hemora.domain.model.School
import com.gemastik.hemora.domain.school.repository.SchoolRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class SchoolRepositoryImpl(
    private val firestore: FirebaseFirestore
) : SchoolRepository {
    
    override fun getSchoolById(schoolId: String): Flow<Result<School>> = flow {
        try {
            val document = firestore.collection("schools").document(schoolId).get().await()
            if (document.exists()) {
                val schoolDto = document.toObject(SchoolDto::class.java)
                if (schoolDto != null) {
                    emit(Result.success(schoolDto.toDomain(schoolId)))
                } else {
                    emit(Result.failure(Exception("Data sekolah tidak valid.")))
                }
            } else {
                emit(Result.failure(Exception("Sekolah tidak ditemukan.")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun regenerateSchoolCode(schoolId: String): Flow<Result<String>> = flow {
        try {
            val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
            val newCode = (1..6).map { chars.random() }.joinToString("")
            
            firestore.collection("schools").document(schoolId)
                .update("schoolCode", newCode)
                .await()
                
            emit(Result.success(newCode))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
