package ru.lyaminvalery.supercalc.model

interface IOperationEval{
    fun runBinaryOperation(operation: BinaryOperation): Boolean
    fun runFunction(operation: FunctionCalc): Boolean
}

abstract class Operation{
    abstract fun runOperation(opEval: IOperationEval): Boolean
}

abstract class BinaryOperation(val name: Char,
                      val priority: Int = 0): Operation(){

    override fun runOperation(opEval: IOperationEval): Boolean {
        return opEval.runBinaryOperation(this)
    }

    abstract fun compute(a: Double, b: Double): Double
    fun hasBiggerPriority(other: Operation): Boolean {
        if(other !is BinaryOperation){
            return true
        }
        return priority > other.priority
    }
}

abstract class FunctionCalc(
    val name: String,
    val argsCount: Int = -1,
    val postfix: Boolean = false
): Operation(){

    override fun runOperation(opEval: IOperationEval): Boolean {
        return opEval.runFunction(this)
    }

    abstract fun compute(args: List<Double>): Double
}