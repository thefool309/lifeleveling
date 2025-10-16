package com.lifeleveling.app.ui.theme

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.screens.TestUser

/*
Components declared in this file
HighlightCard  -  Used for the darker sunk in sections of the screen
CustomButton  -  A shaded button
ShdowedIcon  -  Adds a shadow to icons
PopupCard  -  Shaded card for overlay popup screens
CircleButton  -  A circular button with an icon in the center
ProgressBar  -  The progress bar for different variables
SlidingSwitch  -  The two option switch toggle
Text Sample  -  Inside TestScreen is a sample of shadowed text to use
LevelAndProgress  -  Top bar of level and progress display
 */

// This screen shows the different effects that are within this file
@Preview
@Composable
fun TestScreen() {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    var showPopup by remember { mutableStateOf(false) }
    var switch by remember { mutableStateOf(0) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppTheme.colors.Background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Fixed height size Card Example
                HighlightCard(
                    modifier = Modifier
                        .fillMaxWidth(),
                    height = (screenHeight / 2)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {// =============== Use this example to add shadow to text =============
                        /* I tried to put it in several ways but putting it in manually worked best
                        * Just add the .copy section onto the style to get a drop shadow*/
                        Text(
                            text = "Testing Page Elements",
                            color = AppTheme.colors.SecondaryOne,
                            style = AppTheme.textStyles.HeadingThree.copy(
                                shadow = Shadow(
                                    color = AppTheme.colors.DropShadow,
                                    offset = Offset(3f, 4f),
                                    blurRadius = 6f,
                                )
                            ),
                        )
                        // Progress Bar
                        ProgressBar(
                            progress = .8f,
                        )
                        ProgressBar(
                            progress = .4f,
                            progressColor = AppTheme.colors.SecondaryThree
                        )
                        // Switch
                        SlidingSwitch(
                            selectedIndex = switch,
                            onOptionSelected = { switch = it },
                        )
                        SlidingSwitch(
                            selectedIndex = switch,
                            options = listOf("Day", "Month"),
                            onOptionSelected = { switch = it },
                            textStyle = AppTheme.textStyles.HeadingThree,
                            cornerRadius = 50.dp
                        )
                    }
                    //Custom Button Example
                    CustomButton(
                        onClick = { showPopup = true },
                        modifier = Modifier
                            .align(Alignment.BottomCenter),
                    ) {
                        Text(
                            "Click Me",
                            color = AppTheme.colors.DarkerBackground,
                            style = AppTheme.textStyles.HeadingSix,
                        )
                    }
                }
                // Dynamic height Card adjusts to elements size
                HighlightCard(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Testing Page Elements",
                            color = AppTheme.colors.Gray,
                            style = AppTheme.textStyles.HeadingFive,
                        )
                        Text(
                            "Testing Page Elements",
                            color = AppTheme.colors.Gray,
                            style = AppTheme.textStyles.HeadingFive,
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {// Dynamic height but fixed width
                    HighlightCard(
                        modifier = Modifier,
                        width = (screenWidth / 2),
                        innerPadding = 8.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Same Custom Icon
                            ShadowedIcon(
                                imageVector = ImageVector.vectorResource(R.drawable.person),
                                tint = AppTheme.colors.BrandTwo,
                                modifier = Modifier.size(80.dp),
                                shadowAlpha = 1f,
                                shadowOffset = Offset(5f, 6f)
                            )
                        }
                    }
                    //Example Circle Button
                    CircleButton(
                        onClick = { },
                        imageVector = ImageVector.vectorResource(R.drawable.backpack),
                        size = 64.dp,
                    )
                }
                //Showing popup example
                PopupCard {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Example Popup",
                            color = AppTheme.colors.SecondaryThree,
                            style = AppTheme.textStyles.HeadingFour.copy(
                                shadow = Shadow(
                                    color = AppTheme.colors.DropShadow,
                                    offset = Offset(3f, 4f),
                                    blurRadius = 6f,
                                )
                            )
                        )
                        Text(
                            "Testing popup capabilities and text wrapping. Click me to close.",
                            color = AppTheme.colors.Gray,
                            style = AppTheme.textStyles.Default
                        )
                    }
                }
            }
            //working popup logic
            if (showPopup) {
                Dialog(onDismissRequest = { showPopup = false }) {
                    PopupCard(
                        modifier = Modifier
                            .clickable { showPopup = false }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Example Popup",
                                color = AppTheme.colors.SecondaryThree,
                                style = AppTheme.textStyles.HeadingFour.copy(
                                    shadow = Shadow(
                                        color = AppTheme.colors.DropShadow,
                                        offset = Offset(3f, 4f),
                                        blurRadius = 6f,
                                    )
                                )
                            )
                            Text(
                                "Testing popup capabilities and text wrapping. Click me to close.",
                                color = AppTheme.colors.Gray,
                                style = AppTheme.textStyles.Default
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Darker background Screen
 *@param wrapContent Change this is for the box to be the width of the content
 * @param height Leaving this null will make the card adjust based on content height
 */
@Composable
fun HighlightCard(
    modifier: Modifier = Modifier,
    width: Dp? = null,
    wrapContent: Boolean = false,  // Wraps content
    height: Dp? = null, // If null, dynamic height based on contents
    cornerRadius: Dp = 5.dp,
    outerPadding: Dp = 16.dp,
    innerPadding: Dp = 16.dp,
    backgroundColor: Color = AppTheme.colors.DarkerBackground,
    content: @Composable BoxScope.() -> Unit
) {
    val baseModifier = modifier
        .then(
            when {
                width != null -> Modifier.width(width)
                wrapContent -> Modifier.wrapContentWidth()
                else -> Modifier.fillMaxWidth()
            }
        )
        .then(
            if (height != null) Modifier.height(height) else Modifier.wrapContentHeight()
        )
        .padding(outerPadding)
    Box(
        modifier = baseModifier,
    ) {
        // Top Left Shadow
        InnerShadow(
            modifier = Modifier.matchParentSize(),
            color = backgroundColor,
            shadowColor = AppTheme.colors.DropShadow,
            blur = 4.dp,
            offsetX = 2.dp,
            offsetY = 2.5.dp,
            spread = 1.dp,
            cornerRadius = cornerRadius,
        )
        //Bottom Right Shadow
        InnerShadow(
            modifier = Modifier.matchParentSize(),
            color = Color.Transparent,
            shadowColor = AppTheme.colors.FadedGray,
            blur = 1.5.dp,
            offsetX = (-1).dp,
            offsetY = (-1.5).dp,
            spread = .75.dp,
            cornerRadius = (cornerRadius),
        )

        // Content
        Box(
            modifier = Modifier
                .then(
                    when {
                        wrapContent -> Modifier.wrapContentWidth()
                        else -> Modifier.fillMaxWidth()
                    }
                )
                .then(
                    if (height != null) Modifier.fillMaxHeight()
                    else Modifier.wrapContentWidth())
                .padding(innerPadding),
        ){ content() }
    }
}


/**
 * A custom button shaded inside with a drop shadow.
 * Will grow based on the content inside
 */
@Composable
fun CustomButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 100.dp,
    backgroundColor: Color = AppTheme.colors.Success75,
    horizontalPadding: Dp = 16.dp,
    verticalPadding: Dp = 8.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .wrapContentHeight()
            .wrapContentWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(cornerRadius),
                spotColor = AppTheme.colors.DropShadow,
                ambientColor = AppTheme.colors.DropShadow,
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(Color.White)
            .clickable(onClick = onClick)
    ) {
        // Top left
        InnerShadow(
            modifier = Modifier.matchParentSize(),
            color = backgroundColor,
            shadowColor = AppTheme.colors.Gray,
            blur = 4.dp,
            offsetX = 1.dp,
            offsetY = 1.dp,
            spread = 2.dp,
            cornerRadius = cornerRadius,
        )
        // Bottom right
        InnerShadow(
            modifier = Modifier.matchParentSize(),
            color = Color.Transparent,
            shadowColor = AppTheme.colors.LightShadow,
            blur = 1.5.dp,
            offsetX = (-.5).dp,
            offsetY = (-1).dp,
            spread = .75.dp,
            cornerRadius = (cornerRadius),
        )
        // All over inside
        InnerShadow(
            modifier = Modifier.matchParentSize(),
            color = Color.Transparent,
            shadowColor = AppTheme.colors.DropShadow,
            blur = 2.dp,
            spread = .75.dp,
            cornerRadius = (cornerRadius),
        )
        // Content
        Box(
            modifier = Modifier
                .padding(
                    start = horizontalPadding,
                    end = horizontalPadding,
                    top = verticalPadding,
                    bottom = verticalPadding,
                ),
            contentAlignment = Alignment.Center,
        ) { content() }
    }
}

/**
 * Adds a backdrop to the icon
 * @param imageVector The icon
 * @param tint The color of the icon.
 * @param shadowColor Color of the drop shadow
 * @param shadowOffset Moves the shadow out farther from the icon
 * @param shadowAlpha How dark the shadow will appear
 */
@Composable
fun ShadowedIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    contentDescription: String? = null,
    tint: Color = AppTheme.colors.Background,  // Icon color
    shadowColor: Color = AppTheme.colors.DropShadow,  // Shadow color
    shadowOffset: Offset = Offset(3f,3f),  // Moves the shadow farther out from the icon
    shadowAlpha: Float = .5f  // how dark the shadow should be
) {
    val painter = rememberVectorPainter(image = imageVector)

    Box(
        modifier = modifier
            .drawBehind {5
                drawIntoCanvas { canvas ->
                    canvas.save()
                    canvas.translate(shadowOffset.x, shadowOffset.y)
                    painter.apply {
                        draw(
                            size = this@drawBehind.size,
                            alpha = shadowAlpha,
                            colorFilter = ColorFilter.tint(shadowColor),
                        )
                    }
                    canvas.restore()
                }
            }
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * Shaded popup card for overlays.
 */
@Composable
fun PopupCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 10.dp,
    outerPadding: Dp = 20.dp,
    innerPadding: Dp = 16.dp,
    backgroundColor: Color = AppTheme.colors.Background,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .padding(outerPadding)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = AppTheme.colors.DropShadow,
                spotColor = AppTheme.colors.DropShadow,
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
    ) {
        // Top left
        InnerShadow(
            modifier = Modifier.matchParentSize(),
            color = backgroundColor,
            shadowColor = AppTheme.colors.FadedGray,
            blur = 4.dp,
            offsetX = 1.5.dp,
            offsetY = 2.dp,
            spread = 1.5.dp,
            cornerRadius = cornerRadius,
        )
        // Bottom right
        InnerShadow(
            modifier = Modifier.matchParentSize(),
            color = Color.Transparent,
            shadowColor = AppTheme.colors.FadedGray,
            blur = 2.dp,
            offsetX = (-.5).dp,
            offsetY = (-1).dp,
            spread = .5.dp,
            cornerRadius = (cornerRadius),
        )
        // All over inside
        InnerShadow(
            modifier = Modifier.matchParentSize(),
            color = Color.Transparent,
            shadowColor = AppTheme.colors.DropShadow,
            blur = 2.dp,
            spread = .75.dp,
            cornerRadius = (cornerRadius),
        )

        // Content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) { content() }
    }
}

