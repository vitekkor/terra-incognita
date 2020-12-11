package com.vitekkor.model.core

/** (0, 0) is in the upper-left corner (not including outer walls) */
data class Location(val x: Int, val y: Int) {

    /**
     * Returns a Location obtained by adding x and y to [other].
     */
    operator fun plus(other: Location): Location {
        return Location(x + other.x, y + other.y)
    }

    /**
     * Returns a Location obtained by adding dx and dy from [direction].
     */
    operator fun plus(direction: Direction): Location {
        return direction.plus(this)
    }
}