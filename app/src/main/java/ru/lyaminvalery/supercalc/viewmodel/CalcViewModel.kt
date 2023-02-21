package ru.lyaminvalery.supercalc.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import ru.lyaminvalery.supercalc.model.Parser
import ru.lyaminvalery.supercalc.model.ParserException

class CalcViewModel: ViewModel() {

    private var _inputText = mutableStateOf<String>("")
    private var _outputText = mutableStateOf<String>("")
    private var _failed = mutableStateOf<Boolean>(false)

    val inputText
        get() = _inputText

    val outputText
        get() = _outputText

    val isFailed
        get() = _failed


    fun setText(char: Char){
        _inputText.value = _inputText.value + char
    }

    fun clear(){
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