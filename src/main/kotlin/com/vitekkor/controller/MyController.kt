package com.vitekkor.controller

import com.vitekkor.Styles
import com.vitekkor.model.core.*
import com.vitekkor.model.core.labyrinth.GameMaster
import com.vitekkor.model.core.labyrinth.Labyrinth
import com.vitekkor.model.core.player.Human
import com.vitekkor.model.core.player.Humanlike
import com.vitekkor.model.core.player.Searcher
import com.vitekkor.view.GamePreView
import com.vitekkor.view.GameView
import com.vitekkor.view.MainMenuView
import com.vitekkor.view.MainView
import javafx.animation.Interpolator
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.stage.StageStyle
import javafx.util.Duration
import tornadofx.*
import java.io.File


class MyController : Controller() {
    private val gameView: GameView by inject()
    private val mainView = find(MainView::class)
    private val gamePreView = find(GamePreView::class)
    private val mainMenuView = find(MainMenuView::class)
    private lateinit var labyrinth: Labyrinth

    //var backButton: Button? = null
    fun loadLabyrinth(file: File? = null, size: Pair<Int, Int>? = null): Boolean {
        return if (file != null) {
            load(file)
        } else {
            if (size != null) {
                val path = resources.url("/labyrinths/${size.first}x${size.second}.txt").toURI()
                val localLabyrinth = File(path)
                load(localLabyrinth)
            } else {
                showAlert("File is not selected")
                false
            }
        }
    }

    private fun load(file: File): Boolean {
        try {
            labyrinth = Labyrinth.createFromFile(file)
            return true
        } catch (e: IllegalArgumentException) {
            showAlert(e.message!!)
        } catch (e: IllegalStateException) {
            showAlert(e.message!!)
        }
        return false
    }

    private val howTo = File(resources.url("/error.txt").toURI()).readText()
    private var errorAlert: Alert? = null

    private fun showAlert(message: String) {
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
        if (errorAlert == null) {
            errorAlert = createDialog("Error", message, contentText, Alert.AlertType.ERROR)
            errorAlert!!.graphic = ImageView(Image(resources.stream("/error.png"))).apply {
                tooltip(howTo) {
                    showDelay = 0.1.seconds
                    showDuration = Duration.INDEFINITE
                    font = Font.font("Consolas")
                }
            }
        } else {
            errorAlert!!.headerText = message
            errorAlert!!.contentText = contentText
        }
        errorAlert!!.showAndWait()
    }

    private lateinit var frontWall: Image
    private lateinit var rearWall: Image
    private lateinit var rightWall: Image
    private lateinit var leftWall: Image
    private lateinit var leftWall1: Image
    private lateinit var emptyCell: Image
    private lateinit var wormhole: Image
    private lateinit var treasure: Image
    private lateinit var treasureCollected: Image
    private lateinit var entrance1: Image
    private lateinit var entrance2: Image
    private lateinit var exit1: Image
    private lateinit var exit2: Image
    private lateinit var playerTile: Image

    //private lateinit var playerMakesMove: ArrayList<Image>
    fun loadAssets() {
        frontWall = Image(resources.stream("/tiles/wall_front.png"))
        rearWall = Image(resources.stream("/tiles/wall_rear.png"))
        rightWall = Image(resources.stream("/tiles/wall_right.png"))
        leftWall = Image(resources.stream("/tiles/wall_left2.png"))
        leftWall1 = Image(resources.stream("/tiles/wall_left.png"))
        emptyCell = Image(resources.stream("/tiles/empty_cell1.png"))
        wormhole = Image(resources.stream("/tiles/wormhole.png"))
        treasure = Image(resources.stream("/tiles/treasure.png"))
        //treasureCollected = Image(resources.stream("/tiles/treasure_collected.png"))
        entrance1 = Image(resources.stream("/tiles/entrance1.png"))
        entrance2 = Image(resources.stream("/tiles/entrance2.png"))
        exit1 = Image(resources.stream("/tiles/exit1.png"))
        exit2 = Image(resources.stream("/tiles/exit2.png"))
        playerTile = Image(resources.stream("/tiles/player_moves/player_1.png"))
        //for (i in 1..15) playerMakesMove.add(Image(resources.stream("/tiles/player_moves/player_$i.png")))
    }

