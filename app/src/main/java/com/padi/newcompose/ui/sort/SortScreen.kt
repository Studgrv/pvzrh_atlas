package com.padi.newcompose.ui.sort

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.padi.newcompose.R
import com.padi.newcompose.ui.anim.AnimatedNavigation
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.DetailScreenDestination
import com.ramcosta.composedestinations.generated.destinations.DetailScreenDestination.invoke
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination<RootGraph>(style = AnimatedNavigation::class)
@Composable
fun SortScreen(navController: DestinationsNavigator) {
    val viewModel: SortViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAllData()
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val options = listOf("植物", "僵尸")
    var selectedOption by remember { mutableStateOf(options[0]) }

    val tabs = when (selectedOption) {
        "植物" -> state.plantTabList
        "僵尸" -> state.zombiesTabList
        else -> emptyList()
    }

    val contentList = when (selectedOption) {
        "植物" -> state.plantList.getOrNull(selectedTabIndex) ?: emptyList()
        "僵尸" -> state.zombiesList.getOrNull(selectedTabIndex) ?: emptyList()
        else -> emptyList()
    }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.app_list_loading))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = 1f,
    )

    Column(modifier = Modifier.fillMaxSize()) {
        if (tabs.isNotEmpty()) {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.padding(horizontal = 8.dp)) {
                options.forEach { option ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = options.indexOf(option), count = options.size
                        ), selected = option == selectedOption, onClick = {
                            selectedOption = option
                            selectedTabIndex = 0
                        }, modifier = Modifier.weight(1f)
                    ) {
                        Text(option)
                    }
                }
            }

            PrimaryScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 0.dp,
                tabs = {
                    tabs.forEachIndexed { index, item ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(item.title) })
                    }
                },
            )
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(3),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(contentList) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(2.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                alpha = 0.8f
                            )
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        onClick = {
                            navController.navigate(DetailScreenDestination(item.jumpUrl))
                        }) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            AsyncImage(
                                model = item.imgUrl,
                                contentDescription = item.name,
                                modifier = Modifier.size(70.dp)
                            )
                            HorizontalDivider(modifier = Modifier.padding(4.dp))
                            Text(item.name)
                        }
                    }
                }
            }
        }else{
            AnimatedVisibility(
                visible = state.isLoading,
                enter = fadeIn(animationSpec = tween(durationMillis = 100)),
                exit = fadeOut(animationSpec = tween(durationMillis = 50)),
            ) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            AnimatedVisibility(
                visible = state.error != null,
                enter = fadeIn(animationSpec = tween(durationMillis = 100)),
                exit = fadeOut(animationSpec = tween(durationMillis = 50)),
            ) {
                Text(
                    text = state.error ?: "Unknown Error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
