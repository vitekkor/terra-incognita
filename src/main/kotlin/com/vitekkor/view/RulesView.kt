package com.vitekkor.view

import com.vitekkor.Styles
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.layout.VBox
import tornadofx.*
import tornadofx.Stylesheet.Companion.scrollPane
import tornadofx.Stylesheet.Companion.tabPane

class RulesView : View("My View") {
    override val root = vbox {
        shortcut("Esc") { replaceWith<MainMenuView>() }
        alignment = Pos.TOP_CENTER
        label("Game Rules") { vboxConstraints { margin = Insets(20.0); marginTop = 50.0 } }
        scrollpane {
            vbox {
                alignment = Pos.CENTER
                label(resources.stream("/rules.txt").bufferedReader().readText()) {
                    //wrappingWidth = app.workspace.currentStage?.width ?: 500.0
                    addClass(Styles.rules)
                    vboxConstraints { margin = Insets(20.0) }
                }
            }
        }

    }
}
