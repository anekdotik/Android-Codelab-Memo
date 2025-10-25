package com.sap.codelab.presentation.home

import com.sap.codelab.domain.model.Memo

/**
 * Defines the UI state and one-time events for the Home screen.
 */
interface HomeContract {

    /**
     * Represents the immutable state of the Home screen.
     */
    data class HomeUiState(
        val memos: List<Memo> = emptyList(),
        val isLoading: Boolean = false,
        val error: Int? = null,
        val isShowAllMemosSelected: Boolean = false
    )

    /**
     * Represents one-time events that the ViewModel sends to the UI.
     */
    sealed interface HomeUiEvent {
        data class NavigateToMemoDetail(val memoId: Long) : HomeUiEvent
        data object NavigateToCreateMemo : HomeUiEvent
        data class ShowSnackbar(val messageResId: Int) : HomeUiEvent
        data object ClearErrorMessage : HomeUiEvent
    }
}