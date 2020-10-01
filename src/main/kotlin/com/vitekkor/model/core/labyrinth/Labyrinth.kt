package com.vitekkor.model.core.labyrinth

import com.vitekkor.model.core.*
import java.io.File
import java.lang.IllegalArgumentException
import kotlin.error

class Labyrinth private constructor(val width: Int, val height: Int, private val map: Map<Location, Room>,
                                    val wormholeMap: Map<Location, Location>) {

    private val Location.isCorrect get() = x in 0 until width && y in 0 until height

    operator fun get(location: Location): Room =
            map[location]
                    ?: if (location.isCorrect) error("Incorrect location: $location") else Wall

    operator fun get(x: Int, y: Int) = get(Location(x, y))

    val entrances: List<Location> = map.entries.filter { (_, room) -> room == Entrance }.map { it.key }

    private val exits: List<Location> = map.entries.filter { (_, room) -> room == Exit }.map { it.key }

    companion object {
        fun createFromFile(fileName: String) = createFromFile(File(fileName))

        fun createFromFile(file: File): Labyrinth {
            val lines = file.readLines()

            require(lines.isNotEmpty()) { "Empty File" }

            val height = lines.size - 2
            val width = lines[0].length - 2

            require(height in 2..25 && width in 2..40) { "Illegal size of labyrinth" }
            require(lines[0].matches(Regex("""#+"""))
                    && lines.last().matches(Regex("""#+"""))) { "Illegal labyrinth symbol" }
            require(lines.last().length - 2 == width) { "Different row sizes" }

            val map = hashMapOf<Location, Room>()
            val wormholes = mutableMapOf<Int, Wormhole>()
            val wormholeLocations = mutableMapOf<Wormhole, Location>()
            val wormholeMap = mutableMapOf<Location, Location>()
            for ((y, line) in lines.drop(1).dropLast(1).withIndex()) {

                require(line.startsWith("#") && line.endsWith("#")) { "Illegal labyrinth symbol" }

                val trimmedLine = line.drop(1).dropLast(1)
                for ((x, char) in trimmedLine.withIndex()) {
                    val location = Location(x, y)
                    map[location] = when (char) {
                        ' ' -> Empty
                        'S' -> Entrance
                        'E' -> Exit
                        '#' -> Wall
                        'T' -> WithContent(Treasure)
                        in '0'..'9' -> Wormhole(char).apply {
                            wormholes[char - '0'] = this
                            wormholeLocations[this] = location
                        }
                        else -> throw IllegalArgumentException("Illegal labyrinth symbol: $char")
                    }
                }
            }
            for ((wormholeId, wormhole) in wormholes) {
                wormhole.next = wormholes[wormholeId + 1]
                        ?: wormholes[0]
                                ?: error("No next wormhole found for $wormholeId")
                wormholeMap[wormholeLocations[wormhole]!!] = map.entries.find { (_, anotherRoom) ->
                    anotherRoom == wormhole.next
                }!!.key
            }
            check(checkWormholes(map, wormholeMap)) { "Wormholes are set incorrectly" }
            check(wormholeMap.entries.size <= 10) { "The number of wormholes can be from 0 to 10" }

            return Labyrinth(width, height, map, wormholeMap)
        }

        private fun checkWormholes(map: HashMap<Location, Room>, wormholeMap: Map<Location, Location>): Boolean {
            val first = wormholeMap.entries.firstOrNull() ?: return true
            val start = first.key
            var current = first.value
            val all = mutableSetOf(start, current)
            while (current != start) {
                current = wormholeMap[current] ?: return false
                if (current != start) {
                    if (current in all) return false
                    all += current
                }
            }
            return all.size == map.values.filterIsInstance<Wormhole>().size
        }
    }

}