package ru.lyaminvalery.supercalc.model

import ru.lyaminvalery.supercalc.model.implemented.BINARY_OPERATIONS
import ru.lyaminvalery.supercalc.model.implemented.CONSTANTS
import ru.lyaminvalery.supercalc.model.implemented.FUNCTIONS
import java.lang.Character.MAX_RADIX
import java.lang.Character.MIN_RADIX

class ParserException(message: String) : Exception(message)

class Frame(val function: FunctionCalc? = null, var parent: Frame? = null){

    private val _numberStack = ArrayDeque<Double>()
    private val _operationStack = ArrayDeque<BinaryOperation>()

    fun hasParent() = parent != null
    fun parentFunction() = parent?.function

    fun collapse(){
        if(_operationStack.isNotEmpty() && _numberStack.size - _operationStack.size != 1){
            throw ParserException("invalid stack")
        }
        while(_operationStack.isNotEmpty()){
            val operation = _operationStack.removeLast()
            val operandB = _numberStack.removeLast()
            val operandA = _numberStack.removeLast()
            _numberStack.addLast(operation.compute(operandA, operandB))
        }
    }

    fun runFunction() {
        function?.let{
            // TODO: if args = -1, grab everything
            if(_numberStack.size > 1 && _numberStack.size != it.argsCount){
                throw ParserException(
                    "${it.name}: argument count doesn't match: expected ${it.argsCount}, got ${_numberStack.size}"
                )
            }
            // frame contains either function or binary operators
            _numberStack.addLast(it.compute(_numberStack))
        }
    }

    fun calculateAll(): Double {
        runFunction()
        collapse()
        return _numberStack.last()
    }

    fun dive(function: FunctionCalc? = null): Frame{
        val frame = Frame(function, this)
        // if function is postfix, we should push last number to it
        if(function?.postfix == true){
            frame.addNumber(_numberStack.last())
        }
        return frame
    }

    fun rise(): Frame{
        val result = calculateAll()
        return parent?.apply {
            addNumber(result)
        } ?: this
    }

    fun addNumber(number: Double){
        _numberStack.addLast(number)
    }

    fun addBinaryOperation(binaryOperation: BinaryOperation){
        if(function != null){
            throw ParserException("binary operators is not allowed in function scope")
        }
        if (_operationStack.isNotEmpty() &&
            !binaryOperation.hasBiggerPriority(_operationStack.last())){
            collapse()
        }

        _operationStack.addLast(binaryOperation)
    }

}


class Parser(val calculatorContext: CalculatorContext): ITokenCollector {

    private var frameTop = Frame()

    private var _radix = 10
    private var source: String = ""
    private var currentPos: Int = -1


    fun eval(tokens: List<Token>, str: String): Double{
        frameTop = Frame()
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
        return frameTop.calculateAll()
    }


    private fun insertMultiplication(previousToken: Token?){
        // TODO: functions
        if(previousToken is NumberToken ||
           previousToken is CloseBraceToken){
            frameTop.addBinaryOperation(calculatorContext.binaryOperations["*"]!!)
        }
    }


    override fun collectNumberToken(position: Int, numberToken: NumberToken, prevToken: Token?) {
        // if there's function, dive
        if(frameTop.function != null){
            frameTop = frameTop.dive()
        }
        frameTop.addNumber(numberToken.parse(source, calculatorContext, _radix))
    }

    override fun collectRadixToken(position: Int, radixToken: RadixToken, prevToken: Token?) {
        _radix = radixToken.parse(source, calculatorContext, _radix)
    }

    override fun collectBinaryToken(position: Int, binaryToken: BinaryToken, prevToken: Token?) {
        val binaryOperation = binaryToken.parse(source, calculatorContext, _radix)
        // if there's function, we should execute it first
        if(frameTop.function != null || frameTop.parentFunction()?.argsCount == 1){
            frameTop = frameTop.rise()
        }
        frameTop.addBinaryOperation(binaryOperation)
    }

    override fun collectIdentifier(
        position: Int,
        identifierToken: IdentifierToken,
        prevToken: Token?
    ) {
//        val subst = identifierToken.extract(source)
//        identifierToken.hasError = false
//        if(subst.lowercase() in functions){
//            frames.addLast(Frame(functions[subst.lowercase()]!!))
//        }
//        else if(subst.uppercase() in constants){
//            frames.last().addNumber(constants[subst.uppercase()]!!)
//        }
//        else if(variables.containsKey(subst)){
//            frames.last().addNumber(variables[subst]!!)
//        }
//        else{
//            identifierToken.hasError = true
//            throw ParserException("unknown identifier: $subst")
//        }
    }

    override fun collectSeparator(position: Int, prevToken: Token?) {
        if(frameTop.parentFunction() == null){
            throw ParserException(", is not allowed here")
        }
        // collect operand for function
        frameTop = frameTop.rise()
        // and dive again for next
        frameTop = frameTop.dive()
    }

    override fun collectOpenBrace(position: Int, prevToken: Token?) {
        insertMultiplication(prevToken)
        frameTop = frameTop.dive()
    }

    override fun collectCloseBrace(position: Int, prevToken: Token?) {
        if(!frameTop.hasParent()){
            throw ParserException("unmatched braces")
        }
        frameTop = frameTop.rise()
    }

    override fun collectSpecial() {}
}