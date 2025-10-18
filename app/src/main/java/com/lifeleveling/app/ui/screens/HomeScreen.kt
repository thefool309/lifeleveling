package com.lifeleveling.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.R
import com.lifeleveling.app.navigation.TempHomeScreen
import com.lifeleveling.app.ui.theme.HighlightCard
import com.lifeleveling.app.ui.theme.LevelAndProgress
import com.lifeleveling.app.ui.theme.LifeExperienceToolTip

@Preview
@Composable
fun HomeScreen() {
    val showLevelTip = remember { mutableStateOf(false) }

    // Main screen pulling everything in 16.dp from edge
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AppTheme.colors.Background)
            .padding(16.dp),
    ) {
        // Central Column for all elements to be stacked into
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Box of Level and Exp
            LevelAndProgress(Modifier.weight(.2f))

            // Coins display
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
            ){
                HighlightCard(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .wrapContentWidth(Alignment.End),
                    wrapContent = true,
                    outerPadding = 0.dp,
                    innerPadding = 8.dp,
                ) {
                    Row(
                        modifier = Modifier
                            .wrapContentWidth(Alignment.End),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.coins, TestUser.coins),
                            style = AppTheme.textStyles.Default,
                            color = AppTheme.colors.Gray
                        )

                        Image(
                            painter = painterResource(R.drawable.coin),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Middle section
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                TempHomeScreen()
            }

            // Bottom health and switch
            Box(
                modifier = Modifier
                    .weight(.2f)
            ) {
                Text("Bottom bar")
            }
        }
    }

    if (showLevelTip.value) {
        LifeExperienceToolTip(showLevelTip)
    }
}