package ru.lyaminvalery.supercalc.model

import kotlin.math.abs
import kotlin.math.pow

val BINARY_OPERATIONS = arrayOf<BinaryOperation>(

    object: BinaryOperation('>', -1){
        override fun compute(a: Double, b: Double): Double = if(a > b) 1.0 else 0.0
    },

    object: BinaryOperation('<', -1){
        override fun compute(a: Double, b: Double): Double = if(a < b) 1.0 else 0.0
    },

    object: BinaryOperation('=', -1){
        override fun compute(a: Double, b: Double): Double = if(abs(a - b) <= 0.00000001) 1.0 else 0.0
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
        override fun compute(a: Double, b: Double): Double = a / b
    },

    object: BinaryOperation('^', 2){
        override fun compute(a: Double, b: Double): Double = a.pow(b)
    },


).associateBy { it.name }