package com.sap.codelab.presentation.details

import com.sap.codelab.domain.model.Memo

interface MemoDetailsContract {
    data class UiState(
        val memo: Memo? = null,
        val isLoading: Boolean = true,
        val error: String? = null
    )

    sealed interface UiEvent {}
}