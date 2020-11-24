package com.vitekkor.model.core

/** (0, 0) is in the upper-left corner (not including outer walls) */
data class Location(val x: Int, val y: Int) {
    operator fun plus(other: Location): Location {
        return Location(x + other.x, y + other.y)
    }

    operator fun plus(other: Direction): Location {
        return other.plus(this)
    }
}