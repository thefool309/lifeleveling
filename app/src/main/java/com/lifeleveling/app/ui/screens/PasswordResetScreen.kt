package com.lifeleveling.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.components.CustomButton
import com.lifeleveling.app.ui.components.CustomDialog
import com.lifeleveling.app.ui.components.CustomTextField
import com.lifeleveling.app.ui.components.HighlightCard


private fun isGoogleMailboxUi(email: String): Boolean =
    email.endsWith("@gmail.com", ignoreCase = true) ||
            email.endsWith("@googlemail.com", ignoreCase = true)

@Composable
fun PasswordResetScreen(
    email: MutableState<String>,
    onReset: (String, (Boolean, Int) -> Unit) -> Unit = { _, _ -> },
    backToLogin: () -> Unit = {}
) {
    val isGmail = isGoogleMailboxUi(email.value)
    val result = remember {mutableStateOf(Pair(true,R.string.resetPasswordMissingEmail))}
    val showPasswordResetDialog = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.Background)
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Logo
            Image(
                painter = painterResource(id=R.drawable.ll_life_tree),
                contentDescription = "logo",
                modifier = Modifier
                    .size(100.dp)
            )

            // Title
            Text(
                text = stringResource(R.string.forgotten_password),
                color = AppTheme.colors.BrandOne,
                style = AppTheme.textStyles.HeadingFour,
                textAlign = TextAlign.Center
            )

            HighlightCard(
                outerPadding = 0.dp,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Instructions
                    Text(
                        text = stringResource(R.string.resetPasswordMissingEmail),
                        style = AppTheme.textStyles.Default,
                        color = AppTheme.colors.SecondaryOne,
                        textAlign = TextAlign.Center
                    )

                    //email
                    CustomTextField(
                        value = email.value,
                        onValueChange = {email.value = it},
                        textStyle = AppTheme.textStyles.HeadingSix,
                        placeholderText = stringResource(R.string.email),
                        placeholderTextColor = AppTheme.colors.Gray,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        supportingUnit = {
                            when {
                                email.value.isEmpty() ->
                                    Text(stringResource(R.string.emailNotEmpty),
                                        style = AppTheme.textStyles.Small,
                                        color = AppTheme.colors.Error)

                                isGmail ->
                                    Text(stringResource(R.string.resetPasswordGoogleAccount),
                                        style = AppTheme.textStyles.Small,
                                        color = AppTheme.colors.Error)
                            }
                        },
                        backgroundColor = AppTheme.colors.DarkerBackground
                    )

                    // Send email button
                    CustomButton(
                        onClick = {
                            val emailTrimmed = email.value.trim()

                            // In case email is empty
                            if (emailTrimmed.isEmpty()) {
                                result.value = Pair(false, R.string.resetPasswordMissingEmail)
                                showPasswordResetDialog.value = true
                            }

                            // Gmail / Googlemail
                            if (isGmail) {
                                result.value = Pair(false,R.string.resetPasswordGoogleAccount)
                                showPasswordResetDialog.value = true
                            }

                            // Normal email/password account calls
                            onReset(emailTrimmed) { ok, message ->
                                result.value = Pair(ok, message)
                                showPasswordResetDialog.value = true
                            }
                        },
                        enabled = !isGmail && email.value.isNotEmpty()
                    ) {
                        Text(
                            text = stringResource(R.string.reset),
                            color = AppTheme.colors.DarkerBackground,
                            style = AppTheme.textStyles.HeadingSix)
                    }
                }
            }

            // Return to Login Page
            Text(
                text = stringResource(R.string.return_to_login),
                color = AppTheme.colors.Gray,
                textAlign = TextAlign.Center,
                style = AppTheme.textStyles.DefaultUnderlined,
                modifier = Modifier.clickable { backToLogin() }
            )
        }
    }

    // Password reset feedback dialog
    if (showPasswordResetDialog.value) {
        CustomDialog(
            toShow = showPasswordResetDialog,
            dismissOnInsideClick = false,
            dismissOnOutsideClick = true
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Message of success or failure
                Text(
                    text = (
                            if (result.value.first)
                                stringResource(result.value.second, email.value.trim())
                            else
                                stringResource(result.value.second)),
                    color = AppTheme.colors.Gray,
                    style = AppTheme.textStyles.Default,
                    textAlign = TextAlign.Center
                )

                // Button to close dialog window
                CustomButton(
                    onClick = {
                        showPasswordResetDialog.value = false
                        // Return to sign in only on success
                        if (result.value.first) {
                            backToLogin()
                        }
                    },
                    width = 120.dp,
                ) {
                    Text(
                        text = stringResource(R.string.close),
                        color = AppTheme.colors.DarkerBackground,
                        style = AppTheme.textStyles.HeadingSix
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPasswordResetScreen() {
    val email = remember { mutableStateOf("") }

    PasswordResetScreen(
        email = email,
        onReset = {} as (String, (Boolean, Int) -> Unit) -> Unit,
        backToLogin = {}
    )
}