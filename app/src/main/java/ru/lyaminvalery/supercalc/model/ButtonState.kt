package ru.lyaminvalery.supercalc.model

abstract class ButtonState (val caption: String){
    abstract fun onClick()
    open fun enabled(allowedTokens: List<String>) = true
}

class TextButtonState(caption: String,
                      val setText: (String) -> Unit,
                      text: String = "",): ButtonState(caption) {
    private val buttonText: String =  if (text != "") text else caption

    override fun onClick() {
        setText(buttonText)
    }

    override fun enabled(allowedTokens: List<String>) = buttonText.trim() in allowedTokens
}

class ControlButtonState(caption: String,
                         val doOperation: () -> Unit): ButtonState(caption) {
    override fun onClick() {
        doOperation()
    }
}