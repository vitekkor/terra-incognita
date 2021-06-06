package com.vitekkor

import com.vitekkor.view.SplashScreen
import javafx.scene.input.KeyCombination
import javafx.stage.Stage
import tornadofx.App
import tornadofx.reloadStylesheetsOnFocus

class MyApp : App(SplashScreen::class, Styles::class) {

    init {
        reloadStylesheetsOnFocus()
    }

    override fun start(stage: Stage) {
        super.start(stage)
        stage.fullScreenExitKeyCombination = KeyCombination.NO_MATCH
        stage.fullScreenExitHint = ""
        stage.isFullScreen = true
    }
    override fun shouldShowPrimaryStage() = false
}