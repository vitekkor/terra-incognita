package com.vitekkor.view

import com.vitekkor.Styles
import javafx.geometry.Pos
import javafx.scene.paint.Paint
import tornadofx.*
import java.awt.Color

class MainView : View("Terra Incognita") {
    private val menu = find(MainMenuView::class)

    /*private val backButton: Button = button("Back") {
        //isVisible = false
        hboxConstraints { marginBottom = 25.0 }
    }*/
    override val root = borderpane {
        style { backgroundColor = multi(c("#02030A")) }
        top = vbox {
            alignment = Pos.TOP_CENTER
            label("Terra Incognita") {
                addClass(Styles.main)
                vboxConstraints { marginTop = 50.0 }
            }
        }
        center = menu.root
        //bottom = hbox { add(backButton) }
    }

    fun setFragment(fragment: Fragment) {
        root.center = fragment.root
    }
}
