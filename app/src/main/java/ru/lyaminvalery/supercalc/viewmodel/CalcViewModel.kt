package ru.lyaminvalery.supercalc.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import ru.lyaminvalery.supercalc.model.*
import java.lang.Character.MAX_RADIX
import java.lang.Character.MIN_RADIX

class CalcViewModel: ViewModel() {

     enum class Mode {
        PLAIN,
        SCIENTIFIC,
        PROGRAMMER
    }

    val currentMode = mutableStateOf(Mode.PLAIN)

    val inputText = mutableStateOf<String>("")
    val outputText = mutableStateOf<String>("")
    val isFailed = mutableStateOf<Boolean>(false)

    val radix = mutableStateOf<Int>(10)
    val isRadixValid = mutableStateOf(true)
    var result: Double = 0.0

    private val DEFAULT_CHARSET = ('1'..'9').map{ it.toString()}.toList()
    private val PROGRAMMER_CHARSET = DEFAULT_CHARSET.toMutableList()
                                                    .apply{
                                                        addAll(('A' .. 'F').map { it.toString() })
                                                    }.toList()

    val numpadButtons = mutableStateOf(value = DEFAULT_CHARSET)

    val allowedTokens = mutableStateOf<List<String>>(value = listOf())

    val currentPos = mutableStateOf<Int>(0)
    private var buffer: StringBuilder = StringBuilder()



    fun setText(text: String){
        buffer.insert(currentPos.value, text)
        currentPos.value += text.length
        inputText.value = buffer.toString()
    }

    fun setInputPosition(position: Int){
        currentPos.value = position
    }

    fun setRadix(radixStr: String){
        try{
            val radix = radixStr.toInt()
            if(radix < MIN_RADIX || radix > MAX_RADIX) {
                isRadixValid.value = false
                return
            }
            isRadixValid.value = true
            this.radix.value = radix
            outputText.value = NumberParser.numberToRadix(result, radix)
        }
        catch(e: NumberFormatException){
            isRadixValid.value = false
        }

    }

    fun changeMode(mode: Mode){
        currentMode.value = mode
//        val values = Mode.values()
//        currentMode = values[(currentMode.ordinal + 1) % values.size]
        if (currentMode.value != Mode.PROGRAMMER){
            numpadButtons.value = DEFAULT_CHARSET
        }
        else{
            numpadButtons.value = PROGRAMMER_CHARSET
        }

        radix.value = 10
        isRadixValid.value = true
    }

    fun backspace(){
        if (currentPos.value == 0)
            return
        buffer.deleteCharAt(currentPos.value - 1)
        currentPos.value--
        inputText.value = buffer.toString()
    }

    fun clear(){
        buffer.clear()
        currentPos.value = 0
        inputText.value = ""
        outputText.value = ""
        isFailed.value = false
    }


    fun compute(){
        val parser = Parser()
        isFailed.value = false
        try{
            result = parser.parse(inputText.value)
            outputText.value = NumberParser.numberToRadix(result, radix.value)
        }
        catch (nf: NumberFormatException){
            outputText.value = "NF: ${nf.message}"
            isFailed.value = false
        }
        catch (pe: ParserException){
            outputText.value = "PE: ${pe.message}"
            isFailed.value = false
        }
    }
}