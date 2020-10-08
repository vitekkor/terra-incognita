package com.vitekkor.controller

import com.vitekkor.model.core.labyrinth.Labyrinth
import com.vitekkor.view.GamePreView
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.layout.GridPane
import javafx.scene.text.Text
import tornadofx.*
import java.io.File


class MyController : Controller() {
    val view: GamePreView by inject()
    //var backButton: Button? = null
    fun loadLabyrinth(file: File? = null, size: Pair<Int?, Int?> = null to null) {
        if (file != null) {
            load(file)
        } else {
            if (size != null) {
                val path = resources.url("/labyrinths/${size.first}x${size.second}.txt").toURI()
                val localLabyrinth = File(path)
                load(localLabyrinth)
            } else {
                showAlert("File is not selected")
            }
        }
    }

    private fun load(file: File) {
        try {
            Labyrinth.createFromFile(file)
        } catch (e: IllegalArgumentException) {
            showAlert(e.message!!)
        } catch (e: IllegalStateException) {
            showAlert(e.message!!)
        }
    }

    private fun showAlert(message: String) {
        val alert = Alert(AlertType.ERROR)
        alert.title = "Error"
        alert.headerText = message

        val contentText = when {
            message == "Empty File" -> "File could not be empty"
            message.matches(Regex(""".*[Ww]ormhole.+""")) -> "The maze can contain from 0 to 10 holes." +
                    "\nTheir numbering must be continuous from zero to N - 1, where N is their number." +
                    "\nThere can't be two holes with the same number."
            message.matches(Regex("""Illegal labyrinth symbol: \w""")) ->
                "The following characters can be used in the file:" +
                        "\n# - walls\nS - start\nE - end\nT - treasure\nDigit form 0 to 9 - wormholes"
            message == "Illegal size of labyrinth" -> "Please select the file with the correct size of the labyrinth." +
                    "\nMaximum size of labyrinth 40 (width) x 25 (height), minimum 2 x 2"
            message == "Illegal labyrinth symbol" -> "All labyrinth must be rectangular, surrounded by walls on all sides (symbol \"#\")"
            message == "Different row sizes" -> "All labyrinth must be rectangular"
            message == "The labyrinth already contains a start" || message == "The labyrinth must contain a start" ->
                "The starting cell must be one"
            message == "The labyrinth already contains a end" || message == "The labyrinth must contain a end" ->
                "The ending cell must be one"
            message == "The labyrinth must contain at least one treasure" -> "The labyrinth must contain at least one treasure"
            else -> ""
        } + "\nTo view the correct formatting of the file hover over the icon"

        val expContent = GridPane()
        val text = Text(contentText)
        text.wrapIn(expContent)
        expContent.maxWidth = Double.MAX_VALUE
        alert.dialogPane.content = expContent
        //alert.graphic.onMouseEntered =
        alert.showAndWait()
    }
}