/**
 * Circular button with a center icon.
 * @param imageVector Icon to display.
 * @param onClick Logic of what the button does.
 * @param size Changes the size of the button, icon scales with.
 * @param backgroundColor Color of the button.
 * @param iconTint Color of the icon inside
 * @param shadowColor Color of the button drop shadow.
 * @param elevation Button drop shadow adjustment.
 */
@Composable
fun CircleButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    imageVector: ImageVector,  // Icon
    contentDescription: String? = null,
    size: Dp = 64.dp, // Button Size, icon will scale with it
    backgroundColor: Color = AppTheme.colors.SecondaryOne, // Button color
    iconTint: Color = AppTheme.colors.DarkerBackground,  // Icon color
    shadowColor: Color = AppTheme.colors.DropShadow,  // Drop shadow color
    elevation: Dp = 8.dp,  // Button drop shadow adjustment
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = elevation,
                shape = CircleShape,
                ambientColor = shadowColor,
                spotColor = shadowColor,
            )
            .clip(CircleShape)
            .background(Color.White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(
                    bounded = true,
                    radius = size / 2
                )
            ) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        // Top left
        InnerShadow(
            modifier = Modifier.matchParentSize(),
            color = backgroundColor,
            shadowColor = AppTheme.colors.Gray,
            blur = 4.dp,
            offsetX = 1.dp,
            offsetY = 1.dp,
            spread = 2.dp,
            cornerRadius = size / 2,
        )
        // Bottom right
        InnerShadow(
            modifier = Modifier.matchParentSize(),
            color = Color.Transparent,
            shadowColor = AppTheme.colors.LightShadow,
            blur = 1.5.dp,
            offsetX = (-.5).dp,
            offsetY = (-1).dp,
            spread = 1.dp,
            cornerRadius = size / 2,
        )
        // All over inside
        InnerShadow(
            modifier = Modifier.matchParentSize(),
            color = Color.Transparent,
            shadowColor = AppTheme.colors.DropShadow,
            blur = 2.dp,
            spread = .5.dp,
            cornerRadius = size / 2,
        )
        ShadowedIcon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = iconTint,
            shadowColor = shadowColor,
            modifier = Modifier.size(size * .75f)
        )
    }
}

