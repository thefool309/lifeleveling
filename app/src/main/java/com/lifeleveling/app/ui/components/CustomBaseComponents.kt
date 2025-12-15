package com.lifeleveling.app.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.ui.theme.InnerShadow
import com.lifeleveling.app.ui.theme.resolveEnumColor

/*
Components declared in this file
HighlightCard  -  Used for the darker sunk in sections of the screen
CustomButton  -  A shaded button
ShadowedIcon  -  Adds a shadow to icons
PopupCard  -  Shaded card for overlay popup screens
CircleButton  -  A circular button with an icon in the center
ProgressBar  -  The progress bar for different variables
SlidingSwitch  -  The two option switch toggle
Text Sample  -  Inside TestScreen is a sample of shadowed text to use
CustomCheckbox  -  Precolored and laid out checkbox item
CustomDialog  -  CustomDialog call with saved preferences. Can be adjusted to hold text or buttons
DropDownTextMenu  -  Menu for string lists
DropDownReminderMenu  -  Dropdown designed to show the icon and text of reminder lists
SeparatorLine  -  Easy call of the custom separator
CustomTextField  -  A basic outline textField just will all the specifics already saved to make them uniform
ScrollFadeEdges  -  Applies a dark shading at the top and bottom if the screen or element can be scrolled in that direction
 */

// This screen shows the different effects that are within this file
@Preview
@OptIn(ExperimentalMaterial3Api::class)
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
                                    offset = Offset(2f, 2f),
                                    blurRadius = 2f,
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
                        width = 255.dp,
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

                    var checked by remember { mutableStateOf(true) }
                    // Check box
                    CustomCheckbox(
                        checked = checked,
                        onCheckedChange = { checked = it },
                    )

                    //Example Circle Button
                    CircleButton(
                        onClick = { },
                        imageVector = ImageVector.vectorResource(R.drawable.backpack),
                        size = 64.dp,
                    )
                }

                // dropdown example
                val options = listOf(
                    "One", "Two", "Three", "Four", "Five", "Six", "Seven",
                )
                var selectedIndex by remember { mutableStateOf(0) }
                val expanded = remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                ){
                    DropDownTextMenu(
                        options = options,
                        selectedIndex = selectedIndex,
                        onSelectedChange = { selectedIndex = it },
                        expanded = expanded,
                    )
                }

                // Testing Text Fields
                Box(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                ){
                    var text by remember { mutableStateOf("") }
                    CustomTextField(
                        value = text,
                        onValueChange = { text = it },
                    )
                }

