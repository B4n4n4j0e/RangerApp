package com.example.rangerapp.feature_map.presentation.util

sealed class Screen(val route: String) {
    object AddEditMapEntryScreen : Screen("add_edit_map_entry_screen")
    object MapScreen : Screen("map_screen")
    object ExportScreen : Screen("export_screen")
    object SplashScreen : Screen("splash_screen")

}
