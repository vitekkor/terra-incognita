package com.vitekkor.view

import com.vitekkor.controller.MyController
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.input.KeyCombination
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Text
import tornadofx.*

class GameView : View() {
    private val controller: MyController by inject()
    private val movesLimitLabel = Label("").apply {
        stackpaneConstraints { alignment = Pos.TOP_RIGHT; margin = Insets(20.0); marginTop = 40.0 }
    }
    private val playersName = Label("").apply {
        stackpaneConstraints { alignment = Pos.TOP_RIGHT; margin = Insets(20.0) }
    }
    private val terraLabel = Label("Terra Incognita").apply {
        stackpaneConstraints { alignment = Pos.TOP_CENTER; margin = Insets(20.0) }
        addClass("main")
    }
    private val helpButton = Button("Help").apply {
        action {
            val tooltip = Tooltip("Help")
            tooltip.opacity = 0.0
            tooltip.show(this@GameView.currentWindow)
            tooltip.opacityProperty().animate(1.0, 0.5.seconds) {
                setOnFinished {
                    timeline(true) {
                        keyframe(0.5.seconds) {}
                        setOnFinished {
                            tooltip.opacityProperty().animate(0.0, 0.5.seconds) {
                                setOnFinished { tooltip.hide() }
                            }
                        }
                    }
                }
            }
        }
        stackpaneConstraints { alignment = Pos.TOP_LEFT; margin = Insets(20.0) }
    }
    override val root = stackpane {
        background = Background(BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY))
        shortcut(KeyCombination.valueOf("w")) { controller.makeMove('w') }
        shortcut(KeyCombination.valueOf("d")) { controller.makeMove('d') }
        shortcut(KeyCombination.valueOf("s")) { controller.makeMove('s') }
        shortcut(KeyCombination.valueOf("a")) { controller.makeMove('a') }
        shortcut(KeyCombination.valueOf("Esc")) { controller.exitFromGameView() }
    }

    init {
        val stackPane = controller.createMap()
        root.add(stackPane)
        stackPane.toBack()
        movableAndZoomableStackpane(stackPane)
        movesLimitLabel.text = "Moves left: ${controller.startGame()}"
    }

    fun setMovesLeft(movesLeft: Int) {
        movesLimitLabel.text = "Moves left: $movesLeft"
    }

    fun setPlayersName(name: String) {
        playersName.text = name
    }

    fun newGame() {
        root.clear()
        root.add(terraLabel)
        root.add(movesLimitLabel)
        root.add(playersName)
        root.add(helpButton)
        val stackPane = controller.createMap()
        root.add(stackPane)
        stackPane.toBack()
        movableAndZoomableStackpane(stackPane)
        movesLimitLabel.text = "Moves left: ${controller.startGame()}"
    }

    private fun movableAndZoomableStackpane(stackPane: StackPane) {
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
    }
}
