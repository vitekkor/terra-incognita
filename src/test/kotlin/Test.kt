import com.vitekkor.model.core.labyrinth.Labyrinth
import com.vitekkor.model.core.player.Searcher
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class Test {
    @Test
    fun testSearch() {
        println("Starting testSearch...")
        for (iter in 1..50) {
            println("TestSearch iteration $iter...")
            for (i in 2..40) {
                for (j in 2..25) {
                    println("Trying to solve labyrinth ${i}x${j}...")
                    val lab = Labyrinth.createFromFile("src/main/resources/labyrinths/${i}x${j}.txt")
                    assertTrue(Searcher.searchPath(lab, 500).isNotEmpty(), "Can't solve lab ${i}x${j}")
                    println("Successful!")
                }
            }
        }
        println("All iterations were successful!")
    }

    @Test
    fun testBadSearch() {
        println("Starting testBadSearch...")
        for (iter in 1..25) {
            println("TestBadSearch iteration $iter...")
            for (i in 2..8) {
                println("Trying to solve labyrinth bad${i}.txt...")
                val lab = Labyrinth.createFromFile("src/test/resources/bad_labs/bad${i}.txt")
                assertTrue(Searcher.searchPath(lab, 1000).isEmpty(), "Unsolvable lab bad${i}.txt was solved")
                println("The lab is not solved. Everything all right....")
            }
        }
        println("All iterations were successful!")
    }

    @Test
    fun createLabyrinthTest() {
        println("Starting createLabyrinthTest...")
        println("Trying to create labyrinth empty.txt...")
        val empty = assertThrows<IllegalArgumentException> {
            Labyrinth.createFromFile("src/test/resources/incorrect_labs/empty.txt")
        }
        assertEquals("Empty File", empty.message)
        for (i in 1..4) {
            println("Trying to create labyrinth IllegalSize$i.txt...")
            val size = assertThrows<IllegalArgumentException> {
                Labyrinth.createFromFile("src/test/resources/incorrect_labs/IllegalSize$i.txt")
            }
            assertEquals("Illegal size of labyrinth", size.message)
        }
        for (i in 1..6) {
            println("Trying to create labyrinth IllegalSymbol$i.txt...")
            val symbol = assertThrows<IllegalArgumentException> {
                Labyrinth.createFromFile("src/test/resources/incorrect_labs/IllegalSymbol$i.txt")
            }
            assertEquals("Illegal labyrinth symbol", symbol.message)
        }
        for (i in 1..3) {
            println("Trying to create labyrinth DifferentRow$i.txt...")
            val rows = assertThrows<IllegalArgumentException> {
                Labyrinth.createFromFile("src/test/resources/incorrect_labs/DifferentRow$i.txt")
            }
            assertEquals("Different row sizes", rows.message)
        }
        for (i in 1..2) {
            println("Trying to create labyrinth Start$i.txt...")
            val start = assertThrows<IllegalArgumentException> {
                Labyrinth.createFromFile("src/test/resources/incorrect_labs/Start$i.txt")
            }
            assertEquals("The labyrinth already contains a start", start.message)
        }
        for (i in 1..2) {
            println("Trying to create labyrinth End$i.txt...")
            val end = assertThrows<IllegalArgumentException> {
                Labyrinth.createFromFile("src/test/resources/incorrect_labs/End$i.txt")
            }
            assertEquals("The labyrinth already contains an end", end.message)
        }
        val chars = arrayOf('s', 'e', 't', 'A', 'a', '$', '"', '\'')
        for (i in 1..8) {
            println("Trying to create labyrinth IllegalSymbol2_$i.txt...")
            val symbol = assertThrows<IllegalArgumentException> {
                Labyrinth.createFromFile("src/test/resources/incorrect_labs/IllegalSymbol2_$i.txt")
            }
            assertEquals("Illegal labyrinth symbol: ${chars[i - 1]}", symbol.message)
        }
        println("Trying to create labyrinth StartNotExists.txt...")
        val startExists = assertThrows<IllegalArgumentException> {
            Labyrinth.createFromFile("src/test/resources/incorrect_labs/StartNotExists.txt")
        }
        assertEquals("The labyrinth must contain a start", startExists.message)
        println("Trying to create labyrinth EndNotExists.txt...")
        val endExists = assertThrows<IllegalArgumentException> {
            Labyrinth.createFromFile("src/test/resources/incorrect_labs/EndNotExists.txt")
        }
        assertEquals("The labyrinth must contain an end", endExists.message)
        println("Trying to create labyrinth TreasureNotExists.txt...")
        val treasureExists = assertThrows<IllegalArgumentException> {
            Labyrinth.createFromFile("src/test/resources/incorrect_labs/TreasureNotExists.txt")
        }
        assertEquals("The labyrinth must contain at least one treasure", treasureExists.message)
        for (i in 1..4) {
            println("Trying to create labyrinth WormHoleErr$i.txt...")
            val wormholeError = assertThrows<IllegalStateException> {
                Labyrinth.createFromFile("src/test/resources/incorrect_labs/WormHoleErr$i.txt")
            }
            assertEquals("No next wormhole found for $i", wormholeError.message)
        }
        for (i in 1..4) {
            println("Trying to create labyrinth WormHoleIncorrect$i.txt...")
            val wormholeError = assertThrows<IllegalStateException> {
                Labyrinth.createFromFile("src/test/resources/incorrect_labs/WormHoleIncorrect$i.txt")
            }
            assertEquals(wormholeError.message, "Wormholes are set incorrectly")
        }
        println("Success!")
    }
}
