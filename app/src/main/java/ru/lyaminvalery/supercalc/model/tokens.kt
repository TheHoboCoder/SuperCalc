package ru.lyaminvalery.supercalc.model

interface ITokenCollector{
    fun collectNumberToken(position: Int, numberToken: NumberToken, prevToken: Token?)
    fun collectRadixToken(position: Int, radixToken: RadixToken, prevToken: Token?)
    fun collectBinaryToken(position: Int, binaryToken: BinaryToken, prevToken: Token?)
    fun collectUnaryToken(position: Int, unaryToken: UnaryToken, prevToken: Token?)
    fun collectIdentifier(position: Int, identifierToken: IdentifierToken, prevToken: Token?)
    fun collectSeparator(position: Int, prevToken: Token?)
    fun collectOpenBrace(position: Int, prevToken: Token?)
    fun collectCloseBrace(position: Int, prevToken: Token?)
    fun collectSpecial()
}


abstract class Token(var start: Int,
            var end: Int,
            var hasError: Boolean = false){
    fun extract(str: String) = str.substring(start, end)
    abstract fun collect(position: Int, collector: ITokenCollector, prevToken: Token?)
}


class NumberToken(start: Int,
                  end: Int,
                  hasError: Boolean = false,
                  radix: Int = 10): Token(start, end, hasError)
{

    override fun collect(position: Int, collector: ITokenCollector, prevToken: Token?) {
        collector.collectNumberToken(position, this, prevToken)
    }

}

class RadixToken(start: Int,
                  end: Int,
                  hasError: Boolean = false, ): Token(start, end, hasError)
{
    override fun collect(position: Int, collector: ITokenCollector, prevToken: Token?) {
        collector.collectRadixToken(position, this, prevToken)
    }

}

class BinaryToken(start: Int,
                     end: Int,
                     hasError: Boolean = false, ): Token(start, end, hasError){

    override fun collect(position: Int, collector: ITokenCollector, prevToken: Token?) {
        collector.collectBinaryToken(position, this, prevToken)
    }
}

class UnaryToken(start: Int,
                  end: Int,
                  hasError: Boolean = false, ): Token(start, end, hasError){

    override fun collect(position: Int, collector: ITokenCollector, prevToken: Token?) {
        collector.collectUnaryToken(position, this, prevToken)
    }
}

class IdentifierToken(start: Int,
                 end: Int,
                 hasError: Boolean = false, ): Token(start, end, hasError){

    override fun collect(position: Int, collector: ITokenCollector, prevToken: Token?) {
        collector.collectIdentifier(position, this, prevToken)
    }
}

class SeparatorToken(start: Int,
                     end: Int,
                     hasError: Boolean = false,): Token(start, end, hasError){

    override fun collect(position: Int, collector: ITokenCollector, prevToken: Token?) {
        collector.collectSeparator(position, prevToken)
    }
}

class OpenBraceToken(start: Int,
                      end: Int,
                      hasError: Boolean = false,): Token(start, end, hasError){

    override fun collect(position: Int, collector: ITokenCollector, prevToken: Token?) {
        collector.collectOpenBrace(position, prevToken)
    }
}

class CloseBraceToken(start: Int,
                     end: Int,
                     hasError: Boolean = false,): Token(start, end, hasError){

    override fun collect(position: Int, collector: ITokenCollector, prevToken: Token?) {
        collector.collectCloseBrace(position, prevToken)
    }
}

class RadixStartToken(start: Int,
                     end: Int,
                     hasError: Boolean = false,): Token(start, end, hasError){

    override fun collect(position: Int, collector: ITokenCollector, prevToken: Token?) {
        collector.collectSpecial()
    }
}

class RadixEndToken(start: Int,
                      end: Int,
                      hasError: Boolean = false,): Token(start, end, hasError){

    override fun collect(position: Int, collector: ITokenCollector, prevToken: Token?) {
        collector.collectSpecial()
    }
}