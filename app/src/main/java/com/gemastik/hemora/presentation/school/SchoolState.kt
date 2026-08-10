package com.gemastik.hemora.presentation.school

data class SchoolState(
    val isLoading: Boolean = false,
    val schoolName: String = "",
    val schoolCode: String = "",
    val error: String? = null,
    val isRegenerating: Boolean = false
)
