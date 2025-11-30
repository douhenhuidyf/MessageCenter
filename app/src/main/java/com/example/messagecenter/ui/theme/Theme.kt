package com.example.messagecenter.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

import com.example.messagecenter.ui.theme.theme.HanSansTypography

private val DarkColorScheme = darkColorScheme(
    background = Color(0xFF121212),
    primary = Color(0xFF121212),

    secondaryContainer = Color(0xFF262626),
    primaryContainer = Color(0xFF123064),

    onSecondaryContainer = Color.White,
    onPrimaryContainer = Color.White
)

private val LightColorScheme = lightColorScheme(
    background = Color.White,
    primary = Color.White,

    secondaryContainer = Color(0xFFF3F4F6),
    primaryContainer = Color(0xFFD2E3FF),

    onSecondaryContainer = Color.Black,
    onPrimaryContainer = Color.Black

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun MessageCenterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = HanSansTypography,
        content = content
    )
}
