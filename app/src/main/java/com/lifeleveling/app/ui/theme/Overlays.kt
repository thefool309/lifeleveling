package com.lifeleveling.app.ui.theme

import android.graphics.drawable.Animatable
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewModelScope
import com.lifeleveling.app.R
import com.lifeleveling.app.data.LocalUserManager
import com.lifeleveling.app.ui.components.CustomButton
import com.lifeleveling.app.ui.components.CustomDialog
import com.lifeleveling.app.ui.components.PopupCard
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

@Composable
fun LevelUpOverlay() {
    val userManager = LocalUserManager.current
    val userState by userManager.uiState.collectAsState()

    if (userState.levelUpFlag) {
        // Dialog handles animation
        Dialog(
            onDismissRequest = { /* Do nothing because handled outside */ },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            // This box dims background and handles clicking outside the popup
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppTheme.colors.DarkerBackground.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                PopupCard(
                    modifier = Modifier
                        .align(Alignment.Center)
                ) {
                    Column(
                        modifier = Modifier,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.level_up),
                            style = AppTheme.textStyles.HeadingFour,
                            color = AppTheme.colors.SecondaryThree
                        )
                        Text(
                            text = stringResource(R.string.new_level, userState.userBase?.level ?: 1),
                            style = AppTheme.textStyles.Default,
                            color = AppTheme.colors.Gray
                        )
                        Text(
                            text = stringResource(R.string.level_coins, userState.levelUpCoins),
                            style = AppTheme.textStyles.Default,
                            color = AppTheme.colors.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CustomButton(
                            onClick = {
                                val user = userManager.uiState.value.userBase
                                userManager.clearLevelUpFlag() // dismisses the overlay message
                                // Launches a small write to firestore of the updated level values
                                userManager.writeLevelUp()
                            }
                        ) {
                            Text(
                                text = stringResource(R.string.awesome),
                                style = AppTheme.textStyles.HeadingSix,
                                color = AppTheme.colors.Background
                            )
                        }
                    }
                }
            }
        }
    }
}