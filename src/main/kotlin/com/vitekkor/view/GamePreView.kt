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

/**
 * Game setup screen view: labyrinth selection, setting name and move limit
 */
class GamePreView : Fragment() {
    private val gameView: GameView by inject()
    private val mainView: MainView by inject()
    private val controller: MyController by inject()
    private var min = 2
    private lateinit var height: List<Int>
    private lateinit var width: List<Int>
    private lateinit var selectedHeight: SimpleIntegerProperty
    private lateinit var selectedWidth: SimpleIntegerProperty
    private val emptyHBox = hbox { alignment = Pos.CENTER }

    /**Default labyrinth width*/
    private lateinit var comboboxWidth: ComboBox<Number>

    /**Default labyrinth height*/
    private lateinit var comboboxHeight: ComboBox<Number>

    /**Hbox with default labyrinths*/
    private lateinit var defaultLabyrinth: HBox
    private val movesLimit = textfield("1000") {
        hboxConstraints { marginLeftRight(20.0) }
        style += "-fx-text-fill: #e7e5e5"
    }
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
    private val playerName = textfield("Name") {
        hboxConstraints { marginLeftRight(20.0) }
        style += "-fx-text-fill: #e7e5e5"
    }


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
                file = chooseFile(
                    "Choose file",
                    extensions,
                    initialDirectory,
                    FileChooserMode.Single,
                    this@GamePreView.currentWindow
                )
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
                when {// проверяем корректность введённых имени и лимита ходов
                    playerName.text.trim().isEmpty() -> playerName.addClass("error")
                    movesLimit.text.trim().isEmpty() ||
                            movesLimit.text.trim().toIntOrNull() ?: -1 < 1 -> movesLimit.addClass("error")
                    defaultLabyrinth.parent != null -> {// если выбран лабиринт по умолчанию
                        // пытаемся создать лабиринт
                        if (controller.loadLabyrinth(
                                size =
                                comboboxWidth.selectedItem!!.toInt() to comboboxHeight.selectedItem!!.toInt()
                            )
                        ) {
                            controller.moveLimit = movesLimit.text.trim().toInt() // устанавливаем лимит ходов
                            controller.name = playerName.text // имя
                            // и начинаем игру
                            mainView.replaceWith<GameView>(ViewTransition.Fade(0.3.seconds))
                            gameView.newGame()
                            gameView.setPlayersName(playerName.text)
                        }
                    }
                    else -> { // если выбран пользовательский лабиринт из файла
                        // пытаемся создать лабиринт
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
        runAsync { // заполняем размеры дефолтных лабиринтов
            height = List(39) { min++ }
            width = height.subList(0, 24)
            selectedHeight = SimpleIntegerProperty(height.first())
            selectedWidth = SimpleIntegerProperty(width.first())
        } ui {
            comboboxWidth = combobox(selectedHeight, height)
            comboboxHeight = combobox(selectedWidth, width)
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

    override fun onDock() {
        super.onDock()
        movesLimit.text = controller.moveLimit.toString()
        playerName.text = controller.name
        playerName.style += ";-fx-text-fill: #e7e5e5"
        movesLimit.style += ";-fx-text-fill: #e7e5e5"
    }
}
