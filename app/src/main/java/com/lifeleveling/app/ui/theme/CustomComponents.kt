package com.lifeleveling.app.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun TestScreen() {
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
                fixedHeight = 200.dp
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
        }
    }
}

//Darker Background Cards
@Composable
fun HighlightCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 5.dp,
    outerPadding: Dp = 16.dp,
    innerPadding: Dp = 16.dp,
    backgroundColor: Color = AppTheme.colors.DarkerBackground,
    fixedHeight: Dp? = null, // If null, dynamic height
    content: @Composable BoxScope.() -> Unit
) {
    val baseModifier = modifier
        .fillMaxWidth()
        .then(
            if (fixedHeight != null) Modifier.height(fixedHeight) else Modifier
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
                .then(if (fixedHeight != null) Modifier.fillMaxHeight() else Modifier.wrapContentWidth())
                .padding(innerPadding),
        ){ content() }
    }
}