package com.vitekkor.model.core.player

import com.vitekkor.model.core.Direction
import com.vitekkor.model.core.Move
import com.vitekkor.model.core.MoveResult
import com.vitekkor.model.core.WalkMove

class Humanlike(movesList: MutableList<Direction>) : Human() {
    private val iterator = movesList.iterator()
    override var move: Move = WalkMove(iterator.next())
    override fun getNextMove(): Move {
        return move
    }

    override fun setMoveResult(result: MoveResult) {
        super.setMoveResult(result)
        if (iterator.hasNext()) move = WalkMove(iterator.next())
    }

    override fun setNextMove(newMove: Move) {}
}