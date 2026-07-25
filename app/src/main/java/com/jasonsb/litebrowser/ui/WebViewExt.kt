package com.jasonsb.litebrowser.ui

import android.webkit.WebView

fun WebView.applyProductionSettings() {
    settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
    }
}

fun WebView.attachBrowserClient(listener: WebViewEventListener) {
    this.webViewClient = DefaultWebViewClient(listener)
}
