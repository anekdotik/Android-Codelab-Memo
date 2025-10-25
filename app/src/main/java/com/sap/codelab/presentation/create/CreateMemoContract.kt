package com.sap.codelab.presentation.create

interface CreateMemoContract {
    data class UiState(
        val title: String = "",
        val description: String = "",
        val isMemoSaved: Boolean = false,
        val titleError: Int? = null,
        val descriptionError: Int? = null
    )

    sealed interface UiEvent {
        data class ShowSnackbar(val messageResId: Int) : UiEvent
        data object NavigateBack : UiEvent
    }
}