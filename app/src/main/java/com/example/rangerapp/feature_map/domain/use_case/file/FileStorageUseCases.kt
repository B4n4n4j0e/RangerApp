package com.example.rangerapp.feature_map.domain.use_case.file

data class FileStorageUseCases(
    val save: SaveImage,
    val delete: Delete,
    val getBitmapFromUri: GetBitmapFromUri,
    val exportJson: ExportJson,
    val exportCsv: ExportCsv,
    val importJson: ImportJson,
    val saveTempUri: SaveTempUri
    
)
