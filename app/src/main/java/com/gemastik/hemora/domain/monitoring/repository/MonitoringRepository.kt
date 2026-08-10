package com.gemastik.hemora.domain.monitoring.repository

import com.gemastik.hemora.domain.model.User
import kotlinx.coroutines.flow.Flow

interface MonitoringRepository {
    fun getStudentsBySchool(schoolId: String): Flow<Result<List<User>>>
}
