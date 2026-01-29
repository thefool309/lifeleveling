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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.ui.components.HighlightCard
import com.lifeleveling.app.ui.components.ShadowedIcon
import com.lifeleveling.app.ui.components.SlidingSwitch
import com.lifeleveling.app.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.lifeleveling.app.data.LocalNavController
import com.lifeleveling.app.data.LocalUserManager
import com.lifeleveling.app.ui.components.ScrollFadeEdges
import com.lifeleveling.app.ui.components.SeparatorLine
import com.lifeleveling.app.ui.components.CustomButton
import com.lifeleveling.app.ui.components.CustomDialog

/**
 * The composable for the Main settings screen.
 * Contains clickable options to navigate to several other screens.
 * Contains the switch to control the user's preference on the theme colors.
 * @author StephenC1993
 */
@Preview
@Composable
fun SettingScreen(){
    val userManager = LocalUserManager.current
    val userState by userManager.uiState.collectAsState()
    val navController = LocalNavController.current

    val scrollState = rememberScrollState()

    val showResetLifePointsDialog = remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = AppTheme.colors.Background
    ){
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ){
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
            ){
                Column(modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ){
                        Spacer(modifier = Modifier.height(8.dp))

                        // Controls theme between light and dark mode
                        SlidingSwitch(
                            options = listOf(stringResource(R.string.darkMode), stringResource(R.string.lightMode)),
                            selectedIndex = if (userState.userBase?.isDarkTheme ?: true) 0 else 1,
                            onOptionSelected = { index ->
                                val newIsDark = index == 0
                                userManager.updateTheme(newIsDark)
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
                    ){
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
                                .clickable { navController.navigate("notifications") }
                        )
                    }

                    SeparatorLine()

                    // All Reminders
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ){
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
                                .clickable { navController.navigate("MyReminders") }
                        )
                    }

                    SeparatorLine()

                    // User's Account Settings
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ){
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
                                .clickable { navController.navigate("userAccount") }
                        )
                    }

                    SeparatorLine()

                    // User's Journey
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ){
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
                                .clickable {
                                    navController.navigate("journeyStats")
                                }
                        )
                    }

                    SeparatorLine()

                    // Reset Life Points
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ){
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
                    ){
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
                                .clickable { navController.navigate("selfCare") }
                        )
                    }

                    SeparatorLine()

                    // Terms and Privacy Policy
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ){
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
                                .clickable { navController.navigate("termsAndPrivacy") }
                        )
                    }

                    SeparatorLine()

                    // Logout
                    // TODO: Add a confirmation dialogue for logging out
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ){

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
                                .clickable { userManager.signOut() }
                        )
                    }
                }

                ScrollFadeEdges(
                    scrollState = scrollState,
                )
            }
        }
    }
}