package ru.lyaminvalery.supercalc.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import ru.lyaminvalery.supercalc.model.*

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

    val addOperationList = mutableStateOf<List<String>>(value = listOf())

    private val DEFAULT_CHARSET = ('1'..'9').toList()
    private val PROGRAMMER_CHARSET = DEFAULT_CHARSET.toMutableList()
                                                    .apply{
                                                        addAll(('A' .. 'F'))
                                                    }.toList()

    private val PRIMARY_COLUMN = listOf(
        TextButtonState("*", this::setText),
        TextButtonState("-", this::setText),
        TextButtonState("+", this::setText),
        ControlButtonState("=", this::compute)
    )

    private val SECONDARY_COLUMN_SCIENTIFIC = listOf(
        TextButtonState("%", this::setText),
        TextButtonState("√", this::setText),
        TextButtonState("^", this::setText),
        TextButtonState("...", this::setText, text = " ")
    )

    private val MAIN_ROW_ONE = listOf(
        TextButtonState("%", this::setText),
        ControlButtonState("C", this::clear),
        ControlButtonState("⌫", this::backspace),
        TextButtonState("/", this::setText)
    )

    private val MAIN_ROW_TWO = listOf(
        TextButtonState("(", this::setText),
        TextButtonState(")", this::setText),
        ControlButtonState("C", this::clear),
        ControlButtonState("⌫", this::backspace),
        TextButtonState("/", this::setText)
    )

    private val SECONDARY_COLUMN_PROGRAMMER = listOf(
        TextButtonState(">", this::setText),
        TextButtonState("<", this::setText),
        TextButtonState("≥", this::setText),
        TextButtonState("≤", this::setText)
    )

    private val SCIENTIFIC_ROWS = listOf<List<ButtonState>>(
        listOf(
            TextButtonState("...", this::setText, text = "sin("),
            TextButtonState("sin", this::setText, text = "sin("),
            TextButtonState("cos", this::setText, text = "cos("),
            TextButtonState("tg", this::setText, text = "tg("),
            TextButtonState("ctg", this::setText, text = "ctg(")
        ),

        listOf(
            TextButtonState("...", this::setText, text = "sin("),
            TextButtonState("log", this::setText, text = "log("),
            TextButtonState("ln", this::setText, text = "ln("),
            TextButtonState("π", this::setText, text = "PI"),
            TextButtonState("e", this::setText, text = "E")
        ),
    )

    private val PROGRAMMER_ROWS = listOf<List<ButtonState>>(
        listOf(
            TextButtonState("x2", this::setText, text = "#2:"),
            TextButtonState("x8", this::setText, text = "#8:"),
            TextButtonState("x16", this::setText, text = "#16:"),
            TextButtonState("&", this::setText, text = " & "),
            TextButtonState("|", this::setText, text = " | "),
        ),

        listOf(
            TextButtonState("=", this::setText, text = " = "),
            TextButtonState("if", this::setText, text = "if("),
            TextButtonState("min", this::setText, text = "min("),
            TextButtonState("max", this::setText, text = "max("),
            TextButtonState("e", this::setText, text = "E")
        ),
    )

    val primaryColumn = mutableStateOf(PRIMARY_COLUMN)
    val primaryRow = mutableStateOf(MAIN_ROW_ONE)
    val secondaryColumn = mutableStateOf<List<ButtonState>>(listOf())
    val additionalRows = mutableStateOf<List<List<ButtonState>>>(listOf())


    val numpadButtons = mutableStateOf(value =
                         DEFAULT_CHARSET.map { TextButtonState(it.toString(), this::setText) })

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

    fun changeMode(mode: Mode){
        currentMode.value = mode
//        val values = Mode.values()
//        currentMode = values[(currentMode.ordinal + 1) % values.size]
        if (currentMode.value != Mode.PROGRAMMER){
            numpadButtons.value = DEFAULT_CHARSET.map { TextButtonState(it.toString(), this::setText)}
        }
        else{
            numpadButtons.value = PROGRAMMER_CHARSET.map { TextButtonState(it.toString(), this::setText)}
        }

        when(currentMode.value){
            Mode.PLAIN -> {
                primaryRow.value = MAIN_ROW_ONE
                secondaryColumn.value = listOf()
                additionalRows.value = listOf()
            }
            Mode.SCIENTIFIC -> {
                primaryRow.value = MAIN_ROW_TWO
                secondaryColumn.value = SECONDARY_COLUMN_SCIENTIFIC
                additionalRows.value = SCIENTIFIC_ROWS
            }
            Mode.PROGRAMMER -> {
                primaryRow.value = MAIN_ROW_TWO
                secondaryColumn.value = SECONDARY_COLUMN_PROGRAMMER
                additionalRows.value = PROGRAMMER_ROWS
            }
        }
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
            outputText.value = parser.parse(inputText.value).toString()
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