package ru.lyaminvalery.supercalc.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import ru.lyaminvalery.supercalc.model.Parser
import ru.lyaminvalery.supercalc.model.ParserException

class CalcViewModel: ViewModel() {

    private enum class Mode {
        PLAIN,
        SCIENTIFIC,
        PROGRAMMER
    }

    private var _currentMode: Mode = Mode.PLAIN

    private val _inputText = mutableStateOf<String>("")
    private val _outputText = mutableStateOf<String>("")
    private val _failed = mutableStateOf<Boolean>(false)

    private val _addOperationList = mutableStateOf<List<String>>(value = listOf())

    private val DEFAULT_CHARSET = ('1'..'9').toList()
    private val PROGRAMMER_CHARSET = DEFAULT_CHARSET.toMutableList()
                                                    .apply{
                                                        addAll(('A' .. 'F'))
                                                    }.toList()

    private val _charset = mutableStateOf<List<Char>>(value = DEFAULT_CHARSET)

    private val _currentPos = mutableStateOf<Int>(0)
    private var buffer: StringBuilder = StringBuilder()

    val currentPos
        get() = _currentPos

    val inputText
        get() = _inputText

    val outputText
        get() = _outputText

    val isFailed
        get() = _failed

    val addOperationList
        get() = _addOperationList

    val charset
        get() = _charset


    fun setText(text: String){
        buffer.insert(_currentPos.value, text)
        _currentPos.value += text.length
        _inputText.value = buffer.toString()
    }

    fun setInputPosition(position: Int){
        _currentPos.value = position
    }

    fun changeMode(){
        val values = Mode.values()
        _currentMode = values[(_currentMode.ordinal + 1) % values.size]
        when(_currentMode){
            Mode.PLAIN -> {
                _addOperationList.value = listOf()
                charset.value = DEFAULT_CHARSET
            }
            Mode.SCIENTIFIC -> {
                _addOperationList.value = listOf("ln", "log", "PI", "E", "sin", "cos", "tg", "ctg", "rad", "degrees", "min", "max")
                charset.value = DEFAULT_CHARSET
            }
            Mode.PROGRAMMER -> {
                _addOperationList.value = listOf("#2:", "#8:", "#16:", ">", "<", "=", "&", "|", "if")
                charset.value = PROGRAMMER_CHARSET
            }
        }
    }

    fun backspace(){
        if (_currentPos.value == 0)
            return
        buffer.deleteCharAt(_currentPos.value - 1)
        _currentPos.value--
        _inputText.value = buffer.toString()
    }

    fun clear(){
        buffer.clear()
        _currentPos.value = 0
        _inputText.value = ""
        _outputText.value = ""
        _failed.value = false
    }

    fun compute(){
        val parser = Parser()
        _failed.value = false
        try{
            _outputText.value = parser.parse(_inputText.value).toString()
        }
        catch (nf: NumberFormatException){
            _outputText.value = "NF: ${nf.message}"
            _failed.value = true
        }
        catch (pe: ParserException){
            _outputText.value = "PE: ${pe.message}"
            _failed.value = true
        }
    }
}