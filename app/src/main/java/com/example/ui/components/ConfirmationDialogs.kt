package com.example.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R

@Composable
fun ResetCounterDialog(
  ducoTitle: String,
  onConfirm: () -> Unit,
  onDismiss: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(24.dp),
    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
    icon = {
      Icon(
        imageVector = Icons.Rounded.Refresh,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
      )
    },
    title = {
      Text(
        text = stringResource(R.string.dialog_reset_title),
        style = MaterialTheme.typography.titleLarge,
      )
    },
    text = {
      Text(
        text = stringResource(R.string.dialog_reset_message, ducoTitle),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    },
    confirmButton = {
      Button(
        onClick = onConfirm,
        modifier = Modifier.testTag("confirm_reset_button"),
      ) {
        Text(text = stringResource(R.string.action_reset))
      }
    },
    dismissButton = {
      TextButton(
        onClick = onDismiss,
        modifier = Modifier.testTag("cancel_reset_button"),
      ) {
        Text(text = stringResource(R.string.action_cancel))
      }
    },
  )
}

@Composable
fun DeleteDucoDialog(
  ducoTitle: String,
  onConfirm: () -> Unit,
  onDismiss: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(24.dp),
    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
    icon = {
      Icon(
        imageVector = Icons.Outlined.DeleteOutline,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.error,
      )
    },
    title = {
      Text(
        text = stringResource(R.string.dialog_delete_title),
        style = MaterialTheme.typography.titleLarge,
      )
    },
    text = {
      Text(
        text = stringResource(R.string.dialog_delete_message, ducoTitle),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    },
    confirmButton = {
      Button(
        onClick = onConfirm,
        colors =
          ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError,
          ),
        modifier = Modifier.testTag("confirm_delete_button"),
      ) {
        Text(text = stringResource(R.string.action_delete))
      }
    },
    dismissButton = {
      TextButton(
        onClick = onDismiss,
        modifier = Modifier.testTag("cancel_delete_button"),
      ) {
        Text(text = stringResource(R.string.action_cancel))
      }
    },
  )
}
