package ru.lyaminvalery.supercalc

import org.junit.Test
import ru.lyaminvalery.supercalc.model.NumberParser
import org.junit.Assert.*

class NumberParserTest {

    @Test
    fun simpleparsing_test(){
        assertEquals(3452.0, NumberParser().parseString("3452"), 0.0001)
        assertEquals(-3452.0, NumberParser().parseString("-3452"), 0.0001)
    }

    @Test
    fun base_converting_test(){
        val number = 789.34
        assertEquals(number, NumberParser().parseString("789,34"), 0.01)
        assertEquals(number, NumberParser(base=2).parseString("1100010101,01010111000"), 0.01)
        assertEquals(number, NumberParser(base=8).parseString("1425,25605075341"), 0.01)
        assertEquals(number, NumberParser(base=16).parseString("315,570A3D70A3E"), 0.01)
    }

    @Test
    fun leading_zeros_test(){
        assertEquals(67.0, NumberParser().parseString("0000000067"), 0.0001)
        assertEquals(-67.0, NumberParser().parseString("-0000000067"), 0.0001)
    }

    @Test
    fun fractional_part_test(){
        assertEquals(345.678, NumberParser().parseString("345,678"), 0.0001)
    }

    @Test
    fun fractional_part_no_leading(){
        assertEquals(.209, NumberParser().parseString(",209"), 0.0001)
    }

    @Test
    fun fractional_part_leading_zeros(){
        assertEquals(0.000034, NumberParser().parseString("0,000034"), 0.000001)
    }

//    @Test
//    fun fractional_part_empty(){
//        assertEquals(52.0, NumberParser().parseString("52,"), 0.0001)
//    }

    @Test
    fun wrong_format_two_separators(){
        val exception = assertThrows(NumberFormatException::class.java){
            NumberParser().parseString("23,67,1")
        }
        assertEquals("unexpected char ,", exception.message)
    }

    @Test
    fun wrong_format_empty_separators(){
        val exception = assertThrows(NumberFormatException::class.java){
            NumberParser().parseString(",")
        }
        assertEquals("empty number", exception.message)
    }

    @Test
    fun wrong_format_random_char(){
        val exception = assertThrows(NumberFormatException::class.java){
            NumberParser().parseString("45Ls")
        }
        assertEquals("unexpected char L", exception.message)
    }

    @Test
    fun wrong_format_unsigned(){
        val exception = assertThrows(NumberFormatException::class.java){
            NumberParser(unsigned=true).parseString("-67")
        }
        assertEquals("unexpected char -", exception.message)
    }

    @Test
    fun wrong_format_int(){
        val exception = assertThrows(NumberFormatException::class.java){
            NumberParser(int=true).parseString("67,2")
        }
        assertEquals("unexpected char ,", exception.message)
    }

    @Test
    fun empty_number(){
        val exception = assertThrows(NumberFormatException::class.java){
            NumberParser().parseString("-")
        }
        assertEquals("empty number", exception.message)

        val exception2 = assertThrows(NumberFormatException::class.java){
            NumberParser().parseString("")
        }
        assertEquals("empty number", exception2.message)
    }

}