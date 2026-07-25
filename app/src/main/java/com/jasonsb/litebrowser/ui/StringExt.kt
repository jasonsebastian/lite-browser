package com.jasonsb.litebrowser.ui

import java.net.URLEncoder

/**
 * Parses raw text input into a valid HTTP URL or a Google search query.
 * Returns null if the input is empty or blank.
 */
fun String.toSearchQueryOrUrl(): String? {
    val trimmed = this.trim()
    if (trimmed.isEmpty()) return null

    return when {
        trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
        trimmed.contains(".") && !trimmed.contains(" ") -> "https://$trimmed"
        else -> "https://www.google.com/search?q=${URLEncoder.encode(trimmed, "UTF-8")}"
    }
}
