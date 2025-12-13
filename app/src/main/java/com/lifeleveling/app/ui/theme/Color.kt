package com.lifeleveling.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Dark Mode Colors
val BrandOne_Dark = Color(0xff93c2ee)
val BrandTwo_Dark = Color(0xffd3b69c)
val SecondaryOne_Dark = Color(0xffc9b6e6)
val SecondaryTwo_Dark = Color(0xffafd6ad)
val SecondaryThree_Dark = Color(0xffd7a3d5)
val Background_Dark = Color(0xff2d2a32)
val DarkerBackground_Dark = Color(0xff242228)
val PopUpBackground_Dark = Color(0xff29262d)
val DropShadow_Dark = Color(0xff171519)
val LightShadow_Dark = Color(0xfff1eff3)
val Gray_Dark = Color(0xffebe8ee)
val FadedGray_Dark = Color(0xff767477)
val Success_Dark = Color(0xff66c55f)
val Success75_Dark = Color(0xbf66c55f)
val Error_Dark = Color(0xffe89591)
val Error75_Dark = Color(0xbfe89591)
val Warning_Dark = Color(0xffcbc449)

// Light Mode Colors
val BrandOne_Light = Color(0xff264d71)
val BrandTwo_Light = Color(0xff54493e)
val SecondaryOne_Light = Color(0xff553287)
val SecondaryTwo_Light = Color(0xff384e37)
val SecondaryThree_Light = Color(0xff633e62)
val Background_Light = Color(0xffedeaf0)
val DarkerBackground_Light = Color(0xffd4d1d6)
val PopUpBackground_Light = Color(0xffbcbabe)
val DropShadow_Light = Color(0xff5e5d5f)
val LightShadow_Light = Color(0xffa5a2a7)
val Gray_Light = Color(0xff2d2a32)
val FadedGray_Light = Color(0xff767477)
val Success_Light = Color(0xff1a4916)
val Success75_Light = Color(0xbf1a4916)
val Error_Light = Color(0xff921e19)
val Error75_Light = Color(0xbf921e19)
val Warning_Light = Color(0xff444118)

/**
 * Enum representation of colors for easier saving
 * @author Elyseia
 */
enum class enumColor {
    BrandOne,
    BrandTwo,
    SecondaryOne,
    SecondaryTwo,
    SecondaryThree,
    Background,
    DarkerBackground,
    PopUpBackground,
    DropShadow,
    LightShadow,
    Gray,
    FadedGray,
    Success,
    Success75,
    Error,
    Error75,
    Warning,
}

/**
 * Turns the enum color name into an actual color value
 * @param color The enum name of a color
 * @return Returns a proper color value
 * @author Elyseia
 */
@Composable
fun resolveEnumColor(color: enumColor): Color = when (color) {
    enumColor.BrandOne -> AppTheme.colors.BrandOne
    enumColor.BrandTwo -> AppTheme.colors.BrandTwo
    enumColor.SecondaryOne -> AppTheme.colors.SecondaryOne
    enumColor.SecondaryTwo -> AppTheme.colors.SecondaryTwo
    enumColor.SecondaryThree -> AppTheme.colors.SecondaryThree
    enumColor.Background -> AppTheme.colors.Background
    enumColor.DarkerBackground -> AppTheme.colors.DarkerBackground
    enumColor.PopUpBackground -> AppTheme.colors.PopUpBackground
    enumColor.DropShadow -> AppTheme.colors.DropShadow
    enumColor.LightShadow -> AppTheme.colors.LightShadow
    enumColor.Gray -> AppTheme.colors.Gray
    enumColor.FadedGray -> AppTheme.colors.FadedGray
    enumColor.Success -> AppTheme.colors.Success
    enumColor.Success75 -> AppTheme.colors.Success75
    enumColor.Error -> AppTheme.colors.Error
    enumColor.Error75 -> AppTheme.colors.Error75
    enumColor.Warning -> AppTheme.colors.Warning
}