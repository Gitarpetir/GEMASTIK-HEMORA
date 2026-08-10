package com.gemastik.hemora.domain.school.repository

import com.gemastik.hemora.domain.model.School
import kotlinx.coroutines.flow.Flow

interface SchoolRepository {
    fun getSchoolById(schoolId: String): Flow<Result<School>>
}
