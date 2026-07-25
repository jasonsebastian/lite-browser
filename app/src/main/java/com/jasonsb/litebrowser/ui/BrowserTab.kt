package com.jasonsb.litebrowser.ui

/**
 * The [BrowserTab] represents the UI state for a browser tab.
 */
data class BrowserTab(
    /**
     * The URL for the current tab.
     */
    val url: String,

    /**
     * Current page loading progress, represented by a float between 0 and 1.
     */
    val progress: Float = 0f,

    /**
     * Whether this WebView has a back history item.
     */
    val canGoBack: Boolean = false,
) {

    /**
     * Whether the URL of this tab is secure.
     */
    val isSecure: Boolean
        get() = url.startsWith("https://")
}
