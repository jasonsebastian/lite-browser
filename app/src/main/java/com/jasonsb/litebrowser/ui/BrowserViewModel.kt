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

class BrowserViewModel : ViewModel(), WebViewEventListener {
    private val _uiState = MutableStateFlow(BrowserUiState(BrowserTab("https://www.google.com")))
    val uiState: StateFlow<BrowserUiState> = _uiState.asStateFlow()

    private val _commands = MutableSharedFlow<BrowserTabCommand>()
    val commands: SharedFlow<BrowserTabCommand> = _commands.asSharedFlow()

    fun loadFromAddressBar(input: String) {
        val finalUrl = input.toSearchQueryOrUrl() ?: return

        viewModelScope.launch {
            _commands.emit(BrowserTabCommand.LoadUrl(finalUrl))
        }
    }

    fun handleBackPress() {
        if (_uiState.value.tab.canGoBack) {
            viewModelScope.launch {
                _commands.emit(BrowserTabCommand.GoBack)
            }
        }
    }

    override fun onVisitedHistoryUpdated(url: String, canGoBack: Boolean) {
        _uiState.update { current ->
            current.copy(
                tab = current.tab.copy(
                    url = url,
                    canGoBack = canGoBack,
                )
            )
        }
    }

    override fun onProgressChanged(progress: Int) {
        _uiState.update { current ->
            current.copy(
                tab = current.tab.copy(progress = progress / 100f)
            )
        }
    }
}
