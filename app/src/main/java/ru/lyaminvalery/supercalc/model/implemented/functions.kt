package ru.lyaminvalery.supercalc.model

import kotlin.math.*

val FUNCTIONS = arrayOf<FunctionCalc>(

    object: FunctionCalc("sqrt", 1){
        override fun compute(args: List<Double>): Double = sqrt(args[0])
    },

    object: FunctionCalc("ln", 1){
        override fun compute(args: List<Double>): Double = ln(args[0])
    },

    object: FunctionCalc("lg", 1){
        override fun compute(args: List<Double>): Double = log10(args[0])
    },

    object: FunctionCalc("log", 2){
        override fun compute(args: List<Double>): Double = log(args[0], args[1])
    },

    object: FunctionCalc("sin", 1){
        override fun compute(args: List<Double>): Double = sin(args[0])
    },

    object: FunctionCalc("cos", 1){
        override fun compute(args: List<Double>): Double = cos(args[0])
    },

    object: FunctionCalc("tg", 1){
        override fun compute(args: List<Double>): Double = tan(args[0])
    },

    object: FunctionCalc("ctg", 1){
        override fun compute(args: List<Double>): Double = tan(args[0])
    },

    object: FunctionCalc("rad", 1){
        override fun compute(args: List<Double>): Double = args[0] / 180 * PI
    },

    object: FunctionCalc("degrees", 1){
        override fun compute(args: List<Double>): Double = (args[0] * 180) / PI
    },

    object: FunctionCalc("min", -1){
        override fun compute(args: List<Double>): Double = args.min()
    },

    object: FunctionCalc("max", -1){
        override fun compute(args: List<Double>): Double = args.max()
    },

    object: FunctionCalc("if", 3){
        override fun compute(args: List<Double>): Double = if(abs(args[0] -1) <= 0.000001) args[1] else args[2]
    }


).associateBy { it.name }