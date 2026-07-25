package com.jasonsb.litebrowser.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BrowserViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BrowserUiState(BrowserTab("https://www.google.com")))
    val uiState: StateFlow<BrowserUiState> = _uiState.asStateFlow()

    private val _commands = MutableSharedFlow<BrowserTabCommand>()
    val commands: SharedFlow<BrowserTabCommand> = _commands.asSharedFlow()

    fun onAction(action: BrowserAction) {
        when (action) {
            is BrowserAction.OnInitialUrlSet -> setInitialUrl(action.url)
            is BrowserAction.OnAddressBarEntered -> loadFromAddressBar(action.input)
            BrowserAction.OnAddressBarBackClicked -> handleBackPress()
            BrowserAction.OnSystemBackPressed -> handleBackPress()
            is BrowserAction.OnProgressChanged -> onProgressChanged(action.progress)
            is BrowserAction.OnVisitedHistoryUpdated -> {
                onVisitedHistoryUpdated(action.url, action.canGoBack)
            }
        }
    }

    private fun setInitialUrl(url: String) {
        val finalUrl = url.toSearchQueryOrUrl() ?: return

        _uiState.update { current ->
            current.copy(tab = current.tab.copy(url = finalUrl))
        }
    }

    private fun loadFromAddressBar(input: String) {
        val finalUrl = input.toSearchQueryOrUrl() ?: return

        viewModelScope.launch {
            _commands.emit(BrowserTabCommand.LoadUrl(finalUrl))
        }
    }

    private fun handleBackPress() {
        if (_uiState.value.tab.canGoBack) {
            viewModelScope.launch {
                _commands.emit(BrowserTabCommand.GoBack)
            }
        }
    }

    private fun onVisitedHistoryUpdated(url: String, canGoBack: Boolean) {
        _uiState.update { current ->
            current.copy(
                tab = current.tab.copy(
                    url = url,
                    canGoBack = canGoBack,
                )
            )
        }
    }

    private fun onProgressChanged(progress: Int) {
        _uiState.update { current ->
            current.copy(
                tab = current.tab.copy(progress = progress / 100f)
            )
        }
    }
}

sealed interface BrowserAction {
    /**
     * User set initial URL (e.g., from external app).
     */
    data class OnInitialUrlSet(val url: String) : BrowserAction

    /**
     * User entered an input in the search bar.
     */
    data class OnAddressBarEntered(val input: String) : BrowserAction

    /**
     * User initiated system back press action.
     */
    data object OnSystemBackPressed : BrowserAction

    /**
     * User clicked address bar back button.
     */
    data object OnAddressBarBackClicked : BrowserAction

    /**
     * User updated visited history.
     */
    data class OnVisitedHistoryUpdated(val url: String, val canGoBack: Boolean) : BrowserAction

    /**
     * User loads a page and progress is updated.
     *
     * [progress] is a number from 0 to 100.
     */
    data class OnProgressChanged(val progress: Int) : BrowserAction
}
