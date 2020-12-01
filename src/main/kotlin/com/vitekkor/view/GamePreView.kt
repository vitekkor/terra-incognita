package com.vitekkor.view

import com.vitekkor.controller.MyController
import javafx.beans.property.SimpleIntegerProperty
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.layout.HBox
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File

class GamePreView : Fragment() {
    private val gameView: GameView by inject()
    private val mainView: MainView by inject()
    private val controller: MyController by inject()
    private var min = 2
    private lateinit var length: List<Int>
    private lateinit var wide: List<Int>
    private lateinit var selectedItem1: SimpleIntegerProperty
    private lateinit var selectedItem2: SimpleIntegerProperty
    private val emptyHBox = hbox { alignment = Pos.CENTER }
    private lateinit var comboboxWidth: ComboBox<Number>
    private lateinit var comboboxHeight: ComboBox<Number>
    private lateinit var defaultLabyrinth: HBox
    private val movesLimit = textfield("1000") { hboxConstraints { marginLeftRight(20.0) } }
    private val movesLimitHBox = hbox {
        alignment = Pos.CENTER
        hboxConstraints { margin = Insets(20.0) }
        label("Enter moves limit") { hboxConstraints { marginLeftRight(20.0) } }
        add(movesLimit)
    }
    private lateinit var file: List<File>
    private val initialDirectory = File(resources.url("/labyrinths/").toURI())
    private val extensions = arrayOf(FileChooser.ExtensionFilter("txt", listOf("*.txt")))
    private val cancelButton = button("cancel") {
        action {
            fileLabel.text = ""
            fileLabel.removeFromParent()
            emptyHBox.add(defaultLabyrinth)
            removeFromParent()
        }
    }
    private val fileLabel = label()
    private val playerName = textfield("Name") { hboxConstraints { marginLeftRight(20.0) } }


    override val root = vbox {
        addClass("setBackgroundFill")
        alignment = Pos.CENTER
        hbox {
            alignment = Pos.CENTER
            vboxConstraints { margin = Insets(20.0); marginTop = 50.0 }
            label("Enter the player's name") { hboxConstraints { marginLeftRight(20.0) } }
            add(playerName)
        }
        add(movesLimitHBox)
        add(emptyHBox)

        hbox {
            alignment = Pos.CENTER
            button("Choose labyrinth from file").action {
                file = chooseFile("Choose file", extensions, initialDirectory, FileChooserMode.Single)
                if (file.isNotEmpty()) {
                    defaultLabyrinth.removeFromParent()
                    fileLabel.text = "Chosen file: ${file[0].name}"
                    add(fileLabel.hboxConstraints { marginLeftRight(20.0) })
                    add(cancelButton)
                }
            }
        }
        button("Start") {
            vboxConstraints { margin = Insets(20.0) }
            action {
                when {
                    playerName.text.trim().isEmpty() -> playerName.addClass("error")
                    movesLimit.text.trim().isEmpty() || movesLimit.text.trim().toIntOrNull() == null -> movesLimit.addClass("error")
                    defaultLabyrinth.parent != null -> {
                       if (controller.loadLabyrinth(size =
                        comboboxWidth.selectedItem!!.toInt() to comboboxHeight.selectedItem!!.toInt())) {
                           controller.moveLimit = movesLimit.text.trim().toInt()
                           controller.name = playerName.text
                           mainView.replaceWith<GameView>(ViewTransition.Fade(0.3.seconds))
                           gameView.newGame()
                           gameView.setPlayersName(playerName.text)
                       }
                    }
                    else -> {
                        if (controller.loadLabyrinth(file[0])) {
                            controller.moveLimit = movesLimit.text.trim().toInt()
                            controller.name = playerName.text
                            mainView.replaceWith<GameView>(ViewTransition.Fade(0.3.seconds))
                            gameView.newGame()
                            gameView.setPlayersName(playerName.text)
                        }
                    }
                }
            }
        }
    }

    init {
        playerName.onMousePressed = EventHandler { playerName.removeClass("error") }
        playerName.textProperty().addListener { _, _, _ -> playerName.removeClass("error") }
        movesLimit.onMousePressed = EventHandler { movesLimit.removeClass("error") }
        movesLimit.textProperty().addListener { _, _, _ -> movesLimit.removeClass("error") }
        runAsync {
            length = List(39) { min++ }
            wide = length.subList(0, 24)
            selectedItem1 = SimpleIntegerProperty(length.first())
            selectedItem2 = SimpleIntegerProperty(wide.first())
        } ui {
            comboboxWidth = combobox(selectedItem1, length)
            comboboxHeight = combobox(selectedItem2, wide)
            defaultLabyrinth = hbox {
                hboxConstraints { margin = Insets(20.0) }
                label("Choose the size of the labyrinth") { hboxConstraints { marginLeftRight(20.0) } }
                add(comboboxWidth)
                label("X") { hboxConstraints { marginLeftRight(5.0) } }
                add(comboboxHeight)
            }
            emptyHBox.add(defaultLabyrinth)
        }
    }
}
