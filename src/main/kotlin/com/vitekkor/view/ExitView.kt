package com.vitekkor.view

import javafx.geometry.Insets
import javafx.geometry.Pos
import tornadofx.*

class ExitView : View() {
    override val root = vbox {
        addClass("setBackgroundFill")
        alignment = Pos.CENTER
        label("Are you sure want to quit?") { vboxConstraints { margin = Insets(20.0) } }
        hbox {
            vboxConstraints { margin = Insets(20.0) }
            alignment = Pos.CENTER
            button("Yes") { hboxConstraints { margin = Insets(20.0) } }.action { close() }
            button("No") {
                shortcut("Esc") { replaceWith<MainMenuView>() }
                hboxConstraints { margin = Insets(20.0) }
            }.action { replaceWith<MainMenuView>(ViewTransition.Fade(0.3.seconds)) }
        }
    }
}