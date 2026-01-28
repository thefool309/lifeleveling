package com.lifeleveling.app.ui.theme

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.lifeleveling.app.R
import com.lifeleveling.app.data.Reminder

private val iconMap = mapOf(
    "water_drop"     to R.drawable.water_drop,
    "bed_color"      to R.drawable.bed_color,
    "shirt_color"    to R.drawable.shirt_color,
    "med_bottle"     to R.drawable.med_bottle,
    "shower_bath"    to R.drawable.shower_bath,
    "shop_color"     to R.drawable.shop_color,
    "person_running" to R.drawable.person_running,
    "heart"          to R.drawable.heart,
    "bell"           to R.drawable.bell,
    "brain"          to R.drawable.brain,
    "document"       to R.drawable.document,
    "doctor"         to R.drawable.doctor,
    "question_mark"  to R.drawable.question_mark,
    "flame"          to R.drawable.flame,
    "one"            to R.drawable.one,
    "sun_glasses"    to R.drawable.sun_glasses,
)
/**
 * Map stored iconName → drawable id. Falls back to the bell icon if we don’t recognize it (can be updated to the correct error icon).
 * @author fdesouza1992
 */
fun iconResForName(iconName: String?): Int {
    val res = iconMap[iconName]
    // Debugging for making sure names convert correctly
//    Log.d("IconMapping", "iconName = '$iconName', resolved=${res != null}")
    return res ?: R.drawable.question_mark
}



/**
 * Turns the enum color name into an actual color value
 * @param color The enum name of a color
 * @return Returns a proper color value
 * @author Elyseia
 */
@Composable
fun resolveColor(color: String?): Color {
    val colorMap = mapOf(
        "BrandOne"          to AppTheme.colors.BrandOne,
        "BrandTwo"          to AppTheme.colors.BrandTwo,
        "SecondaryOne"      to AppTheme.colors.SecondaryOne,
        "SecondaryTwo"      to AppTheme.colors.SecondaryTwo,
        "SecondaryThree"    to AppTheme.colors.SecondaryThree,
        "Background"        to AppTheme.colors.Background,
        "DarkerBackground"  to AppTheme.colors.DarkerBackground,
        "PopUpBackground"   to AppTheme.colors.PopUpBackground,
        "DropShadow"        to AppTheme.colors.DropShadow,
        "LightShadow"       to AppTheme.colors.LightShadow,
        "Gray"              to AppTheme.colors.Gray,
        "FadedGray"         to AppTheme.colors.FadedGray,
        "Success"           to AppTheme.colors.Success,
        "Success75"         to AppTheme.colors.Success75,
        "Error"             to AppTheme.colors.Error,
        "Error75"           to AppTheme.colors.Error75,
        "Warning"           to AppTheme.colors.Warning,
    )
    return colorMap[color?.trim()] ?: Color.Unspecified
}

fun reminderDotColor(reminder: Reminder): Color {
    val token = reminder.dotColor?.trim()?.lowercase()

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