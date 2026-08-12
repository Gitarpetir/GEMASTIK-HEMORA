package com.gemastik.hemora.presentation.education

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gemastik.hemora.domain.education.usecase.GetEducationDetailUseCase
import com.gemastik.hemora.domain.education.usecase.GetEducationsUseCase
import com.gemastik.hemora.domain.model.Education
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EducationViewModel(
    private val getEducationsUseCase: GetEducationsUseCase,
    private val getEducationDetailUseCase: GetEducationDetailUseCase
) : ViewModel() {

    private val _listState = MutableStateFlow<EducationListState>(EducationListState.Loading)
    val listState: StateFlow<EducationListState> = _listState

    private val _detailState = MutableStateFlow<EducationDetailState>(EducationDetailState.Loading)
    val detailState: StateFlow<EducationDetailState> = _detailState

    fun loadEducations() {
        _listState.value = EducationListState.Loading
        viewModelScope.launch {
            getEducationsUseCase().collect { result ->
                if (result.isSuccess) {
                    _listState.value = EducationListState.Success(result.getOrDefault(emptyList()))
                } else {
                    _listState.value = EducationListState.Error(result.exceptionOrNull()?.message ?: "Gagal memuat materi edukasi")
                }
            }
        }
    }

    fun loadEducationDetail(id: String) {
        _detailState.value = EducationDetailState.Loading
        viewModelScope.launch {
            getEducationDetailUseCase(id).collect { result ->
                if (result.isSuccess) {
                    _detailState.value = EducationDetailState.Success(result.getOrNull()!!)
                } else {
                    _detailState.value = EducationDetailState.Error(result.exceptionOrNull()?.message ?: "Gagal memuat detail edukasi")
                }
            }
        }
    }
}

sealed class EducationListState {
    object Loading : EducationListState()
    data class Success(val educations: List<Education>) : EducationListState()
    data class Error(val message: String) : EducationListState()
}

sealed class EducationDetailState {
    object Loading : EducationDetailState()
    data class Success(val education: Education) : EducationDetailState()
    data class Error(val message: String) : EducationDetailState()
}
