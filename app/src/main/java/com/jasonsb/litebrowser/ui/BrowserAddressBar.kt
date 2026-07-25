package com.jasonsb.litebrowser.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun BrowserAddressBar(
    currentUrl: String,
    onUrlSubmitted: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var textInput by remember(currentUrl) { mutableStateOf(currentUrl) }
    val focusManager = LocalFocusManager.current

    TextField(
        value = textInput,
        onValueChange = { textInput = it },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        textStyle = MaterialTheme.typography.bodySmall,
        placeholder = {
            Text(
                "Search or type web address",
                style = MaterialTheme.typography.bodySmall,
            )
        },
        singleLine = true,
        leadingIcon = {
            val isSecure = currentUrl.startsWith("https://")
            Icon(
                imageVector = if (isSecure) Icons.Default.Lock else Icons.Default.Info,
                contentDescription = if (isSecure) "Secure Connection" else "Not Secure",
            )
        },
        trailingIcon = {
            if (textInput.isNotEmpty()) {
                IconButton(onClick = { textInput = "" }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear input")
                }
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Uri,
            imeAction = ImeAction.Go
        ),
        keyboardActions = KeyboardActions(
            onGo = {
                onUrlSubmitted(textInput)
                focusManager.clearFocus()
            }
        ),
        shape = RoundedCornerShape(24.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}