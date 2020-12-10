package com.vitekkor.model.core
/**
 * Move direction.
 *
 * [Direction.NORTH] - up
 *
 * [Direction.EAST] - to the right
 *
 * [Direction.SOUTH] - down
 *
 * [Direction.WEST] - to the left
 */
enum class Direction(private val dx: Int, private val dy: Int) {
    /**Up*/
    NORTH(0, -1),
    /**To the right*/
    EAST(1, 0),
    /**Down*/
    SOUTH(0, 1),
    /**To the left*/
    WEST(-1, 0);

    /**
     * Returns a Location obtained by adding dx and dy to [location].
     */
    operator fun plus(location: Location) = Location(location.x + dx, location.y + dy)

    /**
     * Returns the next direction when turning from the current to the right.
     */
    fun turnRight() = values()[(ordinal + 1) % values().size]

    /**
     * Returns the next direction when turning from the current to the left.
     */
    fun turnLeft() = values()[if (ordinal > 0) ordinal - 1 else values().size - 1]

    /**
     * Returns the opposite direction
     */
    fun turnBack() = values()[(ordinal + 2) % values().size]
}