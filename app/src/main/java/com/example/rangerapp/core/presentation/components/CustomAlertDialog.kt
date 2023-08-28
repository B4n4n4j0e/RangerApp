package com.example.rangerapp.core.presentation.components

import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable

@Composable
fun CustomAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    isOpen: Boolean = false,
    alertTitle: String,
    alertContent: String

) {
    if (isOpen) {
        AlertDialog(
            onDismissRequest = { onDismissRequest() },
            title = {
                Text(
                    text = alertTitle
                )
            },
            text = {
                Text(text = alertContent)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm()
                    }
                ) {
                    Text(text = "Bestätigen", color = MaterialTheme.colors.primary)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismissRequest()
                    }
                ) {
                    Text(text = "Abbrechen", color = MaterialTheme.colors.primary)
                }
            },
            backgroundColor = MaterialTheme.colors.background,
            contentColor = MaterialTheme.colors.onBackground
        )
    }
}



