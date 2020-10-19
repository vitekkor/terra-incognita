package com.vitekkor

import com.vitekkor.controller.MyController
import com.vitekkor.view.GameView
import tornadofx.*

class MyApp : App(GameView::class, Styles::class) {
    private val controller: MyController by inject()
    init {
        controller.loadAssets()
        reloadStylesheetsOnFocus()
    }
}