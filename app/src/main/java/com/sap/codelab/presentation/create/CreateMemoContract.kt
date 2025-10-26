package com.sap.codelab.presentation.create

import com.google.android.gms.maps.model.LatLng

interface CreateMemoContract {
    data class UiState(
        val title: String = "",
        val description: String = "",
        val isMemoSaved: Boolean = false,
        val titleError: Int? = null,
        val descriptionError: Int? = null,
        val selectedLocation: LatLng? = null
    )

    sealed interface UiEvent {
        data class ShowSnackbar(val messageResId: Int) : UiEvent
        data object NavigateBack : UiEvent
        data object RequestFineLocationPermission : UiEvent
        data object ShowPermissionRationale : UiEvent
        data object NavigateToSelectLocation : UiEvent
    }
}