//                //Showing popup example
//                PopupCard {
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        Text(
//                            "Example Popup",
//                            color = AppTheme.colors.SecondaryThree,
//                            style = AppTheme.textStyles.HeadingFour.copy(
//                                shadow = Shadow(
//                                    color = AppTheme.colors.DropShadow,
//                                    offset = Offset(2f, 2f),
//                                    blurRadius = 2f,
//                                )
//                            )
//                        )
//                        Text(
//                            "Testing popup capabilities and text wrapping. Click me to close.",
//                            color = AppTheme.colors.Gray,
//                            style = AppTheme.textStyles.Default
//                        )
//                    }
//                }
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
                                        offset = Offset(2f, 2f),
                                        blurRadius = 2f,
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
 * A weight can be passed in to adjust the width
 * @param width Sets a specific width. Only use for precision
 * @param enabled Sets a boolean condition of if the button is clickable
 */
@Composable
fun CustomButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    cornerRadius: Dp = 100.dp,
    backgroundColor: Color = AppTheme.colors.Success75,
    disabledColor: Color = AppTheme.colors.FadedGray,
    horizontalPadding: Dp = 16.dp,
    verticalPadding: Dp = 8.dp,
    width: Dp? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val buttonColor = if (enabled) backgroundColor else disabledColor

    val customModifier = modifier
        .wrapContentHeight()
        .then(
            if (width != null) Modifier.width(width) else Modifier.wrapContentWidth()
        )
        .shadow(
            elevation = 8.dp,
            shape = RoundedCornerShape(cornerRadius),
            spotColor = AppTheme.colors.DropShadow,
            ambientColor = AppTheme.colors.DropShadow,
        )
        .clip(RoundedCornerShape(cornerRadius))
        .background(Color.White)
        .then(
            if (enabled) Modifier.clickable(onClick = onClick) else Modifier,
        )

    Box(
        modifier = customModifier,
        contentAlignment = Alignment.Center,
    ) {
        // Top left
        InnerShadow(
            modifier = Modifier.matchParentSize(),
            color = buttonColor,
            shadowColor = AppTheme.colors.LightShadow,
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
            spread = 1.5.dp,
            cornerRadius = (cornerRadius),
        )
        // All over inside
        InnerShadow(
            modifier = Modifier.matchParentSize(),
            color = Color.Transparent,
            shadowColor = AppTheme.colors.DropShadow,
            blur = 2.dp,
            spread = .5.dp,
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
    shadowOffset: Offset = Offset(2f,2f),  // Moves the shadow farther out from the icon
    shadowAlpha: Float = .5f  // how dark the shadow should be
) {
    val painter = rememberVectorPainter(image = imageVector)

    Box(
        modifier = modifier
            .drawBehind {
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
    size: Dp = 48.dp, // Button Size, icon will scale with it
    backgroundColor: Color = AppTheme.colors.SecondaryOne, // Button color
    iconTint: Color = AppTheme.colors.DarkerBackground,  // Icon color
    shadowColor: Color = AppTheme.colors.DropShadow,  // Drop shadow color
    elevation: Dp = 16.dp,  // Button drop shadow adjustment
    iconSizeModifier: Float = .75f
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
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(
                    bounded = true,
                    radius = size / 2
                )
            ) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Image(
            modifier = Modifier
                .clip(CircleShape)
                .matchParentSize(),
            painter = painterResource(R.drawable.circle_button_innerlight),
            contentDescription = null,
        )
        ShadowedIcon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = iconTint,
            shadowColor = shadowColor,
            modifier = Modifier.size(size * iconSizeModifier)
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
    // Outer Box
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
            spread = 2.dp,
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
        // Inner bar
        Box(
            modifier = Modifier
                .padding(
                    start = 1.5.dp,
                    top = 1.5.dp,
                    end = 1.5.dp,
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
                shadowColor = AppTheme.colors.LightShadow,
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
                spread = .5.dp,
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
    modifier: Modifier = Modifier,
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
        modifier = modifier
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
            spread = 3.dp,
            cornerRadius = cornerRadius,
        )
        //Bottom Right Shadow
        InnerShadow(
            modifier = Modifier.matchParentSize(),
            color = Color.Transparent,
            shadowColor = AppTheme.colors.FadedGray,
            blur = 1.5.dp,
            offsetX = (-.5).dp,
            offsetY = (-1).dp,
            spread = 1.dp,
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
                shadowColor = AppTheme.colors.LightShadow,
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
                blur = 2.dp,
                offsetX = (-.5).dp,
                offsetY = (-1).dp,
                spread = 1.25.dp,
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

/**
 * Custom Checkbox
 * @param checked The bool that box's state depends on
 * @param onCheckedChange What to do when it is clicked, defaults to changing the bool state
 * @param size Adjusts size of box and icon inside
 * @param mainColor Outline of unchecked area and fill of checked box
 * @param checkColor Color of the checkmark inside
 */
@Composable
fun CustomCheckbox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    size: Dp = 20.dp,
    cornerRadius: Dp = 4.dp,
    mainColor: Color = AppTheme.colors.SecondaryOne,
    checkColor: Color = AppTheme.colors.DarkerBackground
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                if (checked) mainColor else Color.Transparent
            )
            .clickable { onCheckedChange(!checked) }
            .border(2.dp, mainColor,RoundedCornerShape(cornerRadius)),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = checkColor,
                modifier = Modifier.size(size)
            )
        }
    }
}

/**
 * CustomDialog will pop up a window in the middle of the screen that will dismiss when clicking outside of it
 * @param toShow The boolean for if the window shows or not.
 * @param dismissOnInsideClick If the contents is NOT interactive, leave this as true for the window to disappear if clicked on. False will allow buttons to be on the inside.
 * @param dismissOnOutsideClick Controls if the window will close if you click outside the box
 */
@Composable
fun CustomDialog(
    toShow: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    dismissOnInsideClick: Boolean = true,
    dismissOnOutsideClick: Boolean = true,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = { toShow.value = false },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        // This box dims background and handles clicking outside the popup
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(AppTheme.colors.DarkerBackground.copy(alpha = 0.1f))
                .clickable(enabled = dismissOnOutsideClick) { toShow.value = false }
        ) {
            PopupCard(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clickable(enabled = dismissOnInsideClick) { toShow.value = false },
            ) {
                content()
            }
        }
    }
}

