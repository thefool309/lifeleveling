package com.lifeleveling.app.ui.theme

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
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lifeleveling.app.R

// This screen shows the different effects that are within this file
@Preview
@Composable
fun TestScreen() {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    var showPopup by remember { mutableStateOf(false) }

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
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {// =============== Use this example to add shadow to text =============
                        /* I tried to put it in several ways but putting it in manually worked best
                        * Just add the .copy section onto the style to get a drop shadow*/
                        Text(
                            "Testing Page Elements",
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
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { showPopup = false }
                    ) {
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
                }
            }
        }
    }
}

//Darker Background Cards
@Composable
fun HighlightCard(
    modifier: Modifier = Modifier,
    width: Dp? = null,
    height: Dp? = null, // If null, dynamic height
    cornerRadius: Dp = 5.dp,
    outerPadding: Dp = 16.dp,
    innerPadding: Dp = 16.dp,
    backgroundColor: Color = AppTheme.colors.DarkerBackground,
    content: @Composable BoxScope.() -> Unit
) {
    val baseModifier = modifier
        .then(
            if (width != null) Modifier.width(width) else Modifier.fillMaxWidth()
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
                .fillMaxWidth()
                .then(if (height != null) Modifier.fillMaxHeight() else Modifier.wrapContentWidth())
                .padding(innerPadding),
        ){ content() }
    }
}

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

@Composable
fun ShadowedIcon(
    imageVector: ImageVector,
    contentDescription: String? = null,
    tint: Color = AppTheme.colors.Background,
    shadowColor: Color = AppTheme.colors.DropShadow,
    modifier: Modifier = Modifier,
    shadowOffset: Offset = Offset(3f,3f),
    shadowAlpha: Float = .5f
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

@Composable
fun CircleButton(
    onClick: () -> Unit,
    imageVector: ImageVector,
    contentDescription: String? = null,
    size: Dp = 64.dp, // Button Size
    backgroundColor: Color = AppTheme.colors.SecondaryOne,
    iconTint: Color = AppTheme.colors.DarkerBackground,
    shadowColor: Color = AppTheme.colors.DropShadow,
    elevation: Dp = 8.dp,
    modifier: Modifier = Modifier,
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

@Composable
fun ProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height (16.dp),
    backgroundColor: Color = AppTheme.colors.DarkerBackground,
    progressColor: Color = AppTheme.colors.SecondaryTwo,
    cornerRadius: Dp = 5.dp
) {
    Box(
        modifier = modifier
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

