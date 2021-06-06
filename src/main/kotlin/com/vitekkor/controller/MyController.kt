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
import javafx.scene.text.Text
import javafx.stage.StageStyle
import javafx.stage.Window
import javafx.util.Duration
import tornadofx.*
import java.io.File

/**
 * The controller that provides the connection between the game logic and its display
 */
class MyController : Controller() {
    private lateinit var stackMap: StackPane
    private val gameView: GameView by inject()
    private val mainView = find(MainView::class)
    private val gamePreView = find(GamePreView::class)
    private val mainMenuView = find(MainMenuView::class)

    /**File formatting rules.*/
    private val fileFormat = resources.text("/error.txt")

    /**Alert about an error when trying to create a labyrinth from file.*/
    private var errorAlert: Alert? = null

    /**Player's position.*/
    private lateinit var playerLocation: Location

    /**Labyrinth map with coordinates (upper-left) of each tile.*/
    private val map = mutableMapOf<Pair<Int, Int>, ImageView>()

    /**Current player.*/
    private var player: Human? = null

    /**[GameMaster]*/
    private var gameMaster: GameMaster? = null

    /**Current labyrinth.*/
    private lateinit var labyrinth: Labyrinth
    var moveLimit = 1000

    /**Player's name.*/
    var name = "Player"

    /**True if artificial intelligence is playing the game and false if human is playing*/
    private var notAHuman = false

    /**Boolean property: true if player can make new move and false if not*/
    private val moveAllowedProperty = SimpleBooleanProperty(true).apply {
        addListener(ChangeListener { _, _, moveAllowed ->
            if (moveAllowed && notAHuman) // если можно делать ход и если играет искусственный интеллект
                if (!moveResult.exitReached) // делаем ходы, пока не достигнем выхода
                    makeMove((player!!.getNextMove() as WalkMove).direction)
                // достигли выхода - выводим сообщение о том, что показ прохождения лабиринта закончен
                else displayPassageCompleted()
        })
    }

    /**Result of move*/
    private lateinit var moveResult: GameMaster.GameResult

    /*private fun createMovesAnimation():Group = Group().apply {
        timeline(true) { cycleCount = Animation.INDEFINITE; keyframe(1.seconds) { keyvalue()}  }
    }*/


    /**
     * Creates a labyrinth from a user's [file] or takes a file from resource with a specific [size].
     * @return <code>True</code> if loading was successful, and <code>False</code> if not
     */
    fun loadLabyrinth(file: File? = null, size: Pair<Int, Int>? = null): Boolean {
        return if (file != null) {
            load(file)
        } else {
            if (size != null) {
                val localLabyrinth = File("./labyrinths/${size.first}x${size.second}.txt")
                load(localLabyrinth)
            } else {
                showAlert("File is not selected")
                false
            }
        }
    }

    /**
     * Creates a labyrinth from a [file].
     * @return <code>True</code> if loading was successful, and <code>False</code> if not
     */
    private fun load(file: File): Boolean {
        try {
            labyrinth = Labyrinth.createFromFile(file)
            return true
        } catch (e: IllegalArgumentException) { // выводим сообщение об ошибке при попытке создать лабиринт из файла
            showAlert(e.message!!)
        } catch (e: IllegalStateException) {
            showAlert(e.message!!)
        }
        return false
    }

    /**
     * Called when an error message about a failed attempt to create a labyrinth should be displayed.
     * @param message
     * the string of the detail message of the exception that was thrown when trying to create the labyrinth
     */
    private fun showAlert(message: String) {
        // преобразуем сообщение в "человеческий" вид
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
        // создаем диалоговое окно, если ещё не было создано
        if (errorAlert == null) {
            errorAlert = createDialog("Error", message, contentText, Alert.AlertType.ERROR, gamePreView.currentWindow)
            errorAlert!!.graphic = ImageView(Image(resources.stream("/error.png"))).apply {
                tooltip(fileFormat) {
                    styleClass.remove("tooltip")
                    styleClass.add("error_tooltip")
                    showDelay = 0.1.seconds
                    showDuration = Duration.INDEFINITE
                }
            }
        } else { // передаем сообщение об ошибке
            errorAlert!!.headerText = message
            errorAlert!!.contentText = contentText
        }
        errorAlert!!.showAndWait()
    }

    /*Tiles*/
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

    // предполагалось, что во время ходьбы будет анимация движения ног.
    // Возможно в одной из следующих версий я всё таки реализую это
    private lateinit var playerMovesAnimation: ImageView

