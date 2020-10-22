package com.vitekkor

import com.vitekkor.controller.MyController
import com.vitekkor.view.GameView
import com.vitekkor.view.MainView
import tornadofx.*

class MyApp : App(MainView::class, Styles::class) {
    private val controller: MyController by inject()
    init {
        controller.loadAssets()
        reloadStylesheetsOnFocus()
    }
}