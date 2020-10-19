package com.vitekkor.model.core.player

import com.vitekkor.model.core.*

class Human : AbstractPlayer() {
    override fun getNextMove(): Move = when (readLine()) {
        "w" -> WalkMove(Direction.NORTH)
        "d" -> WalkMove(Direction.EAST)
        "s" -> WalkMove(Direction.SOUTH)
        "a" -> WalkMove(Direction.WEST)
        else -> WaitMove
    }

    override fun setMoveResult(result: MoveResult) {
        TODO("Передать контроллеру")
    }
}