/**
 * Progress bar for changing values.
 * @param progress Percentage value for how full the bar will appear.
 * @param backgroundColor Color of hte unfilled space.
 * @param progressColor Color of the filled in space.
 */
@Composable
fun ProgressBar(
    progress: Float,  // Percentage value for how full the bar is
    modifier: Modifier = Modifier,
    backgroundColor: Color = AppTheme.colors.DarkerBackground,  // Unfilled space
    progressColor: Color = AppTheme.colors.SecondaryTwo,  // Filled space
    cornerRadius: Dp = 5.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height (16.dp)
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
    ) {
        // Top Left
        InnerShadow(
            modifier = Modifier.matchParentSize(),
            color = backgroundColor,
            shadowColor = AppTheme.colors.DropShadow,
            blur = 2.dp,
            offsetX = .5.dp,
            offsetY = .5.dp,
            spread = 3.dp,
            cornerRadius = cornerRadius,
        )
        //Bottom Right Shadow
        InnerShadow(
            modifier = Modifier.matchParentSize(),
            color = Color.Transparent,
            shadowColor = AppTheme.colors.FadedGray,
            blur = 1.dp,
            offsetX = (-.5).dp,
            offsetY = (-1).dp,
            spread = 1.dp,
            cornerRadius = (cornerRadius),
        )
        Box(
            modifier = Modifier
                .padding(
                    start = 1.dp,
                    top = 1.5.dp,
                    end = 1.dp,
                    bottom = 1.5.dp,
                    )
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .clip(RoundedCornerShape(cornerRadius - 1.dp))
                .background(progressColor)
        ) {
            // Top left
            InnerShadow(
                modifier = Modifier.matchParentSize(),
                color = Color.Transparent,
                shadowColor = AppTheme.colors.Gray,
                blur = 4.dp,
                offsetX = 1.dp,
                offsetY = 1.dp,
                spread = 2.dp,
                cornerRadius = (cornerRadius - 1.dp),
            )
            // Bottom right
            InnerShadow(
                modifier = Modifier.matchParentSize(),
                color = Color.Transparent,
                shadowColor = AppTheme.colors.LightShadow,
                blur = 1.5.dp,
                offsetX = (-.5).dp,
                offsetY = (-1).dp,
                spread = 1.dp,
                cornerRadius = (cornerRadius - 1.dp),
            )
            // All over inside
            InnerShadow(
                modifier = Modifier.matchParentSize(),
                color = Color.Transparent,
                shadowColor = AppTheme.colors.DropShadow,
                blur = 2.dp,
                spread = .5.dp,
                cornerRadius = (cornerRadius - 1.dp),
            )
        }
    }
}

