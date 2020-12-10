package com.vitekkor.model.core

/**Result of the player's move.
 * @param room Which room was at target location
 * @param condition Condition after move (contains list of items and exit reaching flag)
 * @param successful True if move was successful
 * @param status status string (normally should not be analyzed)*/
class MoveResult(
        /** Which room was at target location */
        val room: Room,
        /** Condition after move (contains list of items and exit reaching flag) */
        val condition: Condition,
        /** True if move was successful */
        val successful: Boolean,
        /** Status string (normally should not be analyzed) */
        val status: String
)