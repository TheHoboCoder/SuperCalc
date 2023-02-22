package ru.lyaminvalery.supercalc.model
import kotlin.math.pow

class NumberParser(){

    companion object{

        fun isCharAllowed(char: Char,
                          initial: Boolean = false,
                          radix: Int = 10,
                          separator: Char = ',',
                          unsigned: Boolean = false,
                          int: Boolean = false): Boolean {

            if(char.isLetterOrDigit()){
                return try{
                    char.digitToInt(radix)
                    true
                } catch (e: IllegalArgumentException){
                    false
                }
            }

            return ((char==separator && !int) ||
                    (char=='-' && initial && !unsigned))

        }

        fun parseString(string: String,
                        radix: Int = 10,
                        separator: Char = ',',
                        unsigned: Boolean = false,
                        int: Boolean = false): Double{

            val parts = string.split(separator).map{ it.trim() }
            if (parts.size > 2){
                throw NumberFormatException("invalid number: $string")
            }
            if (parts.size == 2 && int){
                throw NumberFormatException("$string is not integer")
            }
            if ((parts[0] == "" || parts[0]=="-") && (parts.size == 1 || parts[1] == "")){
                throw NumberFormatException("empty number")
            }
            val integerPart = if (parts[0] != "") parts[0] else "0"
            val fractionalPart = if (parts.size == 2) parts[1] else "0"

            if (!fractionalPart[0].isLetterOrDigit()){
                throw NumberFormatException("invalid number: $string")
            }
            val res = integerPart.toLong(radix) +
                    fractionalPart.toLong(radix) / radix.toDouble().pow(fractionalPart.length)
            if (res < 0 && unsigned){
                throw NumberFormatException("$string is not unsigned")
            }
            return res
        }
    }

}