/**
 * Creates a dropdown menu for string options
 * @param options List of strings for options
 * @param selectedIndex Variable for storing the selected option index
 * @param onSelectedChange What to do when an option is selected. Pass in { selectedIndex = it } for selected Index to be updated
 * @param expanded The boolean that controls if the menu shows or not
 * @param readOnly Controls if the inner text field can be typed into or only read
 * @param arrowSize Changes the size of the arrow on the dropdown box
 * @param textStyle Sets the style of all the text
 * @param textColor Sets the color of all the text and the arrow
 * @param backgroundMainColor Main color of the text field. Is also used in the menu as one of the alternating colors
 * @param accentColor The second color of the alternating colors in the menu
 * @param outlineColor Color of the text field outline
 * @param selectedBackground A highlight to the option that is currently selected
 * @param selectedText A highlight to the text of the currently selected option
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownTextMenu(
    modifier: Modifier = Modifier,
    options: List<String>,
    selectedIndex: Int,
    onSelectedChange: (Int) -> Unit,
    expanded: MutableState<Boolean>,
    readOnly: Boolean = true,
    arrowSize: Dp = 20.dp,
    textStyle: TextStyle = AppTheme.textStyles.Default,
    textColor: Color = AppTheme.colors.Gray,
    backgroundMainColor: Color = AppTheme.colors.Background,
    accentColor: Color = AppTheme.colors.PopUpBackground,
    outlineColor: Color = AppTheme.colors.FadedGray,
    selectedBackground: Color = AppTheme.colors.SecondaryTwo.copy(alpha = .3f),
    selectedText: Color = AppTheme.colors.Gray,
) {
    Box(
        modifier = modifier
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded.value,
            onExpandedChange = { expanded.value = !expanded.value },
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true)
                    .fillMaxWidth()
                    .widthIn(max = 300.dp),
                value = options.getOrNull(selectedIndex) ?: "",
                onValueChange = { },
                readOnly = readOnly,
                trailingIcon = {
                    ShadowedIcon(
                        imageVector = ImageVector.vectorResource(R.drawable.left_arrow),
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier
                            .rotate(if (expanded.value) 90f else 270f)
                            .size(arrowSize)
                    )
                },
                textStyle = textStyle,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = backgroundMainColor,
                    unfocusedContainerColor = backgroundMainColor,
                    focusedBorderColor = outlineColor,
                    unfocusedBorderColor = outlineColor,
                    disabledBorderColor = outlineColor,
                    cursorColor = Color.Transparent,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                )
            )
            ExposedDropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false },
                modifier = Modifier
                    .shadow(12.dp, RoundedCornerShape(8.dp))
                    .border(1.dp, outlineColor, RoundedCornerShape(8.dp))
                    .background(backgroundMainColor)
            ) {
                options.forEachIndexed { index, option ->
                    val isSelected = index == selectedIndex
                    val isEven = index % 2 == 0
                    val backgroundColor =
                        if (isSelected) selectedBackground
                        else if (isEven) backgroundMainColor
                        else accentColor
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                color = if (index == selectedIndex) selectedText else textColor,
                                style = textStyle
                            )
                        },
                        onClick = {
                            onSelectedChange(index)
                            expanded.value = false
                        },
                        modifier = Modifier
                            .background(backgroundColor)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

/**
 * Creates a dropdown menu for string options
 * @param options List of reminders for options
 * @param selectedIndex Variable for storing the selected option index
 * @param onSelectedChange What to do when an option is selected. Pass in { selectedIndex = it } for selected Index to be updated
 * @param expanded The boolean that controls if the menu shows or not
 * @param readOnly Controls if the inner text field can be typed into or only read
 * @param arrowSize Changes the size of the arrow on the dropdown box
 * @param textStyle Sets the style of all the text
 * @param textColor Sets the color of all the text and the arrow
 * @param backgroundMainColor Main color of the text field. Is also used in the menu as one of the alternating colors
 * @param accentColor The second color of the alternating colors in the menu
 * @param outlineColor Color of the text field outline
 * @param selectedBackground A highlight to the option that is currently selected
 * @param selectedText A highlight to the text of the currently selected option,
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownReminderMenu(
    modifier: Modifier = Modifier,
    options: List<Reminder>,
    selectedIndex: Int,
    onSelectedChange: (Int) -> Unit,
    expanded: MutableState<Boolean>,
    readOnly: Boolean = true,
    arrowSize: Dp = 20.dp,
    textStyle: TextStyle = AppTheme.textStyles.Default,
    textColor: Color = AppTheme.colors.Gray,
    backgroundMainColor: Color = AppTheme.colors.Background,
    accentColor: Color = AppTheme.colors.PopUpBackground,
    outlineColor: Color = AppTheme.colors.FadedGray,
    selectedBackground: Color = AppTheme.colors.SecondaryTwo.copy(alpha = .3f),
    selectedText: Color = AppTheme.colors.Gray,
) {
    Box(
        modifier = modifier
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded.value,
            onExpandedChange = { expanded.value = !expanded.value },
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true)
                    .fillMaxWidth()
                    .widthIn(max = 300.dp),
                value = options.getOrNull(selectedIndex)?.name ?: "",
                onValueChange = { },
                readOnly = readOnly,
                leadingIcon = {
                    val item = options[selectedIndex]
                    ShadowedIcon(
                        modifier = Modifier.size(arrowSize),
                        imageVector = ImageVector.vectorResource(item.icon),
                        tint = item.color?.let { resolveEnumColor(it) } ?: Color.Unspecified
                    )
                },
                trailingIcon = {
                    ShadowedIcon(
                        imageVector = ImageVector.vectorResource(R.drawable.left_arrow),
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier
                            .rotate(if (expanded.value) 90f else 270f)
                            .size(arrowSize)
                    )
                },
                textStyle = textStyle,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = backgroundMainColor,
                    unfocusedContainerColor = backgroundMainColor,
                    focusedBorderColor = outlineColor,
                    unfocusedBorderColor = outlineColor,
                    cursorColor = Color.Transparent,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                )
            )

            ExposedDropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false },
                modifier = Modifier
                    .shadow(12.dp, RoundedCornerShape(8.dp))
                    .border(1.dp, outlineColor, RoundedCornerShape(8.dp))
                    .background(backgroundMainColor)
            ) {
                options.forEachIndexed { index, reminder ->
                    val alreadyInStreaks = TestUser.weeklyStreaks.any { it.reminder.id == reminder.id } ||
                            TestUser.weeklyStreaks.any { it.reminder.id == reminder.id }

                    if (!alreadyInStreaks) {
                        val isSelected = index == selectedIndex
                        val isEven = index % 2 == 0

                        val backgroundColor =
                            if (isSelected) selectedBackground
                            else if (isEven) backgroundMainColor
                            else accentColor

                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    ShadowedIcon(
                                        modifier = Modifier.size(arrowSize),
                                        imageVector = ImageVector.vectorResource(reminder.icon),
                                        tint = reminder.color?.let { resolveEnumColor(it) } ?: Color.Unspecified
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = reminder.name,
                                        style = textStyle,
                                        color = if (isSelected) selectedText else textColor,
                                    )
                                }
                            },
                            onClick = {
                                onSelectedChange(index)
                                expanded.value = false
                            },
                            modifier = Modifier
                                .background(backgroundColor)
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

/**
 * A thin line with a dot at either end for separating UI sections
 */
