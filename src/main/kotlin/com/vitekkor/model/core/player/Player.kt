package com.vitekkor.model.core.player

import com.vitekkor.model.core.Location
import com.vitekkor.model.core.Move
import com.vitekkor.model.core.MoveResult

/**
 * A player playing a labyrinth.Can move.
 * To do this, the GameMaster calls [getNextMove] and then sets move result by calling [setMoveResult]
 */
interface Player {
    /**
     * Called by master to set start player location and labyrinth size
     */
    fun setStartLocationAndSize(location: Location, width: Int, height: Int)

    /**
     * Called by master to get next player's move
     */
    fun getNextMove(): Move

    /**
     * Called by master to set player's last move result
     */
    fun setMoveResult(result: MoveResult)
}