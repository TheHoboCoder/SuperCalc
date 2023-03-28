package ru.lyaminvalery.supercalc.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ProgrammerPanel(modifier: Modifier,
                    setText: (String) -> Unit){


    Row(modifier){

        val rowModifier = Modifier
            .fillMaxWidth()
            .weight(1f)

        CalcButton(
            modifier = rowModifier,
            text = AnnotatedString("="),
            onClick = { setText("=") },
        )

        for(radix in listOf("2", "8", "16")){
            CalcButton(
                modifier = rowModifier,
                text = AnnotatedString("x$radix"),
                onClick = { setText("#$radix:") },
            )
        }

        CalcButton(
            modifier = rowModifier,
            text = AnnotatedString("xN"),
            onClick = { setText("#:") },
        )
    }

    Row(modifier) {
        val rowModifier = Modifier
            .fillMaxWidth()
            .weight(1f)

        CalcButton(
            modifier = rowModifier,
            text = AnnotatedString("if"),
            onClick = { setText("if(") },
        )

        for(operator in listOf("~", "&", "|")){
            CalcButton(
                modifier = rowModifier,
                text = AnnotatedString(operator),
                onClick = { setText(operator) },
            )
        }

        DropDown(modifier = rowModifier,
            functions = listOf("min", "max", "avg"),
            operators = listOf()/*listOf("⊕")*/,
            setText = setText)

    }
}

@Preview
@Composable
fun PreviewProgrammerPanel(){
    Column(
        Modifier
            .fillMaxWidth()
            .height(100.dp)) {
        ProgrammerPanel(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .weight(1f),
            setText = { Log.i("TEXTADDED", it)} )
    }
}