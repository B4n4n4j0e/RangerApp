package com.example.rangerapp.feature_map.presentation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.example.rangerapp.core.presentation.components.CameraPermissionTextProvider
import com.example.rangerapp.core.presentation.components.CoarseLocationPermissionTextProvider
import com.example.rangerapp.core.presentation.components.ExternalImageStoragePermissionTextProvider
import com.example.rangerapp.core.presentation.components.ExternalStoragePermissionTextProvider
import com.example.rangerapp.core.presentation.components.FineLocationPermissionTextProvider
import com.example.rangerapp.core.presentation.components.InternetPermissionTextProvider
import com.example.rangerapp.core.presentation.components.NetworkStatePermissionTextProvider
import com.example.rangerapp.core.presentation.components.PermissionDialog
import com.example.rangerapp.feature_map.domain.model.MapEntry
import com.example.rangerapp.feature_map.presentation.add_edit_entity.mapEntry.AddEditMapEntryScreen
import com.example.rangerapp.feature_map.presentation.export.ExportScreen
import com.example.rangerapp.feature_map.presentation.map.MapScreen
import com.example.rangerapp.feature_map.presentation.map_entries.FilterViewModel
import com.example.rangerapp.feature_map.presentation.start.StartScreen
import com.example.rangerapp.feature_map.presentation.util.Screen
import com.example.rangerapp.ui.theme.JetpackComposeCrashCourseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController

    private val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    private val permissionsToRequest = mutableListOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.CAMERA,
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!minSdk29) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)

        } else permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)



        setContent {
            JetpackComposeCrashCourseTheme {
                navController = rememberNavController()
                val viewModel = viewModel<MainViewModel>()
                val dialogQueue = viewModel.visiblePermissionDialogQueue

                val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions(),
                    onResult = { perms ->
                        permissionsToRequest.forEach { permission ->
                            if (perms[permission] != null) {
                                viewModel.onPermissionResult(
                                    permission = permission,
                                    isGranted = perms[permission] == true
                                )
                            }
                        }
                    }
                )

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "splash_screen"
                    ) {
                        navigation(
                            startDestination = Screen.MapScreen.route,
                            route = "map"
                        ) {
                            composable(route = Screen.MapScreen.route) { entry ->
                                val filterViewModel =
                                    entry.sharedViewModel<FilterViewModel>(navController = navController)

                                MapScreen(
                                    navController = navController,
                                    filterViewModel = filterViewModel
                                )
                            }

                            composable(
                                route = Screen.ExportScreen.route,
                                deepLinks = listOf(
                                    navDeepLink {
                                        action = Intent.ACTION_SEND
                                        mimeType = "application/json"
                                    })
                            ) { entry ->
                                val filterViewModel =
                                    entry.sharedViewModel<FilterViewModel>(navController = navController)

                                ExportScreen(
                                    navController = navController,
                                    filterViewModel = filterViewModel
                                )

                            }
                        }
                        composable(route = Screen.SplashScreen.route) {
                            StartScreen(navController)
                        }
                        composable(
                            route = Screen.AddEditMapEntryScreen.route +
                                    "?mapEntryId={mapEntryId}&mapEntryType={mapEntryType}&latitude={latitude}&longitude={longitude}",
                            arguments = listOf(
                                navArgument(name = "mapEntryId") {
                                    type = NavType.IntType
                                    defaultValue = -1
                                },
                                navArgument(name = "mapEntryType") {
                                    type = NavType.StringType
                                    defaultValue = MapEntry.MapEntryType.values().first().name
                                },

                                navArgument(name = "latitude") {
                                    type = NavType.FloatType
                                    defaultValue = 0
                                },
                                navArgument(name = "longitude") {
                                    type = NavType.FloatType
                                    defaultValue = 0
                                },
                            ),


                            ) {
                            val latitude = it.arguments?.getFloat("latitude") ?: 0
                            val longitude = it.arguments?.getFloat("longitude") ?: 0
                            val mapEntryType = it.arguments?.getString("mapEntryType")
                                ?: MapEntry.MapEntryType.values().first().name

                            val mapEntryId = it.arguments?.getInt("mapEntryId") ?: -1
                            AddEditMapEntryScreen(
                                navController = navController,
                                latitude = latitude.toDouble(),
                                longitude = longitude.toDouble(),
                                editMapEntryType = MapEntry.MapEntryType.valueOf(mapEntryType),
                                mapEntryId = mapEntryId
                            )
                        }

                    }


                    dialogQueue.reversed().forEach { permission ->
                        PermissionDialog(
                            permissionProvider = when (permission) {
                                Manifest.permission.ACCESS_COARSE_LOCATION -> {
                                    CoarseLocationPermissionTextProvider()
                                }

                                Manifest.permission.ACCESS_FINE_LOCATION -> {
                                    FineLocationPermissionTextProvider()
                                }

                                Manifest.permission.INTERNET -> {
                                    InternetPermissionTextProvider()
                                }

                                Manifest.permission.ACCESS_NETWORK_STATE -> {
                                    NetworkStatePermissionTextProvider()
                                }

                                Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                                    ExternalStoragePermissionTextProvider()
                                }

                                Manifest.permission.CAMERA -> {
                                    CameraPermissionTextProvider()
                                }

                                Manifest.permission.READ_EXTERNAL_STORAGE -> {
                                    ExternalStoragePermissionTextProvider()
                                }

                                Manifest.permission.READ_MEDIA_IMAGES -> {
                                    ExternalImageStoragePermissionTextProvider()
                                }

                                else -> return@forEach

                            },
                            isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                                permission
                            ),
                            onDismiss = viewModel::dismissDialog,
                            onOkClick = {
                                viewModel.dismissDialog()
                                multiplePermissionResultLauncher.launch(
                                    arrayOf(permission)
                                )
                            },
                            onGoToAppSettingsClick = ::openAppSettings
                        )


                    }
                    LaunchedEffect(Unit) {
                        multiplePermissionResultLauncher.launch(permissionsToRequest.toTypedArray())
                    }

                }

            }


        }

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        navController.navigate("export_screen")
    }
}


fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavController
): T {
    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}
