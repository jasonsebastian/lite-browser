package com.jasonsb.litebrowser.ui

sealed interface BrowserTabCommand {
    data class LoadUrl(val url: String) : BrowserTabCommand
    data object GoBack : BrowserTabCommand
}
