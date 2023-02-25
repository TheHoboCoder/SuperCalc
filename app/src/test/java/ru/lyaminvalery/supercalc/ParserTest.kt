package ru.lyaminvalery.supercalc

import org.junit.Test
import ru.lyaminvalery.supercalc.model.Parser
import org.junit.Assert.*
import kotlin.math.E
import kotlin.math.PI

class ParserTest {

    @Test
    fun simple_parsing(){
        assertEquals(3.0, Parser().parse("2 + 2 / 2"), 0.00001)
        assertEquals(20.65, Parser().parse("10.5 + 4.5 * 2.7 - 2 + "), 0.00001)

    }

    @Test
    fun parsed_braces(){
        assertEquals(2.0, Parser().parse("(2 + 2) / 2"), 0.00001)
        assertEquals(8.66, Parser().parse("5( 4 / (2 + 1) ) + 2"), 0.01)
        assertEquals(32.0, Parser().parse("(3 + 5) * (2 + 2)"), 0.01)
    }

    @Test
    fun test_pow(){
        assertEquals(32.0, Parser().parse("2 ^ (1 + 2 * 2)"), 0.01)
        assertEquals(2.0, Parser().parse("4 ^ 0.5"), 0.01)
    }

    @Test
    fun test_unary(){
        assertEquals(16.0, Parser().parse("6 + √4(3 + 2!)"), 0.00001)
        //TODO:
        //assertEquals(2.0, Parser().parse("√(1*2 + 4/2)"), 0.00001)
    }

    @Test
    fun test_constants(){
        assertEquals(E*2 + PI / 2, Parser().parse("E*2 + PI / 2"), 0.00001)
    }




}