package com.jasonsb.litebrowser

import com.jasonsb.litebrowser.ui.BrowserViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { BrowserViewModel() }
}
