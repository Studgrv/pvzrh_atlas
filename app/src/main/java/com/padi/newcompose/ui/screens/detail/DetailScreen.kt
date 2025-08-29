package com.padi.newcompose.ui.screens.detail

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.padi.newcompose.ui.anim.AnimatedNavigation
import com.padi.newcompose.utils.FileUtils
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.EmojiEasterEggScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.padi.newcompose.R
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Destination<RootGraph>(style = AnimatedNavigation::class)
@Composable
fun DetailScreen(
    url: String,
    navController: DestinationsNavigator
) {
    val context = LocalContext.current
    var javaScriptContent: String? by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        javaScriptContent = FileUtils.readAssetFile(context, "DetailHideElements.js")
    }
    var isLoading by remember { mutableStateOf(true) }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lib_detail_rocket))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = 1f,
    )
    Scaffold(
        topBar = {
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
        }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(), factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                super.onProgressChanged(view, newProgress)
                                if (newProgress == 100) {
                                    isLoading = false
                                    javaScriptContent?.let { js ->
                                        view?.evaluateJavascript(js, null)
                                    }
                                }
                            }
                        }
                        loadUrl(url)
                    }
                })
            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(animationSpec = tween(durationMillis = 100)),
                exit = fadeOut(animationSpec = tween(durationMillis = 50)),
            ) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                )
            }
        }
    }
}