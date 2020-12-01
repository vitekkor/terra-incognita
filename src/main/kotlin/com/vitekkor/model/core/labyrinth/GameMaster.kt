package com.vitekkor.model.core.labyrinth

import com.vitekkor.model.core.*
import com.vitekkor.model.core.player.Player
import java.util.*

class GameMaster(private var lab: Labyrinth, private var player: Player) {

    fun reload() {
        lab.recover()
        moves = 0
        playerLocation = lab.entrances.let {
            it[random.nextInt(it.size)]
        }.apply {
            player.setStartLocationAndSize(this, lab.width, lab.height)
        }
        playerCondition = Condition()
        playerMoves.clear()
    }

    fun setNewPlayer(newPlayer: Player): Boolean {
        var res = false
        if (newPlayer != player) {
            player = newPlayer
            res = false
        }
        reload()
        return res
    }

    fun setNewLabyrinth(newLabyrinth: Labyrinth): Boolean {
        var res = false
        if (newLabyrinth != lab) {
            lab = newLabyrinth
            res = true
        }
        reload()
        return res
    }

    private var playerLocation = lab.entrances.let {
        it[random.nextInt(it.size)]
    }.apply {
        player.setStartLocationAndSize(this, lab.width, lab.height)
    }

    private var playerCondition: Condition = Condition()

    var moves = 0

    internal val playerMoves = mutableListOf<Move>()

    data class GameResult(val moves: Int, val exitReached: Boolean)

    /*/**The player makes moves until they reach the limit of moves
     * @param moveLimit the limit of moves
     * @return GameResult
     * @see GameResult**/*/
    fun makeMoves(moveLimit: Int): GameResult {
        var wallCount = 0
        while (moves < moveLimit) {
            val oldMoves = moves
            val moveResult = makeMove()
            val newMoves = moves
            wallCount = if (oldMoves == newMoves) wallCount + 1 else 0
            if (wallCount >= 100) return moveResult
            if (moveResult.exitReached) return moveResult
        }
        return GameResult(moves, exitReached = false)
    }

    /*/** The player makes a move
     * @return GameResult
     * @see GameResult
     * **/*/
    fun makeMove(): GameResult {
        if (playerCondition.exitReached) return GameResult(moves, exitReached = true)
        val move = player.getNextMove()
        val moveResult = when (move) {
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
                        newLocation = lab.wormholeMap.getValue(newLocation)
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
            playerMoves.add(move)
        }
        return GameResult(moves, playerCondition.exitReached)
    }

    companion object {
        val random = Random(Calendar.getInstance().timeInMillis)
    }
}
