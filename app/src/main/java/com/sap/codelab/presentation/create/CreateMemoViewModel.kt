package com.sap.codelab.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.sap.codelab.R
import com.sap.codelab.domain.model.Memo
import com.sap.codelab.domain.usecase.SaveMemoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for matching CreateMemo view. Handles user interactions.
 */
@HiltViewModel
class CreateMemoViewModel @Inject constructor(
    private val saveMemoUseCase: SaveMemoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateMemoContract.UiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<CreateMemoContract.UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onTitleChanged(title: String) {
        _uiState.update { it.copy(title = title, titleError = null) }
    }

    fun onDescriptionChanged(description: String) {
        _uiState.update { it.copy(description = description, descriptionError = null) }
    }

    fun onAddLocationClicked(hasPermission: Boolean, shouldShowRationale: Boolean) {
        viewModelScope.launch {
            when {
                hasPermission -> _uiEvent.emit(CreateMemoContract.UiEvent.NavigateToSelectLocation)
                shouldShowRationale -> _uiEvent.emit(CreateMemoContract.UiEvent.ShowPermissionRationale)
                else -> _uiEvent.emit(CreateMemoContract.UiEvent.RequestFineLocationPermission)
            }
        }
    }

    fun onLocationPermissionGranted() {
        viewModelScope.launch {
            _uiEvent.emit(CreateMemoContract.UiEvent.NavigateToSelectLocation)
        }
    }

    fun onLocationSelected(location: LatLng) {
        _uiState.update { it.copy(selectedLocation = location) }
    }

    fun onSaveClicked(
        hasBackgroundPermission: Boolean,
        hasNotificationPermission: Boolean
    ) {
        val currentState = _uiState.value
        if (!validateInput(currentState.title, currentState.description)) return

        if (currentState.selectedLocation == null) {
            emitUiEvent(CreateMemoContract.UiEvent.ShowSnackbar(R.string.error_message_location_not_selected))
            return
        }

        when {
            !hasBackgroundPermission -> {
                emitUiEvent(CreateMemoContract.UiEvent.ShowBackgroundLocationRationale)
                return
            }
            !hasNotificationPermission -> {
                emitUiEvent(CreateMemoContract.UiEvent.RequestNotificationPermission)
                return
            }
        }

        saveMemoAndGeofence()
    }

    private fun saveMemoAndGeofence() = viewModelScope.launch {
        val currentState = _uiState.value
        try {
            currentState.selectedLocation?.let { location ->
                val newMemo = Memo(
                    title = currentState.title.trim(),
                    description = currentState.description.trim(),
                    reminderLatitude = currentState.selectedLocation.latitude,
                    reminderLongitude = currentState.selectedLocation.longitude
                )
                saveMemoUseCase(newMemo)
            }
            _uiState.update { it.copy(isMemoSaved = true) }
            _uiEvent.emit(CreateMemoContract.UiEvent.NavigateBack)
        } catch (e: Exception) {
            _uiEvent.emit(CreateMemoContract.UiEvent.ShowSnackbar(R.string.error_message_memo_save_failed))
        }
    }

    fun onBackgroundRationaleAccepted() {
        emitUiEvent(CreateMemoContract.UiEvent.RequestBackgroundLocationPermission)
    }

    fun onBackgroundPermissionResult(isGranted: Boolean, hasNotificationPermission: Boolean) {
        if (isGranted) {
            onSaveClicked(
                hasBackgroundPermission = true,
                hasNotificationPermission = hasNotificationPermission
            )
        } else {
            emitUiEvent(CreateMemoContract.UiEvent.ShowSnackbar(R.string.permission_dialog_background_location_denied_message))
        }
    }

    fun onNotificationPermissionResult(isGranted: Boolean) {
        if (isGranted) {
            onSaveClicked(
                hasBackgroundPermission = true,
                hasNotificationPermission = true
            )
        } else {
            emitUiEvent(CreateMemoContract.UiEvent.ShowSnackbar(R.string.permission_dialog_notifications_denied_message))
        }
    }

    private fun validateInput(title: String, description: String): Boolean {
        var isValid = true
        if (title.isBlank()) {
            _uiState.update { it.copy(titleError = R.string.error_input_title_empty) }
            isValid = false
        }
        if (description.isBlank()) {
            _uiState.update { it.copy(descriptionError = R.string.error_input_description_empty) }
            isValid = false
        }
        return isValid
    }

    private fun emitUiEvent(event: CreateMemoContract.UiEvent) {
        viewModelScope.launch { _uiEvent.emit(event) }
    }
}