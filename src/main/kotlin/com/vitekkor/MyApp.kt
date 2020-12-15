package com.vitekkor

import com.vitekkor.controller.MyController
import com.vitekkor.view.MainView
import javafx.scene.input.KeyCombination
import javafx.stage.Stage
import tornadofx.App
import tornadofx.reloadStylesheetsOnFocus

class MyApp : App(MainView::class, Styles::class) {
    private val controller: MyController by inject()

    init {
        controller.loadAssets()
        reloadStylesheetsOnFocus()
    }

    override fun start(stage: Stage) {
        super.start(stage)
        stage.fullScreenExitKeyCombination = KeyCombination.NO_MATCH
        stage.fullScreenExitHint = ""
        stage.isFullScreen = true
    }
}