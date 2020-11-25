package com.vitekkor.model.core.player

import com.vitekkor.controller.MyController
import com.vitekkor.model.core.*
import tornadofx.*

open class Human : AbstractPlayer() {
    private val appController = find(MyController::class)
    protected open var move: Move = WaitMove
    override fun getNextMove(): Move = move

    open fun setNextMove(newMove: Move) {
        move = newMove
    }

    override fun setMoveResult(result: MoveResult) {
        appController.showMoveResult(result)
    }
}