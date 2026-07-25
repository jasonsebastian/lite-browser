package com.jasonsb.litebrowser.ui

import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserContainer(
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

    val tab = uiState.tab

    BackHandler(enabled = tab.canGoBack) {
        viewModel.handleBackPress()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    BrowserAddressBar(
                        currentUrl = tab.url,
                        onUrlSubmitted = { input ->
                            viewModel.loadFromAddressBar(input)
                        }
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.handleBackPress() },
                        enabled = tab.canGoBack
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    applyProductionSettings()
                    attachBrowserClient(viewModel)
                    webViewInstance = this
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
