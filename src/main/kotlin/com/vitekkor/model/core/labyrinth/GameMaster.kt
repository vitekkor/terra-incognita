package com.vitekkor.model.core.labyrinth

import java.util.*
import com.vitekkor.model.core.*

class GameMaster(private val lab: Labyrinth, private val player: Player) {

    private var playerLocation = lab.entrances.let {
        it[random.nextInt(it.size)]
    }.apply {
        player.setStartLocationAndSize(this, lab.width, lab.height)
    }

    private var playerCondition: Condition = Condition()

    var moves = 0

    internal val playerPath = mutableMapOf(0 to playerLocation)

    data class GameResult(val moves: Int, val exitReached: Boolean)

    fun makeMoves(moveLimit: Int): GameResult {
        var wallCount = 0
        while (moves < moveLimit) {
            val oldMoves = moves
            val moveResult = makeMove()
            val newMoves = moves
            wallCount = if (oldMoves == newMoves) wallCount + 1 else 0
            if (wallCount >= 100) return moveResult
            playerPath[moves] = playerLocation
            if (moveResult.exitReached) return moveResult
        }
        return GameResult(moves, exitReached = false)
    }

    fun makeMove(): GameResult {
        if (playerCondition.exitReached) return GameResult(moves, exitReached = true)
        val moveResult = when (val move = player.getNextMove()) {
            WaitMove -> {
                MoveResult(lab[playerLocation], playerCondition, true, "Nothing changes")
            }
            is WalkMove -> {
                var newLocation = move.direction + playerLocation
                val newRoom = lab[newLocation]
                val (movePossible, status) = when (newRoom) {
                    Empty, Entrance -> true to "Empty room appears"
                    Wall -> {
                        newLocation = playerLocation
                        false to "Wall prevents from moving"
                    }
                    is WithContent -> {
                        when (val content = newRoom.content) {
                            is Item -> {
                                playerCondition = playerCondition.copy(items = playerCondition.items + content)
                                newRoom.content = null
                                true to "Treasure found"
                            }
                            null -> true to "Empty room appears"
                        }
                    }
                    Exit -> {
                        if (playerCondition.hasTreasure) {
                            playerCondition = playerCondition.copy(exitReached = true)
                            true to "Exit reached, you won"
                        } else {
                            true to "Exit reached but you do not have a treasure"
                        }
                    }
                    is Wormhole -> {
                        newLocation = lab.wormholeMap[newLocation]!!
                        true to "Fall into wormhole!"
                    }
                }
                playerLocation = newLocation
                MoveResult(newRoom, playerCondition, movePossible, status)
            }
        }
        player.setMoveResult(moveResult)
        if (moveResult.successful) {
            moves++
        }
        return GameResult(moves, playerCondition.exitReached)
    }

    companion object {
        val random = Random(Calendar.getInstance().timeInMillis)
    }
}
