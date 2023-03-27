package ru.lyaminvalery.supercalc.model.implemented

import ru.lyaminvalery.supercalc.model.BinaryOperation
import ru.lyaminvalery.supercalc.model.ParserException
import kotlin.math.abs
import kotlin.math.pow

val EPS = 0.00000001

val BINARY_OPERATIONS = arrayOf<BinaryOperation>(

    object: BinaryOperation('>', -1){
        override fun compute(a: Double, b: Double): Double = if(a > b) 1.0 else 0.0
    },

    object: BinaryOperation('<', -1){
        override fun compute(a: Double, b: Double): Double = if(a < b) 1.0 else 0.0
    },

    object: BinaryOperation('=', -1){
        override fun compute(a: Double, b: Double): Double = if(abs(a - b) <= EPS) 1.0 else 0.0
    },

    object: BinaryOperation('&', -1){
        override fun compute(a: Double, b: Double): Double = if(a + b >= 2.0) 1.0 else 0.0
    },

    object: BinaryOperation('|', -1){
        override fun compute(a: Double, b: Double): Double = if(a + b >= 1.0) 1.0 else 0.0
    },

    object: BinaryOperation('+', 0){
        override fun compute(a: Double, b: Double): Double = a + b
    },

    object: BinaryOperation('-', 0){
        override fun compute(a: Double, b: Double): Double = a - b
    },

    object: BinaryOperation('*', 1){
        override fun compute(a: Double, b: Double): Double = a * b
    },

    object: BinaryOperation('/', 1){
        override fun compute(a: Double, b: Double):Double {
            if (abs(b) <= EPS){
                throw ParserException("Division by zero")
            }
            return a / b
        }
    },

    object: BinaryOperation('^', 2){
        override fun compute(a: Double, b: Double): Double = a.pow(b)
    },


).associateBy { it.name }