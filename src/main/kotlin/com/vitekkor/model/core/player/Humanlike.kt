package com.vitekkor.model.core.player

import com.vitekkor.model.core.Move
import com.vitekkor.model.core.MoveResult
/**
 * Humanoid player. Used to simulate human moves.
 * @param movesList The moves the player will make.
 * Usually the result of the work of artificial intelligence (for example, [Searcher]) to find a path in a labyrinth.
 */
class Humanlike(movesList: List<Move>) : Human() {
    /**Iterator for list of moves*/
    private val iterator = movesList.iterator()

    override var move: Move = iterator.next()

    override fun getNextMove(): Move {
        return move
    }

    override fun setMoveResult(result: MoveResult) {
        super.setMoveResult(result)
        if (iterator.hasNext()) move = iterator.next()
    }
}