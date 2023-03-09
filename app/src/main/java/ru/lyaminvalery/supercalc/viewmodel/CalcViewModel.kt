package ru.lyaminvalery.supercalc.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import ru.lyaminvalery.supercalc.model.Parser
import ru.lyaminvalery.supercalc.model.ParserException

class CalcViewModel: ViewModel() {

    private var _inputText = mutableStateOf<String>("")
    private var _outputText = mutableStateOf<String>("")
    private var _failed = mutableStateOf<Boolean>(false)
    // TODO:
    private var _stateNum = 0
    private var _addOperationList = mutableStateOf<List<String>>(value = listOf())

    var currentPos = 0
    private var buffer: StringBuilder = StringBuilder()

    val inputText
        get() = _inputText

    val outputText
        get() = _outputText

    val isFailed
        get() = _failed

    val addOperationList
        get() = _addOperationList


    fun setText(text: String){
        buffer.insert(currentPos, text)
        currentPos += text.length
        _inputText.value = buffer.toString()
    }

    fun changeMode(){
        _stateNum++
        if (_stateNum > 2){
            _stateNum = 0
        }
        when(_stateNum){
            0 -> _addOperationList.value = listOf()
            1 -> _addOperationList.value = listOf(",", "ln()", "log()", "PI", "E")
            2 -> _addOperationList.value = listOf("#2:", "#8:", ">", "<")
        }
    }

    fun backspace(){
        if (currentPos == 0)
            return
        buffer.deleteCharAt(currentPos - 1)
        currentPos--
        _inputText.value = buffer.toString()
    }

    fun clear(){
        buffer.clear()
        currentPos = 0
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