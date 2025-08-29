package com.padi.newcompose.ui.screens.home

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.padi.newcompose.R
import com.padi.newcompose.components.ThemeToggleButton
import com.padi.newcompose.ui.anim.AnimatedNavigation
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.EmojiEasterEggScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ListScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.Direction
import com.setruth.themechange.components.MaskAnimActive
import com.setruth.themechange.components.MaskBox


@Destination<RootGraph>(start = true, style = AnimatedNavigation::class)
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val appPrefs = viewModel.appPre
    val darkTheme by viewModel.darkTheme.collectAsStateWithLifecycle(0)
    var isAnimating by remember { mutableStateOf(false) }
    var pendingThemeChange by remember { mutableStateOf<Boolean?>(null) }
    MaskBox(animTime = 1500L, maskComplete = {
        pendingThemeChange?.let { newTheme ->
            viewModel.updateDarkTheme(if (newTheme) 2 else 1)
            appPrefs.isNight = newTheme
            pendingThemeChange = null
        }
    }, animFinish = {
        isAnimating = false
    }) { maskAnimActiveEvent ->
        MainScaffold(
            isDarkTheme = when (darkTheme) {
                1 -> false
                2 -> true
                else -> false
            },
            isAnimating = isAnimating,
            homeViewModel = viewModel,
            onThemeToggle = { animModel, x, y ->
                if (!isAnimating) {
                    isAnimating = true
                    pendingThemeChange = !when (darkTheme) {
                        1 -> false
                        2 -> true
                        else -> false
                    }
                    maskAnimActiveEvent(animModel, x, y)
                }
            },
            navController = navigator,
        )
    }
}

data class MenuItem(val name: String, val imageResId: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    isDarkTheme: Boolean,
    isAnimating: Boolean,
    homeViewModel: HomeViewModel,
    onThemeToggle: MaskAnimActive,
    navController: DestinationsNavigator
) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        key(isDarkTheme) {
            TopAppBar(title = {
                Row(horizontalArrangement = Arrangement.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "PVZ融合版图鉴")
                }
            }, actions = {
                ThemeToggleButton(
                    isAnimating = isAnimating, onThemeToggle = onThemeToggle, homeViewModel
                )
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = {
                        showDialog = true
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Egg,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            })
        }
        if (showDialog) {
            AlertDialog(
                title = {
                    Text("关于")
                }, text = {
                    Column {
                        ListItem(
                            modifier = Modifier.clickable {

                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            leadingContent = {
                                Image(
                                    painter = painterResource(id = R.drawable.icon),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(50.dp)
                                )
                            },
                            headlineContent = {
                                Text(
                                    "PVZ融合版图鉴",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            },

                            supportingContent = {
                                Text(getAppVersionName(context));
                            })
                        HorizontalDivider(modifier = Modifier.fillMaxWidth())
                        ListItem(
                            modifier = Modifier.clickable {
                                uriHandler.openUri("https://github.com/paditianxiu")
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            leadingContent = {
                                Image(
                                    painter = painterResource(id = R.drawable.paditianxiu),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                            },
                            headlineContent = {
                                Text(
                                    "作者",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            },

                            supportingContent = {
                                Text("帕帝天秀");
                            })
                    }
                }, confirmButton = {

                },

                dismissButton = {

                }, onDismissRequest = {
                    showDialog = false
                })
        }
    }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val menuItems = listOf(
                MenuItem("植物图鉴", R.drawable.plant_atlas),
                MenuItem("僵尸图鉴", R.drawable.zombies_atlas),
                MenuItem("融合DLC", R.drawable.dlc),
                MenuItem("二创Mod", R.drawable.mod)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(menuItems) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        onClick = {
                            navController.navigate(
                                ListScreenDestination(
                                    item.name, item.imageResId
                                )
                            )
                        }) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = item.imageResId),
                                contentDescription = item.name,
                                modifier = Modifier.size(150.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getAppVersionName(context: Context): String {
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName!!
    } catch (e: PackageManager.NameNotFoundException) {
        "Unknown"
    }
}

