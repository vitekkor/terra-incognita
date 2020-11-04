package com.vitekkor.view

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.input.KeyCombination
import tornadofx.*

class MainMenuView : Fragment() {
    override val root = vbox {
        addClass("setBackgroundFill")
        alignment = Pos.CENTER
        button("Play") {
            vboxConstraints { margin = Insets(20.0) }
            action { replaceWith<GamePreView>(ViewTransition.Fade(0.3.seconds)) }
        }
        button("Game rules") {
            vboxConstraints { margin = Insets(20.0) }
            action { replaceWith<RulesView>(ViewTransition.Fade(0.3.seconds)) }
        }
        /*button("Settings") {
            vboxConstraints { margin = Insets(20.0) }
        }*/
        button("Exit") {
            vboxConstraints { margin = Insets(20.0) }
            action { replaceWith<ExitView>(ViewTransition.Fade(0.3.seconds)) }
        }
    }
}