package com.gemastik.hemora.domain.education.usecase

import com.gemastik.hemora.domain.education.repository.EducationRepository
import com.gemastik.hemora.domain.model.Education
import kotlinx.coroutines.flow.Flow

class GetEducationsUseCase(
    private val repository: EducationRepository
) {
    operator fun invoke(): Flow<Result<List<Education>>> {
        return repository.getEducations()
    }
}
