package com.gemastik.hemora.domain.education.usecase

import com.gemastik.hemora.domain.education.repository.EducationRepository
import com.gemastik.hemora.domain.model.Education
import kotlinx.coroutines.flow.Flow

class GetEducationDetailUseCase(
    private val repository: EducationRepository
) {
    operator fun invoke(id: String): Flow<Result<Education>> {
        return repository.getEducationById(id)
    }
}
