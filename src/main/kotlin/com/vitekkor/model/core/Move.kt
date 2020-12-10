package com.vitekkor.model.core

/**Move*/
sealed class Move

/** This move does nothing*/
object WaitMove : Move()

/** This moves player in the given direction*/
class WalkMove(val direction: Direction) : Move()