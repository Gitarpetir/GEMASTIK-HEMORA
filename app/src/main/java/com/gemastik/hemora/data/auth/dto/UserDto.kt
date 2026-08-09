package com.gemastik.hemora.data.auth.dto

import com.gemastik.hemora.domain.model.User
import com.google.firebase.firestore.PropertyName

data class UserDto(
    @get:PropertyName("name") @set:PropertyName("name") var name: String = "",
    @get:PropertyName("email") @set:PropertyName("email") var email: String = "",
    @get:PropertyName("role") @set:PropertyName("role") var role: String = "",
    @get:PropertyName("schoolId") @set:PropertyName("schoolId") var schoolId: String = ""
) {
    fun toDomain(userId: String): User {
        return User(
            userId = userId,
            name = name,
            email = email,
            role = role,
            schoolId = schoolId
        )
    }
}
