package com.vitekkor.view

import com.vitekkor.controller.MyController
import javafx.beans.property.SimpleIntegerProperty
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File

class GamePreView : View("My View") {
    private val controller: MyController by inject()
    private var min = 2
    private val length = List(39) { min++ }
    private val wide = length.subList(0, 23)
    private val selectedItem1 = SimpleIntegerProperty(length.first())
    private val selectedItem2 = SimpleIntegerProperty(wide.first())
    private val emptyHBox = hbox { alignment = Pos.CENTER }
    private val comboboxWidth = combobox(selectedItem1, length)
    private val comboboxHeight = combobox(selectedItem2, wide)
    private val defaultLabyrinth = hbox {
        hboxConstraints { margin = Insets(20.0) }
        label("Choose the size of the labyrinth") { hboxConstraints { marginLeftRight(20.0) } }
        add(comboboxWidth)
        label("X") { hboxConstraints { marginLeftRight(5.0) } }
        add(comboboxHeight)
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
        shortcut("Esc") { replaceWith<MainMenuView>() }
        alignment = Pos.CENTER
        hbox {
            alignment = Pos.CENTER
            vboxConstraints { margin = Insets(20.0); marginTop = 50.0 }
            label("Enter the player's name") { hboxConstraints { marginLeftRight(20.0) } }
            add(playerName)
        }
        add(emptyHBox)
        emptyHBox.add(defaultLabyrinth)
        hbox {
            alignment = Pos.CENTER
            button("Choose labyrinth from file").action {
                file = chooseFile("Choose file", extensions, initialDirectory, FileChooserMode.Single)
                defaultLabyrinth.removeFromParent()
                fileLabel.text = "Chosen file: ${file[0].name}"
                add(fileLabel.hboxConstraints { marginLeftRight(20.0) })
                add(cancelButton)
            }
        }
        button("Start") {
            vboxConstraints { margin = Insets(20.0) }
            action {
                when {
                    playerName.text.trim().isEmpty() -> playerName.addClass("error")
                    defaultLabyrinth.parent != null -> controller.loadLabyrinth(size =
                    comboboxWidth.selectedItem!!.toInt() to comboboxHeight.selectedItem!!.toInt())
                    else -> controller.loadLabyrinth(file[0])
                }
            }
        }
    }

    init {
        playerName.onMousePressed = EventHandler { playerName.removeClass("error") }
        playerName.textProperty().addListener { _, _, _ -> playerName.removeClass("error") }
    }
}
