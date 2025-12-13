package com.lifeleveling.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.lifeleveling.app.R

// Downloading selected Font within app
val LexendDeca = FontFamily(
    Font(R.font.lexend_deca_variable, FontWeight.Normal),
    Font(R.font.lexend_deca_variable, FontWeight.Bold)
)

// Material typography changed to customized styles
/**
 * Saving the values for complete TextStyles.
 * Saves the app standards of the font, size, line height, and more.
 * @author Elyseia
 */
val Typography = Typography(
    // HeadingOne
    displayLarge = TextStyle(
        fontFamily = LexendDeca,
        fontWeight = FontWeight.Normal,
        fontSize = 61.04.sp,
        lineHeight = 76.3.sp,
        letterSpacing = 0.5.sp
    ),
    // HeadingTwo
    displayMedium = TextStyle(
        fontFamily = LexendDeca,
        fontWeight = FontWeight.Normal,
        fontSize = 48.83.sp,
        lineHeight = 61.sp,
        letterSpacing = 0.5.sp
    ),
    // HeadingThree
    displaySmall = TextStyle(
        fontFamily = LexendDeca,
        fontWeight = FontWeight.Normal,
        fontSize = 39.06.sp,
        lineHeight = 48.8.sp,
        letterSpacing = 0.5.sp
    ),
    // HeadingFour
    headlineLarge = TextStyle(
        fontFamily = LexendDeca,
        fontWeight = FontWeight.Normal,
        fontSize = 31.25.sp,
        lineHeight = 39.1.sp,
        letterSpacing = 0.25.sp
    ),
    // HeadingFive
    headlineMedium = TextStyle(
        fontFamily = LexendDeca,
        fontWeight = FontWeight.Normal,
        fontSize = 25.sp,
        lineHeight = 31.3.sp,
        letterSpacing = 0.25.sp
    ),
    // HeadingSix
    headlineSmall = TextStyle(
        fontFamily = LexendDeca,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 25.sp,
        letterSpacing = 0.25.sp
    ),
    // Default
    bodyLarge = TextStyle(
        fontFamily = LexendDeca,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.15.sp
    ),
    // Small
    bodyMedium = TextStyle(
        fontFamily = LexendDeca,
        fontWeight = FontWeight.Normal,
        fontSize = 12.8.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.1.sp
    ),
    // XSmall
    bodySmall = TextStyle(
        fontFamily = LexendDeca,
        fontWeight = FontWeight.Normal,
        fontSize = 8.sp,
        lineHeight = 10.sp,
        letterSpacing = 0.1.sp
    ),
    // BoldDefault
    titleMedium = TextStyle(
        fontFamily = LexendDeca,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.15.sp
    )
)

/**
 * Saved names of Text values pulled from Figma.
 * @author Elyseia
 */
data class TextStyles(
    val HeadingOne: TextStyle,
    val HeadingTwo: TextStyle,
    val HeadingThree: TextStyle,
    val HeadingFour: TextStyle,
    val HeadingFive: TextStyle,
    val HeadingSix: TextStyle,
    val Default: TextStyle,
    val Small: TextStyle,
    val XSmall: TextStyle,
    val Emphasized: TextStyle,
    val DefaultUnderlined: TextStyle,
    val SmallUnderlined: TextStyle
)

val AppTextStyles = TextStyles(
    HeadingOne = Typography.displayLarge,
    HeadingTwo = Typography.displayMedium,
    HeadingThree = Typography.displaySmall,
    HeadingFour = Typography.headlineLarge,
    HeadingFive = Typography.headlineMedium,
    HeadingSix = Typography.headlineSmall,
    Default = Typography.bodyLarge,
    Small = Typography.bodyMedium,
    XSmall = Typography.bodySmall,
    Emphasized = Typography.titleMedium.copy(
        textDecoration = TextDecoration.Underline
    ),
    DefaultUnderlined = Typography.bodyLarge.copy(
        textDecoration = TextDecoration.Underline
    ),
    SmallUnderlined = Typography.bodyMedium.copy(
        textDecoration = TextDecoration.Underline
    )
)