@Composable
fun SeparatorLine(
    modifier: Modifier = Modifier,
    color: Color = AppTheme.colors.FadedGray
) {
    // Separator
    Icon(
        modifier = modifier,
        imageVector = ImageVector.vectorResource(R.drawable.separator_line),
        tint = color,
        contentDescription = null,
    )
}

/**
 * Text Field object with design aspects applied
 * @param value A mutable string to store what is entered in
 * @param onValueChange What to do with the input. Suggested to us { value = it }
 * @param singleLine If it only accepts a single line of writing
 * @param textStyle Style of all the text
 * @param textColor Color of the written text
 * @param cursorColor Color of the cursor
 * @param placeholderTextColor Color of the text displayed if the box is empty
 * @param placeholderText Text to display if the box is empty
 * @param inputFilter Allows the use of filters on what can be entered. Examples: { it.all { char -> char.isDigit() } } is only numbers. .isLetter() is only letters. .isWhitespace() allows spaces
 * @param backgroundColor Color of the text box inside the outline
 * @param outlineColor Color of the outline of the box
 */
@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true,
    textStyle: TextStyle = AppTheme.textStyles.Default,
    textColor: Color = AppTheme.colors.Gray,
    cursorColor: Color = AppTheme.colors.FadedGray,
    placeholderTextColor: Color = AppTheme.colors.FadedGray,
    placeholderText: String = "Enter Text...",
    inputFilter: ((String) -> Boolean)? = null,
    backgroundColor: Color = AppTheme.colors.Background,
    outlineColor: Color = AppTheme.colors.FadedGray,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    supportingUnit: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    OutlinedTextField(
        modifier = modifier
            .widthIn(max = 450.dp)
            .fillMaxWidth(),
        value = value,
        onValueChange = {input ->
            if (inputFilter == null || inputFilter(input)) {
                onValueChange(input)
            }
        },
        singleLine = singleLine,
        textStyle = textStyle.copy(color = textColor),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = backgroundColor,
            unfocusedContainerColor = backgroundColor,
            focusedBorderColor = outlineColor,
            unfocusedBorderColor = outlineColor,
            cursorColor = cursorColor,
            disabledBorderColor = outlineColor,
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
        ),
        placeholder = {
            Text(
                text = placeholderText,
                color = placeholderTextColor,
                style = textStyle,
            )
        },
        keyboardOptions = keyboardOptions,
        supportingText = if (supportingUnit != null) { { supportingUnit.invoke() } } else null,
        visualTransformation = visualTransformation,
    )
}

