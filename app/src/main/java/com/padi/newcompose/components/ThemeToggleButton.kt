package com.padi.newcompose.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.padi.newcompose.ui.screens.home.HomeViewModel
import com.setruth.themechange.components.MaskAnimActive
import com.setruth.themechange.model.MaskAnimModel


@Composable
fun ThemeToggleButton(
    isAnimating: Boolean,
    onThemeToggle: MaskAnimActive,
    homeViewModel: HomeViewModel
) {
    var buttonPosition by remember { mutableStateOf(Offset.Zero) }
    val darkTheme by homeViewModel.darkTheme.collectAsStateWithLifecycle(0)
    val isDarkTheme = when (darkTheme) {
        1 -> false
        2 -> true
        else -> false
    }

    IconButton(
        onClick = {
            if (isAnimating) return@IconButton
            onThemeToggle(
                if (isDarkTheme) MaskAnimModel.SHRINK else MaskAnimModel.EXPEND,
                buttonPosition.x,
                buttonPosition.y
            )
        },
        modifier = Modifier.onGloballyPositioned { coordinates ->
            val position = coordinates.positionInRoot()
            buttonPosition = Offset(
                x = position.x + coordinates.size.width / 2f,
                y = position.y + coordinates.size.height / 2f
            )
        }
    ) {
        Icon(
            imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
            contentDescription = if (isDarkTheme) "切换到亮色模式" else "切换到暗色模式",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

