package com.vitekkor.model.core

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