package ru.lyaminvalery.supercalc.model

class TokenizerException(message: String) : Exception(message)

fun tokenize(
    input: String,
    calculatorContext: CalculatorContext,
    matchers: List<IMatcher>,
): Sequence<Token> = sequence {

    var scopeStart = true
    var currentMatcher: IMatcher? = null
    var prevToken: Token? = null
    var currentToken: Token? = null

    // TODO: wanted to transform, but not
    input.withIndex().forEach {
        (i, char) ->

        if(currentMatcher?.matches(char, calculatorContext, scopeStart) == false){
            currentToken?.let {
                prevToken = it
                currentToken = null
                yield(it)
            }
            val matched = matchers.filter { it.matches(char, calculatorContext, scopeStart) }
            if(matched.size == 0){
                throw TokenizerException("no matches for $char")
            }
            if(matched.size > 1){
                throw TokenizerException("several matches for $char")
            }
            currentMatcher = matched[0]
            currentToken = currentMatcher.create(i)
        }
        else{
            currentToken!!.end = i
        }
    }

    currentToken?.let {
        prevToken = it
        currentToken = null
        yield(it)
    }

    return@sequence

}

fun tokenizeAll(
    input: String,
    calculatorContext: CalculatorContext,
    matchers: List<IMatcher>
) : List<Token> = tokenize(input, calculatorContext, matchers).toList()
