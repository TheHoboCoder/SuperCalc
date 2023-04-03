package ru.lyaminvalery.supercalc.model.implemented

import ru.lyaminvalery.supercalc.model.UnaryOperation
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sqrt

val UNARY_OPERATIONS = arrayOf<UnaryOperation>(

    object: UnaryOperation('√', prefix = true){
        override fun compute(a: Double): Double = sqrt(a)
    },

    object: UnaryOperation('~', prefix = true){
        override fun compute(a: Double): Double = if(abs(a) > EPS) 0.0 else 1.0
    },
    object: UnaryOperation('%', prefix = true){
        override fun compute(a: Double): Double = a / 100
    },

    object: UnaryOperation('!'){
        override fun compute(a: Double): Double {
            val eps = 0.00000000001
            if(a < 0.0 || a - floor(a) > eps)
                throw IllegalArgumentException("$a cannot be factorized!")
            return if (a < eps) 1.0 else (1..a.toLong()).reduce{ x, y -> x*y }.toDouble()
        }
    }

).associateBy { it.name }