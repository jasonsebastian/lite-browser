package com.jasonsb.litebrowser.ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BrowserViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial tab url is google`() {
        val viewModel = BrowserViewModel()

        assertEquals("https://www.google.com", viewModel.uiState.value.tab.url)
    }

    @Test
    fun `OnInitialUrlSet updates tab url with a sanitized url`() {
        val viewModel = BrowserViewModel()

        viewModel.onAction(BrowserAction.OnInitialUrlSet("example.com"))

        assertEquals("https://example.com", viewModel.uiState.value.tab.url)
    }

    @Test
    fun `OnInitialUrlSet passes a full url through unchanged`() {
        val viewModel = BrowserViewModel()

        viewModel.onAction(BrowserAction.OnInitialUrlSet("https://myaccount.google.com/notifications"))

        assertEquals(
            "https://myaccount.google.com/notifications",
            viewModel.uiState.value.tab.url,
        )
    }

    @Test
    fun `OnInitialUrlSet with blank input leaves tab url unchanged`() {
        val viewModel = BrowserViewModel()

        viewModel.onAction(BrowserAction.OnInitialUrlSet("   "))

        assertEquals("https://www.google.com", viewModel.uiState.value.tab.url)
    }

    @Test
    fun `OnAddressBarEntered emits a LoadUrl command with a sanitized url`() = runTest(UnconfinedTestDispatcher()) {
        val viewModel = BrowserViewModel()
        val collected = mutableListOf<BrowserTabCommand>()
        backgroundScope.launch { viewModel.commands.collect { collected.add(it) } }

        viewModel.onAction(BrowserAction.OnAddressBarEntered("example.com"))

        assertEquals(listOf(BrowserTabCommand.LoadUrl("https://example.com")), collected)
    }

    @Test
    fun `OnAddressBarEntered with blank input emits nothing`() = runTest(UnconfinedTestDispatcher()) {
        val viewModel = BrowserViewModel()
        val collected = mutableListOf<BrowserTabCommand>()
        backgroundScope.launch { viewModel.commands.collect { collected.add(it) } }

        viewModel.onAction(BrowserAction.OnAddressBarEntered("   "))

        assertTrue(collected.isEmpty())
    }

    @Test
    fun `OnSystemBackPressed emits GoBack when tab canGoBack is true`() = runTest(UnconfinedTestDispatcher()) {
        val viewModel = BrowserViewModel()
        viewModel.onAction(
            BrowserAction.OnVisitedHistoryUpdated(url = "https://example.com", canGoBack = true)
        )
        val collected = mutableListOf<BrowserTabCommand>()
        backgroundScope.launch { viewModel.commands.collect { collected.add(it) } }

        viewModel.onAction(BrowserAction.OnSystemBackPressed)

        assertEquals(listOf(BrowserTabCommand.GoBack), collected)
    }

    @Test
    fun `OnSystemBackPressed emits nothing when tab canGoBack is false`() = runTest(UnconfinedTestDispatcher()) {
        val viewModel = BrowserViewModel()
        val collected = mutableListOf<BrowserTabCommand>()
        backgroundScope.launch { viewModel.commands.collect { collected.add(it) } }

        viewModel.onAction(BrowserAction.OnSystemBackPressed)

        assertTrue(collected.isEmpty())
    }

    @Test
    fun `OnAddressBarBackClicked behaves like OnSystemBackPressed`() = runTest(UnconfinedTestDispatcher()) {
        val viewModel = BrowserViewModel()
        viewModel.onAction(
            BrowserAction.OnVisitedHistoryUpdated(url = "https://example.com", canGoBack = true)
        )
        val collected = mutableListOf<BrowserTabCommand>()
        backgroundScope.launch { viewModel.commands.collect { collected.add(it) } }

        viewModel.onAction(BrowserAction.OnAddressBarBackClicked)

        assertEquals(listOf(BrowserTabCommand.GoBack), collected)
    }

    @Test
    fun `OnVisitedHistoryUpdated updates tab url and canGoBack`() {
        val viewModel = BrowserViewModel()

        viewModel.onAction(
            BrowserAction.OnVisitedHistoryUpdated(url = "https://example.com", canGoBack = true)
        )

        val tab = viewModel.uiState.value.tab
        assertEquals("https://example.com", tab.url)
        assertTrue(tab.canGoBack)
    }

    @Test
    fun `OnProgressChanged updates tab progress as a 0 to 1 fraction`() {
        val viewModel = BrowserViewModel()

        viewModel.onAction(BrowserAction.OnProgressChanged(50))

        assertEquals(0.5f, viewModel.uiState.value.tab.progress, 0.0001f)
    }
}
