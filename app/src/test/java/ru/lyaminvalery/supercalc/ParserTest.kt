package ru.lyaminvalery.supercalc

import org.junit.Test
import ru.lyaminvalery.supercalc.model.Parser
import org.junit.Assert.*

class ParserTest {

    @Test
    fun simple_parsing(){
        assertEquals(3.0, Parser().parse("2 + 2 / 2"), 0.00001)
        assertEquals(20.65, Parser().parse("10,5 + 4,5 * 2,7 - 2 + "), 0.00001)

    }

    @Test
    fun parsed_braces(){
        assertEquals(2.0, Parser().parse("(2 + 2) / 2"), 0.00001)
        assertEquals(8.66, Parser().parse("5( 4 / (2 + 1) ) + 2"), 0.01)
    }


}