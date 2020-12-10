package com.vitekkor.model.core

/**
 * Condition after move (contains list of items and exit reaching flag)
 * @param items list of treasures
 * @param exitReached <code>true</code> if player reached exit, and <code>false</code> if not
 * @constructor */
data class Condition(val items: List<Item>, val exitReached: Boolean) {
    /**Creates condition with empty list of treasures and exitReached is false*/
    constructor(): this(false)
    /**Creates condition with empty list of treasures and your exitReached*/
    constructor(exitReached: Boolean): this(emptyList(), exitReached)

    /**<code>True</code> if the player found the treasure, and <code>false</code> if not.*/
    val hasTreasure get() = Treasure in items
}