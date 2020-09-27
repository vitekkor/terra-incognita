package com.vitekkor.view

import javafx.beans.property.SimpleIntegerProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File

class GamePreView : View("My View") {
    private var min = 2
    private val length = List(39) { min++ }
    private val wide = length.subList(0, 23)
    private val selectedItem1 = SimpleIntegerProperty(length.first())
    private val selectedItem2 = SimpleIntegerProperty(wide.first())
    private val emptyHBox = hbox { alignment = Pos.CENTER }
    private val defaultLabyrinth = hbox {
        hboxConstraints { margin = Insets(20.0) }
        label("Choose the size of the labyrinth") { hboxConstraints { marginLeftRight(20.0) } }
        combobox(selectedItem1, length)
        label("X") { hboxConstraints { marginLeftRight(5.0) } }
        combobox(selectedItem2, wide)
    }
    private lateinit var file: List<File>
    private val cancelButton = button("cancel") {
        action {
            fileLabel.text = ""
            fileLabel.removeFromParent()
            emptyHBox.add(defaultLabyrinth)
            removeFromParent()
        }
    }
    private val fileLabel = label()


    override val root = vbox {
        shortcut("Esc") { replaceWith<MainMenuView>() }
        alignment = Pos.CENTER
        hbox {
            alignment = Pos.CENTER
            vboxConstraints { margin = Insets(20.0); marginTop = 50.0 }
            label("Enter the player's name") { hboxConstraints { marginLeftRight(20.0) } }
            textfield("Name") { hboxConstraints { marginLeftRight(20.0) } }
        }
        add(emptyHBox)
        emptyHBox.add(defaultLabyrinth)
        hbox {
            alignment = Pos.CENTER
            button("Choose labyrinth from file").action {
                val extensions = arrayOf(FileChooser.ExtensionFilter("txt", listOf("*.txt")))
                file = chooseFile("Choose file", extensions, mode = FileChooserMode.Single)
                if (file != listOf<File>()) {
                    defaultLabyrinth.removeFromParent()
                    fileLabel.text = "Chosen file: ${file[0].name}"
                    add(fileLabel.hboxConstraints { marginLeftRight(20.0) })
                    add(cancelButton)
                }
            }
        }
    }
}
