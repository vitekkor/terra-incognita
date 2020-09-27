package com.vitekkor.view

import com.vitekkor.controller.MyController
import javafx.geometry.Insets
import javafx.geometry.Pos
import tornadofx.*

class MainMenuView : View() {
    override val root = vbox {
        alignment = Pos.CENTER
        button("Play") {
            vboxConstraints { margin = Insets(20.0) }
            action { replaceWith<GamePreView>() }
        }
        button("Game rules") {
            vboxConstraints { margin = Insets(20.0) }
            action { replaceWith<RulesView>() }
        }
        button("Settings") {
            vboxConstraints { margin = Insets(20.0) }
        }
        button("Exit") {
            vboxConstraints { margin = Insets(20.0) }
            action { replaceWith<ExitView>() }
        }
    }
}