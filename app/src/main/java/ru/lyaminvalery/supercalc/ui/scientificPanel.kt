package ru.lyaminvalery.supercalc.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ScientificPanel(modifier: Modifier,
                    setText: (String) -> Unit){


    val isInversed = remember {
        mutableStateOf(false)
    }


    Row(modifier){

        val rowModifier = Modifier
            .fillMaxWidth()
            .weight(1f)

        CalcButton(
            modifier = rowModifier,
            text = AnnotatedString("-1"),
            onClick = { isInversed.value = !isInversed.value},
            isControl = true
        )

        CalcButton(
            modifier = rowModifier,
            text = AnnotatedString("rad"),
            onClick = { setText("rad(") },
        )

        for(trig in listOf("sin", "cos", "tg")){

            val textToSet = if(!isInversed.value) "${trig}(" else "arc${trig}("
            val displayText = if(!isInversed.value)
                AnnotatedString(trig) else buildAnnotatedString {
                append(trig)
                withStyle(SpanStyle(baselineShift = BaselineShift.Superscript)){
                    append("-1")
                }
            }

            CalcButton(
                modifier = rowModifier,
                text = displayText,
                onClick = { setText(textToSet) },
            )

        }

    }

    Row(modifier) {

        val rowModifier = Modifier
            .fillMaxWidth()
            .weight(1f)

        CalcButton(
            modifier = rowModifier,
            text = AnnotatedString("log"),
            onClick = { setText("log(") },
        )

        CalcButton(
            modifier = rowModifier,
            text = AnnotatedString("ln"),
            onClick = { setText("ln(") },
        )

        CalcButton(
            modifier = rowModifier,
            text = AnnotatedString("π"),
            onClick = { setText("PI") },
        )

        CalcButton(
            modifier = rowModifier,
            text = AnnotatedString("e"),
            onClick = { setText("E") },
        )

        DropDown(modifier = rowModifier,
            functions = listOf("deg", "min", "max", "avg"),
            operators = listOf(),
            setText = setText)
    }

}


@Preview
@Composable
fun PreviewScientificPanel(){
    Column(
        Modifier
            .fillMaxWidth()
            .height(100.dp)) {
        ScientificPanel(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .weight(1f),
            setText = { Log.i("TEXTADDED", it)} )
    }
}