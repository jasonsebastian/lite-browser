package com.jasonsb.litebrowser.ui

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.CookieManager
import android.webkit.URLUtil
import android.webkit.WebView
import android.widget.Toast

fun WebView.applyProductionSettings() {
    settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
    }
}

fun WebView.attachBrowserClient(listener: WebViewEventListener) {
    this.webViewClient = DefaultWebViewClient(listener)
    this.webChromeClient = DefaultWebChromeClient(listener)
}

/**
 * Attaches a DownloadListener to the [WebView] that routes file downloads through the native
 * Android [DownloadManager].
 */
fun WebView.setupDownloadHandler() {
    setDownloadListener { url, userAgent, contentDisposition, mimetype, _ ->
        val uri = Uri.parse(url)
        if (uri.scheme != "http" && uri.scheme != "https") {
            return@setDownloadListener
        }

        val fileName = URLUtil.guessFileName(url, contentDisposition, mimetype)

        val request = DownloadManager.Request(uri).apply {
            setMimeType(mimetype)

            // Attach cookies for authenticated sessions (e.g., banking PDFs)
            val cookies = CookieManager.getInstance().getCookie(url)
            if (cookies != null) {
                addRequestHeader("cookie", cookies)
            }
            addRequestHeader("User-Agent", userAgent)

            setTitle(fileName)
            setDescription("Downloading file...")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            // Save to the public Downloads folder
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        }

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)

        Toast.makeText(context, "Downloading $fileName...", Toast.LENGTH_SHORT).show()
    }
}
