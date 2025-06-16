package ru.lyaminvalery.supercalc.model

import ru.lyaminvalery.supercalc.model.implemented.BINARY_OPERATIONS
import ru.lyaminvalery.supercalc.model.implemented.CONSTANTS
import ru.lyaminvalery.supercalc.model.implemented.FUNCTIONS
import ru.lyaminvalery.supercalc.model.implemented.UNARY_OPERATIONS
import java.lang.Character.MAX_RADIX
import java.lang.Character.MIN_RADIX

class ParserException(message: String) : Exception(message)

class Frame(val function: FunctionCalc? = null){

    private val _numberStack = ArrayDeque<Double>()
    private var _stackTop = 0
    private val _operationStack = ArrayDeque<BinaryOperation>()
    private var _unaryOperation: UnaryOperation? = null

     fun collapse(){
        while(_numberStack.size - 1 > _stackTop &&
            _operationStack.size > 0){
            if(_numberStack.size - 1 - _stackTop <= 1)
                throw ParserException("invalid input")
            val operation = _operationStack.removeLast()
            val operandB = _numberStack.removeLast()
            val operandA = _numberStack.removeLast()
            _numberStack.addLast(operation.compute(operandA, operandB))
        }
    }

    fun separate(){
        runUnaryOperation()
        collapse()
        _stackTop++
    }

    fun addNumber(number: Double){
        _numberStack.addLast(number)
    }

    fun addBinaryOperation(binaryOperation: BinaryOperation){
        runUnaryOperation()
        if (_operationStack.size > 0 &&
            !binaryOperation.hasBiggerPriority(_operationStack.last())){
            collapse()
        }

        _operationStack.addLast(binaryOperation)
    }

    fun addUnaryOperation(unaryOperation: UnaryOperation){
        _unaryOperation = unaryOperation

        if(!unaryOperation.prefix){
            runUnaryOperation()
        }
    }

    private fun runUnaryOperation() {
        if(_unaryOperation != null){

            if(_numberStack.size - 1 - _stackTop == 0)
                throw ParserException("invalid input")

            _numberStack.addLast(_unaryOperation!!.compute(_numberStack.removeLast()))

            _unaryOperation = null
        }
    }

    fun calculateAll(): Double {
        runUnaryOperation()
        collapse()
        return function?.compute(_numberStack) ?: _numberStack.last()
    }


}


class Parser: ITokenCollector {

    val binaryOperations = BINARY_OPERATIONS.toMutableMap()
    val unaryOperations = UNARY_OPERATIONS.toMutableMap()
    val functions = FUNCTIONS.toMutableMap()
    val variables = mutableMapOf<String, Double>()
    val constants = CONSTANTS.toMutableMap()

    private val frames = ArrayDeque<Frame>()

    private var _radix = 10
    private var source: String = ""
    private var currentPos: Int = -1


    fun eval(tokens: List<Token>, str: String): Double{
        frames.clear()
        frames.addLast(Frame())
        source = str
        var prevToken: Token? = null
        for(token in tokens){
            if (token.hasError){
                return 0.0
            }
            token.collect(currentPos, this, prevToken)
            currentPos++
            prevToken = token
        }
        return frames.last().calculateAll()
    }


    private fun insertMultiplication(previousToken: Token?){
        if(previousToken is NumberToken ||
           previousToken is CloseBraceToken ||
           previousToken is UnaryToken){
            frames.last().addBinaryOperation(binaryOperations['*']!!)
        }
    }


    override fun collectNumberToken(position: Int, numberToken: NumberToken, prevToken: Token?) {
        try{
            val subst = numberToken.extract(source)
            frames.last().addNumber(NumberParser.parseString(subst, radix = _radix, separator = '.'))
            _radix = 10
            numberToken.hasError = false
        }
        catch(e: NumberFormatException){
            numberToken.hasError = true
            throw ParserException("invalid format")
        }
    }

    override fun collectRadixToken(position: Int, radixToken: RadixToken, prevToken: Token?) {
        try{
            val subst = radixToken.extract(source)
            val radix = NumberParser.parseString(subst, unsigned = true, int = true).toInt()
            if(radix < MIN_RADIX || radix > MAX_RADIX){
                radixToken.hasError = true
                throw ParserException("invalid radix")
            }
            radixToken.hasError = false
            _radix = radix
        }
        catch(e: NumberFormatException){
            radixToken.hasError = true
            throw ParserException("invalid format")
        }
    }


    override fun collectBinaryToken(position: Int, binaryToken: BinaryToken, prevToken: Token?) {
        val subst = binaryToken.extract(source)
        if(!binaryOperations.containsKey(subst[0])){
            binaryToken.hasError = true
            throw ParserException("unknown token")
        }
        val operation: BinaryOperation = binaryOperations[subst[0]]!!
        binaryToken.hasError = false

        frames.last().addBinaryOperation(operation)
    }

    override fun collectUnaryToken(position: Int, unaryToken: UnaryToken, prevToken: Token?) {
        val subst = unaryToken.extract(source)
        if(!unaryOperations.containsKey(subst[0])){
            unaryToken.hasError = true
            throw ParserException("unknown token")
        }
        insertMultiplication(prevToken)
        frames.last().addUnaryOperation(unaryOperations[subst[0]]!!)
    }

    override fun collectIdentifier(
        position: Int,
        identifierToken: IdentifierToken,
        prevToken: Token?
    ) {
        val subst = identifierToken.extract(source)
        identifierToken.hasError = false
        if(subst.lowercase() in functions){
            frames.addLast(Frame(functions[subst.lowercase()]!!))
        }
        else if(subst.uppercase() in constants){
            frames.last().addNumber(constants[subst.uppercase()]!!)
        }
        else if(variables.containsKey(subst)){
            frames.last().addNumber(variables[subst]!!)
        }
        else{
            identifierToken.hasError = true
            throw ParserException("unknown identifier: $subst")
        }
    }

    override fun collectSeparator(position: Int, prevToken: Token?) {
        frames.last().separate()
    }

    override fun collectOpenBrace(position: Int, prevToken: Token?) {
        if(frames.last().function == null){
            insertMultiplication(prevToken)
            frames.addLast(Frame())
        }
    }

    override fun collectCloseBrace(position: Int, prevToken: Token?) {
        val prevFrame = frames.removeLast()
        if(frames.size == 0){
            throw ParserException("unmatched braces")
        }
        frames.last().addNumber(prevFrame.calculateAll())
    }

    override fun collectSpecial() {}
}