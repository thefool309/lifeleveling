package com.lifeleveling.app.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lifeleveling.app.R

@Preview
@Composable
fun TestScreen() {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppTheme.colors.Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HighlightCard(
                modifier = Modifier
                    .fillMaxWidth(),
                height = (screenHeight / 2)
            ) {
                Text(
                    "Testing Page Elements",
                    color = AppTheme.colors.Gray,
                    style = AppTheme.textStyles.HeadingFive,
                    modifier = Modifier.align(Alignment.TopStart)
                )
                Button(
                    onClick = {},
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Text(
                        "Button Test",
                        color = AppTheme.colors.Gray,
                        style = AppTheme.textStyles.Default
                    )
                }
            }
            HighlightCard(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ){
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
            HighlightCard(
                modifier = Modifier,
                width = (screenWidth / 2),
                innerPadding = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.smaller_tree),
                        contentDescription = null,
                        tint = AppTheme.colors.BrandTwo,
                        modifier = Modifier.size(80.dp)
                    )
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
            offsetX = -1.dp,
            offsetY = -1.5.dp,
            spread = .75.dp,
            cornerRadius = (cornerRadius),
        )

        // Content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                //.then(if (width != null) Modifier.fillMaxWidth() else Modifier.wrapContentWidth())
                .then(if (height != null) Modifier.fillMaxHeight() else Modifier.wrapContentWidth())
                .padding(innerPadding),
        ){ content() }
    }
}