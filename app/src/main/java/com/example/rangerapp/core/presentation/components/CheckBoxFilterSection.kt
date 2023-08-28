package com.example.rangerapp.feature_map.presentation.map_entries.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.rangerapp.feature_map.domain.model.MapEntry

@Composable
fun FilterSection(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onFilterChange: (MapEntry.MapEntryType) -> Unit,
    selectedMapEntries: Set<MapEntry.MapEntryType>
) {
    val enumValues = MapEntry.MapEntryType.values()
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        for (i in enumValues.indices step 2) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                for (j in i until minOf(i + 2, enumValues.size)) {
                    DefaultCheckBox(
                        text = enumValues[j].value,
                        checked = selectedMapEntries.contains(enumValues[j]),
                        onCheckedChange = { onFilterChange(enumValues[j]) },
                        modifier = modifier.weight(1f),
                        enabled = enabled
                    )
                }
            }
        }
    }

}