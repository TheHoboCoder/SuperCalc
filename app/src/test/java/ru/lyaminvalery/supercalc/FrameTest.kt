package ru.lyaminvalery.supercalc

import org.junit.Test
import org.junit.Assert.*
import ru.lyaminvalery.supercalc.model.Frame
import ru.lyaminvalery.supercalc.model.ParserException
import ru.lyaminvalery.supercalc.model.implemented.BINARY_OPERATIONS

class FrameTest {

    companion object{
        const val DELTA = 0.000000001
    }

    @Test
    fun test_simple_expressions(){
        val frame = Frame()
        // 2 + 2
        with(frame){
            addNumber(2.0)
            addBinaryOperation(BINARY_OPERATIONS['+']!!)
            addNumber(2.0)
        }
        assertEquals(frame.calculateAll(), 4.0, DELTA)
    }

    @Test
    fun test_priority_one(){
        val frame = Frame()
        // 2 + 5*2 + 3 = 15
        with(frame){
            addNumber(2.0)
            addBinaryOperation(BINARY_OPERATIONS['+']!!)
            addNumber(5.0)
            addBinaryOperation(BINARY_OPERATIONS['*']!!)
            addNumber(2.0)
            addBinaryOperation(BINARY_OPERATIONS['+']!!)
            addNumber(3.0)
        }
        assertEquals(frame.calculateAll(), 15.0, DELTA)
    }

    @Test
    fun test_priority_two(){
        val frame = Frame()
        // 2*5+2*3 = 16
        with(frame){
            addNumber(2.0)
            addBinaryOperation(BINARY_OPERATIONS['*']!!)
            addNumber(5.0)
            addBinaryOperation(BINARY_OPERATIONS['+']!!)
            addNumber(2.0)
            addBinaryOperation(BINARY_OPERATIONS['*']!!)
            addNumber(3.0)
        }
        assertEquals(frame.calculateAll(), 16.0, DELTA)
    }

    @Test
    fun test_invalid_stack(){
        val frame = Frame()
        with(frame) {
            addNumber(2.0)
            addBinaryOperation(BINARY_OPERATIONS['*']!!)
        }
        val exception = assertThrows(ParserException::class.java){
            frame.calculateAll()
        }
    }

    @Test
    fun test_recursive(){
        var frame = Frame()
        // 2*(5+2) = 14
        with(frame) {
            addNumber(2.0)
            addBinaryOperation(BINARY_OPERATIONS['*']!!)
        }
        frame = frame.dive()
        with(frame) {
            addNumber(5.0)
            addBinaryOperation(BINARY_OPERATIONS['+']!!)
            addNumber(2.0)
        }
        assertEquals(frame.rise().calculateAll(), 14.0, DELTA)
    }





}