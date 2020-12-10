package com.vitekkor.model.core.labyrinth

import com.vitekkor.model.core.*
import com.vitekkor.model.core.player.Player
import java.util.*

/**Class GameMaster that controls the entire game. It provides a link between the player and the labyrinth.
 *
 * @see GameMaster.makeMoves
 * @see GameMaster.makeMove
 */
class GameMaster(private var lab: Labyrinth, private var player: Player) {

    /**
     * Called when need to start playing again.
     *
     * It is preferable to use this method instead of creating a new instance of the GameMaster.
     *
     * GameMaster returns to its initial state (state when the class instance was just created).
     */
    fun reset() {
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

    /**Set a new player.
     *
     * GameMaster will be reset automatically.
     */
    fun setNewPlayer(newPlayer: Player): Boolean {
        var res = false
        if (newPlayer != player) {
            player = newPlayer
            res = false
        }
        reset()
        return res
    }

    /**Set a new labyrinth.
     *
     * The reload() function will be called automatically.
     */
    fun setNewLabyrinth(newLabyrinth: Labyrinth): Boolean {
        var res = false
        if (newLabyrinth != lab) {
            lab = newLabyrinth
            res = true
        }
        reset()
        return res
    }

    /**Field that stores the player's location. Initially, the player is at the start.*/
    private var playerLocation = lab.entrances.let {
        it[random.nextInt(it.size)]
    }.apply {
        player.setStartLocationAndSize(this, lab.width, lab.height)
    }

    /**Field that stores the player's condition.
     * @see Condition*/
    private var playerCondition: Condition = Condition()

    /**Number of moves made.*/
    var moves = 0

    /**List of player's moves. Contains only successful moves.*/
    internal val playerMoves = mutableListOf<Move>()

    /**Class that stored game result.
     * @param moves number of moves made
     * @param exitReached true if player reached exit, and false if not
     */
    data class GameResult(val moves: Int, val exitReached: Boolean)

    /**The player makes moves until they reach the limit of moves.
     * @param moveLimit the limit of moves
     * @return GameResult
     * @see GameResult
     */
    fun makeMoves(moveLimit: Int): GameResult {
        var wallCount = 0 // wall counter
        while (moves < moveLimit) {
            val oldMoves = moves
            val moveResult = makeMove() // make one move
            val newMoves = moves
            wallCount = if (oldMoves == newMoves) wallCount + 1 else 0 // if the number of moves has not changed, then we hit the wall
            if (wallCount >= 150) return moveResult // avoiding absolute deadlocks
            if (moveResult.exitReached) return moveResult // player won
        }
        return GameResult(moves, exitReached = false)
    }

    /** The player makes a move.
     * @return GameResult
     * @see GameResult
     */
    fun makeMove(): GameResult {
        if (playerCondition.exitReached) return GameResult(moves, exitReached = true)
        val move = player.getNextMove() // get the next player's move
        val moveResult = when (move) {
            WaitMove -> { // player is waiting
                MoveResult(lab[playerLocation], playerCondition, true, "Nothing changes")
            }
            is WalkMove -> { // player is moving
                var newLocation = move.direction + playerLocation
                val newRoom = lab[newLocation]
                val (movePossible, status) = when (newRoom) {
                    Empty, Entrance -> true to "Empty room appears"
                    Wall -> { // player hits the wall
                        newLocation = playerLocation
                        false to "Wall prevents from moving"
                    }
                    is WithContent -> {
                        when (val content = newRoom.content) {
                            is Item -> { // player find the treasure
                                playerCondition = playerCondition.copy(items = playerCondition.items + content)
                                newRoom.content = null
                                true to "Treasure found"
                            }
                            null -> true to "Empty room appears" // if treasure already has been collected
                        }
                    }
                    Exit -> {
                        if (playerCondition.hasTreasure) {
                            playerCondition = playerCondition.copy(exitReached = true) // player has a treasure and has reached the exit
                            true to "Exit reached, you won"
                        } else {
                            true to "Exit reached but you do not have a treasure"  // player has reached the exit, but hasn't a treasure
                        }
                    }
                    is Wormhole -> {
                        newLocation = lab.wormholeMap.getValue(newLocation) // get new location
                        true to "Fall into wormhole!"
                    }
                }
                playerLocation = newLocation // update player's location
                MoveResult(newRoom, playerCondition, movePossible, status) // set move result
            }
        }
        player.setMoveResult(moveResult) // set move result to player
        if (moveResult.successful) { // take into account only successful moves
            moves++
            playerMoves.add(move)
        }
        return GameResult(moves, playerCondition.exitReached)
    }

    companion object {
        /**Randomizer*/
        val random = Random(Calendar.getInstance().timeInMillis)
    }
}
