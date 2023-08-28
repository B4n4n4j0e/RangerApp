package com.example.rangerapp.feature_map.presentation.add_edit_entity.mapEntry

import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiNature
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Nature
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.rangerapp.BuildConfig
import com.example.rangerapp.R
import com.example.rangerapp.core.presentation.components.CustomAlertDialog
import com.example.rangerapp.core.presentation.components.TakePictureWithUriReturnContract
import com.example.rangerapp.feature_map.domain.model.BiotopCategory
import com.example.rangerapp.feature_map.domain.model.MapEntry
import com.example.rangerapp.feature_map.domain.model.SpeciesCategory
import com.example.rangerapp.feature_map.presentation.add_edit_entity.components.DropDownMenu
import com.example.rangerapp.feature_map.presentation.util.CustomFormat
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun AddEditMapEntryScreen(
    navController: NavController,
    latitude: Double,
    longitude: Double,
    mapEntryId: Int,
    editMapEntryType: MapEntry.MapEntryType,
    viewModel: AddEditMapViewModel = hiltViewModel()
) {


    val descriptionState = viewModel.mapEntryDescription.value
    val speciesState = viewModel.mapEntrySpeciesContent.value
    val speciesCategoryState = viewModel.mapEntrySpeciesCategory.value
    val isSpeciesCategoryExpandedState = viewModel.isMapEntryCategoryExpanded.value
    val isBiotopCategoryExpandedState = viewModel.isBiotopCategoryExpanded.value
    val resultState = viewModel.mapEntryResult.value
    val monitoringTypeState = viewModel.mapEntryMonitoringType.value
    val monitoringMaterialState = viewModel.mapEntryMaterial.value
    val biotopCategoryState = viewModel.mapEntryBiotopCategory.value
    val biotopTypeState = viewModel.mapEntryBiotopType.value

    val textFiledSizeState = viewModel.textFiledSize.value
    val quantityState = viewModel.mapEntryQuantity.value
    val mapEntryVisitorOrigin = viewModel.mapEntryVisitorOrigin.value
    val violationsState = viewModel.mapEntryVisitorViolations.value
    val calendarState = rememberUseCaseState()

    val controlDateState = viewModel.mapEntryControlDate.value

    val latitudeState = viewModel.mapEntryLatitude.value
    val longitudeState = viewModel.mapEntryLongitude.value
    val createdState = viewModel.mapEntryCreated.value
    val updatedState = viewModel.mapEntryUpdated.value

    val mapEntryType = viewModel.mapEntryType.value
    val isDialogOpen = viewModel.isDeleteDialogOpen.value

    val scaffoldState = rememberScaffoldState()

    val mapEntryBackgroundAnimatable = remember {
        Animatable(
            Color(editMapEntryType.color.toArgb())
        )
    }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current


    CalendarDialog(
        state = calendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true,
        ),
        selection = CalendarSelection.Date { date ->
            viewModel.onEvent(
                AddEditMapEntryEvent.EnteredControlDate(
                    date
                )
            )
        }

    )
    val intentSenderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = {
            if (it.resultCode == RESULT_OK) {

            } else {
                viewModel.onEvent(AddEditMapEntryEvent.TriggerError("Eintrag / Bild konnte nicht gelöscht werden."))
            }
        })

    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            val contentResolver = context.contentResolver
            for (uri in uris) {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            viewModel.onEvent(AddEditMapEntryEvent.AddImageUris(uris))
        })


    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = TakePictureWithUriReturnContract()
    ) { (isSuccessful, imageUri) ->
        if (isSuccessful) {
            viewModel.onEvent(AddEditMapEntryEvent.SavePictureFromCamera(imageUri))
        } else {
            viewModel.onEvent(AddEditMapEntryEvent.TriggerError("Bild konnte nicht gespeichert werden. Bitte versuchen Sie es erneut."))

        }

    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AddEditMapViewModel.UiEvent.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }

                is AddEditMapViewModel.UiEvent.RequestPermissionForDeletion -> {
                    intentSenderLauncher.launch(
                        IntentSenderRequest.Builder(event.intentSender).build()
                    )
                }

                AddEditMapViewModel.UiEvent.DeleteMapEntry -> {
                    navController.navigateUp()
                }

                AddEditMapViewModel.UiEvent.SaveMapEntry -> {
                    navController.navigateUp()

                }

                is AddEditMapViewModel.UiEvent.ShareJson -> {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putExtra(Intent.EXTRA_STREAM, event.uri)
                    intent.type = "application/json"
                    context.startActivity(intent)
                }
            }
        }

    }


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.onEvent(AddEditMapEntryEvent.SaveMapEntry)
            }, backgroundColor = MaterialTheme.colors.background) {
                Icon(
                    imageVector = Icons.Default.Save, contentDescription = "Save map entry",
                    tint = MaterialTheme.colors.primary
                )
            }
        },
        scaffoldState = scaffoldState
    ) { paddingValues ->

        CustomAlertDialog(
            onDismissRequest = { viewModel.onEvent(AddEditMapEntryEvent.CloseDeleteDialog) },
            onConfirm = { viewModel.onEvent(AddEditMapEntryEvent.DeleteMapEntry) },
            alertTitle = "Eintrag löschen",
            alertContent = "Möchten Sie den Eintrag wirklich löschen?",
            isOpen = isDialogOpen
        )

        val imageBitmap = ImageBitmap.imageResource(context.resources, R.drawable.logo_transparent)


        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                bitmap = imageBitmap,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxSize(),
                colorFilter = ColorFilter.tint(mapEntryBackgroundAnimatable.value.copy(alpha = 0.2f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                )
                {
                    for (entry in enumValues<MapEntry.MapEntryType>()) {
                        val color = entry.color
                        val colorInt = entry.color.toArgb()

                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(
                                    width = 3.dp,
                                    color = if (mapEntryType.color == entry.color) {
                                        color
                                    } else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable {
                                    scope.launch {
                                        mapEntryBackgroundAnimatable.animateTo(
                                            targetValue = Color(colorInt),
                                            animationSpec = tween(durationMillis = 500)
                                        )
                                    }
                                    viewModel.onEvent(AddEditMapEntryEvent.ChangeMapEntryType(entry))
                                }
                        ) {
                            when (entry) {
                                MapEntry.MapEntryType.OTHER_OBSERVATION -> {
                                    Icon(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .padding(5.dp),
                                        painter = painterResource(R.drawable.unknown_document),
                                        tint = color,
                                        contentDescription = "Other observations"
                                    )
                                }

                                MapEntry.MapEntryType.VISITOR_CONTACT -> {
                                    Icon(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .padding(5.dp)
                                            .fillMaxSize(1f),
                                        imageVector = Icons.Default.PersonAdd,
                                        contentDescription = "Visitors contact",
                                        tint = color
                                    )
                                }

                                MapEntry.MapEntryType.SPECIES_REPORT -> {
                                    Icon(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .padding(5.dp)
                                            .padding(5.dp)
                                            .fillMaxSize(1f),
                                        imageVector = Icons.Default.EmojiNature,
                                        contentDescription = "Species report",
                                        tint = color
                                    )
                                }

                                MapEntry.MapEntryType.PLANT_CONTROL -> {
                                    Icon(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .padding(5.dp)
                                            .fillMaxSize(1f),
                                        imageVector = Icons.Default.Home,
                                        contentDescription = "Plant control",
                                        tint = color
                                    )
                                }

                                MapEntry.MapEntryType.MONITORING -> {
                                    Icon(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .padding(5.dp)
                                            .fillMaxSize(1f),
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Monitoring",
                                        tint = color
                                    )
                                }

                                MapEntry.MapEntryType.BIOTOP -> {
                                    Icon(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .padding(5.dp)
                                            .fillMaxSize(1f),
                                        imageVector = Icons.Outlined.Nature,
                                        contentDescription = "Biotop",
                                        tint = color
                                    )
                                }
                            }

                        }
                    }
                }


                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                )
                {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(mapEntryType.value, style = MaterialTheme.typography.h5)
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                    }

                    if (mapEntryType == MapEntry.MapEntryType.VISITOR_CONTACT) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                OutlinedTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = violationsState,
                                    onValueChange = {
                                        viewModel.onEvent(
                                            AddEditMapEntryEvent.EnteredViolation(
                                                it
                                            )
                                        )
                                    },
                                    label = {
                                        Text(text = "Verstöße")
                                    },
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                )
                            }
                        }
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                OutlinedTextField(
                                    value = mapEntryVisitorOrigin,
                                    modifier = Modifier.fillMaxWidth(),
                                    onValueChange = {
                                        viewModel.onEvent(
                                            AddEditMapEntryEvent.EnteredOrigin(
                                                it
                                            )
                                        )
                                    },
                                    label = {
                                        Text(text = "Herkunft")
                                    },
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                )

                            }
                        }
                    }

                    if (mapEntryType == MapEntry.MapEntryType.MONITORING) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                OutlinedTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = monitoringTypeState,
                                    onValueChange = {
                                        viewModel.onEvent(
                                            AddEditMapEntryEvent.EnteredMonitoringType(
                                                it
                                            )
                                        )
                                    },
                                    label = {
                                        Text(text = "Monitoring Typ")
                                    },
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                )
                            }
                        }
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                OutlinedTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = monitoringMaterialState,
                                    onValueChange = {
                                        viewModel.onEvent(
                                            AddEditMapEntryEvent.EnteredMaterial(
                                                it
                                            )
                                        )
                                    },
                                    label = {
                                        Text(text = "Material")
                                    },
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                )
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                OutlinedTextField(
                                    value = controlDateState?.let { CustomFormat.formatLocalDate(it) }
                                        ?: "-", readOnly = true,
                                    onValueChange = {},
                                    label = {
                                        Text(text = "Nächste Kontrolle")
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = "Wähle Erstellungs Start Datum",
                                            modifier = Modifier
                                                .clickable {
                                                    calendarState.show()
                                                }

                                        )
                                    },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Setze Kontrolldatum zurück",
                                            modifier = Modifier
                                                .clickable {
                                                    viewModel.onEvent(
                                                        AddEditMapEntryEvent.EnteredControlDate(
                                                            null
                                                        )
                                                    )
                                                }

                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(5.dp)
                                )
                            }
                        }


                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                OutlinedTextField(
                                    value = resultState,
                                    modifier = Modifier.fillMaxWidth(),
                                    onValueChange = {
                                        viewModel.onEvent(
                                            AddEditMapEntryEvent.EnteredResult(
                                                it
                                            )
                                        )
                                    },
                                    label = {
                                        Text(text = "Ergebnis")
                                    },
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                )

                            }
                        }
                    }


                    if (mapEntryType == MapEntry.MapEntryType.SPECIES_REPORT) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                OutlinedTextField(
                                    value = speciesState,
                                    modifier = Modifier.fillMaxWidth(),
                                    onValueChange = {
                                        viewModel.onEvent(
                                            AddEditMapEntryEvent.EnteredSpecies(
                                                it
                                            )
                                        )
                                    },
                                    label = {
                                        Text(text = "Art")
                                    },
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                )
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                DropDownMenu(
                                    expanded = isSpeciesCategoryExpandedState,
                                    values = enumValues<SpeciesCategory>().map { it.value },
                                    onValueChange = {
                                        viewModel.onEvent(
                                            AddEditMapEntryEvent.EnteredSpeciesCategory(
                                                it
                                            )
                                        )
                                    },
                                    onClick = {
                                        viewModel.onEvent(
                                            AddEditMapEntryEvent.EnteredSpeciesCategory(
                                                it
                                            )
                                        )
                                    },
                                    label = "Kategorie",
                                    selectedItem = speciesCategoryState,
                                    toggleTab = { viewModel.onEvent(AddEditMapEntryEvent.ToggleSpeciesCategoryTab) },
                                    textFiledSize = textFiledSizeState,
                                    globallyPositioned = {
                                        viewModel.onEvent(
                                            AddEditMapEntryEvent.ChangePosition(
                                                it
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }

                    if (mapEntryType == MapEntry.MapEntryType.BIOTOP) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                OutlinedTextField(
                                    value = biotopTypeState,
                                    modifier = Modifier.fillMaxWidth(),
                                    onValueChange = {
                                        viewModel.onEvent(
                                            AddEditMapEntryEvent.EnteredBiotopType(
                                                it
                                            )
                                        )
                                    },
                                    label = {
                                        Text(text = "Biotop Beschreibung")
                                    },
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                )
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                DropDownMenu(
                                    expanded = isBiotopCategoryExpandedState,
                                    values = enumValues<BiotopCategory>().map { it.value },
                                    onValueChange = {
                                        viewModel.onEvent(
                                            AddEditMapEntryEvent.EnteredBiotopCategory(
                                                it
                                            )
                                        )
                                    },
                                    onClick = {
                                        viewModel.onEvent(
                                            AddEditMapEntryEvent.EnteredBiotopCategory(
                                                it
                                            )
                                        )
                                    },
                                    label = "Kategorie",
                                    selectedItem = biotopCategoryState,
                                    toggleTab = { viewModel.onEvent(AddEditMapEntryEvent.ToggleBiotopCategoryTab) },
                                    textFiledSize = textFiledSizeState,
                                    globallyPositioned = {
                                        viewModel.onEvent(
                                            AddEditMapEntryEvent.ChangePosition(
                                                it
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }


                    if (mapEntryType == MapEntry.MapEntryType.VISITOR_CONTACT || mapEntryType == MapEntry.MapEntryType.SPECIES_REPORT) {

                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                OutlinedTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = quantityState,
                                    onValueChange = {
                                        viewModel.onEvent(
                                            AddEditMapEntryEvent.EnteredQuantity(
                                                it
                                            )
                                        )
                                    },
                                    label = {
                                        Text(text = "Anzahl")
                                    },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done
                                    )

                                )
                            }
                        }
                    }


                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = descriptionState,
                                onValueChange = {
                                    viewModel.onEvent(
                                        AddEditMapEntryEvent.EnteredDescription(
                                            it
                                        )
                                    )
                                },
                                label = {
                                    Text(text = "Beschreibung")
                                },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Column(modifier = Modifier.weight(2f)) {
                                Text(String.format("Längengrad: %.4f", longitudeState))
                                Text(String.format("Breitengrad: %.4f", latitudeState))
                            }

                            IconButton(
                                onClick = {
                                    viewModel.onEvent(AddEditMapEntryEvent.OpenDeleteDialog)
                                },
                                content = {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Entry",
                                        tint = if (mapEntryId != -1) {
                                            MaterialTheme.colors.primary
                                        } else {
                                            MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                                        }
                                    )
                                },
                                enabled = mapEntryId != -1
                            )
                            IconButton(
                                onClick = {
                                    viewModel.onEvent(AddEditMapEntryEvent.ShareMapEntry)
                                },
                                content = {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Teilen",
                                        tint = if (mapEntryId != -1) {
                                            MaterialTheme.colors.primary
                                        } else {
                                            MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                                        }
                                    )
                                },
                                enabled = mapEntryId != -1

                            )
                            if (mapEntryType != MapEntry.MapEntryType.VISITOR_CONTACT) {
                                IconButton(
                                    onClick = {
                                        val tmpFile =
                                            File.createTempFile(
                                                "tmp_image_file",
                                                ".jpg",
                                                context.cacheDir,

                                                )
                                                .apply {
                                                    createNewFile()
                                                    deleteOnExit()
                                                }


                                        val tmpUri = FileProvider.getUriForFile(
                                            context,
                                            "${BuildConfig.APPLICATION_ID}.provider",
                                            tmpFile
                                        )
                                        takePhotoLauncher.launch(tmpUri)

                                    },
                                    content = {
                                        Icon(
                                            imageVector = Icons.Default.PhotoCamera,
                                            contentDescription = "Kamera",
                                            tint = MaterialTheme.colors.primary
                                        )
                                    },
                                )
                                IconButton(
                                    onClick = {
                                        multiplePhotoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    content = {
                                        Icon(
                                            imageVector = Icons.Default.PhotoLibrary,
                                            contentDescription = "Wähle ein Bild",
                                            tint = MaterialTheme.colors.primary
                                        )
                                    }
                                )


                            }
                            if (mapEntryType == MapEntry.MapEntryType.VISITOR_CONTACT) {
                                Spacer(modifier = Modifier.weight(1f))

                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (mapEntryType != MapEntry.MapEntryType.VISITOR_CONTACT
                    ) {
                        items(viewModel.mapEntryImageUris.value.toList()) { uri ->
                            Box(modifier = Modifier.fillMaxWidth()) {
                                AsyncImage(
                                    model = uri.normalizeScheme(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                        .clickable {
                                            val intent = Intent(Intent.ACTION_VIEW)
                                            intent.setDataAndType(uri, "image/*")
                                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                            context.startActivity(intent)
                                        }
                                )

                                IconButton(
                                    onClick = {
                                        viewModel.onEvent(AddEditMapEntryEvent.DeleteImageUri(uri))
                                    },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }
                    if (mapEntryId != -1) {
                        item {
                            Text(
                                text = "Letztes update am: ${
                                    CustomFormat.formatLocalDateTime(updatedState)
                                }"
                            )
                            Text(
                                text = "Erstellt am: ${
                                    CustomFormat.formatLocalDateTime(createdState)
                                }"
                            )
                        }

                    }

                }
            }
        }

    }
}








