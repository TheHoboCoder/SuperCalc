package ru.lyaminvalery.supercalc.model

val OPERATIONS = listOf<Char>('+', '-', '/', '*')

enum class Token{
    INITIAL,
    NUMBER_OPERAND,
    NUMBER_RADIX,
    OPERATION,
    BRACE_OPEN,
    BRACE_CLOSE,
    RADIX_SPECIFICATION_START,
    RADIX_SPECIFICATION_END,
    NAME,
}

fun allowedNext(token: Token): Array<Token>{
    when(token){

        Token.INITIAL, Token.OPERATION, Token.BRACE_OPEN
                                        -> return arrayOf(Token.NUMBER_OPERAND,
                                                          Token.BRACE_OPEN,
                                                          Token.RADIX_SPECIFICATION_START,
                                                          Token.NAME)

        Token.BRACE_CLOSE -> return arrayOf(Token.NUMBER_OPERAND,
                                            Token.BRACE_OPEN,
                                            Token.BRACE_CLOSE,
                                            Token.RADIX_SPECIFICATION_START,
                                            Token.NAME)

        Token.NUMBER_OPERAND, Token.NAME -> return arrayOf(Token.OPERATION,
                                                           Token.BRACE_OPEN)

        Token.RADIX_SPECIFICATION_START -> return arrayOf(Token.NUMBER_RADIX)

        Token.NUMBER_RADIX -> return arrayOf(Token.RADIX_SPECIFICATION_END)

        Token.RADIX_SPECIFICATION_END -> return arrayOf(Token.NUMBER_OPERAND)

    }
}

fun getTokenForChar(char: Char, initial: Boolean = false): Token{
    if(NumberParser.isCharAllowed(char, initial=initial)){
        return Token.NUMBER_OPERAND
    }
    else if (OPERATIONS.contains(char)){
        return Token.OPERATION
    }
    else if(char.uppercaseChar() == '#'){
        return Token.RADIX_SPECIFICATION_START
    }
    else if(char == ':'){
        return Token.RADIX_SPECIFICATION_END
    }
    else if(char.isLetter()){
        return Token.NAME
    }
    else if(char == '('){
        return Token.BRACE_OPEN
    }
    else if(char == ')'){
        return Token.BRACE_CLOSE
    }
    else{
        throw ParserException("Unknown token")
    }
}