    fun getTile(type: String): ImageView = ImageView().apply {
        this.image = when (type) {
            "front_wall" -> frontWall
            "rear_wall" -> rearWall
            "right_wall" -> rightWall
            "left_wall" -> leftWall
            "left_wall1" -> leftWall1
            "wormhole" -> wormhole
            "treasure" -> treasure
            "treasure_collected" -> treasureCollected
            "entrance1" -> entrance1
            "entrance2" -> entrance2
            "exit1" -> exit1
            "exit2" -> exit2
            "player" -> playerTile
            else -> emptyCell
        }
        this.fitHeight = this.image.height / 3.0
        this.fitWidth = this.image.width / 3.0
        isVisible = false
    }

    private lateinit var playerMovesAnimation: ImageView

    /*private fun createMovesAnimation():Group = Group().apply {
        timeline(true) { cycleCount = Animation.INDEFINITE; keyframe(1.seconds) { keyvalue()}  }
    }*/
    private val map = mutableMapOf<Pair<Int, Int>, ImageView>()

    fun createMap(): StackPane {
        map.clear()
        val playerStackPane = StackPane()
        val stackPane = StackPane()
        fun getStartOrExit(entrance: Boolean, x: Int, y: Int): ImageView {
            val number = if (labyrinth[x, y + 1] is Empty || labyrinth[x, y - 1] is Empty) 1 else 2
            val tileName = if (entrance) "entrance" else "exit"
            val tile = getTile("$tileName$number")
            tile.translateX = x * DX_X - DY_X * y
            tile.translateY = x * DX_Y + DY_Y * y
            return tile
        }

        fun getEmptyCell(x: Int, y: Int): ImageView {
            val tile = getTile(labyrinth[x, y].toString())
            tile.translateX = x * DX_X - DY_X * y
            tile.translateY = x * DX_Y + DY_Y * y
            return tile
        }

        for (i in 0 until labyrinth.width) {
            val rearWall = getTile("rear_wall")
            rearWall.translateX = i * DX_X + DY_X
            rearWall.translateY = i * DX_Y - DY_Y
            stackPane.add(rearWall)
            map[i to -1] = rearWall
        }
        for (i in 0 until labyrinth.height) {
            for (j in 0 until labyrinth.width) {
                if (j == 0) {
                    val leftWall = getTile("left_wall1")
                    leftWall.translateX = -DX_X - DY_X * i
                    leftWall.translateY = -DX_Y + DY_Y * i
                    stackPane.add(leftWall)
                    map[-1 to i] = leftWall
                }
                when (labyrinth[j, i].toString()) {
                    "wall" -> {
                        val wall = getTile("left_wall")
                        wall.translateX = j * DX_X - DY_X * i
                        wall.translateY = j * DX_Y + DY_Y * i
                        stackPane.add(wall)
                        map[j to i] = wall
                    }
                    "treasure" -> {
                        val treasure = getTile("treasure")
                        treasure.translateX = j * DX_X - DY_X * i
                        treasure.translateY = j * DX_Y + DY_Y * i
                        stackPane.add(treasure)
                        map[j to i] = treasure
                    }
                    "emptyCell" -> {
                        val emptyCell = getEmptyCell(j, i)
                        stackPane.add(emptyCell)
                        map[j to i] = emptyCell
                    }
                    "wormhole" -> {
                        val wormhole = getTile("wormhole")
                        wormhole.translateX = j * DX_X - DY_X * i
                        wormhole.translateY = j * DX_Y + DY_Y * i
                        stackPane.add(wormhole)
                        map[j to i] = wormhole
                    }
                    "entrance" -> {
                        val entrance = getStartOrExit(true, j, i)
                        entrance.isVisible = true
                        playerMovesAnimation = getTile("player")
                        playerMovesAnimation.isVisible = true
                        playerMovesAnimation.translateX = entrance.translateX
                        playerMovesAnimation.translateY = entrance.translateY
                        stackPane.add(entrance)
                        playerStackPane.add(playerMovesAnimation)
                        map[j to i] = entrance
                    }
                    "exit" -> {
                        val exit = getStartOrExit(false, j, i)
                        //exit.isVisible = false
                        stackPane.add(exit)
                        map[j to i] = exit
                    }
                }
            }
            val wall = getTile("right_wall")
            wall.translateX = DX_X * labyrinth.width - DY_X * i
            wall.translateY = DX_Y * labyrinth.width + DY_Y * i
            stackPane.add(wall)
            map[labyrinth.width to i] = wall
        }
        for (i in 0 until labyrinth.width) {
            val frontWall = getTile("front_wall")
            frontWall.translateX = i * DX_X - DY_X * labyrinth.height
            frontWall.translateY = i * DX_Y + DY_Y * labyrinth.height
            stackPane.add(frontWall)
            map[i to labyrinth.height] = frontWall
        }
        stackPane.add(playerStackPane)
        return stackPane
    }


