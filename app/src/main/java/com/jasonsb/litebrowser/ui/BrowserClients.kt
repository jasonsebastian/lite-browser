package com.jasonsb.litebrowser.ui

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient

class DefaultWebViewClient(
    private val listener: WebViewEventListener
) : WebViewClient() {

    override fun doUpdateVisitedHistory(view: WebView, url: String?, isReload: Boolean) {
        super.doUpdateVisitedHistory(view, url, isReload)
        listener.onVisitedHistoryUpdated(
            url = view.url.orEmpty(),
            canGoBack = view.canGoBack(),
        )
    }
}

class DefaultWebChromeClient(
    private val listener: WebViewEventListener
) : WebChromeClient() {

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        listener.onProgressChanged(newProgress)
    }

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        return listener.onShowFileChooser(filePathCallback, fileChooserParams)
    }
}
