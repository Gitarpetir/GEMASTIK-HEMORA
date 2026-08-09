package com.gemastik.hemora.domain.model

data class User(
    val userId: String,
    val name: String,
    val email: String,
    val role: String,
    val schoolId: String
)
