package com.padi.newcompose

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    var isFirstLaunch: Boolean
        get() = prefs.getBoolean(KEY_FIRST_LAUNCH, true)
        set(value) = prefs.edit { putBoolean(KEY_FIRST_LAUNCH, value) }
    var isNight: Boolean
        get() = prefs.getBoolean(NIGHT, false)
        set(value) = prefs.edit { putBoolean(NIGHT, value) }

    companion object {
        private const val PREFS_NAME = "conf"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val NIGHT = "night"
    }
}