/**
 * Adds a dark gradient at the top or bottom of a scrollable column if it can be scrolled up or down.
 * Needs to be placed inside the box that contains the column, after the column.
 * @param scrollState Pass in a scrollState variable that derives from the column
 * @param topFadeHeight Controls the size of the top faded bit
 * @param bottomFadeHeight Controls the size of the bottom faded section
 * @param fadeColor The color in the gradient
 */
@Composable
fun ScrollFadeEdges(
    scrollState: ScrollState,
    topFadeHeight: Dp = 28.dp,
    bottomFadeHeight: Dp = 28.dp,
    fadeColor: Color = AppTheme.colors.DropShadow.copy(alpha = .5f)
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        // Top Fade if scrollable
        if (scrollState.value > 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(topFadeHeight)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                fadeColor,
                                Color.Transparent,
                            )
                        )
                    )
                    .align(Alignment.TopCenter)
            )
        }

        // Bottom fade if scrollable
        val maxScroll = scrollState.maxValue
        if (scrollState.value < maxScroll) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(bottomFadeHeight)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                fadeColor,
                            )
                        )
                    )
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

/**
 * Adds a dark gradient at the top or bottom of a scrollable LAZY column if it can be scrolled up or down.
 * Needs to be placed inside the box that contains the column, after the column.
 * @param gridState Pass in a scrollState variable that derives from the lazy column
 * @param topFadeHeight Controls the size of the top faded bit
 * @param bottomFadeHeight Controls the size of the bottom faded section
 * @param fadeColor The color in the gradient
 */
