package com.vitekkor.view

import com.vitekkor.controller.MyController
import tornadofx.*
import java.io.File

class GameView : View() {
    private val controller: MyController by inject()
    override val root = stackpane {}

    init {
        controller.loadLabyrinth(File(resources.url("/labyrinths/3x3.txt").toURI()))
        val stackPane = controller.createMap()
        var x = 0.0
        var y = 0.0
        var lastTranslateX = 0.0
        var lastTranslateY = 0.0
        stackPane.setOnMousePressed { e ->
            lastTranslateX = stackPane.translateX
            lastTranslateY = stackPane.translateY
            x = e.screenX
            y = e.screenY
        }
        stackPane.setOnMouseDragged { e ->
            stackPane.translateX = lastTranslateX + e.screenX - x
            stackPane.translateY = lastTranslateY + e.screenY - y
        }
        stackPane.setOnScroll { e ->
            var zoomFactor = 1.05
            val deltaY: Double = e.deltaY
            if (deltaY < 0) {
                zoomFactor = 2.0 - zoomFactor
            }
            stackPane.scaleX *= zoomFactor
            stackPane.scaleY *= zoomFactor
        }
        root.add(stackPane)
    }
}
