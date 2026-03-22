package com.hutudev.calendar.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = White,
    secondary = Gray300,
    tertiary = WeekendGray,
    background = Black,
    surface = Gray900,
    onPrimary = Black,
    onSecondary = White,
    onTertiary = White,
    onBackground = White,
    onSurface = White
)

private val LightColorScheme = lightColorScheme(
    primary = SlateDeepBlue,
    secondary = Gray800,
    tertiary = WeekendGray,
    background = SoftBackground,
    surface = Gray100, // Minimal boundary colors
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = SlateDeepBlue,
    onSurface = SlateDeepBlue
)

@Composable
fun MinimalCalendarTheme(
    themeConfig: ThemeConfig = ThemeConfig.SYSTEM,
    dynamicColor: Boolean = false, // 强制关闭动态主题，维持极简黑白灰基调
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeConfig) {
        ThemeConfig.LIGHT -> false
        ThemeConfig.DARK -> true
        ThemeConfig.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                view.isForceDarkAllowed = false
                window.decorView.isForceDarkAllowed = false
                view.rootView.isForceDarkAllowed = false
            }

            // 因为使用了 enableEdgeToEdge(), 应该保持状态栏透明，通过 WindowCompat 控制图标颜色
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
