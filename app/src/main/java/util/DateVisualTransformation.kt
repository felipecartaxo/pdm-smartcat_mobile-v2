package com.application.smartcat.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class DateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {

        val trimmed = if (text.text.length >= 8) text.text.substring(0, 8) else text.text
        val out = buildString {
            for (i in trimmed.indices) {
                append(trimmed[i])
                if ((i == 1 || i == 3) && i != trimmed.lastIndex) {
                    append('/')
                }
            }
        }
        val offsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return when {
                    offset <= 1 -> offset
                    offset <= 3 -> offset + 1
                    offset <= 8 -> offset + 2
                    else -> out.length
                }
            }
            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    offset <= 2 -> offset
                    offset <= 5 -> offset - 1
                    offset <= 10 -> offset - 2
                    else -> trimmed.length
                }
            }
        }
        return TransformedText(AnnotatedString(out), offsetTranslator)
    }
}