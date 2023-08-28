package com.example.rangerapp.feature_map.presentation.add_edit_entity.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity

@Composable
fun DropDownMenu(
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    values: List<String>,
    selectedItem: String = "",
    onValueChange: (String) -> Unit,
    globallyPositioned: (LayoutCoordinates) -> Unit,
    textFiledSize: Size,
    onClick: (String) -> Unit,
    label: String,
    toggleTab: () -> Unit

) {


    val icon = if (expanded) {
        Icons.Filled.KeyboardArrowUp
    } else {
        Icons.Filled.KeyboardArrowDown
    }
    Box(modifier = modifier) {
        OutlinedTextField(
            value = selectedItem,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { toggleTab() }
                .onGloballyPositioned {
                    globallyPositioned(it)
                },
            enabled = false,
            readOnly = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                disabledBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                disabledLabelColor = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                disabledPlaceholderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                disabledTextColor = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                disabledTrailingIconColor = MaterialTheme.colors.onSurface
            ),

            label = { Text(text = label) },
            trailingIcon = {
                Icon(
                    icon,
                    contentDescription = "Open/Close tab"
                )
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { toggleTab() },
            modifier = Modifier
                .width(with(LocalDensity.current) { textFiledSize.width.toDp() })
        ) {
            values.forEach { label ->
                DropdownMenuItem(onClick = {
                    onClick(label)
                }) {
                    Text(text = label)

                }
            }
        }


    }
}


/*
.onGloballyPositioned { coordinates ->
onClick(coordinates.size.toSize())
},

 */