package com.gemastik.hemora.data.school.dto

import com.gemastik.hemora.domain.model.School
import com.google.firebase.firestore.PropertyName

data class SchoolDto(
    @get:PropertyName("schoolName") @set:PropertyName("schoolName") var schoolName: String = "",
    @get:PropertyName("schoolCode") @set:PropertyName("schoolCode") var schoolCode: String = ""
) {
    fun toDomain(schoolId: String): School {
        return School(
            schoolId = schoolId,
            schoolName = schoolName,
            schoolCode = schoolCode
        )
    }
}
