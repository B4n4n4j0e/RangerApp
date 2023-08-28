package com.example.rangerapp.core.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PermissionDialog(
    permissionProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    onGoToAppSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        buttons = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Divider()
                Text(text = if (isPermanentlyDeclined) {
                    "Berechtigung vergeben"
                } else {
                    "Ok"
                },
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isPermanentlyDeclined) {
                                onGoToAppSettingsClick()
                            } else {
                                onOkClick()
                            }
                        }
                        .padding(16.dp)
                )
            }
        },
        title = {
            Text(text = "Berechtigung benötigt")
        },
        text = {

            Text(
                text = permissionProvider.getDescription(isPermanentlyDeclined)
            )

        },
        modifier = modifier

    )

}


interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): String
}

class CameraPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "Es scheint, als hätten Sie die Berechtigung für die Nutzung der " +
                    "Kamera dauerhaft verweigert. Sie können die Berechtigung in " +
                    "den Einstellungen der App erteilen."
        } else {
            "Dies App benötigt Zugriff auf Ihren Kamera, damit Bilder in der App gemacht werden können."
        }
    }
}


class FineLocationPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "Es scheint, als hätten Sie die Berechtigung für die Nutzung der " +
                    "Standorterkennung dauerhaft verweigert. Sie können die Berechtigung in " +
                    "den Einstellungen der App erteilen."
        } else {
            "Dies App benötigt Zugriff auf Ihren Standort, damit Sie Karteneinträge zu Ihrem Standort hinzufügen kann."
        }
    }
}


class CoarseLocationPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "Es scheint, als hätten Sie die Berechtigung für die Nutzung der " +
                    "genauen Standorterkennung dauerhaft verweigert. Sie können die Berechtigung in " +
                    "den Einstellungen der App erteilen."
        } else {
            "Dies App benötigt Zugriff auf Ihren Standort, damit Sie Karteneinträge zu Ihrem Standort hinzufügen kann."
        }
    }
}

class InternetPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "Es scheint, als hätten Sie die Berechtigung für die Nutzung des Internets " +
                    " dauerhaft verweigert. Sie können die Berechtigung in " +
                    "den Einstellungen der App erteilen."
        } else {
            "Dies App benötigt Zugriff auf das Internet, um Karten aus dem Internet laden zu können."
        }
    }
}

class NetworkStatePermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "Es scheint, als hätten Sie die Berechtigung für die Nutzung des " +
                    "Netzwerkstatus dauerhaft verweigert. Sie können die Berechtigung in " +
                    "den Einstellungen der App erteilen."
        } else {
            "Dies App benötigt Zugriff auf den Netzwerkstatus, um Karten aus dem Internet laden zu können."
        }
    }
}

class ExternalStoragePermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "Es scheint, als hätten Sie die Berechtigung für die Nutzung des " +
                    "externen Speichers dauerhaft verweigert. Sie können die Berechtigung in " +
                    "den Einstellungen der App erteilen."
        } else {
            "Dies App benötigt Zugriff auf dem externen Speicher, um Karten herunterzuladen und Bilder anzuzeigen oder zu speichern."
        }
    }


}

class ExternalImageStoragePermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "Es scheint, als hätten Sie die Berechtigung für die Nutzung des " +
                    "externen Speichers dauerhaft verweigert. Sie können die Berechtigung in " +
                    "den Einstellungen der App erteilen."
        } else {
            "Dies App benötigt Zugriff auf dem externen Speicher, Bilder zu anzuzeigen."
        }
    }


}