package ru.lyaminvalery.supercalc.model

import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import kotlin.math.*

class ParserException(message: String) : Exception(message)


class Parser{

    companion object {

        val BINARY_OPERATIONS = arrayOf<BinaryOperation>(

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


        val UNARY_OPERATIONS = arrayOf<UnaryOperation>(

            object: UnaryOperation('√', prefix = true){
                override fun compute(a: Double): Double = sqrt(a)
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

            ).associateBy { it.name }


        val CONSTANTS = mapOf<String, Double>(
            "E" to E,
            "PI" to PI
        )

        enum class TokenType{
            INITIAL,
            NUMBER_OPERAND,
            NUMBER_RADIX,
            SEPARATOR,
            BINARY_OPERATION,
            UNARY_OPERATION,
            BRACE_OPEN,
            BRACE_CLOSE,
            RADIX_SPECIFICATION_START,
            RADIX_SPECIFICATION_END,
            IDENTIFIER,
        }

        fun isAllowed(previousToken: TokenType, currentToken: TokenType): Boolean{
            val defaultList = arrayOf(TokenType.NUMBER_OPERAND,
                                        TokenType.BRACE_OPEN,
                                        TokenType.RADIX_SPECIFICATION_START,
                                        TokenType.UNARY_OPERATION,
                                        TokenType.IDENTIFIER)
            when(previousToken){

                TokenType.INITIAL, TokenType.BINARY_OPERATION, TokenType.BRACE_OPEN, TokenType.SEPARATOR
                                                -> return currentToken in defaultList

                TokenType.BRACE_CLOSE -> return currentToken in arrayOf(TokenType.BRACE_CLOSE, TokenType.BINARY_OPERATION)

                TokenType.NUMBER_OPERAND, TokenType.IDENTIFIER -> return currentToken in arrayOf(TokenType.BINARY_OPERATION,
                                                                                           TokenType.UNARY_OPERATION,
                                                                                           TokenType.BRACE_OPEN,
                                                                                           TokenType.BRACE_CLOSE)
                TokenType.UNARY_OPERATION  -> return currentToken in defaultList ||
                                                     currentToken in arrayOf(TokenType.BINARY_OPERATION, TokenType.BRACE_CLOSE)

                TokenType.RADIX_SPECIFICATION_START -> return currentToken == TokenType.NUMBER_RADIX

                TokenType.NUMBER_RADIX -> return currentToken == TokenType.RADIX_SPECIFICATION_END

                TokenType.RADIX_SPECIFICATION_END -> return currentToken == TokenType.NUMBER_OPERAND

            }
        }


       fun getTokenForChar(char: Char, previousToken: TokenType, radix: Int = 10): TokenType{

            if(previousToken != TokenType.RADIX_SPECIFICATION_START &&
                NumberParser.isCharAllowed(char,
                    separator = '.',
                    initial=(previousToken==TokenType.INITIAL),
                    radix=radix)){
                return TokenType.NUMBER_OPERAND
            }
            else if(NumberParser.isCharAllowed(char, int=true, unsigned=true) ){
                return TokenType.NUMBER_RADIX
            }
            else if(char.uppercaseChar() == '#'){
                return TokenType.RADIX_SPECIFICATION_START
            }
            else if(char in BINARY_OPERATIONS){
                return TokenType.BINARY_OPERATION
            }
            else if(char in UNARY_OPERATIONS){
                return TokenType.UNARY_OPERATION
            }
            else if(char == ':'){
                return TokenType.RADIX_SPECIFICATION_END
            }
            else if(char == '('){
                return TokenType.BRACE_OPEN
            }
            else if(char == ')'){
                return TokenType.BRACE_CLOSE
            }
            else if(char.isLetter()){
                return TokenType.IDENTIFIER
            }
            else if(char == ','){
                return TokenType.SEPARATOR
            }
            else{
                throw ParserException("Unknown token")
            }
       }


    }

    private var previousTokenType: TokenType = TokenType.INITIAL
    private var _unaryOperation: UnaryOperation? = null
    private var _radix: Int = 10

    private val _tokenList = mutableListOf<Token>()
    private val _operationStack = ArrayDeque<BinaryOperation>()
    private val _numberStack = ArrayDeque<Double>()
    private val _stringBuffer = StringBuilder()
    private var _index: Int = 0

    private val index: Int
        get() = _index

    private fun collapse(){
        while (_operationStack.size != 0) {
            val prev = _operationStack.removeLast()
            val b = _numberStack.removeLast()
            val a = _numberStack.removeLast()
            _numberStack.addLast(prev.compute(a, b))
        }
    }

    private fun collapseResult(): Double{
        if (_numberStack.size == 0){
            return 0.0
        }
        if(previousTokenType == TokenType.NUMBER_OPERAND){
            parseNumber()
        }
//        if(_unaryOperation != null){
//            _numberStack.addLast(_unaryOperation!!.compute(_numberStack.removeLast()))
//        }
        collapse()
        return _numberStack.last()

    }

    private fun doBinaryOperation(operation: BinaryOperation){
        if (_operationStack.size > 0 &&
            !operation.hasBiggerPriority( _operationStack.last())){
            collapse()
        }
        _operationStack.addLast(operation)
    }

    private fun doUnaryOperation(operation: UnaryOperation){
        if(operation.prefix){
            _unaryOperation = operation
        }
        else{
            _numberStack.addLast(operation.compute(_numberStack.removeLast()))
        }
    }

    private fun parseNumber(){
        val number = NumberParser.parseString(_stringBuffer.toString(),
            radix=_radix, separator='.')

        if(_unaryOperation != null){
            _numberStack.addLast(_unaryOperation!!.compute(number))
        }
        else{
            _numberStack.addLast(number)
        }
    }

    private fun evaluateToken(tokenType: TokenType){
        when(tokenType){

            TokenType.NUMBER_OPERAND -> {
                parseNumber()
            }

            TokenType.RADIX_SPECIFICATION_END -> {
                _radix = NumberParser.parseString(_stringBuffer.toString(), int=true, unsigned=true).toInt()
            }

            TokenType.BINARY_OPERATION -> {
                doBinaryOperation(BINARY_OPERATIONS[_stringBuffer[0]]!!)
            }

            TokenType.UNARY_OPERATION -> {
                doUnaryOperation(UNARY_OPERATIONS[_stringBuffer[0]]!!)
            }

            TokenType.IDENTIFIER -> {
                if(_stringBuffer.toString().toUpperCase(Locale.current) in CONSTANTS){
                    _numberStack.addLast(CONSTANTS[_stringBuffer.toString().toUpperCase(Locale.current)]!!)
                    // TODO: functions
                }
            }

            else -> {}
        }
    }


    fun parse(input: String, start: Int = 0): Double{

        _index = start
        previousTokenType = TokenType.INITIAL
        while(_index < input.length){

            val char = input[index]
            if (char == ' '){
                _index++
                continue
            }

            val tokenType = getTokenForChar(char, previousTokenType, _radix)
            if(tokenType != previousTokenType){
                if(!isAllowed(previousTokenType, tokenType)){
                    throw ParserException("$_index: $char is not allowed here")
                }
                evaluateToken(previousTokenType)
                _stringBuffer.clear()
                _tokenList.add(Token(tokenType, index, index))
            }
            _stringBuffer.append(char)

            if(tokenType == TokenType.BRACE_OPEN){
                if(previousTokenType == TokenType.NUMBER_OPERAND ||
                    (previousTokenType == TokenType.UNARY_OPERATION && !_unaryOperation!!.prefix)){
                    _operationStack.add(BINARY_OPERATIONS['*']!!)
                }

                val parser = Parser()
                _numberStack.addLast(parser.parse(input, _index + 1))
                _index = parser.index + 1
                previousTokenType = TokenType.BRACE_CLOSE
                continue
            }

            previousTokenType = tokenType
            if(tokenType == TokenType.BRACE_CLOSE){
                return collapseResult()
            }

            _index++
        }


        return collapseResult()
    }

}
