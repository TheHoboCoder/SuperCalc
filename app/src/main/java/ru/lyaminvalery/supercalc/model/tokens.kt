package ru.lyaminvalery.supercalc.model

import java.lang.Character.MAX_RADIX
import java.lang.Character.MIN_RADIX
import kotlin.collections.contains

interface ITokenCollector{
    fun collectNumberToken(position: Int, numberToken: NumberToken, prevToken: Token?)
    fun collectRadixToken(position: Int, radixToken: RadixToken, prevToken: Token?)
    fun collectBinaryToken(position: Int, binaryToken: BinaryToken, prevToken: Token?)
    fun collectIdentifier(position: Int, identifierToken: IdentifierToken, prevToken: Token?)
    fun collectFunction(position: Int, function: FunctionToken, prevToken: Token?)
    fun collectSeparator(position: Int, prevToken: Token?)
    fun collectOpenBrace(position: Int, prevToken: Token?)
    fun collectCloseBrace(position: Int, prevToken: Token?)
    fun collectSpecial()
}

data class CalculatorContext(
    val names: MutableMap<String, Double>,
    val binaryOperations: Map<String, BinaryOperation>,
    val functions: Map<String, FunctionCalc>
)

interface IParsable<T>{
    fun parse(
        source: String,
        calculatorContext: CalculatorContext,
        radix: Int = 10
    ): T
}

abstract class Token(var start: Int,
            var end: Int,
            var hasError: Boolean = false){

    fun extract(str: String) = str.substring(start, end)
    abstract fun collect(position: Int, collector: ITokenCollector, prevToken: Token?)
}

// remove scope start -> string start
// or not -> just create isNextTokenAllowed()
// нужно подумать, какая шляпа получается: как мы отличим например унарный минус от бинарного?
// scope start -> prevToken?
// ну допустим мы будем хранить массив matcher для текущего
// есть граф начало -> список 
interface IMatcher {
    fun matches(
        char: Char,
        calculatorContext: CalculatorContext,
        scopeStart: Boolean = false
    ): Boolean
    fun create(start: Int): Token
    fun isAllowedOn(prevToken: Token?, nextToken: Token): Boolean
}

class NumberToken(start: Int,
                  end: Int,
                  hasError: Boolean = false): Token(start, end, hasError), IParsable<Double>
{
    companion object Matcher: IMatcher{
        override fun matches(
            char: Char,
            calculatorContext: CalculatorContext,
            scopeStart: Boolean
        ): Boolean {
            return char.isLetterOrDigit() || char == '.'
        }

        override fun create(start: Int): Token = NumberToken(start, end=start)

        override fun isAllowedOn(prevToken: Token?, nextToken: Token) = true
    }

    var value: Double = 0.0

    override fun parse(
        source: String,
        calculatorContext: CalculatorContext,
        radix: Int
    ): Double {
        try{
            val subst = this.extract(source)
            value = NumberParser.parseString(subst, radix = radix, separator = '.')
            this.hasError = false
            return value
        }
        catch(e: NumberFormatException){
            this.hasError = true
            throw ParserException("invalid format")
        }
    }

    override fun collect(position: Int, collector: ITokenCollector, prevToken: Token?) {
        collector.collectNumberToken(position, this, prevToken)
    }

}

class RadixToken(start: Int,
                  end: Int,
                  hasError: Boolean = false, ): Token(start, end, hasError), IParsable<Int>
{

    companion object Matcher: IMatcher{
        override fun matches(
            char: Char,
            calculatorContext: CalculatorContext,
            scopeStart: Boolean
        ): Boolean {
            return char.isDigit()
        }
        override fun create(start: Int): Token = RadixToken(start, end=start)
        override fun isAllowedOn(prevToken: Token?, nextToken: Token) =
            prevToken is RadixStartToken && nextToken is RadixEndToken
    }

    var value: Int = 10

   override fun parse(
        source: String,
        calculatorContext: CalculatorContext,
        radix: Int
    ): Int {
        val subst = this.extract(source)
        val radix = NumberParser.parseString(subst, unsigned = true, int = true).toInt()
        if(radix < MIN_RADIX || radix > MAX_RADIX){
            this.hasError = true
            throw ParserException("invalid radix")
        }
       this.hasError = false
        value = radix
       return value
    }

    override fun collect(position: Int, collector: ITokenCollector, prevToken: Token?) {
        collector.collectRadixToken(position, this, prevToken)
    }

}

class BinaryToken(start: Int,
                     end: Int,
                     hasError: Boolean = false, ): Token(start, end, hasError), IParsable<BinaryOperation>{

    companion object Matcher: IMatcher{
        override fun matches(
            char: Char,
            calculatorContext: CalculatorContext,
            scopeStart: Boolean
        ): Boolean {
            return !scopeStart && char in calculatorContext.binaryOperations.keys.flatMap { it.toList() }
        }

        override fun create(start: Int): Token = BinaryToken(start, end=start)
    }

    var value: BinaryOperation? = null

    override fun parse(
        source: String,
        calculatorContext: CalculatorContext,
        radix: Int
    ): BinaryOperation {
        val subst = this.extract(source)
        if(!calculatorContext.binaryOperations.containsKey(subst)){
            this.hasError = true
            value = null
            throw ParserException("unknown binary operation")
        }
        var value = calculatorContext.binaryOperations[subst]
        this.hasError = true
        return value!!
    }

    override fun collect(position: Int, collector: ITokenCollector, prevToken: Token?) {
        collector.collectBinaryToken(position, this, prevToken)
    }
}

