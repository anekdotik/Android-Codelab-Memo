package com.sap.codelab.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun onSaveClicked() {
        val title = _uiState.value.title.trim()
        val description = _uiState.value.description.trim()

        if (!validateInput(title, description)) {
            return
        }

        viewModelScope.launch {
            try {
                val newMemo = Memo(
                    title = title,
                    description = description,
                    reminderDate = 0,
                    reminderLatitude = 0.0,
                    reminderLongitude = 0.0
                )
                saveMemoUseCase(newMemo)
                _uiState.update { it.copy(isMemoSaved = true) }
                _uiEvent.emit(CreateMemoContract.UiEvent.NavigateBack)
            } catch (e: Exception) {
                _uiEvent.emit(CreateMemoContract.UiEvent.ShowSnackbar(R.string.error_save_memo))
            }
        }
    }

    private fun validateInput(title: String, description: String): Boolean {
        var isValid = true
        if (title.isBlank()) {
            _uiState.update { it.copy(titleError = R.string.memo_title_empty_error) }
            isValid = false
        }
        if (description.isBlank()) {
            _uiState.update { it.copy(descriptionError = R.string.memo_text_empty_error) }
            isValid = false
        }
        return isValid
    }
}