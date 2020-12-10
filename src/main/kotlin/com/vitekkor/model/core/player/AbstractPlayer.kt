package com.vitekkor.model.core.player

import com.vitekkor.model.core.Location

/**Abstract player. Provides a skeletal implementation of the [Player] interface.*/
abstract class AbstractPlayer : Player {

    override fun setStartLocationAndSize(location: Location, width: Int, height: Int) {
        startLocation = location
        this.width = width
        this.height = height
    }

    /**Starting position from which the player starts playing*/
    lateinit var startLocation: Location

    /**Labyrinth width*/
    var width = 0

    /**Labyrinth height*/
    var height = 0
}