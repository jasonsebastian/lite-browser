package com.jasonsb.litebrowser.ui

import java.net.URLEncoder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class StringExtTest {

    @Test
    fun `empty input returns null`() {
        assertNull("".toSearchQueryOrUrl())
    }

    @Test
    fun `blank input returns null`() {
        assertNull("   ".toSearchQueryOrUrl())
    }

    @Test
    fun `http url is passed through unchanged`() {
        assertEquals("http://example.com", "http://example.com".toSearchQueryOrUrl())
    }

    @Test
    fun `https url is passed through unchanged`() {
        assertEquals("https://example.com", "https://example.com".toSearchQueryOrUrl())
    }

    @Test
    fun `surrounding whitespace is trimmed before evaluation`() {
        assertEquals("https://example.com", "  https://example.com  ".toSearchQueryOrUrl())
    }

    @Test
    fun `bare domain without scheme is prefixed with https`() {
        assertEquals("https://example.com", "example.com".toSearchQueryOrUrl())
    }

    @Test
    fun `bare domain with path is prefixed with https`() {
        assertEquals(
            "https://myaccount.google.com/notifications",
            "myaccount.google.com/notifications".toSearchQueryOrUrl(),
        )
    }

    @Test
    fun `plain text without a dot becomes a google search query`() {
        val expected = "https://www.google.com/search?q=" + URLEncoder.encode("hello", "UTF-8")
        assertEquals(expected, "hello".toSearchQueryOrUrl())
    }

    @Test
    fun `text with spaces becomes a google search query even if it contains a dot`() {
        val expected = "https://www.google.com/search?q=" + URLEncoder.encode("hello world.com", "UTF-8")
        assertEquals(expected, "hello world.com".toSearchQueryOrUrl())
    }

    @Test
    fun `non-http scheme without a dot falls back to a search query`() {
        val input = "javascript:alert(1)"
        val expected = "https://www.google.com/search?q=" + URLEncoder.encode(input, "UTF-8")
        assertEquals(expected, input.toSearchQueryOrUrl())
    }
}
