package com.example.rangerapp.core.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.rangerapp.feature_map.domain.model.*

@Composable
fun MapEntryComparison(
    modifier: Modifier,
    currentEntry: MapEntry,
    importedEntry: MapEntry,
    onSelectCurrentEntry: () -> Unit,
    onSelectImportEntry: () -> Unit,
) {

    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
            .padding(10.dp)
            .border(width = 1.dp, color = MaterialTheme.colors.onBackground)
            .padding(5.dp)
    ) {

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = currentEntry.getMapEntryType().value
            )
            Text(
                text = currentEntry.description ?: "",
                color = getColorForEquality(
                    obj1 = currentEntry.description,
                    obj2 = importedEntry.description
                )
            )

            when (currentEntry.getMapEntryType()) {
                MapEntry.MapEntryType.SPECIES_REPORT -> {
                    currentEntry as SpeciesReport
                    importedEntry as SpeciesReport
                    Text(
                        text = currentEntry.quantity.toString(),
                        color = getColorForEquality(
                            obj1 = importedEntry.quantity,
                            obj2 = currentEntry.quantity
                        )
                    )
                    Text(
                        text = currentEntry.species,
                        color = getColorForEquality(
                            obj1 = importedEntry.species,
                            obj2 = currentEntry.species
                        )
                    )
                    Text(
                        text = currentEntry.category.value,
                        color = getColorForEquality(
                            obj1 = importedEntry.category,
                            obj2 = currentEntry.category
                        )
                    )
                }
                MapEntry.MapEntryType.VISITOR_CONTACT -> {
                    currentEntry as VisitorContact
                    importedEntry as VisitorContact
                    Text(
                        text = currentEntry.origin ?: "",
                        color = getColorForEquality(
                            obj1 = importedEntry.origin,
                            obj2 = currentEntry.origin
                        )
                    )
                    Text(
                        text = currentEntry.quantity.toString(),
                        color = getColorForEquality(
                            obj1 = importedEntry.quantity,
                            obj2 = currentEntry.quantity
                        )
                    )
                    Text(
                        text = currentEntry.violations ?: "",
                        color = getColorForEquality(
                            obj1 = importedEntry.violations,
                            obj2 = currentEntry.violations
                        )
                    )

                }
                MapEntry.MapEntryType.PLANT_CONTROL -> {

                }
                MapEntry.MapEntryType.OTHER_OBSERVATION -> {

                }
                MapEntry.MapEntryType.MONITORING -> {
                    currentEntry as Monitoring
                    importedEntry as Monitoring
                    Text(
                        text = currentEntry.material ?: "",
                        color = getColorForEquality(
                            obj1 = importedEntry.material,
                            obj2 = currentEntry.material
                        )
                    )
                    Text(
                        text = currentEntry.result ?: "",
                        color = getColorForEquality(
                            obj1 = importedEntry.result,
                            obj2 = currentEntry.result
                        )
                    )
                    Text(
                        text = currentEntry.monitoringType ?: "",
                        color = getColorForEquality(
                            obj1 = importedEntry.monitoringType,
                            obj2 = currentEntry.monitoringType
                        )
                    )
                    Text(
                        text = currentEntry.nextControlDate.toString(),
                        color = getColorForEquality(
                            obj1 = importedEntry.nextControlDate,
                            obj2 = currentEntry.nextControlDate
                        )
                    )
                }
                MapEntry.MapEntryType.BIOTOP -> {
                    currentEntry as Biotop
                    importedEntry as Biotop
                    Text(
                        text = currentEntry.category.value,
                        color = getColorForEquality(
                            obj1 = importedEntry.category,
                            obj2 = currentEntry.category
                        )
                    )
                    Text(
                        text = currentEntry.type,
                        color = getColorForEquality(
                            obj1 = importedEntry.type,
                            obj2 = currentEntry.type
                        )
                    )

                }
            }
            IconButton(
                onClick = { onSelectCurrentEntry() },
                Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "Behalte momentanen Eintrag",
                    tint = MaterialTheme.colors.primary
                )

            }
        }
        Divider(
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 5.dp)
                .width(1.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = currentEntry.getMapEntryType().value
            )
            Text(
                text = importedEntry.description ?: "",
                color = getColorForEquality(
                    obj1 = currentEntry.description,
                    obj2 = importedEntry.description
                )
            )

            when (currentEntry.getMapEntryType()) {
                MapEntry.MapEntryType.SPECIES_REPORT -> {
                    currentEntry as SpeciesReport
                    importedEntry as SpeciesReport
                    Text(
                        text = importedEntry.quantity.toString(),
                        color = getColorForEquality(
                            obj1 = importedEntry.quantity,
                            obj2 = currentEntry.quantity
                        )
                    )
                    Text(
                        text = importedEntry.species,
                        color = getColorForEquality(
                            obj1 = importedEntry.species,
                            obj2 = currentEntry.species
                        )
                    )
                    Text(
                        text = importedEntry.category.value,
                        color = getColorForEquality(
                            obj1 = importedEntry.category,
                            obj2 = currentEntry.category
                        )
                    )
                }
                MapEntry.MapEntryType.VISITOR_CONTACT -> {
                    currentEntry as VisitorContact
                    importedEntry as VisitorContact
                    Text(
                        text = importedEntry.origin ?: "",
                        color = getColorForEquality(
                            obj1 = importedEntry.origin,
                            obj2 = currentEntry.origin
                        )
                    )
                    Text(
                        text = importedEntry.quantity.toString(),
                        color = getColorForEquality(
                            obj1 = importedEntry.quantity,
                            obj2 = currentEntry.quantity
                        )
                    )
                    Text(
                        text = importedEntry.violations ?: "",
                        color = getColorForEquality(
                            obj1 = importedEntry.violations,
                            obj2 = currentEntry.violations
                        )
                    )

                }
                MapEntry.MapEntryType.PLANT_CONTROL -> {

                }
                MapEntry.MapEntryType.OTHER_OBSERVATION -> {

                }
                MapEntry.MapEntryType.MONITORING -> {
                    currentEntry as Monitoring
                    importedEntry as Monitoring
                    Text(
                        text = importedEntry.material ?: "",
                        color = getColorForEquality(
                            obj1 = importedEntry.material,
                            obj2 = currentEntry.material
                        )
                    )
                    Text(
                        text = importedEntry.result ?: "",
                        color = getColorForEquality(
                            obj1 = importedEntry.result,
                            obj2 = currentEntry.result
                        )
                    )
                    Text(
                        text = importedEntry.monitoringType ?: "",
                        color = getColorForEquality(
                            obj1 = importedEntry.monitoringType,
                            obj2 = currentEntry.monitoringType
                        )
                    )
                    Text(
                        text = importedEntry.nextControlDate.toString(),
                        color = getColorForEquality(
                            obj1 = importedEntry.nextControlDate,
                            obj2 = currentEntry.nextControlDate
                        )
                    )
                }
                MapEntry.MapEntryType.BIOTOP -> {
                    currentEntry as Biotop
                    importedEntry as Biotop
                    Text(
                        text = importedEntry.category.value,
                        color = getColorForEquality(
                            obj1 = importedEntry.category,
                            obj2 = currentEntry.category
                        )
                    )
                    Text(
                        text = importedEntry.type,
                        color = getColorForEquality(
                            obj1 = importedEntry.type,
                            obj2 = currentEntry.type
                        )
                    )
                }
            }

            IconButton(
                onClick = { onSelectImportEntry() },
                Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "Importiere neuen Eintrag",
                    tint = MaterialTheme.colors.primary
                )

            }

        }
    }

}

@Composable
fun getColorForEquality(obj1: Any?, obj2: Any?): Color {
    return if (obj1 == obj2) {
        MaterialTheme.colors.onBackground
    } else {
        MaterialTheme.colors.error
    }
}


