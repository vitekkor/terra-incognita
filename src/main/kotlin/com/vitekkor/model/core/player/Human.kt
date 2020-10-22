package com.vitekkor.model.core.player

import com.vitekkor.controller.MyController
import com.vitekkor.model.core.*
import tornadofx.*

class Human : AbstractPlayer() {
    private val appController = find(MyController::class)
    var move: Move = WaitMove
    override fun getNextMove(): Move = move

    fun setNextMove(newMove: Move) {
        move = newMove
    }

    override fun setMoveResult(result: MoveResult) {
        appController.showMoveResult(result)
    }
}