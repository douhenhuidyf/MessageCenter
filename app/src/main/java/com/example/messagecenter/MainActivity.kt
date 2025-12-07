package com.example.messagecenter


import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.messagecenter.data.AppViewModelProvider
import com.example.messagecenter.data.viewmodel.SettingsViewModel
import com.example.messagecenter.ui.theme.MessageCenterTheme
import com.example.messagecenter.navigation.AppNavHost

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val themeMode by settingsViewModel.themeMode.collectAsState()
            val languageCode by settingsViewModel.languageCode.collectAsState()

            LaunchedEffect(languageCode) {
                val appLocale = if (languageCode.isEmpty()) {
                    LocaleListCompat.getEmptyLocaleList()
                } else {
                    LocaleListCompat.forLanguageTags(languageCode)
                }
                AppCompatDelegate.setApplicationLocales(appLocale)
            }

            val darkTheme = when (themeMode) {
                1 -> false
                2 -> true
                else -> isSystemInDarkTheme()
            }
            MessageCenterTheme(darkTheme = darkTheme) {
                MessageCenterApp()
            }
        }
    }
}


@Composable
fun MessageCenterApp(){
    AppNavHost()
}