@Composable
fun LazyColumnFadeEdges(
    gridState: LazyGridState,
    topFadeHeight: Dp = 24.dp,
    bottomFadeHeight: Dp = 24.dp,
    fadeColor: Color = AppTheme.colors.DropShadow.copy(alpha = .5f)
) {
    val canScrollUp = gridState.firstVisibleItemIndex > 0 || gridState.firstVisibleItemScrollOffset > 0
    val layoutInfo = gridState.layoutInfo
    val visible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
    val total = layoutInfo.totalItemsCount
    val canScrollDown = visible < total - 1

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Top Fade if scrollable
        if (canScrollUp) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(topFadeHeight)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                fadeColor,
                                Color.Transparent,
                            )
                        )
                    )
                    .align(Alignment.TopCenter)
            )
        }

        // Bottom fade if scrollable
        if (canScrollDown) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(bottomFadeHeight)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                fadeColor,
                            )
                        )
                    )
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

/**
 * Creates a dropdown menu for string options
 * @param colors list of color choices
 * @param selectedIndex Variable for storing the selected option index
 * @param onSelectedChange What to do when an option is selected. Pass in { selectedIndex = it } for selected Index to be updated
 * @param expanded The boolean that controls if the menu shows or not
 * @param readOnly Controls if the inner text field can be typed into or only read
 * @param arrowSize Changes the size of the arrow on the dropdown box
 * @param textColor Sets the color of all the text and the arrow
 * @param backgroundMainColor Main color of the text field. Is also used in the menu as one of the alternating colors
 * @param accentColor The second color of the alternating colors in the menu
 * @param outlineColor Color of the text field outline
 * @param selectedBackground A highlight to the option that is currently selected
 * @author sgcfsu1993 (Stephen C.) (Made from Elyseia's original DropDownTextMenu)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownColorMenu(
    modifier: Modifier = Modifier,
    colors: List<Color>,
    selectedIndex: Int,
    onSelectedChange: (Int) -> Unit,
    expanded: MutableState<Boolean>,
    readOnly: Boolean = true,
    arrowSize: Dp = 20.dp,
    backgroundMainColor: Color = AppTheme.colors.Background,
    accentColor: Color = AppTheme.colors.PopUpBackground,
    outlineColor: Color = AppTheme.colors.FadedGray,
    selectedBackground: Color = AppTheme.colors.SecondaryTwo.copy(alpha = .3f),
    textColor: Color = AppTheme.colors.Gray,
) {
    Box(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded.value,
            onExpandedChange = { expanded.value = !expanded.value },
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true)
                    .fillMaxWidth()
                    .widthIn(max = 300.dp),
                value = "",
                onValueChange = { },
                readOnly = readOnly,
                leadingIcon = {

                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(colors[selectedIndex], shape = CircleShape)
                    )
                },
                trailingIcon = {
                    ShadowedIcon(
                        imageVector = ImageVector.vectorResource(R.drawable.left_arrow),
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier
                            .rotate(if (expanded.value) 90f else 270f)
                            .size(arrowSize)
                    )
                },
                textStyle = AppTheme.textStyles.Default,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = backgroundMainColor,
                    unfocusedContainerColor = backgroundMainColor,
                    focusedBorderColor = outlineColor,
                    unfocusedBorderColor = outlineColor,
                    cursorColor = Color.Transparent,
                )
            )

            ExposedDropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false },
                modifier = Modifier
                    .shadow(12.dp, RoundedCornerShape(8.dp))
                    .border(1.dp, outlineColor, RoundedCornerShape(8.dp))
                    .background(backgroundMainColor)
            ) {
                colors.forEachIndexed { index, color ->
                    val isSelected = index == selectedIndex
                    val backgroundColor =
                        if (isSelected) selectedBackground else if (index % 2 == 0) backgroundMainColor else accentColor

                    DropdownMenuItem(
                        text = {

                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(color, shape = CircleShape)
                            )
                        },
                        onClick = {
                            onSelectedChange(index)
                            expanded.value = false
                        },
                        modifier = Modifier
                            .background(backgroundColor)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}