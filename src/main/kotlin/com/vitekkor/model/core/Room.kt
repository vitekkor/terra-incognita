package com.vitekkor.model.core

/**The cell of the labyrinth
 * @param content room content. In this version - treasure*/
sealed class Room(open val content: RoomContent? = null)

/**Empty room without treasure*/
object Empty : Room() {
    override fun toString(): String {
        return "emptyCell"
    }
}
/**Room with content.
 * @see Room.content*/
data class WithContent(override var content: RoomContent?) : Room() {
    override fun toString(): String {
        return "treasure"
    }
}

/**Wall. Prevents to move.*/
object Wall : Room() {
    override fun toString(): String {
        return "wall"
    }
}

/**Starting cell*/
object Entrance : Room() {
    override fun toString(): String {
        return "entrance"
    }
}

/**Ending cell*/
object Exit : Room() {
    override fun toString(): String {
        return "exit"
    }
}

/**
 * Wormhole. When player hits a wormhole, he automatically jumps to the [next] hole by number, and if this hole is the last one, then to the first hole
 * @param id wormhole number
 */
data class Wormhole(private val id: Int) : Room() {
    /**Private storage of next wormhole.*/
    private var nextStored: Wormhole? = null

    /**Next wormhole by number. If this wormhole is the last one, then to the first wormhole*/
    var next: Wormhole
        get() = nextStored ?: error("No next wormhole was set for $id")
        set(value) {
            if (nextStored != null) {
                error("Next wormhole is already initialized for $id")
            }
            nextStored = value
        }
    constructor(id: Char) : this(id - '0')

    override fun toString(): String {
        return "wormhole"
    }
}