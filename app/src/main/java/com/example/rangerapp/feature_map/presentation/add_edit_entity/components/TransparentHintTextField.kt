package com.example.rangerapp.feature_map.presentation.add_edit_entity.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction

@Composable
fun TransparentHintTextField(
    text: String,
    hint: String,
    modifier: Modifier = Modifier,
    isHintVisible: Boolean = true,
    onValueChange: (String) -> Unit,
    textStyle: TextStyle = TextStyle(),
    singleLine: Boolean = false,

    onFocusChange: (FocusState) -> Unit
) {
    val outlinedTextFieldStyle = MaterialTheme.typography.body1.copy(
        color = LocalContentColor.current
    )

    Box(modifier = modifier) {
        BasicTextField(
            value = text,
            singleLine = singleLine,
            maxLines = 10,
            onValueChange = onValueChange,
            textStyle = outlinedTextFieldStyle.merge(textStyle),
            cursorBrush = SolidColor(MaterialTheme.colors.primary),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    onFocusChange(it)
                },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            )
        )
        if (isHintVisible) {
            Text(text = hint, style = textStyle)
        }
    }

}