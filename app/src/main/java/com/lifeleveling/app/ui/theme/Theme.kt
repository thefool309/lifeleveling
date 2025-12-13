package com.lifeleveling.app.ui.theme

import android.app.Activity
import android.os.Build
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/*
Usage:
To access colors use AppTheme.colors.[insert your color choice]
To access Typography styles use AppTheme.textStyles.[text style you need]
*/

// Universally usable names pulled from Figma for color scheme
/**
 * The common names of the colors pulled from Figma.
 * A class that holds them all for assigning values for dark versus light.
 * @author Elyseia
 */
data class Colors(
    val BrandOne: Color,
    val BrandTwo: Color,
    val SecondaryOne: Color,
    val SecondaryTwo: Color,
    val SecondaryThree: Color,
    val Background: Color,
    val DarkerBackground: Color,
    val PopUpBackground: Color,
    val DropShadow: Color,
    val LightShadow: Color,
    val Gray: Color,
    val FadedGray: Color,
    val Success: Color,
    val Success75: Color,
    val Error: Color,
    val Error75: Color,
    val Warning: Color
)

// Dark Mode colors assigned
/**
 *  The assignment of the dark mode colors into an object
 *  @author Elyseia
 */
private val DarkCustomScheme = Colors(
    BrandOne = BrandOne_Dark,
    BrandTwo = BrandTwo_Dark,
    SecondaryOne = SecondaryOne_Dark,
    SecondaryTwo = SecondaryTwo_Dark,
    SecondaryThree = SecondaryThree_Dark,
    Background = Background_Dark,
    DarkerBackground = DarkerBackground_Dark,
    PopUpBackground = PopUpBackground_Dark,
    DropShadow = DropShadow_Dark,
    LightShadow = LightShadow_Dark,
    Gray = Gray_Dark,
    FadedGray = FadedGray_Dark,
    Success = Success_Dark,
    Success75 = Success75_Dark,
    Error = Error_Dark,
    Error75 = Error75_Dark,
    Warning = Warning_Dark
)

//Light Mode colors assigned
/**
 *  The assignment of the light mode colors into an object
 *  @author Elyseia
 */
private val LightCustomScheme = Colors(
    BrandOne = BrandOne_Light,
    BrandTwo = BrandTwo_Light,
    SecondaryOne = SecondaryOne_Light,
    SecondaryTwo = SecondaryTwo_Light,
    SecondaryThree = SecondaryThree_Light,
    Background = Background_Light,
    DarkerBackground = DarkerBackground_Light,
    PopUpBackground = PopUpBackground_Light,
    DropShadow = DropShadow_Light,
    LightShadow = LightShadow_Light,
    Gray = Gray_Light,
    FadedGray = FadedGray_Light,
    Success = Success_Light,
    Success75 = Success75_Light,
    Error = Error_Light,
    Error75 = Error75_Light,
    Warning = Warning_Light
)

// Materials Dark colors
/**
 * Overriding the material3 base dark colors to app theme colors
 * @Elyseia
 */
private val DarkMaterialColors = darkColorScheme(
    primary = BrandOne_Dark,
    onPrimary = Background_Dark,
    primaryContainer = DarkerBackground_Dark,
    onPrimaryContainer = Gray_Dark,
    secondary = BrandTwo_Dark,
    onSecondary = Background_Dark,
    secondaryContainer = DarkerBackground_Dark,
    onSecondaryContainer = Gray_Dark,
    tertiary = SecondaryOne_Dark,
    onTertiary = DarkerBackground_Dark,
    tertiaryContainer = Background_Dark,
    onTertiaryContainer = Gray_Dark,
    error = Error75_Dark,
    onError = Background_Dark,
    errorContainer = Error_Dark,
    onErrorContainer = Background_Dark,
    background = Background_Dark,
    onBackground = Gray_Dark,
    surface = DarkerBackground_Dark,
    onSurface = Gray_Dark,
    surfaceVariant = PopUpBackground_Dark,
    onSurfaceVariant = Gray_Dark,
    inverseSurface = Gray_Dark,
    inverseOnSurface = Background_Dark,
    inversePrimary = Background_Dark,
    outline = FadedGray_Dark,
    outlineVariant = DropShadow_Dark,
    scrim = Background_Dark,
    surfaceTint = LightShadow_Dark
)

// Materials Light colors
/**
 * Overriding the material3 base light colors to app theme colors
 * @Elyseia
 */
private val LightMaterialColors = lightColorScheme(
    primary = BrandOne_Light,
    onPrimary = Background_Light,
    primaryContainer = DarkerBackground_Light,
    onPrimaryContainer = Gray_Light,
    secondary = BrandTwo_Light,
    onSecondary = Background_Light,
    secondaryContainer = DarkerBackground_Light,
    onSecondaryContainer = Gray_Light,
    tertiary = SecondaryOne_Light,
    onTertiary = DarkerBackground_Light,
    tertiaryContainer = Background_Light,
    onTertiaryContainer = Gray_Light,
    error = Error75_Light,
    onError = Background_Light,
    errorContainer = Error_Light,
    onErrorContainer = Background_Light,
    background = Background_Light,
    onBackground = Gray_Light,
    surface = DarkerBackground_Light,
    onSurface = Gray_Light,
    surfaceVariant = PopUpBackground_Light,
    onSurfaceVariant = Gray_Light,
    inverseSurface = Gray_Light,
    inverseOnSurface = Background_Light,
    inversePrimary = Background_Light,
    outline = FadedGray_Light,
    outlineVariant = DropShadow_Light,
    scrim = Background_Light,
    surfaceTint = LightShadow_Light
)

// Making a default fall back
val LocalAppColors = staticCompositionLocalOf { DarkCustomScheme }
val LocalAppTextStyles = staticCompositionLocalOf { AppTextStyles }

// Theme Wrapper
/**
 * Sets up the theme controls for the application.
 * @param darkTheme A boolean value that controls if the app is in dark or light mode
 */
@Composable
fun LifelevelingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val materialColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkMaterialColors
        else -> LightMaterialColors
    }

    val customScheme = if (darkTheme) DarkCustomScheme else LightCustomScheme

    CompositionLocalProvider(
        LocalAppColors provides customScheme,
        LocalAppTextStyles provides AppTextStyles
        ) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            typography = Typography,
            content = content
        )
    }
}

// Custom accessor of colors and typography
/**
 * This object exposes the Colors and TextStyles that are for this application.
 * Makes it so that UI can call saved values as AppTheme.colors.Background and AppTheme.textStyles.Default
 * @author Elyseia
 */
object AppTheme {
    val colors: Colors
        @Composable
        get() = LocalAppColors.current

    val textStyles: TextStyles
        @Composable
        get() = LocalAppTextStyles.current

    val materialTypography: androidx.compose.material3.Typography
        @Composable
        get() = MaterialTheme.typography

    val materialColors: ColorScheme
        @Composable
        get() = MaterialTheme.colorScheme
}

// Hides system top and bottom bars
/**
 * Hides the System bars until the user 'pulls' them down.
 * @author Elyseia
 */
@Composable
fun HideSystemBars() {
    val view = LocalView.current
    LaunchedEffect(Unit) {
        val window = (view.context as Activity).window
        val controller = WindowCompat.getInsetsController(window, view)
        controller.systemBarsBehavior =
            WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsets.Type.systemBars())
    }
}