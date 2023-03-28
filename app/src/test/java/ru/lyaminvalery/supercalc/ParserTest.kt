package ru.lyaminvalery.supercalc

import org.junit.Test
import ru.lyaminvalery.supercalc.model.Parser
import org.junit.Assert.*
import ru.lyaminvalery.supercalc.model.ParserException
import kotlin.math.E
import kotlin.math.PI

class ParserTest {

    @Test
    fun simple_input(){
        assertEquals(2.0, Parser().parse("2"), 0.00001)
        assertEquals(2.0, Parser().parse("√4"), 0.00001)
    }

    @Test
    fun test_throws(){
        assertThrows(ParserException::class.java){
            Parser().parse("2+*-3")
        }
    }


    @Test
    fun simple_parsing(){
        assertEquals(3.0, Parser().parse("2 + 2 / 2"), 0.00001)
        assertEquals(20.65, Parser().parse("10.5 + 4.5 * 2.7 - 2 + "), 0.00001)
        //assertEquals(69.0, Parser().parse("65+2√4"), 0.00001)

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
        assertEquals(2.0, Parser().parse("√(1*2 + 4/2)"), 0.00001)
        // TODO: возможно сделать обязательным указание умножения перед скобками
        // assertEquals(12.0, Parser().parse("2!(5 + 1)"), 0.00001)
    }

    @Test
    fun test_invalid_input(){
        assertThrows(ParserException::class.java){
            Parser().parse("(5 + 4) + ! - 2")
        }
    }

    @Test
    fun test_constants(){
        assertEquals(E*2 + PI / 2, Parser().parse("E*2 + PI / 2"), 0.00001)
    }

    @Test
    fun test_radix(){
        assertEquals(233.82863, Parser().parse("(#2:101 + 5) * #8:34.5 - √#16:abc"), 0.00001)
    }

    @Test
    fun test_commas(){
        assertEquals(1.0, Parser().parse("(5 + 4), 2-2!, 7 + (5 + 1 / 8), 6, 1"), 0.00001)
    }

    @Test
    fun test_functions(){
        assertEquals(4.79588, Parser().parse("2 * (4.5 + lg(10 + 5 + sqrt(100))) - log(2 ^ 7, √4 + 2 - 2!) "), 0.00001)
    }

    @Test
    fun test_trig(){
        assertEquals(20.0, Parser().parse("deg(arcsin(sin(rad(20)))"), 0.00001)
    }

    @Test
    fun test_logical(){
        assertEquals(1.0, Parser().parse("8 = 16 / 2"), 0.00001)
        assertEquals(1.0, Parser().parse("8 & (-2)"), 0.00001)
        assertEquals(0.0, Parser().parse("8 & 0.00000000001"), 0.00001)
        assertEquals(1.0, Parser().parse("0.00000003 | 0.00000000001"), 0.00001)
        assertEquals(1.0, Parser().parse("8 & 3+5*(10 - 2)"), 0.00001)
        assertEquals(0.0, Parser().parse("~7"), 0.00001)
        assertEquals(1.0, Parser().parse("7 > 6"), 0.00001)
        assertEquals(1.0, Parser().parse("7 > 6.9999999"), 0.00001)
        assertEquals(0.0, Parser().parse("7 > 6.9999999999999999"), 0.00001)
        assertEquals(1.0, Parser().parse("7 ≥ 6.9999999999999999"), 0.00001)
    }


}