package com.jasonsb.litebrowser.ui

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * A wrapper to hold the function signature needed by [WebChromeClient].
 */
class FileUploadState(
    val onShowFileChooser: (ValueCallback<Array<Uri>>?, WebChromeClient.FileChooserParams?) -> Boolean
)

/**
 * Encapsulates the [ActivityResultLauncher] and [ValueCallback] state needed to handle HTML file
 * uploads in a [WebView].
 */
@Composable
fun rememberFileUploadHandler(): FileUploadState {
    var fileUploadCallback by remember { mutableStateOf<ValueCallback<Array<Uri>>?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            fileUploadCallback?.onReceiveValue(uris.toTypedArray())
        } else {
            // Prevent the "Frozen Button" bug
            fileUploadCallback?.onReceiveValue(null)
        }
        fileUploadCallback = null
    }

    return remember(filePickerLauncher) {
        FileUploadState(
            onShowFileChooser = { callback, params ->
                fileUploadCallback = callback

                val rawTypes = params?.acceptTypes ?: emptyArray()
                val validTypes = rawTypes.filter { it.isNotBlank() }.toTypedArray()
                val mimeTypesToLaunch = if (validTypes.isNotEmpty()) validTypes else arrayOf("*/*")
                filePickerLauncher.launch(mimeTypesToLaunch)

                true
            }
        )
    }
}