/**
 * Two option toggle switch.
 * @param options The list of options to appear in order.
 * @param selectedIndex A value that the switch will change based on the selection.
 * @param horizontalPadding How far from edge the text appears.
 * @param verticalPadding Padding above and below text.
 * @param backgroundColor Back of switch color.
 * @param selectedColor Color of the indicator.
 * @param unselectedColor Color of the text for the unselected option.
 * @param textStyle The style of the text inside. Will change the size of the entire switch to fit it.
 * @param insetAmount The offset of the indicator to appear inside the switch.
 * @param extraWidth Adds space to the button to stretch it wider if needed.
 */
@Composable
fun SlidingSwitch(
    options: List<String> = listOf("Light Mode", "Dark Mode"), // Switch options
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit,
    horizontalPadding: Dp = 24.dp,
    verticalPadding: Dp = 12.dp,
    backgroundColor: Color = AppTheme.colors.DarkerBackground,
    selectedColor: Color = AppTheme.colors.BrandOne,
    unselectedColor: Color = AppTheme.colors.Gray,
    cornerRadius: Dp = 24.dp,
    textStyle: TextStyle = AppTheme.textStyles.Default,
    insetAmount: Dp = 5.dp,
    extraWidth: Dp = 32.dp, // Adjust this to adjust how wide the whole button appears
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val textSizes = options.map {
        textMeasurer.measure(
            text = AnnotatedString(it),
            style = textStyle
        ).size
    }
    val textWidths = textSizes.map { with(density) { it.width.toDp() } }
    val textHeights = textSizes.map { with(density) { it.height.toDp() } }

    val maxTextWidth = textWidths.maxOrNull() ?: 0.dp
    val maxTextHeight = textHeights.maxOrNull() ?: 0.dp

    val totalWidth = (maxTextWidth * options.size) + (horizontalPadding * 2) + extraWidth
    val totalHeight = maxTextHeight + (verticalPadding * 2)

    val optionWidth = totalWidth / options.size

    val sliderWidth = optionWidth - (insetAmount * 2)
    val sliderHeight = totalHeight - (insetAmount * 2)

    val animatedOffset by animateDpAsState(
        targetValue = optionWidth * selectedIndex + insetAmount
    )

    Box (
        modifier = Modifier
            .width(totalWidth)
            .height(totalHeight)
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
    ) {
        // Top Left
        InnerShadow(
            modifier = Modifier.matchParentSize(),
            color = backgroundColor,
            shadowColor = AppTheme.colors.DropShadow,
            blur = 2.dp,
            offsetX = .5.dp,
            offsetY = .5.dp,
            spread = 5.dp,
            cornerRadius = cornerRadius,
        )
        //Bottom Right Shadow
        InnerShadow(
            modifier = Modifier.matchParentSize(),
            color = Color.Transparent,
            shadowColor = AppTheme.colors.FadedGray,
            blur = 2.dp,
            offsetX = (-.5).dp,
            offsetY = (-1).dp,
            spread = 1.25.dp,
            cornerRadius = (cornerRadius),
        )

        // Slider
        Box(
            modifier = Modifier
                .offset(x = animatedOffset)
                .width(sliderWidth)
                .height(sliderHeight)
                .align(Alignment.CenterStart)
                .clip(RoundedCornerShape(cornerRadius))
                .background(selectedColor)
        ) {
            // Top left
            InnerShadow(
                modifier = Modifier.matchParentSize(),
                color = selectedColor,
                shadowColor = AppTheme.colors.Gray,
                blur = 4.dp,
                offsetX = 1.dp,
                offsetY = 1.dp,
                spread = 2.dp,
                cornerRadius = cornerRadius,
            )
            // Bottom right
            InnerShadow(
                modifier = Modifier.matchParentSize(),
                color = Color.Transparent,
                shadowColor = AppTheme.colors.LightShadow,
                blur = 1.5.dp,
                offsetX = (-.5).dp,
                offsetY = (-1).dp,
                spread = .75.dp,
                cornerRadius = (cornerRadius),
            )
            // All over inside
            InnerShadow(
                modifier = Modifier.matchParentSize(),
                color = Color.Transparent,
                shadowColor = AppTheme.colors.DropShadow,
                blur = 2.dp,
                spread = .75.dp,
                cornerRadius = (cornerRadius),
            )
        }

        // Text Options
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            options.forEachIndexed { index, option ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { if (index != selectedIndex) onOptionSelected(index) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = option,
                        style = textStyle,
                        color = if (index == selectedIndex) backgroundColor else unselectedColor
                    )
                }
            }
        }
    }
}

