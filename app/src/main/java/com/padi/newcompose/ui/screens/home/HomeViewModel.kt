package com.padi.newcompose.ui.screens.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.padi.newcompose.AppPreferences
import com.padi.newcompose.ui.theme.ThemeSettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val application: Application,
    private val themeSettingsManager: ThemeSettingsManager,
): ViewModel() {
    val darkTheme = themeSettingsManager.darkTheme

    fun updateDarkTheme(value: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            themeSettingsManager.setDarkTheme(value)
        }

    val appPre = appPreferences



}