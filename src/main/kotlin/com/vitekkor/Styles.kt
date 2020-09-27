package com.vitekkor

import javafx.geometry.Pos
import javafx.scene.text.FontWeight
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val heading by cssclass()
        val main by cssclass()
        val rules by cssclass()
        val mainFont = loadFont("/fonts/Super Maizen.otf", 32)!!
    }

    init {
        label and main {
            fontSize = 32.px
            font = mainFont
        }
        label{
            font = mainFont
            fontSize = 20.px
        }
        button{
            font = mainFont
            fontSize = 16.px
            focusTraversable = false
        }
        rules {
            font = mainFont
            fontSize = 16.px
        }
    }
}