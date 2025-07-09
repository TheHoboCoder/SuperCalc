package ru.lyaminvalery.supercalc

import junit.framework.TestCase.assertEquals
import org.junit.Test
import ru.lyaminvalery.supercalc.model.Parser
import org.junit.Assert.*
import ru.lyaminvalery.supercalc.model.BinaryOperation
import ru.lyaminvalery.supercalc.model.BinaryToken
import ru.lyaminvalery.supercalc.model.NumberToken
import ru.lyaminvalery.supercalc.model.Token

class ParserTest {

    companion object{
        const val DELTA = 0.000000001
    }

//    @Test
//    fun test_empty(){
//        val parser = Parser()
//        assertEquals(parser.eval(listOf(), ""), 0.0, DELTA)
//    }
//
//    @Test
//    fun test_simple_numbers(){
//        val parser = Parser()
//        assertEquals(parser.eval(listOf(
//            NumberToken(start = 0, end=1)
//        ), "10"), 10.0, DELTA)
//        assertEquals(parser.eval(listOf(
//            NumberToken(start = 0, end=1)
//        ), "+7"), 7.0, DELTA)
//        assertEquals(parser.eval(listOf(
//            NumberToken(start = 0, end=1)
//        ), "-2.45"), -2.45, DELTA)
//    }
//
//    @Test
//    fun test_simple_expressions(){
//        val parser = Parser()
//        assertEquals(
//            parser.eval(
//                listOf<Token>(
//                    NumberToken(start = 0, end=1),
//                    BinaryToken(start = 2, end=3),
//                    NumberToken(start = 4, end=5),
//                ),
//                "2 + 2"
//            ),
//            4.0,
//            DELTA
//        )
//    }
}