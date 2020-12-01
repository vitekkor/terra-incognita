package com.vitekkor.model.core.player

import com.vitekkor.model.core.Move
import com.vitekkor.model.core.MoveResult

class Humanlike(movesList: List<Move>) : Human() {
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