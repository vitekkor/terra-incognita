package com.vitekkor.model.core

sealed class Room(open val content: RoomContent? = null)

object Empty : Room() {
    override fun toString(): String {
        return "emptyCell"
    }
}

data class WithContent(override var content: RoomContent?) : Room() {
    override fun toString(): String {
        return "treasure"
    }
}

object Wall : Room() {
    override fun toString(): String {
        return "wall"
    }
}

object Entrance : Room() {
    override fun toString(): String {
        return "entrance"
    }
}

object Exit : Room() {
    override fun toString(): String {
        return "exit"
    }
}

data class Wormhole(private val id: Int) : Room() {
    private var nextStored: Wormhole? = null

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