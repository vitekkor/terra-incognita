package com.vitekkor.view

import com.vitekkor.controller.MyController
import com.vitekkor.model.core.Direction
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.input.KeyCombination
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import tornadofx.*

/**
 * Game view
 */
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
            controller.passLabyrinth()
        }
        stackpaneConstraints { alignment = Pos.TOP_LEFT; margin = Insets(20.0) }
    }
    override val root = stackpane {
        background = Background(BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY))
        shortcut(KeyCombination.valueOf("w")) { controller.makeMove(Direction.NORTH) }
        shortcut(KeyCombination.valueOf("d")) { controller.makeMove(Direction.EAST) }
        shortcut(KeyCombination.valueOf("s")) { controller.makeMove(Direction.SOUTH) }
        shortcut(KeyCombination.valueOf("a")) { controller.makeMove(Direction.WEST) }
        shortcut(KeyCombination.valueOf("Esc")) { controller.exitFromGameView() }
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
        val (stackPane, translateX, translateY) = controller.createMap()
        root.add(stackPane)
        stackPane.toBack()
        stackPane.translateX = translateX
        stackPane.translateY = translateY
        movesLimitLabel.text = "Moves left: ${controller.startGame()}"
    }
}
