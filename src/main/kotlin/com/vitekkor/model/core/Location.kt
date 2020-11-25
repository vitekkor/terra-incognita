package com.vitekkor.model.core

/** (0, 0) is in the upper-left corner (not including outer walls) */
data class Location(val x: Int, val y: Int) {
    operator fun plus(other: Location): Location {
        return Location(x + other.x, y + other.y)
    }

    operator fun plus(other: Direction): Location {
        return other.plus(this)
    }

    operator fun minus(location: Location): Direction {
        val (dx, dy) = x - location.x to y - location.y
        return when {
            dx == 0 && dy == -1 -> Direction.NORTH
            dx == 1 && dy == 0 -> Direction.EAST
            dx == 0 && dy == 1 -> Direction.SOUTH
            dx == -1 && dy == 0 -> Direction.WEST
            else -> Direction.NORTH
        }
    }
}