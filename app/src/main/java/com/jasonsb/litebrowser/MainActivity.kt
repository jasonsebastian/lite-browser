package com.jasonsb.litebrowser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jasonsb.litebrowser.ui.BrowserContainer
import com.jasonsb.litebrowser.ui.theme.LiteBrowserTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            LiteBrowserTheme {
                BrowserContainer()
            }
        }
    }
}
