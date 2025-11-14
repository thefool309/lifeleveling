package com.lifeleveling.app.ui.screens


import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import com.lifeleveling.app.ui.components.CustomButton
import com.lifeleveling.app.ui.components.CustomDialog


@Composable
fun SettingScreen(
    navController: NavController? = null,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onSignOut: () -> Unit ={},
    onDeleteAccount: () -> Unit = {},
){

    val showDeleteDialog = remember {mutableStateOf(false)}

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = AppTheme.colors.Background
    ){
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        ){
            Text(
                text = stringResource(R.string.settings),
                color = AppTheme.colors.SecondaryOne,
                style = AppTheme.textStyles.HeadingThree.copy(
                    shadow = Shadow(
                        color = AppTheme.colors.DropShadow,
                        offset = Offset(3f, 4f),
                        blurRadius = 6f,
                    )
                ),
            )

            Spacer(modifier = Modifier.height(24.dp))

            HighlightCard(
                modifier = Modifier
                    .fillMaxWidth(),

                outerPadding = 0.dp
            ){
                Column(modifier = Modifier
                    .fillMaxWidth()
                ){
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .scale(1.2f)
                            .fillMaxWidth(),

                        contentAlignment = Alignment.Center
                    ){

                        SlidingSwitch(
                            options = listOf(stringResource(R.string.darkMode), stringResource(R.string.lightMode)),
                            selectedIndex = if (isDarkTheme) 0 else 1,
                            onOptionSelected = { index ->
                                val newIsDark = index == 0
                                onThemeChange(newIsDark)
                            },
                            horizontalPadding = 12.dp,
                            verticalPadding = 8.dp,
                            backgroundColor = AppTheme.colors.DarkerBackground,
                            selectedColor = AppTheme.colors.BrandOne,
                            unselectedColor = AppTheme.colors.Gray,
                            cornerRadius = 32.dp,
                            textStyle = AppTheme.textStyles.Default,
                            insetAmount = 4.dp,
                            extraWidth = 64.dp,

                        )

                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier

                            .fillMaxWidth()
                            .height(1.dp)
                            .background(AppTheme.colors.Gray)
                            .padding(horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier){

                        ShadowedIcon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.bell),
                            contentDescription = "Bell icon",
                            tint = AppTheme.colors.SecondaryThree,
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.CenterVertically)

                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        Text(
                            text = stringResource(R.string.notificationSettings),
                            color = AppTheme.colors.Gray,
                            style = AppTheme.textStyles.HeadingSix.copy(
                                shadow = Shadow(
                                    color = AppTheme.colors.DropShadow,
                                    offset = Offset(3f, 4f),
                                    blurRadius = 6f,
                                )
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .clickable { navController?.navigate("notifications") }

                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier

                            .fillMaxWidth()
                            .height(1.dp)
                            .background(AppTheme.colors.Gray)
                            .padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier){

                        ShadowedIcon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.heart),
                            contentDescription = "Bell icon",
                            tint = AppTheme.colors.SecondaryThree,
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.CenterVertically)

                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        Text(
                            text = stringResource(R.string.selfCareTips),
                            color = AppTheme.colors.Gray,
                            style = AppTheme.textStyles.HeadingSix.copy(
                                shadow = Shadow(
                                    color = AppTheme.colors.DropShadow,
                                    offset = Offset(3f, 4f),
                                    blurRadius = 6f,
                                )
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .clickable { navController?.navigate("selfcare") }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier

                            .fillMaxWidth()
                            .height(1.dp)
                            .background(AppTheme.colors.Gray)
                            .padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier){

                        ShadowedIcon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.document),
                            contentDescription = "Bell icon",
                            tint = AppTheme.colors.SecondaryThree,
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.CenterVertically)

                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        Text(
                            text = stringResource(R.string.termsAndPrivacy),
                            color = AppTheme.colors.Gray,
                            style = AppTheme.textStyles.HeadingSix.copy(
                                shadow = Shadow(
                                    color = AppTheme.colors.DropShadow,
                                    offset = Offset(3f, 4f),
                                    blurRadius = 6f,
                                )
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .clickable { navController?.navigate("termsAndPrivacy") }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier

                            .fillMaxWidth()
                            .height(1.dp)
                            .background(AppTheme.colors.Gray)
                            .padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier){

                        ShadowedIcon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.back_arrow),
                            contentDescription = "Bell icon",
                            tint = AppTheme.colors.SecondaryThree,
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.CenterVertically)

                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        Text(
                            text = stringResource(R.string.resetLifePoints),
                            color = AppTheme.colors.Gray,
                            style = AppTheme.textStyles.HeadingSix.copy(
                                shadow = Shadow(
                                    color = AppTheme.colors.DropShadow,
                                    offset = Offset(3f, 4f),
                                    blurRadius = 6f,
                                )
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)

                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier

                            .fillMaxWidth()
                            .height(1.dp)
                            .background(AppTheme.colors.Gray)
                            .padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier){

                        ShadowedIcon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.trash_solid_full),
                            contentDescription = "Bell icon",
                            tint = AppTheme.colors.SecondaryThree,
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.CenterVertically)

                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        Text(
                            text = stringResource(R.string.deleteAccount),
                            color = AppTheme.colors.Gray,
                            style = AppTheme.textStyles.HeadingSix.copy(
                                shadow = Shadow(
                                    color = AppTheme.colors.DropShadow,
                                    offset = Offset(3f, 4f),
                                    blurRadius = 6f,
                                )
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .clickable { showDeleteDialog.value = true }

                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier

                            .fillMaxWidth()
                            .height(1.dp)
                            .background(AppTheme.colors.Gray)
                            .padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier){

                        ShadowedIcon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.minus),
                            contentDescription = "Bell icon",
                            tint = AppTheme.colors.SecondaryThree,
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.CenterVertically)


                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        Text(
                            text = stringResource(R.string.logOut),
                            color = AppTheme.colors.Gray,
                            style = AppTheme.textStyles.HeadingSix.copy(
                                shadow = Shadow(
                                    color = AppTheme.colors.DropShadow,
                                    offset = Offset(3f, 4f),
                                    blurRadius = 6f,
                                )
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .clickable { onSignOut() }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    if (showDeleteDialog.value) {
        CustomDialog(
            toShow = showDeleteDialog,
            dismissOnInsideClick = false,     // keep dialog open while interacting with buttons
            dismissOnOutsideClick = true
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.deleteAccountQuestion),
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
                    text = stringResource(R.string.deleteDialogBoxWarning),
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
                        onClick = { showDeleteDialog.value = false },
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

                    // Confirm delete
                    CustomButton(
                        onClick = {
                            showDeleteDialog.value = false
                            onDeleteAccount()
                        },
                        width = 120.dp,
                        backgroundColor = AppTheme.colors.Error75
                    ) {
                        Text(
                            text = stringResource(R.string.delete),
                            color = AppTheme.colors.DarkerBackground,
                            style = AppTheme.textStyles.HeadingSix
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)

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
        onDeleteAccount = {}
    )
}
