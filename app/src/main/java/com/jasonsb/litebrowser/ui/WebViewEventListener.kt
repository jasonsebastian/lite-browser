package com.jasonsb.litebrowser.ui

interface WebViewEventListener {
    fun onVisitedHistoryUpdated(url: String, canGoBack: Boolean)
}
