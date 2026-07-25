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
        val trimmed = input.trim()
        val finalUrl = when {
            trimmed.isEmpty() -> return
            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
            trimmed.contains(".") && !trimmed.contains(" ") -> "https://$trimmed"
            else -> "https://www.google.com/search?q=${
                java.net.URLEncoder.encode(
                    trimmed,
                    "UTF-8"
                )
            }"
        }

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
}

data class BrowserUiState(
    val tab: BrowserTab,
)

data class BrowserTab(
    val url: String,
    val canGoBack: Boolean = false,
)

sealed interface BrowserTabCommand {
    data class LoadUrl(val url: String) : BrowserTabCommand
    data object GoBack : BrowserTabCommand
}
