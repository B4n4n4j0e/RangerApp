package com.example.rangerapp.core.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import java.time.LocalDate

@Composable
fun RangeDateSelection(
    modifier: Modifier,
    startDate: String,
    startLabel: String,
    onStartIconClick: (LocalDate?) -> Unit,
    endDate: String,
    endDateLabel: String,
    onEndIconClick: (LocalDate?) -> Unit,
) {

    val startCalendarState = rememberUseCaseState()
    val endCalendarState = rememberUseCaseState()


    CalendarDialog(
        state = startCalendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true,
        ),
        selection = CalendarSelection.Date { date ->
            onStartIconClick(date)
        }

    )

    CalendarDialog(
        state = endCalendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true,
        ),
        selection = CalendarSelection.Date { date ->
            onEndIconClick(date)
        }

    )




    Column(modifier = modifier) {

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            OutlinedTextField(
                value = startDate,
                readOnly = true,
                onValueChange = {},
                label = {
                    Text(text = startLabel)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Wähle Erstellungs Start Datum",
                        modifier = Modifier
                            .clickable {
                                startCalendarState.show()
                            }

                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Wähle Erstellungs Start Datum",
                        modifier = Modifier
                            .clickable {
                                onStartIconClick(null)
                            }

                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(5.dp)
            )
            OutlinedTextField(
                value = endDate,
                readOnly = true,
                onValueChange = {},
                label = {
                    Text(text = endDateLabel)
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.DateRange,
                        contentDescription = "Wähle End Datum",
                        modifier = Modifier.clickable {
                            endCalendarState.show()
                        })
                },
                trailingIcon = {
                    Icon(imageVector = Icons.Default.Close,
                        contentDescription = "Datum zurücksetzen",
                        modifier = Modifier.clickable {
                            onEndIconClick(null)
                        }
                    )
                }, modifier = Modifier
                    .weight(1f)
                    .padding(5.dp)

            )

        }

    }
}