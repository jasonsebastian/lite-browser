package com.jasonsb.litebrowser.ui

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun BrowserScreen(
    modifier: Modifier = Modifier,
    viewModel: BrowserViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    LaunchedEffect(Unit) {
        viewModel.commands.collectLatest { command ->
            when (command) {
                is BrowserTabCommand.LoadUrl -> webViewInstance?.loadUrl(command.url)
                is BrowserTabCommand.GoBack -> webViewInstance?.goBack()
            }
        }
    }

    BrowserWebView(
        tab = uiState.tab,
        bindWebViewInstance = { webViewInstance = it },
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserWebView(
    tab: BrowserTab,
    bindWebViewInstance: (WebView) -> Unit,
    onAction: (BrowserAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val fileUploadHandler = rememberFileUploadHandler()

    val webViewEventListener = object : WebViewEventListener {
        override fun onVisitedHistoryUpdated(url: String, canGoBack: Boolean) {
            onAction(BrowserAction.OnVisitedHistoryUpdated(url, canGoBack))
        }

        override fun onProgressChanged(progress: Int) {
            onAction(BrowserAction.OnProgressChanged(progress))
        }

        override fun onShowFileChooser(
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: WebChromeClient.FileChooserParams?
        ): Boolean {
            return fileUploadHandler.onShowFileChooser(filePathCallback, fileChooserParams)
        }
    }

    BackHandler(enabled = tab.canGoBack) {
        onAction(BrowserAction.OnSystemBackPressed)
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopAppBar(
                    title = {
                        BrowserAddressBar(
                            currentUrl = tab.url,
                            onUrlSubmitted = { onAction(BrowserAction.OnAddressBarEntered(it)) }
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { onAction(BrowserAction.OnAddressBarBackClicked) },
                            enabled = tab.canGoBack
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
                BrowserLoadingBar(tab.progress)
            }
        },
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    applyProductionSettings()
                    setupDownloadHandler()
                    attachBrowserClients(webViewEventListener)
                    bindWebViewInstance(this)
                    loadUrl(tab.url)
                }
            },
            onRelease = { webView ->
                webView.destroy()
            },
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
        )
    }
}
