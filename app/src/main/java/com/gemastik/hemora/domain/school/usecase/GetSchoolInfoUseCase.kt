package com.gemastik.hemora.domain.school.usecase

import com.gemastik.hemora.domain.model.School
import com.gemastik.hemora.domain.school.repository.SchoolRepository
import kotlinx.coroutines.flow.Flow

class GetSchoolInfoUseCase(private val schoolRepository: SchoolRepository) {
    operator fun invoke(schoolId: String): Flow<Result<School>> {
        return schoolRepository.getSchoolById(schoolId)
    }
}
