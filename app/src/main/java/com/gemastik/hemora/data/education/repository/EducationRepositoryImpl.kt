package com.gemastik.hemora.data.education.repository

import com.gemastik.hemora.data.education.dto.EducationDto
import com.gemastik.hemora.domain.education.repository.EducationRepository
import com.gemastik.hemora.domain.model.Education
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class EducationRepositoryImpl(
    private val firestore: FirebaseFirestore
) : EducationRepository {

    override fun getEducations(): Flow<Result<List<Education>>> = flow {
        try {
            val snapshot = firestore.collection("education").get().await()
            val educations = snapshot.documents.mapNotNull { doc ->
                doc.toObject(EducationDto::class.java)?.let { dto ->
                    Education(
                        educationId = doc.id,
                        title = dto.title,
                        content = dto.content,
                        category = dto.category
                    )
                }
            }
            emit(Result.success(educations))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getEducationById(id: String): Flow<Result<Education>> = flow {
        try {
            val doc = firestore.collection("education").document(id).get().await()
            val dto = doc.toObject(EducationDto::class.java)
            if (dto != null) {
                val education = Education(
                    educationId = doc.id,
                    title = dto.title,
                    content = dto.content,
                    category = dto.category
                )
                emit(Result.success(education))
            } else {
                emit(Result.failure(Exception("Materi edukasi tidak ditemukan")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
