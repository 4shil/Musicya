package com.fourshil.musicya.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.fourshil.musicya.ui.theme.NeoDimens

/**
 * Dialog to show when storage permission needs rationale explanation.
 */
@Composable
fun PermissionRationaleDialog(
    onDismiss: () -> Unit,
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Folder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text("Music Access Required")
        },
        text = {
            Text(
                "Musicya needs access to your music files to display your library. " +
                "Without this permission, we cannot scan or play your music.\n\n" +
                "Your files stay on your device — we never upload them.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Column(horizontalAlignment = Alignment.End) {
                Button(onClick = {
                    onDismiss()
                    onRequestPermission()
                }) {
                    Text("Grant Permission")
                }
                Spacer(modifier = Modifier.height(NeoDimens.SpacingS))
                TextButton(onClick = {
                    onDismiss()
                    onOpenSettings()
                }) {
                    Text("Open Settings", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Not Now")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large
    )
}