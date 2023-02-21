package ru.lyaminvalery.supercalc.model

abstract class ParserState(val parser: Parser){
    private var _states = listOf<ParserState>()
    private var _started = false
    abstract fun check(char: Char, initial: Boolean): Boolean
    abstract fun doSome(char: Char, initial: Boolean)
    open fun onStart() {}
    open fun onEnd() {}

    fun run(char: Char, initial: Boolean = false): ParserState{
        if (check(char, initial)){
            if (!_started) {
                onStart()
                _started = true
            }
            doSome(char, initial)
            return this
        }
        onEnd()
        val nextState = _states.firstOrNull{ it.check(char, initial) }
        if (nextState == null){
            throw ParserException("disallowed token $char")
        }
        else{
            return nextState
        }
    }

}

class InitialState(parser: Parser): ParserState(parser){
    override fun check(char: Char, initial: Boolean): Boolean = false
    override fun doSome(char: Char, initial: Boolean) {}
}

open class NumberParsingState(parser: Parser): ParserState(parser){
    protected var _numberParser = NumberParser()

    override fun onStart() {
        _numberParser = NumberParser()
    }

    override fun check(char: Char, initial: Boolean): Boolean {
        return _numberParser.isCharAllowed(char, initial)
    }

    override fun doSome(char: Char, initial: Boolean) {
        _numberParser.nextChar(char, initial)
    }

    override fun onEnd() {
       //parser.pushNumber(_numberParser.result())
    }

}

class SpecifierNumberParsingState(parser: Parser): NumberParsingState(parser){
    override fun onStart() {
        _numberParser = NumberParser(unsigned=true, int=true)
    }
}
