package com.jasonsb.litebrowser.ui

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient

interface WebViewEventListener {

    /**
     * Notify the host application to update its visited links database.
     */
    fun onVisitedHistoryUpdated(url: String, canGoBack: Boolean)

    /**
     * Tell the host application the current progress of loading a page.
     */
    fun onProgressChanged(progress: Int)

    /**
     * Asks the client app to show a file chooser.
     */
    fun onShowFileChooser(
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    ): Boolean
}