    //private lateinit var playerMakesMove: ArrayList<Image>
    /**Load assets from resources*/
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

    /**
     * Returns image view with a specific [type] of tile.
     * As default - empty cell.
     */
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

    /**
     * Creates a map of the labyrinth that was previously created by the controller.
     * Call only after calling [loadLabyrinth]!
     * @return StackPane with tiles
     */
    fun createMap(): Triple<StackPane, Double, Double> {
        map.clear()
        val playerStackPane = StackPane()
        val stackPane = StackPane()
        var entranceCoordinates = 0.0 to 0.0

        // "строим" верхние стены
        for (i in 0 until labyrinth.width) {
            val rearWall = getTile("rear_wall")
            rearWall.translateX = i * DX_X + DY_X // смещение по оси X
            rearWall.translateY = i * DX_Y - DY_Y // смещение по оси Y
            stackPane.add(rearWall)
            map[i to -1] = rearWall
        }
        // строим
        for (i in 0 until labyrinth.height) {
            for (j in 0 until labyrinth.width) {
                if (j == 0) { // добавляем левую боковую стену
                    val leftWall = getTile("left_wall1")
                    leftWall.translateX = -DX_X - DY_X * i
                    leftWall.translateY = -DX_Y + DY_Y * i
                    stackPane.add(leftWall)
                    map[-1 to i] = leftWall
                }
                when (labyrinth[j, i].toString()) {
                    "wall" -> { // добавляем стену
                        val wall = getTile("left_wall")
                        wall.translateX = j * DX_X - DY_X * i
                        wall.translateY = j * DX_Y + DY_Y * i
                        stackPane.add(wall)
                        map[j to i] = wall
                    }
                    "treasure" -> { // добавляем сокровище
                        val treasure = getTile("treasure")
                        treasure.translateX = j * DX_X - DY_X * i
                        treasure.translateY = j * DX_Y + DY_Y * i
                        stackPane.add(treasure)
                        map[j to i] = treasure
                    }
                    "emptyCell" -> { // добавляем пустую клетку
                        val emptyCell = getEmptyCell(j, i)
                        stackPane.add(emptyCell)
                        map[j to i] = emptyCell
                    }
                    "wormhole" -> { // добавляем червоточину
                        val wormhole = getTile("wormhole")
                        wormhole.translateX = j * DX_X - DY_X * i
                        wormhole.translateY = j * DX_Y + DY_Y * i
                        stackPane.add(wormhole)
                        map[j to i] = wormhole
                    }
                    "entrance" -> { // добавляем вход
                        val entrance = getStartOrExit(true, j, i)
                        entranceCoordinates = -entrance.translateX to -entrance.translateY
                        entrance.isVisible = true
                        // добавляем игрока
                        playerMovesAnimation = getTile("player")
                        playerMovesAnimation.isVisible = true
                        playerMovesAnimation.translateX = entrance.translateX
                        playerMovesAnimation.translateY = entrance.translateY

                        stackPane.add(entrance)
                        playerStackPane.add(playerMovesAnimation)
                        map[j to i] = entrance
                    }
                    "exit" -> { // добавляем выход
                        val exit = getStartOrExit(false, j, i)
                        stackPane.add(exit)
                        map[j to i] = exit
                    }
                }
            }
            // добавляем правую боковую стену
            val wall = getTile("right_wall")
            wall.translateX = DX_X * labyrinth.width - DY_X * i
            wall.translateY = DX_Y * labyrinth.width + DY_Y * i
            stackPane.add(wall)
            map[labyrinth.width to i] = wall
        }
        // "строим" нижние стены
        for (i in 0 until labyrinth.width) {
            val frontWall = getTile("front_wall")
            frontWall.translateX = i * DX_X - DY_X * labyrinth.height
            frontWall.translateY = i * DX_Y + DY_Y * labyrinth.height
            stackPane.add(frontWall)
            map[i to labyrinth.height] = frontWall
        }

        stackPane.add(playerStackPane) // добавляем игрока
        stackMap = stackPane
        return Triple(stackPane, entranceCoordinates.first, entranceCoordinates.second)
    }

    /**
     * Returns the empty cell tile with correct offset
     */
    private fun getEmptyCell(x: Int, y: Int): ImageView {
        val tile = getTile(labyrinth[x, y].toString())
        tile.translateX = x * DX_X - DY_X * y // смещение по оси X
        tile.translateY = x * DX_Y + DY_Y * y // смещение по оси X
        return tile
    }

