package com.gemastik.hemora.domain.monitoring.usecase

import com.gemastik.hemora.domain.model.User
import com.gemastik.hemora.domain.monitoring.repository.MonitoringRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStudentsUseCase @Inject constructor(
    private val repository: MonitoringRepository
) {
    operator fun invoke(schoolId: String): Flow<Result<List<User>>> {
        return repository.getStudentsBySchool(schoolId)
    }
}
