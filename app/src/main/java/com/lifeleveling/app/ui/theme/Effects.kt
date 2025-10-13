package com.lifeleveling.app.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun InnerShadow(
    modifier: Modifier = Modifier,
    color: Color = AppTheme.colors.DarkerBackground,
    cornerRadius: Dp = 5.dp,
    shadowColor: Color = AppTheme.colors.DropShadow,
    blur: Dp = 1.dp,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    spread: Dp = 1.dp,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .clip(shape)
            .background(color)
            .drawWithCache {
                val blurPx = blur.toPx()
                val spreadPx = spread.toPx()
                val offsetXPx = offsetX.toPx()
                val offsetYPx = offsetY.toPx()

                val innerRect = Rect(
                    left = spreadPx,
                    top = spreadPx,
                    right = size.width - spreadPx,
                    bottom = size.height - spreadPx
                )

                val outerPath = Path().apply {
                    addRect(Rect(0f, 0f, size.width, size.height))
                }
                val innerPath = Path().apply {
                    addRoundRect(
                        RoundRect(
                            innerRect,
                            CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
                        )
                    )
                }

                val shadowPath = Path().apply {
                    op(outerPath, innerPath, PathOperation.Difference)
                }

                val paint = Paint().apply {
                    this.color = shadowColor
                    this.asFrameworkPaint().apply {
                        maskFilter = android.graphics.BlurMaskFilter(
                            blurPx,
                            android.graphics.BlurMaskFilter.Blur.NORMAL
                        )
                    }
                }

                onDrawWithContent {
                    drawContent()
                    drawIntoCanvas { canvas ->
                        withTransform({
                            translate(offsetXPx, offsetYPx)
                        }) {
                            canvas.drawPath(shadowPath, paint)
                        }
                    }
                }
            },
        content = content
    )
}