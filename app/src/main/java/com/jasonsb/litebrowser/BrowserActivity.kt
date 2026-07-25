package com.jasonsb.litebrowser

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jasonsb.litebrowser.ui.BrowserAction
import com.jasonsb.litebrowser.ui.BrowserScreen
import com.jasonsb.litebrowser.ui.BrowserViewModel
import com.jasonsb.litebrowser.ui.theme.LiteBrowserTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class BrowserActivity : ComponentActivity() {

    private val viewModel: BrowserViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        extractUrlFromIntent(intent)?.let { url ->
            viewModel.onAction(BrowserAction.OnInitialUrlSet(url))
        }

        enableEdgeToEdge()

        setContent {
            LiteBrowserTheme {
                BrowserScreen()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        extractUrlFromIntent(intent)?.let { url ->
            viewModel.onAction(BrowserAction.OnAddressBarEntered(url))
        }
    }

    private fun extractUrlFromIntent(intent: Intent?): String? {
        return if (intent?.action == Intent.ACTION_VIEW) {
            intent.dataString
        } else {
            null
        }
    }
}
