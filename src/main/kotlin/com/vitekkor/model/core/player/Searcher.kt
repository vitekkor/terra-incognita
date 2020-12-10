package com.vitekkor.model.core.player

import com.vitekkor.model.core.*
import com.vitekkor.model.core.labyrinth.GameMaster
import com.vitekkor.model.core.labyrinth.GameMaster.Companion.random
import com.vitekkor.model.core.labyrinth.Labyrinth

/**
 * An implementation of artificial intelligence that finds treasures and exits the labyrinth.
 * Extends from [AbstractPlayer]
 */
class Searcher : AbstractPlayer() {
    private val wayToExit = mutableListOf<Direction>() // путь к выходу
    private val map = mutableMapOf<Location, Room>() // карта
    private lateinit var currentLocation: Location // текущая локация
    private lateinit var lastDirection: Direction // последнее направление движения
    private var haveTreasure = false // нашли ли сокровище
    private var exitFound = false // нашли ли выход

    override fun setStartLocationAndSize(location: Location, width: Int, height: Int) {
        // устанавливаем начальные координаты и добавляем вход в нашу карту
        super.setStartLocationAndSize(location, width, height)
        currentLocation = location
        map[currentLocation] = Empty
    }

    override fun getNextMove(): Move {
        if (haveTreasure && exitFound) { // если нашли сокровище и выход
            if (wayToExit.isEmpty()) { // строим путь к выходу,если он ещё не построен
                findPathToExit(currentLocation)?.let { wayToExit.addAll(it) }
            }
            if (wayToExit.isNotEmpty()) { // есть путь к выходу - идём к нему
                lastDirection = wayToExit.first()
                wayToExit.removeAt(0)
            }
        } else { // нет сокровища или не нашли выход
            // возможные направления - фильтруем те направления, в которых мы уже были
            val possibleDirections = Direction.values().filter { !map.contains(it + currentLocation) }.toMutableList()
            // если их не осталось, то добавляем все
            if (possibleDirections.isEmpty()) possibleDirections.addAll(Direction.values())
            // и рандомно выбираем одно из них
            lastDirection = possibleDirections[random.nextInt(possibleDirections.size)]
        }
        return WalkMove(lastDirection)
    }

    override fun setMoveResult(result: MoveResult) {
        // заполняем карту
        map[currentLocation + lastDirection] = result.room
        if (result.successful) {
            // если успешный ход, то обновляем локацию
            currentLocation += lastDirection
        }
        when (result.room) {
            is WithContent -> haveTreasure = true // нашли сокровище
            is Exit -> exitFound = true // нашли выход
            is Wormhole -> { // провалились в червоточину
                map.clear() // необходимо очистить карту, т.к мы не знаем теперь, где находимся
                exitFound = false // соответственно, где выход, мы тоже не знаем
            }
            else -> {
            }
        }
    }

    private val visited = mutableSetOf<Location>() // посещённые клетки карты при поиске пути к выходу
    /**
     * Find path to exit from location
     * @param from location from which to build a path
     * @param previousPath previous path. Empty by default
     * @return list of directions to move from the starting location to arrive at the exit. Can be empty if there is no way*/
    private fun findPathToExit(
        from: Location,
        previousPath: MutableList<Direction> = mutableListOf()
    ): MutableList<Direction>? {
        if (map[from] is Exit) { // если from это уже выход, то возвращаем путь
            return previousPath
        }
        visited.add(from) // добавляем посещённую location
        // фильтруем направления
        // отбрасываем те, по которым мы уже ходили
        // и те, которые ведут нас в стену или в червоточину
        // и снова вызываем функцию поиска пути, но уже из новых локаций from + direction
        // и с previousPath, содержащим весь предыдущий путь
        // возвращаем минимальный путь
        return Direction.values().filter {
            val newLocation = from + it
            !visited.contains(newLocation) && (map[newLocation] is Empty || map[newLocation] is WithContent || map[newLocation] is Exit)
        }.mapNotNull { findPathToExit(from + it, mutableListOf<Direction>().apply { addAll(previousPath); add(it) }) }
            .minByOrNull { it.size }
    }
    /**
     * Call to reset Searcher to start searching again
     *
     * It is preferable to use this method instead of creating a new instance of the Searcher.
     */
    private fun reset() {
        wayToExit.clear()
        visited.clear()
        map.clear()
        haveTreasure = false
        exitFound = false
    }
    companion object {

        /**
         * Searching the way in the labyrinth and collect the treasure.
         * @param labyrinth a labyrinth in which to find a path
         * @param moveLimit the maximum number of moves Searcher can make
         * @return list of moves. Can be empty if there is no way */
        fun searchPath(labyrinth: Labyrinth, moveLimit: Int): List<Move> {
            val searcher = Searcher()
            val gameMaster = GameMaster(labyrinth, searcher)
            for (i in 1..10000) { // много итераций, чтобы повысить шанс нахождения пути
                searcher.reset() // сброс для нового поиска
                gameMaster.reset()
                val result = gameMaster.makeMoves(1000) // делаем 1000 ходов
                if (result.exitReached) {
                    searcher.reset()
                    if (gameMaster.playerMoves.size <= moveLimit) // возвращаем путь, если он удовлетворяет лимиту
                        return gameMaster.playerMoves
                }
            }
            return emptyList() // не нашли путь
        }
    }
}