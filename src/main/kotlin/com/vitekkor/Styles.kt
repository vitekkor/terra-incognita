package com.vitekkor

import javafx.scene.paint.Paint
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val main by cssclass()
        val rules by cssclass()
        val mainFont = loadFont("/fonts/Super Maizen.otf", 32)!!
    }

    init {
        label {
            font = mainFont
            fontSize = 20.px
            textFill = Paint.valueOf("#e7e5e5")
            and(main) {
                fontSize = 64.px
            }
        }
        button {
            textFill = Paint.valueOf("#e7e5e5")
            borderColor += box(c("#2A73FF"))
            borderRadius += box(20.0.px)
            backgroundColor = multi(c("#05071F"))
            backgroundRadius += box(20.0.px)
            font = mainFont
            fontSize = 16.px
            focusTraversable = false
            and(hover) {
                backgroundColor = multi(c("#10165F"))
            }
            and(pressed) {
                backgroundColor = multi(c("#1620A1"))
            }
        }
        rules {
            font = mainFont
            fontSize = 16.px
        }
    }
}