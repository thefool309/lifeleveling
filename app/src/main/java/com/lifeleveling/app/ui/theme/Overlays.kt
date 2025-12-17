package com.lifeleveling.app.ui.theme

import android.graphics.drawable.Animatable
import android.widget.ImageView
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.components.CustomButton
import com.lifeleveling.app.ui.components.PopupCard

/**
 * The display of the application's splash screen.
 * @param backgroundColor The color that should be behind the splash screen
 *
 * @author Elyseia
 */
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

/**
 * An overlay screen that will fade the current screen displayed and show a loading circle when the loading flag is toggled
 * @param backgroundColor The background color that gives the 'faded' look to the UI behind it.
 * @param progressColor The color of the spinner wheel.
 *
 * @author Elyseia, fdesouza1992
 */
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

/**
 * An overlay that is triggered when the user levels up.
 * Will display a congratulations message, the new level, and how many coins were awarded.
 * Has a button that dismisses the message and triggers a small write to firestore of the updated information.
 * Takes in parameters from the state to avoid recollecting the state on render
 * @param level Pass in the state handling of the user's level
 * @param levelUpCoins Pass in the state handling of the user's levelUpCoins
 * @param onDismiss Pass in logic for what happens when the window is dismissed.
 * @author Elyseia
 */
@Composable
fun LevelUpOverlay(
    level: Long,
    levelUpCoins: Long,
    onDismiss: () -> Unit,
) {
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
                        text = stringResource(R.string.new_level, level),
                        style = AppTheme.textStyles.Default,
                        color = AppTheme.colors.Gray
                    )
                    Text(
                        text = stringResource(R.string.level_coins, levelUpCoins),
                        style = AppTheme.textStyles.Default,
                        color = AppTheme.colors.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomButton(
                        onClick = { onDismiss() },
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