    private lateinit var playerLocation: Location

    fun showMoveResult(result: MoveResult) {
        val move: WalkMove = player!!.getNextMove() as WalkMove
        if (result.successful) {
            val (x, y) = when (move.direction) {
                Direction.NORTH -> {
                    playerLocation = playerLocation.copy(y = playerLocation.y - 1)
                    DY_X to -DY_Y
                }
                Direction.EAST -> {
                    playerLocation = playerLocation.copy(x = playerLocation.x + 1)
                    DX_X to DX_Y
                }
                Direction.SOUTH -> {
                    playerLocation = playerLocation.copy(y = playerLocation.y + 1)
                    -DY_X to DY_Y
                }
                Direction.WEST -> {
                    playerLocation = playerLocation.copy(x = playerLocation.x - 1)
                    -DX_X to -DX_Y
                }
            }
            map[playerLocation.x to playerLocation.y]!!.isVisible = true
            timeline(true) {
                keyframe(1.seconds) {
                    keyvalue(playerMovesAnimation.translateXProperty(), x + playerMovesAnimation.translateX)
                    keyvalue(playerMovesAnimation.translateYProperty(), y + playerMovesAnimation.translateY)
                }
                setOnFinished {
                    if (result.room is Wormhole) {
                        val newXOldX = labyrinth.wormholeMap.getValue(playerLocation).x - playerLocation.x
                        val newYOldY = labyrinth.wormholeMap.getValue(playerLocation).y - playerLocation.y
                        playerLocation = labyrinth.wormholeMap.getValue(playerLocation)
                        val newX = playerMovesAnimation.translateX + newXOldX * DX_X - newYOldY * DY_X
                        val newY = playerMovesAnimation.translateY + newYOldY * DY_Y + newXOldX * DX_Y
                        map[playerLocation.x to playerLocation.y]!!.isVisible = true
                        timeline(true) {
                            keyframe(0.67.seconds) {
                                keyvalue(playerMovesAnimation.translateXProperty(), newX, Interpolator.EASE_BOTH)
                                keyvalue(playerMovesAnimation.translateYProperty(), newY, Interpolator.EASE_BOTH)
                                setOnFinished { moveNotMadeProperty.value = true }
                            }
                        }
                    } else moveNotMadeProperty.value = true
                }
            }

        } else {
            val pos = move.direction + playerLocation; map[pos.x to pos.y]!!.isVisible = true
            moveNotMadeProperty.value = true
        }
        if (!result.condition.exitReached) {
            val tooltip = Tooltip(result.status)
            tooltip.opacity = 0.0
            tooltip.show(gameView.currentWindow)
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
    }


    private var player: Human? = null
    private var gameMaster: GameMaster? = null
    var moveLimit = 1000
    var name = "Player"
    private val moveNotMadeProperty = SimpleBooleanProperty(true).apply {
        addListener(ChangeListener { _, _, moveNotMade ->
            if (moveNotMade && notAHuman)
                if (!moveResult.exitReached)
                    makeMove((player!!.getNextMove() as WalkMove).direction)
                else displayPassageCompleted()
        })
    }
    private lateinit var moveResult: GameMaster.GameResult

    fun makeMove(direction: Direction) {
        if (moveNotMadeProperty.value) {
            moveNotMadeProperty.value = false
            if (!notAHuman) player!!.setNextMove(WalkMove(direction))
            val moves = gameMaster!!.moves
            var wallCount = 0
            if (moves < moveLimit) {
                val oldMoves = gameMaster!!.moves
                moveResult = gameMaster!!.makeMove()
                val newMoves = gameMaster!!.moves
                wallCount += if (oldMoves == newMoves) 1 else 0
                if (wallCount >= 100) endGame(moveResult)
                gameView.setMovesLeft(moveLimit - newMoves)
                if (moveResult.exitReached && !notAHuman) endGame(moveResult)
            } else endGame(GameMaster.GameResult(moves, exitReached = false))
        }
    }

    fun startGame(): Int {
        notAHuman = false
        if (player == null) player = Human()
        labyrinth.recover()
        if (gameMaster == null) gameMaster = GameMaster(labyrinth, player!!) else {
            gameMaster!!.setNewLabyrinth(labyrinth)
            gameMaster!!.setNewPlayer(player!!)
        }
        playerLocation = labyrinth.entrances[0]
        return moveLimit
    }

    private fun endGame(result: GameMaster.GameResult) {
        val headerText = if (result.exitReached) "You Win!" else "Game Over"
        val contentText = if (result.exitReached)
            "Congratulations! You made ${result.moves} moves, collected treasures, and reached the exit."
        else "Unfortunately, you lost. Try again."
        val alert = createDialog("Game Result", headerText, contentText, Alert.AlertType.INFORMATION)
        val toGamePreView = ButtonType("Play another labyrinth")
        val playAgain = ButtonType("Play again")
        val toMainMenu = ButtonType("Menu")
        val tryAgain = ButtonType("Try again")
        val buttons = arrayListOf<ButtonType>()
        if (result.exitReached) {
            buttons.add(toGamePreView); buttons.add(playAgain)
        } else buttons.add(tryAgain)
        buttons.add(toMainMenu)
        alert.buttonTypes.setAll(buttons)
        val dialogResult = alert.showAndWait()
        when (dialogResult.get()) {
            toGamePreView -> {
                mainView.root.center.replaceWith(gamePreView.root)
                gameView.replaceWith<MainView>(ViewTransition.Fade(0.3.seconds))
            } // go to game settings
            toMainMenu -> {
                mainView.root.center.replaceWith(mainMenuView.root)
                gameView.replaceWith<MainView>(ViewTransition.Fade(0.3.seconds))
            } // go to menu
            tryAgain -> tryAgain() //reload game
            playAgain -> tryAgain()
        }
    }

    private fun tryAgain() {
        gameView.newGame()
        moveNotMadeProperty.value = true
        notAHuman = false
    }

    fun exitFromGameView() {
        val contentText = "All your progress will be reset"
        val alert = createDialog("Exit", "All your progress will be reset", contentText, Alert.AlertType.CONFIRMATION)
        val yes = ButtonType("Yes")
        val no = ButtonType("No")
        alert.buttonTypes.setAll(yes, no)
        val isHuman = notAHuman
        notAHuman = false
        val dialogResult = alert.showAndWait()
        when (dialogResult.get()) {
            yes -> {
                mainView.root.center.replaceWith(gamePreView.root)
                gameView.replaceWith<MainView>(ViewTransition.Fade(0.3.seconds))
            }
            no -> {
                alert.close()
                notAHuman = isHuman
                moveNotMadeProperty.value = false
                moveNotMadeProperty.value = true
            }
        }
    }

    private fun createDialog(title: String, headerText: String, contentText: String, alertType: Alert.AlertType): Alert {
        val alert = Alert(alertType)
        alert.initStyle(StageStyle.UNDECORATED)
        alert.title = title
        alert.headerText = headerText
        val expContent = GridPane()
        val text = Text(contentText)
        text.fill = Paint.valueOf(Styles.colorOfText)
        text.font = Styles.dialogFont
        text.wrapIn(expContent)
        expContent.maxWidth = Double.MAX_VALUE
        alert.dialogPane.content = expContent
        val dialogPane = alert.dialogPane
        dialogPane.stylesheets.add(resources["/dialog.css"])
        dialogPane.styleClass.add("notification")
        return alert
    }

    private var notAHuman = false
    fun passLabyrinth() {
        notAHuman = false
        gameView.newGame()
        val status = TaskStatus()
        var result: List<Move> = emptyList()
        runAsync(status) {
            labyrinth.recover()
            result = Searcher.searchPath(labyrinth, moveLimit)
        }
        val alert = createDialog("Terra Incognita", "Trying to solve..", "", Alert.AlertType.INFORMATION)
        val label = Text("").apply {
            fill = Paint.valueOf(Styles.colorOfText)
            font = Styles.dialogFont
            visibleWhen { status.completed }
        }
        val vBox = VBox().apply {
            progressbar(status.progress) {
                visibleWhen { status.running }
            }
            add(label)
            maxWidth = Double.MAX_VALUE
        }
        status.completed.addListener(ChangeListener { _, _, completed ->
            label.text = if (completed && result.isNotEmpty()) "Successful!\nPress Ok to show the pass"
            else "Couldn't solve the labyrinth..."
        })
        alert.dialogPane.content = vBox
        val dialogResult = alert.showAndWait()
        fun showPath() {
            if (status.completed.value) {
                alert.close()
                if (result.isNotEmpty()) {
                    moveNotMadeProperty.value = true
                    notAHuman = true
                    playerLocation = labyrinth.entrances[0]
                    player = Humanlike(result)
                    gameMaster!!.setNewPlayer(player!!)
                    makeMove((player!!.getNextMove() as WalkMove).direction)
                }
            }
        }
        try {
            if (dialogResult.get().text == "OK") showPath()
        } catch (e: NoSuchElementException) {
           showPath()
        }

    }

    private var displayPassageCompletedDialog: Alert? = null
    private fun displayPassageCompleted() {
        if (displayPassageCompletedDialog == null) {
            displayPassageCompletedDialog = createDialog(
                    "",
                    "That's it.",
                    "Here is the solution to the labyrinth. Click OK to exit",
                    Alert.AlertType.INFORMATION
            )
        }
        val taskStatus = TaskStatus().apply {
            this.completed.addListener(ChangeListener { _, _, completed ->
                if (completed && displayPassageCompletedDialog!!.result.text == "OK") {
                    displayPassageCompletedDialog!!.close()
                    mainView.root.center.replaceWith(gamePreView.root)
                    gameView.replaceWith<MainView>(ViewTransition.Fade(0.3.seconds))
                }
            })
        }
        displayPassageCompletedDialog!!.result = null
        displayPassageCompletedDialog!!.show()
        runAsync(taskStatus) {
            while (displayPassageCompletedDialog!!.result == null) {
            }
        }
    }

    companion object {
        /**X offset for x (66.0)**/
        private const val DX_X = 66.0

        /**X offset for y (20.3)**/
        private const val DX_Y = 20.3

        /**Y offset for x (39.0)**/
        private const val DY_X = 39.0

        /**Y offset for y (37.0)**/
        private const val DY_Y = 37.0
    }
}