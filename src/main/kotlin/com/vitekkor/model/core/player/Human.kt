package com.vitekkor.model.core.player

import com.vitekkor.controller.MyController
import com.vitekkor.model.core.Move
import com.vitekkor.model.core.MoveResult
import com.vitekkor.model.core.WaitMove
import tornadofx.find

/**
 * Implementation of a player to control him by a human.
 */
open class Human : AbstractPlayer() {

    private val appController = find(MyController::class)

    /**Next move*/
    protected open var move: Move = WaitMove

    override fun getNextMove(): Move = move

    open fun setNextMove(newMove: Move) {
        move = newMove
    }
    override fun setMoveResult(result: MoveResult) {
        appController.showMoveResult(result)
    }
}