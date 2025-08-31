package com.padi.newcompose

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.outlined.Egg
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.padi.newcompose.ui.theme.NewComposeTheme
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.padi.newcompose.components.BottomBar
import com.padi.newcompose.components.ThemeToggleButton
import com.padi.newcompose.ui.screens.home.HomeViewModel
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.DetailScreenDestination
import com.ramcosta.composedestinations.generated.destinations.HomeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.ListScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SortScreenDestination
import com.setruth.themechange.components.MaskBox
import dagger.hilt.android.AndroidEntryPoint
import kotlin.collections.contains


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val viewModel: HomeViewModel = hiltViewModel()
            val darkTheme by viewModel.darkTheme.collectAsStateWithLifecycle(0)
            val navBackStackEntry by navController.currentBackStackEntryAsState()

            val isDarkTheme = when (darkTheme) {
                1 -> false
                2 -> true
                else -> false
            }

            var isAnimating by remember { mutableStateOf(false) }
            var pendingThemeChange by remember { mutableStateOf<Boolean?>(null) }
            val currentRoute = navBackStackEntry?.destination?.route
            var showDialog by remember { mutableStateOf(false) }
            val context = LocalContext.current
            val uriHandler = LocalUriHandler.current
            val appPrefs = viewModel.appPre
            NewComposeTheme(
                darkTheme = when (darkTheme) {
                    1 -> false
                    2 -> true
                    else -> isSystemInDarkTheme()
                },
            ) {
                MaskBox(animTime = 1500L, maskComplete = {
                    pendingThemeChange?.let { newTheme ->
                        viewModel.updateDarkTheme(if (newTheme) 2 else 1)
                        appPrefs.isNight = newTheme
                        pendingThemeChange = null
                    }
                }, animFinish = {
                    isAnimating = false
                }) { maskAnimActiveEvent ->
                    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                        key(isDarkTheme) {
                            val showTopBar = currentRoute in listOf(
                                HomeScreenDestination.route,
                                SortScreenDestination.route,
                            )
                            if (showTopBar) {
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
                                    if (currentRoute == HomeScreenDestination.route) {
                                        ThemeToggleButton(
                                            isAnimating = isAnimating,
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
                                            viewModel
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    IconButton(
                                        onClick = {
                                            showDialog = true
                                        },
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Egg,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                })
                            }


                            when (currentRoute) {
                                ListScreenDestination.route -> {
                                }

                                DetailScreenDestination.route -> {
                                    TopAppBar(navigationIcon = {
                                        IconButton(onClick = {
                                            navController.popBackStack()
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowBack,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }, title = { Text("详情页") })
                                }
                            }


                            if (showDialog) {
                                AlertDialog(
                                    title = {
                                    Text("关于")
                                }, text = {
                                    Column {
                                        ListItem(
                                            modifier = Modifier.clickable {
                                                uriHandler.openUri("https://github.com/paditianxiu/pvzrh_atlas")
                                            },
                                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                                            leadingContent = {
                                                Image(
                                                    painter = painterResource(id = R.drawable.icon),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(50.dp)
                                                )
                                            },
                                            headlineContent = {
                                                Text(
                                                    "PVZ融合版图鉴",
                                                    style = MaterialTheme.typography.bodyLarge.copy(
                                                        fontWeight = FontWeight.Bold
                                                    )
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
                                                    "帕帝天秀",
                                                    style = MaterialTheme.typography.bodyLarge.copy(
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                )
                                            },

                                            supportingContent = {
                                                Text("作者");
                                            })
                                    }
                                }, confirmButton = {

                                },

                                    dismissButton = {

                                    }, onDismissRequest = {
                                        showDialog = false
                                    })
                            }

                        }
                    }, bottomBar = {
                        val showBottomBar = currentRoute in listOf(
                            HomeScreenDestination.route,
                            SortScreenDestination.route,
                        )
                        if (showBottomBar) {
                            BottomBar(currentRoute) { route ->
                                navController.navigate(route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                }
                            }
                        }
                    }) { innerPadding ->
                        DestinationsNavHost(
                            navGraph = NavGraphs.root,
                            navController = navController,
                            modifier = Modifier.padding(innerPadding)
                        )
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



