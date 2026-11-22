package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R

enum class DucoTab {
  HOME,
  ADD_DUCO,
}

@Composable
fun DucoBottomNavBar(
  currentTab: DucoTab,
  onSelectHome: () -> Unit,
  onSelectAddDuco: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier =
      modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .padding(horizontal = 20.dp, vertical = 8.dp),
    contentAlignment = Alignment.Center,
  ) {
    Surface(
      modifier = Modifier.fillMaxWidth().widthIn(max = 420.dp),
      shape = RoundedCornerShape(32.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLowest,
      tonalElevation = 3.dp,
      shadowElevation = 8.dp,
      border =
        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f)),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        NavPillItem(
          label = stringResource(R.string.nav_home),
          selected = currentTab == DucoTab.HOME,
          selectedIcon = Icons.Filled.Home,
          unselectedIcon = Icons.Outlined.Home,
          onClick = onSelectHome,
          modifier = Modifier.weight(1f).testTag("nav_home_button"),
        )
        NavPillItem(
          label = stringResource(R.string.nav_add_duco),
          selected = currentTab == DucoTab.ADD_DUCO,
          selectedIcon = Icons.Filled.AddCircle,
          unselectedIcon = Icons.Outlined.AddCircleOutline,
          onClick = onSelectAddDuco,
          modifier = Modifier.weight(1f).testTag("nav_add_duco_button"),
        )
      }
    }
  }
}

@Composable
private fun NavPillItem(
  label: String,
  selected: Boolean,
  selectedIcon: ImageVector,
  unselectedIcon: ImageVector,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val containerColor =
    if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
  val contentColor =
    if (selected) MaterialTheme.colorScheme.onPrimary
    else MaterialTheme.colorScheme.onSurfaceVariant

  Surface(
    onClick = onClick,
    modifier = modifier.height(50.dp).minimumInteractiveComponentSize(),
    shape = RoundedCornerShape(26.dp),
    color = containerColor,
  ) {
    Row(
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 16.dp),
    ) {
      Icon(
        imageVector = if (selected) selectedIcon else unselectedIcon,
        contentDescription = label,
        tint = contentColor,
        modifier = Modifier.size(22.dp),
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = contentColor,
      )
    }
  }
}
