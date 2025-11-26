package com.lifeleveling.app.ui.theme

import android.graphics.drawable.Animatable
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.lifeleveling.app.R

@Composable
fun SplashAnimationOverlay(
    backgroundColor: Color = AppTheme.colors.Background
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { context ->
                ImageView(context).apply {
                    val drawable = context.getDrawable(R.drawable.splash_screen)
                    setImageDrawable(drawable)
                    scaleType = ImageView.ScaleType.CENTER_INSIDE
                    drawable?.let {
                        if (it is Animatable) it.start()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(1f)
        )
    }
}

@Composable
fun LoadingOverlay(
    modifier: Modifier = Modifier,
    backgroundColor: Color = AppTheme.colors.DarkerBackground.copy(alpha = 0.1f),
    progressColor: Color = AppTheme.colors.BrandOne.copy(alpha = 0.5f),
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = progressColor,
            strokeWidth = 4.dp
        )
    }
}