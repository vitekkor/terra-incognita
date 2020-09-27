package com.vitekkor.view

import com.vitekkor.Styles
import com.vitekkor.controller.MyController
import javafx.geometry.Pos
import javafx.scene.control.Button
import tornadofx.*

class MainView : View("Terra Incognita") {
    private val menu: MainMenuView by inject()
    /*private val backButton: Button = button("Back") {
        //isVisible = false
        hboxConstraints { marginBottom = 25.0 }
    }*/
    override val root = borderpane {
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
}
