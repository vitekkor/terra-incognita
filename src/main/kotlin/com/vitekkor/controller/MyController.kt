package com.vitekkor.controller

import com.vitekkor.model.core.Empty
import com.vitekkor.model.core.Wall
import com.vitekkor.model.core.labyrinth.Labyrinth
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane
import javafx.scene.layout.StackPane
import javafx.scene.text.Text
import tornadofx.*
import java.io.File


class MyController : Controller() {
    //val view: GamePreView by inject()
    private lateinit var labyrinth: Labyrinth

    //var backButton: Button? = null
    fun loadLabyrinth(file: File? = null, size: Pair<Int, Int>? = null) {
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
            labyrinth = Labyrinth.createFromFile(file)
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
            message.matches(Regex(""".*[Ww]ormhole.+""")) -> "The labyrinth can contain from 0 to 10 holes." +
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

    private lateinit var frontWall: Image
    private lateinit var rearWall: Image
    private lateinit var rightWall: Image
    private lateinit var leftWall: Image
    private lateinit var emptyCell: Image
    private lateinit var wormhole: Image
    private lateinit var treasure: Image
    private lateinit var treasureCollected: Image
    private lateinit var entrance1: Image
    private lateinit var entrance2: Image
    private lateinit var exit1: Image
    private lateinit var exit2: Image
    fun loadAssets() {
        frontWall = Image(resources.stream("/tiles/wall_front.png"))
        rearWall = Image(resources.stream("/tiles/wall_rear.png"))
        rightWall = Image(resources.stream("/tiles/wall_right.png"))
        leftWall = Image(resources.stream("/tiles/wall_left.png"))
        emptyCell = Image(resources.stream("/tiles/empty_cell1.png"))
        wormhole = Image(resources.stream("/tiles/wormhole.png"))
        treasure = Image(resources.stream("/tiles/treasure.png"))
        treasureCollected = Image(resources.stream("/tiles/treasure_collected.png"))
        entrance1 = Image(resources.stream("/tiles/entrance1.png"))
        entrance2 = Image(resources.stream("/tiles/entrance2.png"))
        exit1 = Image(resources.stream("/tiles/exit1.png"))
        exit2 = Image(resources.stream("/tiles/exit2.png"))
    }

    private fun getTile(type: String): ImageView = ImageView().apply {
        this.image = when (type) {
            "front_wall" -> frontWall
            "rear_wall" -> rearWall
            "right_wall" -> rightWall
            "left_wall" -> leftWall
            "wormhole" -> wormhole
            "treasure" -> treasure
            "treasure_collected" -> treasureCollected
            "entrance1" -> entrance1
            "entrance2" -> entrance2
            "exit1" -> exit1
            "exit2" -> exit2
            else -> emptyCell
        }
        this.fitHeight = this.image.height / 3.0
        this.fitWidth = this.image.width / 3.0
    }

    fun createMap(): StackPane {
        val stackPane = StackPane()

        fun getWall(x: Int, y: Int): ImageView {
            var i = x
            var j = y
            while (i < labyrinth.width) {
                if (labyrinth[i, j] is Wall) i++ else break
            }
            val horizontalWalls = i - x
            i = x
            while (j < labyrinth.height) {
                if (labyrinth[i, j] is Wall) j++ else break
            }
            val verticalWalls = j - y
            val wall: ImageView
            wall = if (horizontalWalls > verticalWalls) getTile("front_wall") else getTile("left_wall")
            wall.translateX = y * 66.0 - 39.0 * x
            wall.translateY = y * 20.3 + 37.0 * x
            return wall
        }

        fun getStartOrExit(entrance: Boolean, x: Int, y: Int): ImageView {
            val number = if (labyrinth[x, y + 1] is Empty || labyrinth[x, y - 1] is Empty) 1 else 2
            val tileName = if (entrance) "entrance" else "exit"
            val tile = getTile("$tileName$number")
            if (number == 2) {
                tile.translateX = x * 66.0 - 24.0 * y
                tile.translateY = x * 20.3 + 23.0 * y
            } else {
                tile.translateX = x * 66.0 - 26.0 * y
                tile.translateY = x * 20.3 + 20.0 * y
            }
            return tile
        }

        fun getEmptyCell(x: Int, y: Int): ImageView {
            val tile = getTile(labyrinth[x, y].toString())
            tile.translateX = x * 66.0 - 39.0 * y
            tile.translateY = x * 20.3 + 37.0 * y
            return tile
        }

        for (i in 0 until labyrinth.width) {
            stackPane.add(getTile("rear_wall"))
            stackPane.children.last().translateX = i * 66.0 + 39.0
            stackPane.children.last().translateY = i * 20.3 - 37.0
        }
        for (i in 0 until labyrinth.width) {
            for (j in 0 until labyrinth.height) {
                if (j == 0) {
                    stackPane.add(getTile("left_wall"))
                    stackPane.children.last().translateX = -66.0 - 39.0 * i
                    stackPane.children.last().translateY = -20.3 + 37.0 * i
                }
                when (labyrinth[i, j].toString()) {
                    "wall" -> stackPane.add(getWall(j, i))
                    "treasure" -> {
                        val treasure = getTile("treasure")
                        treasure.translateX = j * 66.0 - 39.0 * i
                        treasure.translateY = j * 20.3 + 37.0 * i
                        stackPane.add(treasure)
                        //children.add(getTile("treasure_collected"))
                    }
                    "emptyCell" -> stackPane.add(getEmptyCell(j, i))
                    "wormhole" -> {
                        val tile = getTile(labyrinth[i, j].toString())
                        tile.translateX = j * 66.0 - 32.0 * i
                        tile.translateY = j * 20.3 + 39.0 * i
                        stackPane.add(tile)
                        //children.add(tile)
                    }
                    "entrance" -> {
                        val tile = getStartOrExit(true, j, i)
                        stackPane.add(getEmptyCell(j, i))
                        stackPane.add(tile)
                    }
                    "exit" -> {
                        val tile = getStartOrExit(false, j, i)
                        stackPane.add(getEmptyCell(j, i))
                        stackPane.add(tile)
                    }
                }
            }
            val wall = getTile("right_wall")
            wall.translateX = 66.0 * labyrinth.height - 39.0 * i
            wall.translateY = 20.3 * labyrinth.height + 37.0 * i
            stackPane.add(wall)
        }
        for (i in 0 until labyrinth.width) {
            stackPane.add(getTile("front_wall"))
            stackPane.children.last().translateX = i * 66.0 - 39.0 * labyrinth.height
            stackPane.children.last().translateY = i * 20.3 + 37.0 * labyrinth.height
        }
        return stackPane
    }

}