package com.sap.codelab.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sap.codelab.R
import com.sap.codelab.domain.model.Memo
import com.sap.codelab.domain.usecase.GetAllMemosUseCase
import com.sap.codelab.domain.usecase.GetOpenMemosUseCase
import com.sap.codelab.domain.usecase.SaveMemoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllMemosUseCase: GetAllMemosUseCase,
    private val getOpenMemosUseCase: GetOpenMemosUseCase,
    private val saveMemoUseCase: SaveMemoUseCase
) : ViewModel() {

    // Represents the current UI state
    private val _uiState = MutableStateFlow(HomeContract.HomeUiState())
    val uiState = _uiState.asStateFlow()

    // Represents one-time UI events
    private val _uiEvent = MutableSharedFlow<HomeContract.HomeUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var loadMemosJob: Job? = null

    init {
        onShowOpenMemosSelected()
    }

    /**
     * Handles the user action to show all memos
     */
    fun onShowAllMemosSelected() {
        loadMemos(isShowAll = true)
    }

    /**
     * Handles the user action to show only open memos
     */
    fun onShowOpenMemosSelected() {
        loadMemos(isShowAll = false)
    }

    /**
     * Handles the user action of clicking on a memo in the list.
     * Emits a navigation event to the UI.
     * @param memoId The ID of the clicked memo.
     */
    fun onMemoClicked(memoId: Long) {
        emitUiEvent(HomeContract.HomeUiEvent.NavigateToMemoDetail(memoId))
    }

    /**
     * Handles the user action of checking/unchecking a memo's completion status.
     * Updates the memo status and handles potential errors.
     * @param memo The memo to update.
     * @param isChecked The new checked status.
     */
    fun onMemoCheckedChanged(memo: Memo, isChecked: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                if (isChecked) {
                    saveMemoUseCase(memo.copy(isDone = true))
                }
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = R.string.error_update_memo) }
                emitUiEvent(HomeContract.HomeUiEvent.ShowSnackbar(R.string.error_update_memo))
            }
        }
    }

    /**
     * Handles the user action of clicking the FAB to add a new memo.
     * Emits a navigation event to the UI.
     */
    fun onAddMemoClicked() {
        emitUiEvent(HomeContract.HomeUiEvent.NavigateToCreateMemo)
    }

    /**
     * Call this when the Create Memo screen returns successfully, to refresh the list.
     * Refreshes the memo list based on the current filter.
     */
    fun onCreateMemoSuccess() {
        loadMemos(isShowAll = _uiState.value.isShowAllMemosSelected)
    }

    /**
     * Clears any error message displayed in the UI's UiState.
     * This should be called by the UI after it has processed and displayed a persistent error.
     */
    fun onErrorMessageCleared() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Loads memos from the repository based on the filter (show all or only open).
     * Manages loading state, error state, and updates the UiState.
     * @param isShowAll If true, loads all memos; otherwise, loads only open memos.
     */
    private fun loadMemos(isShowAll: Boolean) {
        loadMemosJob?.cancel() // Cancel any previous collection job to avoid multiple listeners

        val memoFlow = if (isShowAll) getAllMemosUseCase() else getOpenMemosUseCase()

        loadMemosJob = memoFlow
            .onStart { _uiState.update { it.copy(isLoading = true, error = null) } }
            .onEach { memos ->
                _uiState.update { it.copy(memos = memos, isShowAllMemosSelected = isShowAll, isLoading = false, error = null) }
            }
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, error = R.string.error_load_memos) }
                emitUiEvent(HomeContract.HomeUiEvent.ShowSnackbar(R.string.error_load_memos))
            }
            .launchIn(viewModelScope)
    }

    /**
     * Emits a one-time UI event to the SharedFlow.
     * @param event The HomeUiEvent to emit.
     */
    private fun emitUiEvent(event: HomeContract.HomeUiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}