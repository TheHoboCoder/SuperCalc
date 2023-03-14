package ru.lyaminvalery.supercalc.model

abstract class BinaryOperation(val name: Char,
                      val priority: Int = 0){

    abstract fun compute(a: Double, b: Double): Double
    fun hasBiggerPriority(other: BinaryOperation) = priority > other.priority
}

abstract class UnaryOperation(val name: Char,
                     val prefix: Boolean = false){
    abstract fun compute(a: Double): Double
}

abstract class FunctionCalc(val name: String, val argsCount: Int = -1){
    abstract fun compute(args: List<Double>): Double

    fun run(args: List<Double>): Double{
        if(argsCount != -1 && args.size != argsCount){
            throw ParserException("$name accepts $argsCount parameters, ${args.size} given")
        }
        return compute(args)
    }
}

abstract class FunctionScope(val start: Char, val end: Char){
    abstract fun compute(a: Double): Double
}

//abstract class TokenType{
//    var nextTokens = arrayOf<TokenType>()
//    abstract fun isCharAllowed(char: Char, previousTokenType: TokenType, radix: Int = 10): Boolean
//
//    fun read(char: Char, previousTokenType: TokenType, radix: Int = 10): TokenType {
//        if (isCharAllowed(char, previousTokenType)) return this
//
//        return nextTokens.firstOrNull { it.isCharAllowed(char, previousTokenType) }
//            ?: throw ParserException("unexpected char $char")
//    }
//}

class Token(var tokenType: Parser.Companion.TokenType,
            var start: Int,
            var end: Int,
            var hasError: Boolean = false)