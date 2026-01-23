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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.Navigator
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.components.CircleButton
import com.lifeleveling.app.ui.components.CustomButton
import com.lifeleveling.app.ui.components.CustomDialog
import com.lifeleveling.app.ui.components.CustomTextField
import com.lifeleveling.app.ui.components.HighlightCard
import com.lifeleveling.app.ui.components.ShadowedIcon
import com.lifeleveling.app.ui.theme.AppTheme

//@Preview
@Composable
fun UserAccountScreen(
navController: NavController,
onDeleteAccount: () -> Unit = {},
){
    val displayName = rememberSaveable { mutableStateOf("Bobby") }
    val email = rememberSaveable { mutableStateOf("bobbyemail@fakeemail.com") }
    val password = rememberSaveable { mutableStateOf("") }
    val passwordConfirmed = rememberSaveable { mutableStateOf("") }
    val editUserName = rememberSaveable { mutableStateOf(false) }
    val editPassword = rememberSaveable { mutableStateOf(false) }
    val editEmail = rememberSaveable { mutableStateOf(false) }
    val showDeleteDialog = rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AppTheme.colors.Background)
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){

                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = "Edit Account Information", softWrap = true,
                        color = AppTheme.colors.SecondaryOne,
                        style = AppTheme.textStyles.HeadingThree.copy(
                            shadow = Shadow(
                                color = AppTheme.colors.DropShadow,
                                offset = Offset(2f, 2f),
                                blurRadius = 2f,
                            )
                        ),
                    )
                Spacer(modifier = Modifier.width(16.dp))
                CircleButton(
                    modifier = Modifier
                        .align(Alignment.Top)
                        .clickable {  },
                    onClick = {navController.popBackStack()},
                    imageVector = ImageVector.vectorResource(R.drawable.back_arrow),
                    size = 48.dp
                )
            }
            HighlightCard(
                modifier = Modifier
                    .fillMaxWidth(),
                outerPadding = 0.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                        .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier
                                .clickable {
                                    editUserName.value = true
                                },
                            text = "Username",
                            color = AppTheme.colors.SecondaryOne,
                            style = AppTheme.textStyles.HeadingFive.copy(
                                shadow = Shadow(
                                    color = AppTheme.colors.DropShadow,
                                    offset = Offset(2f, 2f),
                                    blurRadius = 2f,
                                )
                            ),
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            modifier = Modifier
                                .clickable {
                                    editUserName.value = true
                                },
                            text = "Edit",
                            color = AppTheme.colors.SecondaryOne,
                            style = AppTheme.textStyles.SmallUnderlined.copy(
                                shadow = Shadow(
                                    color = AppTheme.colors.DropShadow,
                                    offset = Offset(2f, 2f),
                                    blurRadius = 2f,
                                )
                            ),
                        )
                    }

                    CustomTextField(
                        enabled = editUserName.value,
                        value = displayName.value,
                        onValueChange = { displayName.value = it },
                        textStyle = AppTheme.textStyles.HeadingSix,
                        placeholderText = "Username",
                        placeholderTextColor = AppTheme.colors.Gray,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        supportingUnit = {
                            when {
                                displayName.value.isEmpty() ->
                                    Text(
                                        "Username cannot be empty",
                                        style = AppTheme.textStyles.Small,
                                        color = AppTheme.colors.Error
                                    )

                            }
                        },
                        backgroundColor = AppTheme.colors.DarkerBackground
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier
                                .clickable {
                                    editUserName.value = true
                                },
                            text = "Email",
                            color = AppTheme.colors.SecondaryOne,
                            style = AppTheme.textStyles.HeadingFive.copy(
                                shadow = Shadow(
                                    color = AppTheme.colors.DropShadow,
                                    offset = Offset(2f, 2f),
                                    blurRadius = 2f,
                                )
                            ),
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            modifier = Modifier
                                .clickable {
                                    editEmail.value = true
                                },
                            text = "Edit",
                            color = AppTheme.colors.SecondaryOne,
                            style = AppTheme.textStyles.SmallUnderlined.copy(
                                shadow = Shadow(
                                    color = AppTheme.colors.DropShadow,
                                    offset = Offset(2f, 2f),
                                    blurRadius = 2f,
                                )
                            ),
                        )
                    }
                    CustomTextField(
                        enabled = editEmail.value,
                        value = email.value,
                        onValueChange = { email.value = it },
                        textStyle = AppTheme.textStyles.HeadingSix,
                        placeholderText = "Email",
                        placeholderTextColor = AppTheme.colors.Gray,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        supportingUnit = {
                            when {
                                email.value.isEmpty() ->
                                    Text(
                                        "Email cannot be empty",
                                        style = AppTheme.textStyles.Small,
                                        color = AppTheme.colors.Error
                                    )

                            }
                        },
                        backgroundColor = AppTheme.colors.DarkerBackground
                    )

                    Text(
                        modifier = Modifier
                            .padding(all = 16.dp)
                            .clickable {
                                editPassword.value = !editPassword.value
                            },
                        text = "Edit password",
                        color = AppTheme.colors.SecondaryOne,
                        style = AppTheme.textStyles.DefaultUnderlined.copy(
                            shadow = Shadow(
                                color = AppTheme.colors.DropShadow,
                                offset = Offset(2f, 2f),
                                blurRadius = 2f,
                            )
                        ),
                    )
                    if(editPassword.value){

                        CustomTextField(
                            value = password.value,
                            onValueChange = { password.value = it },
                            textStyle = AppTheme.textStyles.HeadingSix,
                            placeholderText = "New Password",
                            placeholderTextColor = AppTheme.colors.Gray,
                            visualTransformation = PasswordVisualTransformation(),
                            supportingUnit = {
                                when {
                                    password.value.isEmpty() ->
                                        Text(
                                            "Password cannot be empty",
                                            style = AppTheme.textStyles.Small,
                                            color = AppTheme.colors.Error
                                        )

                                }
                            },
                            backgroundColor = AppTheme.colors.DarkerBackground
                        )
                        CustomTextField(
                            value = passwordConfirmed.value,
                            onValueChange = { passwordConfirmed.value = it },
                            textStyle = AppTheme.textStyles.HeadingSix,
                            placeholderText = "Confirm Password",
                            placeholderTextColor = AppTheme.colors.Gray,
                            visualTransformation = PasswordVisualTransformation(),
                            supportingUnit = {
                                when {
                                    passwordConfirmed.value.isEmpty() ->
                                        Text(
                                            "Password cannot be empty",
                                            style = AppTheme.textStyles.Small,
                                            color = AppTheme.colors.Error
                                        )

                                }
                            },
                            backgroundColor = AppTheme.colors.DarkerBackground
                        )

                    }

                    CustomButton(
                        onClick = {},
                        enabled = !displayName.value.isEmpty() && !email.value.isEmpty() && (!editPassword.value || (password.value == passwordConfirmed.value && password.value.isNotEmpty())),
                        content = {
                            Text(
                                "Save",
                                color = AppTheme.colors.DarkerBackground,
                                style = AppTheme.textStyles.HeadingSix
                            )
                        }
                    )
                }
            }


            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ){

                ShadowedIcon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.trash_solid_full),
                    tint = AppTheme.colors.Error,
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = stringResource(R.string.deleteAccount),
                    color = AppTheme.colors.Gray,
                    style = AppTheme.textStyles.DefaultUnderlined.copy(
                        shadow = Shadow(
                            color = AppTheme.colors.DropShadow,
                            offset = Offset(2f, 2f),
                            blurRadius = 2f,
                        )
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable { showDeleteDialog.value = true }

                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }

    // Delete Account Dialog Box
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
