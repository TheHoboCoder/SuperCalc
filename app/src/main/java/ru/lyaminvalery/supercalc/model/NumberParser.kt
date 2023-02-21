package ru.lyaminvalery.supercalc.model

import kotlin.math.min
import kotlin.math.pow

class NumberParser(val base: Int = 10,
                   val separator: Char = ',',
                   val unsigned: Boolean = false,
                   val int: Boolean = false){

    companion object{
        val MAX_BASE = 10 + ('A'..'Z').count()
    }

    private var _value: Double = 0.0
    private var _digits: Int = 0
    private var _fractionPartParser: NumberParser? = null
    private var _sign = 1
    private val _alphabet = mutableListOf<Char>()

    init{
        _alphabet.addAll((0 until min(10, base)).map{ Character.forDigit(it, 10)} )
        var sym = 'A'
        for(t in 10 until base){
            _alphabet.add(sym)
            sym = sym.inc()
        }
    }

    fun result(): Double {

        if (digits == 0 && (_fractionPartParser == null || _fractionPartParser?.digits == 0))
            throw NumberFormatException("empty number")

        if(_fractionPartParser != null){
            _value += _fractionPartParser?.inverse()!!
        }

        return _sign * _value
    }

    val hasResult: Boolean
        get() = _digits > 0

    val digits: Int
        get() = _digits

    fun inverse(): Double{
        return result() / base.toDouble().pow(digits)
    }


    fun parseString(string: String): Double{
        var initial = true
        for(c in string){
            nextChar(c, initial)
            initial = false
        }
        return result()
    }

    fun isCharAllowed(c: Char, initial: Boolean = false): Boolean
            = _alphabet.contains(c) || (!unsigned && initial && c == '-') || (!int && c == separator)

    fun nextChar(c: Char, initial: Boolean = false){

        if (_fractionPartParser != null) {
            _fractionPartParser?.nextChar(c)
            return
        }

        val num = _alphabet.indexOfFirst { it==c }
        if (num != -1){
            _digits++
            // лидирующий ноль
            if (num == 0 && _value == 0.0) return
            _value *= base
            _value += num
        }
        else if (c == '-' && !unsigned && initial){
            _sign = -1
        }
        else if (c == separator && !int && _fractionPartParser == null){
            _fractionPartParser = NumberParser(base, separator, unsigned=true, int=true)
        }
        else{
            throw NumberFormatException("unexpected char $c")
        }
    }

}