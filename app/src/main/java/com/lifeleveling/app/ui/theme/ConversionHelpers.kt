package com.lifeleveling.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.lifeleveling.app.R
import com.lifeleveling.app.data.Reminder

fun iconResForNameCalendar(iconName: String?): Int {
    return when (iconName) {
        "water_drop"     -> R.drawable.water_drop
        "bed_color"      -> R.drawable.bed_color
        "shirt_color"    -> R.drawable.shirt_color
        "med_bottle"     -> R.drawable.med_bottle
        "shower_bath"    -> R.drawable.shower_bath
        "shop_color"     -> R.drawable.shop_color
        "person_running" -> R.drawable.person_running
        "heart"          -> R.drawable.heart
        "bell"           -> R.drawable.bell
        "brain"          -> R.drawable.brain
        "document"       -> R.drawable.document
        "doctor"         -> R.drawable.doctor
        else             -> R.drawable.bell
    }
}



/**
 * Turns the enum color name into an actual color value
 * @param color The enum name of a color
 * @return Returns a proper color value
 * @author Elyseia
 */
@Composable
fun resolveColor(color: String?): Color {
    return when (color) {
        "BrandOne" -> AppTheme.colors.BrandOne
        "BrandTwo" -> AppTheme.colors.BrandTwo
        "SecondaryOne" -> AppTheme.colors.SecondaryOne
        "SecondaryTwo" -> AppTheme.colors.SecondaryTwo
        "SecondaryThree" -> AppTheme.colors.SecondaryThree
        "Background" -> AppTheme.colors.Background
        "DarkerBackground" -> AppTheme.colors.DarkerBackground
        "PopUpBackground" -> AppTheme.colors.PopUpBackground
        "DropShadow" -> AppTheme.colors.DropShadow
        "LightShadow" -> AppTheme.colors.LightShadow
        "Gray" -> AppTheme.colors.Gray
        "FadedGray" -> AppTheme.colors.FadedGray
        "Success" -> AppTheme.colors.Success
        "Success75" -> AppTheme.colors.Success75
        "Error" -> AppTheme.colors.Error
        "Error75" -> AppTheme.colors.Error75
        "Warning" -> AppTheme.colors.Warning
        else -> { Color.Unspecified }
    }
}

fun reminderDotColor(reminder: Reminder): Color {
    val token = reminder.colorToken?.trim()?.lowercase()

    // 1) Handles named tokens (string)
    val named = when (token) {
        "red" -> Color.Red
        "blue" -> Color.Blue
        "green" -> Color.Green
        "magenta" -> Color.Magenta
        "yellow" -> Color.Yellow
        "cyan" -> Color.Cyan
        "light_gray", "lightgrey", "light gray" -> Color.LightGray
        "white" -> Color.White
        else -> null
    }
    if (named != null) return named

    // 2) Handles numeric tokens ("0", "1", ...)
    val palette = listOf(
        Color.Red,
        Color.Blue,
        Color.Green,
        Color.Magenta,
        Color.Yellow,
        Color.Cyan,
        Color.LightGray,
        Color.White
    )

    val index = token?.toIntOrNull()?.coerceIn(0, palette.lastIndex) ?: 0
    return palette[index]
}