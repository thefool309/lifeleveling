package com.lifeleveling.app.navigation

import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.R

@Composable
fun SplashAnimationOverlay(
    backgroundColor: Color = AppTheme.colors.Background
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { contx ->
                ImageView(contx).apply {
                    val drawable = contx.getDrawable(R.drawable.splash_screen)
                    setImageDrawable(drawable)
                    scaleType = ImageView.ScaleType.CENTER_INSIDE
                    drawable?.let {
                        if (it is android.graphics.drawable.Animatable) it.start()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .aspectRatio(1f)
        )
    }
}