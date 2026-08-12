package com.gemastik.hemora.domain.education.repository

import com.gemastik.hemora.domain.model.Education
import kotlinx.coroutines.flow.Flow

interface EducationRepository {
    fun getEducations(): Flow<Result<List<Education>>>
    fun getEducationById(id: String): Flow<Result<Education>>
}