@Composable
fun LevelAndProgress(
    modifier: Modifier = Modifier, // add a weight for how much of the page or a size
) {
    var showLevelTip by remember { mutableStateOf(false) }

    Column (
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Level and info icon
        Row(
            modifier = Modifier
                .align(Alignment.Start),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Level Display
            Text(
                text = stringResource(R.string.level, TestUser.level),
                color = AppTheme.colors.SecondaryOne,
                style = AppTheme.textStyles.HeadingThree.copy(
                    shadow = Shadow(
                        color = AppTheme.colors.DropShadow,
                        offset = Offset(3f, 4f),
                        blurRadius = 6f,
                    )
                ),
            )
            // Info Icon
            ShadowedIcon(
                imageVector = ImageVector.vectorResource(R.drawable.info),
                tint = AppTheme.colors.FadedGray,
                modifier = Modifier
                    .size(20.dp)
                    .offset(y = 9.74.dp)
                    .clickable {
                        if(!showLevelTip) {showLevelTip = true} else {showLevelTip = false}
                    }
            )
        }

        // Progress Bar
        ProgressBar(
            progress = TestUser.currentExp.toFloat() / TestUser.expToLevel
        )

        // Experience Display
        Text(
            text = stringResource(R.string.exp_display, TestUser.currentExp, TestUser.expToLevel),
            color = AppTheme.colors.Gray,
            style = AppTheme.textStyles.Default,
            modifier = Modifier.align(Alignment.End)
        )
    }
}