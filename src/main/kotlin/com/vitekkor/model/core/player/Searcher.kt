package com.vitekkor.model.core.player

import com.vitekkor.model.core.*
import com.vitekkor.model.core.labyrinth.GameMaster
import com.vitekkor.model.core.labyrinth.GameMaster.Companion.random
import com.vitekkor.model.core.labyrinth.Labyrinth

class Searcher : AbstractPlayer() {
    private val wayToExit = mutableListOf<Direction>()
    private val map = mutableMapOf<Location, Room>()
    private lateinit var currentLocation: Location
    private lateinit var lastDirection: Direction
    private var haveTreasure = false
    private var exitFound = false

    override fun setStartLocationAndSize(location: Location, width: Int, height: Int) {
        super.setStartLocationAndSize(location, width, height)
        currentLocation = location
        map[currentLocation] = Empty
    }

    override fun getNextMove(): Move {
        if (haveTreasure && exitFound) {
            if (wayToExit.isEmpty()) {
                findPathToExit(currentLocation)?.let { wayToExit.addAll(it) }
            }
            if (wayToExit.isNotEmpty()) {
                lastDirection = wayToExit.first()
                wayToExit.removeAt(0)
            }
        } else {
            val possibleDirections = Direction.values().filter { !map.contains(it + currentLocation) }.toMutableList()
            if (possibleDirections.isEmpty()) possibleDirections.addAll(Direction.values())
            lastDirection = possibleDirections[random.nextInt(possibleDirections.size)]
        }
        return WalkMove(lastDirection)
    }

    override fun setMoveResult(result: MoveResult) {
        map[currentLocation + lastDirection] = result.room
        if (result.successful) {
            currentLocation += lastDirection
        }
        when (result.room) {
            is WithContent -> haveTreasure = true
            is Exit -> exitFound = true
            is Wormhole -> {
                map.clear()
                exitFound = false
            }
            else -> {
            }
        }
    }

    private val visited = mutableSetOf<Location>()
    private fun findPathToExit(
        from: Location,
        previousPath: MutableList<Direction> = mutableListOf()
    ): MutableList<Direction>? {
        if (map[from] is Exit) {
            return previousPath
        }
        visited.add(from)
        return Direction.values().filter {
            val newLocation = from + it
            !visited.contains(newLocation) && (map[newLocation] is Empty || map[newLocation] is WithContent || map[newLocation] is Exit)
        }.mapNotNull { findPathToExit(from + it, mutableListOf<Direction>().apply { addAll(previousPath); add(it) }) }
            .minByOrNull { it.size }
    }

    private fun reload() {
        wayToExit.clear()
        visited.clear()
        map.clear()
        haveTreasure = false
        exitFound = false
    }

    companion object {
        fun searchPath(labyrinth: Labyrinth, moveLimit: Int): List<Move> {
            val searcher = Searcher()
            val gameMaster = GameMaster(labyrinth, searcher)
            for (i in 1..10000) {
                searcher.reload()
                gameMaster.reload()
                val result = gameMaster.makeMoves(1000)
                if (result.exitReached) {
                    searcher.reload()
                    if (gameMaster.playerMoves.size <= moveLimit)
                        return gameMaster.playerMoves
                }
            }
            return emptyList()
        }
    }
}