package com.gemastik.hemora.domain.school.usecase

import com.gemastik.hemora.domain.school.repository.SchoolRepository
import kotlinx.coroutines.flow.Flow

class RegenerateSchoolCodeUseCase(private val schoolRepository: SchoolRepository) {
    operator fun invoke(schoolId: String): Flow<Result<String>> {
        return schoolRepository.regenerateSchoolCode(schoolId)
    }
}
