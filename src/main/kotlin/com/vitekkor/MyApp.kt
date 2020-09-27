package com.vitekkor

import com.vitekkor.view.MainView
import tornadofx.*

class MyApp: App(MainView::class, Styles::class){
    init {
        reloadStylesheetsOnFocus()
    }
}