package com.gemastik.hemora.presentation.monitoring

import com.gemastik.hemora.domain.model.User

sealed class DaftarSiswiState {
    object Loading : DaftarSiswiState()
    data class Success(val students: List<User>) : DaftarSiswiState()
    data class Error(val message: String) : DaftarSiswiState()
}
