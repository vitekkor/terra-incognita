package com.vitekkor.view

import com.vitekkor.Styles
import javafx.geometry.Pos
import javafx.scene.input.KeyCombination
import tornadofx.*

/**
 * Main view for menu, game preview, rules view and exit view
 */
class MainView : View("Terra Incognita") {
    private val menu = find(MainMenuView::class)

    override val root = borderpane {
        shortcut(KeyCombination.valueOf("Esc")) {
            if (center === menu.root) {
                center.replaceWith(find(ExitView::class).root, ViewTransition.Fade(0.3.seconds))
            } else {
                center.replaceWith(menu.root, ViewTransition.Fade(0.3.seconds))
            }
        }
        style { backgroundColor = multi(c("#02030A")) }
        top = vbox {
            alignment = Pos.TOP_CENTER
            label("Terra Incognita") {
                addClass(Styles.main)
                vboxConstraints { marginTop = 50.0 }
            }
        }
        center = menu.root
    }
}
