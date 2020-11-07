package com.vitekkor

import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.TextAlignment
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val setBackgroundFill by cssclass()
        val main by cssclass()
        val notification by cssclass()
        val rules by cssclass()
        val mainFont = loadFont("/fonts/Super_Maizen.otf", 32)!!
        val dialogFont = loadFont("/fonts/Super_Maizen.otf", 16)!!
        private val colorOfBackground = c("#05071F")
        private val colorOfBorder = c("#2A73FF")
        private val radiusOfBorder = 20.0.px
        val colorOfText = "#e7e5e5"
    }

    init {
        setBackgroundFill {
            backgroundColor = multi(colorOfBackground)
        }
        label {
            font = mainFont
            fontSize = 20.px
            textFill = Paint.valueOf("#e7e5e5")
            and(main) {
                fontSize = 64.px
            }
        }
        button {
            textFill = Paint.valueOf(colorOfText)
            borderColor += box(colorOfBorder)
            borderRadius += box(radiusOfBorder)
            backgroundColor = multi(colorOfBackground)
            backgroundRadius += box(radiusOfBorder)
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
            textAlignment = TextAlignment.CENTER
        }
        s(comboBox, listCell) {
            baseColor = c("#05074F")
            backgroundColor = multi(colorOfBackground)
            textFill = Paint.valueOf(colorOfText)
            selectionBarText = Paint.valueOf(colorOfText)
            borderColor += box(colorOfBorder)
            backgroundRadius += box(radiusOfBorder)
            borderRadius += box(radiusOfBorder)
            and(hover) {
                backgroundColor = multi(c("#10165F"))
            }
        }
        s(comboBox, listView) {
            backgroundColor = multi(colorOfBackground)
            backgroundRadius += box(radiusOfBorder)
        }
        textField {
            backgroundColor = multi(colorOfBackground)
            textFill = Paint.valueOf(colorOfText)
            borderColor += box(colorOfBorder)
            borderRadius += box(radiusOfBorder)
            and(error) {
                borderColor += box(Color.RED)
            }
        }
        s(scrollPane, Stylesheet.listCell) {
            baseColor = Color.TRANSPARENT
            textFill = Paint.valueOf(colorOfText)
            borderColor += box(colorOfBorder)
            backgroundRadius += box(radiusOfBorder)
            borderRadius += box(radiusOfBorder)
        }
        s(scrollBar) {
            baseColor = c("#05074F")
            backgroundRadius += box(radiusOfBorder)
        }
        tooltip {
            textFill = Paint.valueOf(colorOfText)
            borderColor += box(colorOfBorder)
            borderRadius += box(radiusOfBorder)
            backgroundColor = multi(colorOfBackground)
            backgroundRadius += box(radiusOfBorder)
            font = mainFont
            fontSize = 16.px
        }
    }
}