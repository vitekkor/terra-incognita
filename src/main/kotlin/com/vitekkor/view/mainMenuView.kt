package com.vitekkor.view

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.input.KeyCombination
import tornadofx.*

class MainMenuView : Fragment() {
    override val root = vbox {
        shortcut(KeyCombination.valueOf("Esc")) { replaceWith<ExitView>() }
        alignment = Pos.CENTER
        button("Play") {
            vboxConstraints { margin = Insets(20.0) }
            action { replaceWith<GamePreView>() }
        }
        button("Game rules") {
            vboxConstraints { margin = Insets(20.0) }
            action { replaceWith<RulesView>() }
        }
        /*button("Settings") {
            vboxConstraints { margin = Insets(20.0) }
        }*/
        button("Exit") {
            vboxConstraints { margin = Insets(20.0) }
            action { replaceWith<ExitView>() }
        }
    }
}