class FunctionToken(start: Int,
                    end: Int,
                    hasError: Boolean = false, ): Token(start, end, hasError), IParsable<FunctionCalc>{

    companion object Matcher: IMatcher{
        override fun matches(
            char: Char,
            calculatorContext: CalculatorContext,
            scopeStart: Boolean
        ): Boolean {
            return char in calculatorContext.functions.keys.flatMap { it.toList() }
        }

        override fun create(start: Int): Token = FunctionToken(start, end=start)
    }

    override fun collect(
        position: Int,
        collector: ITokenCollector,
        prevToken: Token?
    ) {
       collector.collectFunction(position, this, prevToken)
    }

    override fun parse(
        source: String,
        calculatorContext: CalculatorContext,
        radix: Int
    ): FunctionCalc {
        val subst = this.extract(source)
        if(subst.lowercase() !in calculatorContext.functions){
            this.hasError = true
            throw ParserException("unknown function $subst")
        }
        this.hasError = false
        return calculatorContext.functions[subst.lowercase()]!!
    }

}

class IdentifierToken(start: Int,
                 end: Int,
                 hasError: Boolean = false, ): Token(start, end, hasError), IParsable<Double>{

    // TODO: how to fix ambiugity?
    companion object Matcher: IMatcher{
        override fun matches(
            char: Char,
            calculatorContext: CalculatorContext,
            scopeStart: Boolean
        ): Boolean {
            return char in calculatorContext.names.keys.flatMap { it.toList() }
        }

        override fun create(start: Int): Token = IdentifierToken(start, end=start)
    }

    override fun parse(
        source: String,
        calculatorContext: CalculatorContext,
        radix: Int
    ): Double {
        val subst = this.extract(source)
        if(subst.lowercase() !in calculatorContext.names){
            this.hasError = true
            throw ParserException("unknown name $subst")
        }
        this.hasError = false
        return calculatorContext.names[subst.lowercase()]!!
    }

    override fun collect(position: Int, collector: ITokenCollector, prevToken: Token?) {
        collector.collectIdentifier(position, this, prevToken)
    }
}

class SeparatorToken(start: Int,
                     end: Int,
                     hasError: Boolean = false,): Token(start, end, hasError){

    companion object Matcher: IMatcher{
        override fun matches(
            char: Char,
            calculatorContext: CalculatorContext,
            scopeStart: Boolean
        ): Boolean {
            return char == ','
        }

        override fun create(start: Int): Token = SeparatorToken(start, end=start)
    }

    override fun collect(position: Int, collector: ITokenCollector, prevToken: Token?) {
        collector.collectSeparator(position, prevToken)
    }
}

class OpenBraceToken(start: Int,
                      end: Int,
                      hasError: Boolean = false,): Token(start, end, hasError){

    companion object Matcher: IMatcher{
        override fun matches(
            char: Char,
            calculatorContext: CalculatorContext,
            scopeStart: Boolean
        ): Boolean {
            return char == '('
        }

        override fun create(start: Int): Token = OpenBraceToken(start, end=start)
    }

    override fun collect(position: Int, collector: ITokenCollector, prevToken: Token?) {
        collector.collectOpenBrace(position, prevToken)
    }
}

class CloseBraceToken(start: Int,
                     end: Int,
                     hasError: Boolean = false,): Token(start, end, hasError){

    companion object Matcher: IMatcher{
        override fun matches(
            char: Char,
            calculatorContext: CalculatorContext,
            scopeStart: Boolean
        ): Boolean {
            return char == ')'
        }

        override fun create(start: Int): Token = CloseBraceToken(start, end=start)
    }

    override fun collect(position: Int, collector: ITokenCollector, prevToken: Token?) {
        collector.collectCloseBrace(position, prevToken)
    }
}

class RadixStartToken(start: Int,
                     end: Int,
                     hasError: Boolean = false,): Token(start, end, hasError){

    companion object Matcher: IMatcher{
        override fun matches(
            char: Char,
            calculatorContext: CalculatorContext,
            scopeStart: Boolean
        ): Boolean {
            return char == '#'
        }

        override fun create(start: Int): Token = RadixStartToken(start, end=start)
    }

    override fun collect(position: Int, collector: ITokenCollector, prevToken: Token?) {
        collector.collectSpecial()
    }
}

class RadixEndToken(start: Int,
                      end: Int,
                      hasError: Boolean = false,): Token(start, end, hasError){

    companion object Matcher: IMatcher{
        override fun matches(
            char: Char,
            calculatorContext: CalculatorContext,
            scopeStart: Boolean
        ): Boolean {
            return char == ':'
        }

        override fun create(start: Int): Token = RadixEndToken(start, end=start)
    }

    override fun collect(position: Int, collector: ITokenCollector, prevToken: Token?) {
        collector.collectSpecial()
    }
}

