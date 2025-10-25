package com.sap.codelab.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sap.codelab.R
import com.sap.codelab.domain.usecase.GetMemoByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemoDetailsViewModel @Inject constructor(
    private val getMemoByIdUseCase: GetMemoByIdUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(MemoDetailsContract.UiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadMemo()
    }

    private fun loadMemo() {
        val memoId = savedStateHandle.get<Long>("memoId")
        if (memoId == null || memoId <= 0) {
            _uiState.update { it.copy(isLoading = false, error = R.string.error_invalid_memo_id) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val memo = getMemoByIdUseCase(memoId)
                if (memo != null) {
                    _uiState.update { it.copy(isLoading = false, memo = memo) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = R.string.error_memo_not_found) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = R.string.error_load_memo) }
            }
        }
    }
}