package com.vitekkor.model.core

enum class Direction(private val dx: Int, private val dy: Int) {
    NORTH(0, -1),
    EAST(1, 0),
    SOUTH(0, 1),
    WEST(-1, 0);

    operator fun plus(location: Location) = Location(location.x + dx, location.y + dy)

    fun turnRight() = values()[(ordinal + 1) % values().size]

    fun turnLeft() = values()[if (ordinal > 0) ordinal - 1 else values().size - 1]

    fun turnBack() = values()[(ordinal + 2) % values().size]
}