    /**
     * Returns the starting or ending tile correctly rotated to match the other cells in the labyrinth and
     * with correct offset
     */
    private fun getStartOrExit(entrance: Boolean, x: Int, y: Int): ImageView {
        // выбираем как должен быть повернут вход/выход
        val number = if (labyrinth[x, y + 1] is Empty || labyrinth[x, y - 1] is Empty) 1 else 2
        val tileName = if (entrance) "entrance" else "exit" // вход или выход
        val tile = getTile("$tileName$number")
        tile.translateX = x * DX_X - DY_X * y // смещение по оси X
        tile.translateY = x * DX_Y + DY_Y * y // смещение по оси X
        return tile
    }

    /**
     * Shows player's [Condition] via popup and draws animation of the move
     * */
    fun showMoveResult(result: MoveResult) {
        val move: WalkMove = player!!.getNextMove() as WalkMove
        // если ход был успешен, то рисуем анимацию хода
        if (result.successful) {
            // offset
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
            map[playerLocation.x to playerLocation.y]!!.isVisible = true // отображаем клетку, в которую идём
            // отрисовываем перемещение игрока
            timeline(true) {
                keyframe(1.seconds) {
                    keyvalue(playerMovesAnimation.translateXProperty(), x + playerMovesAnimation.translateX)
                    keyvalue(playerMovesAnimation.translateYProperty(), y + playerMovesAnimation.translateY)
                    keyvalue(stackMap.translateXProperty(), stackMap.translateX - x)
                    keyvalue(stackMap.translateYProperty(), stackMap.translateY - y)
                }
                setOnFinished {
                    // если клетка, в которую походил игрок, червоточина, то надо отрисовать перемещение в следующую
                    // червоточину
                    if (result.room is Wormhole) {
                        // рассчитываем перемещение
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
                                keyvalue(stackMap.translateXProperty(), -newX, Interpolator.EASE_BOTH)
                                keyvalue(stackMap.translateYProperty(), -newY, Interpolator.EASE_BOTH)
                                setOnFinished {
                                    moveAllowedProperty.value = true
                                }//закончили отрисовку, можно ходить дальше
                            }
                        }
                    } else moveAllowedProperty.value = true //закончили отрисовку, можно ходить дальше
                }
            }

        } else { // упёрлись в стену. Нужно сделать её видимой
            val pos = move.direction + playerLocation; map[pos.x to pos.y]!!.isVisible = true
            moveAllowedProperty.value = true //закончили отрисовку, можно ходить дальше
        }
        if (!result.condition.exitReached) { // если ещё не нашли выход <=> играем дальше
            // то надо вывести результат хода
            val tooltip = Tooltip(result.status)
            tooltip.opacity = 0.0
            tooltip.show(gameView.currentWindow)
            tooltip.opacityProperty().animate(1.0, 0.5.seconds) {
                setOnFinished {
                    timeline(true) {
                        keyframe(1.seconds) {}
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

    /**Player make move with specific [direction]*/
    fun makeMove(direction: Direction) {
        if (moveAllowedProperty.value) { //если ходить можно, то делаем ход
            moveAllowedProperty.value = false
            // если играет человек, то необходимо в Human()
            // передать следующий ход, чтобы GameMaster мог потом его считать из getNextMove()
            if (!notAHuman) player!!.setNextMove(WalkMove(direction))
            //  далее всё аналогично методу GameMaster().makeMoves(limit: Int)
            val moves = gameMaster!!.moves
            var wallCount = 0
            if (moves < moveLimit) {
                val oldMoves = gameMaster!!.moves
                moveResult = gameMaster!!.makeMove()
                val newMoves = gameMaster!!.moves
                wallCount += if (oldMoves == newMoves) 1 else 0
                if (wallCount >= 100) endGame(moveResult)
                gameView.setMovesLeft(moveLimit - newMoves)
                // если достигли выхода и играет человек, то выводим сообщение о том, что игрок выиграл
                if (moveResult.exitReached && !notAHuman) endGame(moveResult)
            } else endGame(GameMaster.GameResult(moves, exitReached = false))
        }
    }

    /**
     * Starts a new game or resets the current one
     * @return limit of moves
     */
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

    /**
     * Shows alert about result of the game
     */
    private fun endGame(result: GameMaster.GameResult) {
        val headerText = if (result.exitReached) "You Win!" else "Game Over"
        // информация о результате игры
        val contentText = "$name. " + if (result.exitReached)
            "Congratulations! You made ${result.moves} moves, collected treasures, and reached the exit."
        else "Unfortunately, you lost. Try again."
        val alert = createDialog("Game Result", headerText, contentText, Alert.AlertType.INFORMATION, gameView.currentWindow)
        // устанавливаем свои кнопки
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
            toGamePreView -> {// go to game settings
                mainView.root.center.replaceWith(gamePreView.root)
                gameView.replaceWith<MainView>(ViewTransition.Fade(0.3.seconds))
            }
            toMainMenu -> { // go to menu
                mainView.root.center.replaceWith(mainMenuView.root)
                gameView.replaceWith<MainView>(ViewTransition.Fade(0.3.seconds))
            }
            tryAgain -> playAgain() // reload game
            playAgain -> playAgain() // reload game
        }
    }

    /**
     * Play again in current labyrinth
     */
    private fun playAgain() {
        gameView.newGame()
        moveAllowedProperty.value = true
        notAHuman = false
    }

    /**
     * Shows a message asking if you really want to stop playing.
     */
    fun exitFromGameView() {
        val contentText = "All your progress will be reset"
        val alert = createDialog(
            "Exit",
            "All your progress will be reset",
            contentText,
            Alert.AlertType.CONFIRMATION,
            gameView.currentWindow
        )
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
                moveAllowedProperty.value = false // если пытались прекратить играть во время пока прохождения
                moveAllowedProperty.value = true // при нажатии no надо продолжить показывать прохождение
            }
        }
    }

    /**
     * Returns alert with specific [title], [headerText], [contentText] and [Alert.AlertType]
     */
    private fun createDialog(
        title: String,
        headerText: String,
        contentText: String,
        alertType: Alert.AlertType,
        owner: Window?
    ): Alert {
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
        alert.initOwner(owner)
        return alert
    }

    /**
     * Trying to pass the labyrinth.
     * If there is a solution, then it shows it, if not, then it displays the corresponding message.
     */
    fun passLabyrinth() {
        notAHuman = false
        gameView.newGame()
        val status = TaskStatus()
        var result: List<Move> = emptyList()
        runAsync(status) {
            result = Searcher.searchPath(labyrinth, moveLimit) // пытаемся решить
        }
        val alert = createDialog(
            "Terra Incognita",
            "Trying to solve..",
            "",
            Alert.AlertType.INFORMATION,
            gameView.currentWindow
        )
        val label = Text("").apply {
            fill = Paint.valueOf(Styles.colorOfText)
            font = Styles.dialogFont
            visibleWhen { status.completed }
        }
        val vBox = VBox().apply {
            progressbar(status.progress) {
                visibleWhen { status.running } // отображение прогресса
            }
            add(label)
            maxWidth = Double.MAX_VALUE
        }
        status.completed.addListener(ChangeListener { _, _, completed ->
            label.text = if (completed && result.isNotEmpty()) "Successful!\nPress Ok to show the pass"
            else "Couldn't solve \nthe current labyrinth..."
        })

        alert.dialogPane.content = vBox
        val dialogResult = alert.showAndWait()

        fun showPath() { // показываем прохождение
            alert.close()
            if (result.isNotEmpty()) {
                moveAllowedProperty.value = true
                notAHuman = true
                playerLocation = labyrinth.entrances[0]
                player = Humanlike(result)
                gameMaster!!.setNewPlayer(player!!)
                makeMove((player!!.getNextMove() as WalkMove).direction)
                // далее функция будет вызываться сама, когда moveAllowedProperty.value изменяется с false на true
            }
        }
        if (status.completed.value) { // закрываем диалоговое окно
            try {
                if (dialogResult.get().text == "OK") showPath()
            } catch (e: NoSuchElementException) {
                showPath()
            }
        }

    }

    private var displayPassageCompletedDialog: Alert? = null

    /**
     * Shows alert about successful completion the labyrinth
     */
    private fun displayPassageCompleted() { // показываем сообщение, после того, как прохождение лабиринта было показано
        if (displayPassageCompletedDialog == null) {
            displayPassageCompletedDialog = createDialog(
                "",
                "That's it.",
                "Here is the solution to the labyrinth. Click OK to exit",
                Alert.AlertType.INFORMATION,
                gameView.currentWindow
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
        const val DX_X = 66.0

        /**X offset for y (20.3)**/
        const val DX_Y = 20.3

        /**Y offset for x (39.0)**/
        const val DY_X = 39.0

        /**Y offset for y (37.0)**/
        const val DY_Y = 37.0
    }
}