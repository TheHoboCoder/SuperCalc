package ru.lyaminvalery.supercalc.model

class ParserException(message: String) : Exception(message)


class Operation(val name: Char,
                val func: (Double, Double) -> Double,
                private val priority: Int = 0){

    fun run(a: Double, b: Double): Double = func(a, b)
    fun hasBiggerPriority(other: Operation) = priority > other.priority

}

class Parser{

    private val _operationStack = ArrayDeque<Operation>()
    private val _numberStack = ArrayDeque<Double>()
    private val _currentNumber = StringBuilder()
    private var _index: Int = 0

    private val index: Int
        get() = _index

    private enum class States{
        INITIAL,
        READING_NUMBER,
        READING_OPERATION,
        CLOSED_BRACE
    }

    private var _currentState: States = States.INITIAL

    private val operations = mapOf<Char, Operation>('+' to Operation('+', { a, b -> a + b}, 0),
                                                    '-' to Operation('-', { a, b -> a - b}, 0),
                                                    '*' to Operation('*', { a, b -> a * b}, 1),
                                                    '/' to Operation('/', { a, b -> a / b}, 1))

    private fun collapse(){
        while (_numberStack.size > 1) {
            val prev = _operationStack.removeLast()
            val b = _numberStack.removeLast()
            val a = _numberStack.removeLast()
            _numberStack.addLast(prev.run(a, b))
        }
    }

    private fun collapseResult(): Double{
        if (_numberStack.size == 0){
            return 0.0
        }
        if(_currentState == States.READING_NUMBER){
            _numberStack.addLast(NumberParser.parseString(_currentNumber.toString()))
        }
        if(_currentState == States.READING_OPERATION){
            _operationStack.removeLast()
        }
        collapse()
        return _numberStack.last()

    }

    private fun doOperation(operation: Operation){
        if (_operationStack.size > 0 &&
            !operation.hasBiggerPriority( _operationStack.last())){
            collapse()
        }
        _operationStack.addLast(operation)
    }

    private fun addNumberIfNeeded(): Boolean{
        if(_currentState == States.READING_NUMBER) {
            _numberStack.addLast(NumberParser.parseString(_currentNumber.toString()))
            _currentNumber.clear()
            return true
        }
        return false
    }

    fun parse(input: String, start: Int = 0): Double{

        _index = start
        while(index < input.length){
            val char = input[index]
            if (char == ' '){
                _index++
                continue
            }

            if(_currentState != States.CLOSED_BRACE &&
                NumberParser.isCharAllowed(char, initial=(index==start))){
                _currentState = States.READING_NUMBER
                _currentNumber.append(char)
            }
            else if((_currentState == States.READING_NUMBER ||
                    _currentState == States.CLOSED_BRACE)
                    && operations.contains(char)) {

                addNumberIfNeeded()
                _currentState = States.READING_OPERATION
                doOperation(operations[char]!!)

            }
            else if(char == '('){
                if(addNumberIfNeeded()){
                    _operationStack.addLast(operations['*']!!)
                }
                // рекурсия
                val parser = Parser()
                _numberStack.addLast(parser.parse(input, _index + 1))
                _index = parser.index
                _currentState = States.CLOSED_BRACE
            }
            else if(char == ')'){
                return collapseResult()
            }
            else{
                throw ParserException("unexpected token $char at $index")
            }

            _index++
        }


        return collapseResult()
    }

}
