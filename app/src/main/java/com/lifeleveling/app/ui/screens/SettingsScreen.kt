package com.lifeleveling.app.ui.screens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.ui.components.HighlightCard
import com.lifeleveling.app.ui.components.ShadowedIcon
import com.lifeleveling.app.ui.components.SlidingSwitch
import com.lifeleveling.app.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.lifeleveling.app.ui.components.ScrollFadeEdges
import com.lifeleveling.app.ui.components.SeparatorLine
import com.lifeleveling.app.ui.components.CustomButton
import com.lifeleveling.app.ui.components.CustomDialog


@Composable
fun SettingScreen(
    navController: NavController? = null,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onSignOut: () -> Unit ={},
    onResetLifePoints: () -> Unit = {},
) {
    val scrollState = rememberScrollState()

    val showDeleteDialog = remember { mutableStateOf(false) }
    val showResetLifePointsDialog = remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = AppTheme.colors.Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.settings),
                color = AppTheme.colors.SecondaryOne,
                style = AppTheme.textStyles.HeadingThree.copy(
                    shadow = Shadow(
                        color = AppTheme.colors.DropShadow,
                        offset = Offset(2f, 2f),
                        blurRadius = 2f,
                    )
                ),
            )

            HighlightCard(
                modifier = Modifier
                    .fillMaxWidth(),
                outerPadding = 0.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        SlidingSwitch(
                            options = listOf(stringResource(R.string.darkMode), stringResource(R.string.lightMode)),
                            selectedIndex = if (isDarkTheme) 0 else 1,
                            onOptionSelected = { index ->
                                val newIsDark = index == 0
                                onThemeChange(newIsDark)
                            },
                            horizontalPadding = 12.dp,
                            backgroundColor = AppTheme.colors.DarkerBackground,
                            selectedColor = AppTheme.colors.BrandOne,
                            unselectedColor = AppTheme.colors.Gray,
                            cornerRadius = 32.dp,
                            textStyle = AppTheme.textStyles.HeadingSix,
                            extraWidth = 52.dp,

                            )
                    }

                    SeparatorLine()

                    // Notification Settings
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        ShadowedIcon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.bell),
                            tint = AppTheme.colors.BrandTwo,
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.CenterVertically)

                        )
                        Text(
                            text = stringResource(R.string.notificationSettings),
                            color = AppTheme.colors.Gray,
                            style = AppTheme.textStyles.HeadingSix.copy(
                                shadow = Shadow(
                                    color = AppTheme.colors.DropShadow,
                                    offset = Offset(2f, 2f),
                                    blurRadius = 2f,
                                )
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .clickable { navController?.navigate("notifications") }

                        )
                    }

                    SeparatorLine()

                    // All Reminders
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        ShadowedIcon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.bars_solid_full),
                            tint = AppTheme.colors.BrandOne,
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.CenterVertically)

                        )
                        Text(
                            text = stringResource(R.string.my_reminders),
                            color = AppTheme.colors.Gray,
                            style = AppTheme.textStyles.HeadingSix.copy(
                                shadow = Shadow(
                                    color = AppTheme.colors.DropShadow,
                                    offset = Offset(2f, 2f),
                                    blurRadius = 2f,
                                )
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .clickable { navController?.navigate("MyReminders") }
                        )
                    }

                    SeparatorLine()

                    // User's Account Settings
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        ShadowedIcon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.person),
                            tint = AppTheme.colors.SecondaryOne,
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.CenterVertically)

                        )
                        Text(
                            text = "Account Settings",
                            color = AppTheme.colors.Gray,
                            style = AppTheme.textStyles.HeadingSix.copy(
                                shadow = Shadow(
                                    color = AppTheme.colors.DropShadow,
                                    offset = Offset(2f, 2f),
                                    blurRadius = 2f,
                                )
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .clickable { navController?.navigate("userAccountScreen") }
                        )
                    }

                    SeparatorLine()

                    // User's Journey
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        ShadowedIcon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.badge),
                            tint = AppTheme.colors.SecondaryTwo,
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.CenterVertically)

                        )
                        Text(
                            text = stringResource(R.string.my_journey_stats),
                            color = AppTheme.colors.Gray,
                            style = AppTheme.textStyles.HeadingSix.copy(
                                shadow = Shadow(
                                    color = AppTheme.colors.DropShadow,
                                    offset = Offset(2f, 2f),
                                    blurRadius = 2f,
                                )
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .clickable { navController?.navigate("journey_stats") }
                        )
                    }

                    SeparatorLine()

                    // Reset Life Points
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        ShadowedIcon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.reset_arrows),
                            tint = AppTheme.colors.SecondaryOne,
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.CenterVertically)

                        )
                        Text(
                            text = stringResource(R.string.resetLifePoints),
                            color = AppTheme.colors.Gray,
                            style = AppTheme.textStyles.HeadingSix.copy(
                                shadow = Shadow(
                                    color = AppTheme.colors.DropShadow,
                                    offset = Offset(2f, 2f),
                                    blurRadius = 2f,
                                )
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .clickable { showResetLifePointsDialog.value = true }

                        )
                    }

                    SeparatorLine()

                    // Self Care Tips
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        ShadowedIcon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.heart),
                            tint = AppTheme.colors.SecondaryThree,
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.CenterVertically)

                        )
                        Text(
                            text = stringResource(R.string.selfCareTips),
                            color = AppTheme.colors.Gray,
                            style = AppTheme.textStyles.HeadingSix.copy(
                                shadow = Shadow(
                                    color = AppTheme.colors.DropShadow,
                                    offset = Offset(2f, 2f),
                                    blurRadius = 2f,
                                )
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .clickable { navController?.navigate("selfcare") }
                        )
                    }

                    SeparatorLine()

                    // Terms and Privacy Policy
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        ShadowedIcon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.document),
                            tint = AppTheme.colors.SecondaryTwo,
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.CenterVertically)

                        )
                        Text(
                            text = stringResource(R.string.about_life_leveling),
                            color = AppTheme.colors.Gray,
                            style = AppTheme.textStyles.HeadingSix.copy(
                                shadow = Shadow(
                                    color = AppTheme.colors.DropShadow,
                                    offset = Offset(2f, 2f),
                                    blurRadius = 2f,
                                )
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .clickable { navController?.navigate("termsAndPrivacy") }
                        )
                    }

                    SeparatorLine()

                    // Logout
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {

                        ShadowedIcon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.logout),
                            tint = AppTheme.colors.Error,
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.CenterVertically)


                        )
                        Text(
                            text = stringResource(R.string.logOut),
                            color = AppTheme.colors.Gray,
                            style = AppTheme.textStyles.HeadingSix.copy(
                                shadow = Shadow(
                                    color = AppTheme.colors.DropShadow,
                                    offset = Offset(2f, 2f),
                                    blurRadius = 2f,
                                )
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .clickable { onSignOut() }
                        )
                    }
                }

                ScrollFadeEdges(
                    scrollState = scrollState,
                )
            }
        }
    }

    // Reset Life Points Dialog Box
    if (showResetLifePointsDialog.value) {
        CustomDialog(
            toShow = showResetLifePointsDialog,
            dismissOnInsideClick = false,
            dismissOnOutsideClick = true
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.resetLifePoints),
                    color = AppTheme.colors.SecondaryOne,
                    style = AppTheme.textStyles.HeadingFour.copy(
                        shadow = Shadow(
                            color = AppTheme.colors.DropShadow,
                            offset = Offset(3f, 4f),
                            blurRadius = 6f,
                        )
                    )
                )
                Text(
                    text = stringResource(R.string.resetLifePointsWarning),
                    color = AppTheme.colors.Gray,
                    style = AppTheme.textStyles.Default
                )

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    // Cancel button
                    CustomButton(
                        onClick = { showResetLifePointsDialog.value = false },
                        width = 120.dp,
                        backgroundColor = AppTheme.colors.Success75
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            color = AppTheme.colors.DarkerBackground,
                            style = AppTheme.textStyles.HeadingSix
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    // Confirm Reset Button
                    CustomButton(
                        onClick = {
                            showResetLifePointsDialog.value = false
                            onResetLifePoints()
                        },
                        width = 120.dp,
                        backgroundColor = AppTheme.colors.Error75
                    ) {
                        Text(
                            text = stringResource(R.string.reset),
                            color = AppTheme.colors.DarkerBackground,
                            style = AppTheme.textStyles.HeadingSix
                        )
                    }
                }
            }
        }
    }



    @Composable
    fun PreviewSettingScreen() {
        // Create a mock navController
        val navController = rememberNavController()

        // Create a state variable for the theme toggle
        var isDarkTheme by remember { mutableStateOf(false) }

        SettingScreen(
            navController = navController,
            isDarkTheme = isDarkTheme,
            onThemeChange = { newIsDark ->
                isDarkTheme = newIsDark // update the state in preview
            },
            onSignOut = {},
            onResetLifePoints = {}
        )
    }
}
