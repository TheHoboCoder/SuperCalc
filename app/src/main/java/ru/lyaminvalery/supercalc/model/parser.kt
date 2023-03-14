package ru.lyaminvalery.supercalc.model

import kotlin.math.*

class ParserException(message: String) : Exception(message)


class Parser{

    companion object {







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

                TokenType.BRACE_CLOSE -> return currentToken in arrayOf(TokenType.BRACE_CLOSE, TokenType.BINARY_OPERATION, TokenType.SEPARATOR)

                TokenType.NUMBER_OPERAND, TokenType.IDENTIFIER -> return currentToken in arrayOf(TokenType.BINARY_OPERATION,
                                                                                           TokenType.UNARY_OPERATION,
                                                                                           TokenType.BRACE_OPEN,
                                                                                           TokenType.BRACE_CLOSE,
                                                                                            TokenType.SEPARATOR)
                TokenType.UNARY_OPERATION  -> return currentToken in defaultList ||
                                                     currentToken in arrayOf(TokenType.BINARY_OPERATION, TokenType.BRACE_CLOSE, TokenType.SEPARATOR)

                TokenType.RADIX_SPECIFICATION_START -> return currentToken == TokenType.NUMBER_RADIX

                TokenType.NUMBER_RADIX -> return currentToken in arrayOf(TokenType.RADIX_SPECIFICATION_END, TokenType.NUMBER_RADIX)

                TokenType.RADIX_SPECIFICATION_END -> return currentToken == TokenType.NUMBER_OPERAND

            }
        }


       fun getTokenForChar(char: Char, previousToken: TokenType, radix: Int = 10): TokenType{

            if(previousToken != TokenType.RADIX_SPECIFICATION_START &&
                previousToken != TokenType.NUMBER_RADIX &&
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
    private var _function: FunctionCalc? = null
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
            if(_numberStack.size < 2){
                throw ParserException("invalid input")
            }
            val prev = _operationStack.removeLast()
            val b = _numberStack.removeLast()
            val a = _numberStack.removeLast()
            _numberStack.addLast(prev.compute(a, b))
        }
    }

    private fun collapseResult(): List<Double>{

        if(previousTokenType == TokenType.NUMBER_OPERAND){
            parseNumber()
        }
        if(_unaryOperation != null){
            _numberStack.addLast(_unaryOperation!!.compute(_numberStack.removeLast()))
        }
        if (_numberStack.size == 0){
            return listOf(0.0)
        }
        collapse()
        return _numberStack

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

        _radix = 10

        if(_unaryOperation != null){
            _numberStack.addLast(_unaryOperation!!.compute(number))
            _unaryOperation = null
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

            TokenType.NUMBER_RADIX -> {
                _radix = NumberParser.parseString(_stringBuffer.toString(), int=true, unsigned=true).toInt()
            }

            TokenType.BINARY_OPERATION -> {
                doBinaryOperation(BINARY_OPERATIONS[_stringBuffer[0]]!!)
            }

            TokenType.UNARY_OPERATION -> {
                doUnaryOperation(UNARY_OPERATIONS[_stringBuffer[0]]!!)
            }

            TokenType.IDENTIFIER -> {
                if(_stringBuffer.toString().uppercase() in CONSTANTS){
                    _numberStack.addLast(CONSTANTS[_stringBuffer.toString().uppercase()]!!)
                }
                if(_stringBuffer.toString().lowercase() in FUNCTIONS){
                    _function = FUNCTIONS[_stringBuffer.toString().lowercase()]
                }
            }

            else -> {}
        }
    }

    private fun parseRes(input: String, start: Int = 0): List<Double>{
        _index = start
        previousTokenType = TokenType.INITIAL
        while(_index < input.length){

            val char = input[index]
            if (char == ' '){
                _index++
                continue
            }

            val tokenType = getTokenForChar(char, previousTokenType, _radix)
            if(tokenType !in listOf(TokenType.NUMBER_OPERAND, TokenType.NUMBER_RADIX, TokenType.IDENTIFIER)
                || tokenType != previousTokenType){
                if(!isAllowed(previousTokenType, tokenType)){
                    throw ParserException("$_index: $char is not allowed here")
                }
                evaluateToken(previousTokenType)
                _stringBuffer.clear()
                if(_function != null && tokenType != TokenType.BRACE_OPEN){
                    throw ParserException("function must be called with braces")
                }
                _tokenList.add(Token(tokenType, index, index ))
            }
            // _tokenList.last().end ++
            _stringBuffer.append(char)

            // TODO:
//            if(tokenType == TokenType.UNARY_OPERATION && previousTokenType == TokenType.NUMBER_OPERAND){
//                _operationStack.add(BINARY_OPERATIONS['*']!!)
//            }

            if(tokenType == TokenType.BRACE_OPEN){
                if(previousTokenType == TokenType.NUMBER_OPERAND /* ||
                    (previousTokenType == TokenType.UNARY_OPERATION && !_unaryOperation!!.prefix)*/){
                    _operationStack.add(BINARY_OPERATIONS['*']!!)
                }

                val parser = Parser()
                if(_function == null){
                    _numberStack.addLast(parser.parse(input, _index + 1))
                }
                else{
                    _numberStack.addLast(_function!!.run(parser.parseRes(input, _index + 1)))
                    _function = null
                }

                _index = parser.index + 1
                previousTokenType = TokenType.BRACE_CLOSE
                continue
            }


            previousTokenType = tokenType
            if(tokenType == TokenType.BRACE_CLOSE){
                return collapseResult()
            }
            if(tokenType == TokenType.SEPARATOR){
                collapseResult()
                previousTokenType = TokenType.INITIAL
            }

            _index++
        }


        return collapseResult()
    }


    fun parse(input: String, start: Int = 0): Double{
        return parseRes(input, start).last()
    }

}
