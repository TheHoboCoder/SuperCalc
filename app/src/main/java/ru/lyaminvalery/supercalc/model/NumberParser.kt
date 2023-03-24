package ru.lyaminvalery.supercalc.model
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.truncate

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

        fun numberToRadix(r: Double, radix: Int, digits: Int = 10): String{
            var intPart = truncate(r.absoluteValue).toInt()
            var frPart = (r.absoluteValue - intPart)

            val intDigits = mutableListOf<String>()
            val frDigits = mutableListOf<String>()

            while(intPart > 0){
                intDigits.add((intPart % radix).toString(radix))
                intPart = intPart.div(radix)
            }
            if(intDigits.size == 0){
                intDigits.add("0")
            }

            var counter = 0
            while(frPart > 0 && counter < digits){
                val res = frPart * radix
                frDigits.add(truncate(res).toInt().toString(radix))
                frPart = res - truncate(res)
                counter ++
            }
            if(frDigits.size == 0){
                frDigits.add("0")
            }

            val sign = if (r.sign == -1.0) "-" else ""
            val intString = intDigits.reversed().joinToString(separator = "") { it }
            val frString = frDigits.joinToString(separator = "") { it }
            return "${sign}${intString.uppercase()}.${frString.uppercase()}"
        }

    }

}