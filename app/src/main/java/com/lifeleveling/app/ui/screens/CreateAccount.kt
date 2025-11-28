package com.lifeleveling.app.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.lifeleveling.app.R
import com.lifeleveling.app.data.LocalNavController
import com.lifeleveling.app.data.LocalUserManager
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.ui.components.CustomButton
import com.lifeleveling.app.ui.components.CustomCheckbox
import com.lifeleveling.app.ui.components.CustomTextField
import com.lifeleveling.app.ui.components.HighlightCard
import kotlin.text.isEmpty

// Helper Function to block gmail/googlemail on the email/password path
private fun isGoogleMailboxUi(email: String): Boolean =
    email.endsWith("@gmail.com", ignoreCase = true) ||
            email.endsWith("@googlemail.com", ignoreCase = true)

// @Preview(showBackground = true)
@Composable
fun CreateAccountScreen() {
    val userManager = LocalUserManager.current
    val userState by userManager.uiState.collectAsState()
    val navController = LocalNavController.current

    val googleLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        userManager.signInWithGoogleIntent(result.data)
    }
    val context = LocalContext.current

    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val isGmail = isGoogleMailboxUi(email.value)
    val pwordRules = PasswordRules(password.value)
    val isPasswordValid = pwordRules.all{it.second}
    val termsCheck = remember { mutableStateOf(false) }
    val termsAndPrivacy = buildAnnotatedString {
        withStyle(style = AppTheme.textStyles.Default.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
            append(stringResource(R.string.accept))
        }
        append(" ")
        pushLink(
            LinkAnnotation.Clickable(
                tag = "terms",
                linkInteractionListener = {
                    /* Add in logic of what clicking on terms does here */
                }
            )
        )
        withStyle(style = AppTheme.textStyles.DefaultUnderlined.toSpanStyle().copy(color = AppTheme.colors.SecondaryThree)) {
            append(stringResource(R.string.termsAndConditions))
        }
        pop()
        append(" ")
        withStyle(style = AppTheme.textStyles.Default.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
            append(stringResource(R.string.and))
        }
        append(" ")
        pushLink(
            LinkAnnotation.Clickable(
                tag = "privacy",
                linkInteractionListener = {
                    /* Add in logic of what clicking on privacy does here */
                },
            ),
        )
        withStyle(style = AppTheme.textStyles.DefaultUnderlined.toSpanStyle().copy(color = AppTheme.colors.SecondaryThree)) {
            append(stringResource(R.string.privacyPolicy))
        }
        pop()
    }

    //screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AppTheme.colors.Background)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ){
            //logo
            Image(
                painter = painterResource(id=R.drawable.ll_life_tree),
                contentDescription = "logo",
                modifier = Modifier
                    .size(100.dp)
            )

            Text(stringResource(R.string.createAccountTitle), color = AppTheme.colors.BrandOne,style = AppTheme.textStyles.HeadingFour, textAlign = TextAlign.Center)

            //inner box holding text fields
            HighlightCard(
                modifier = Modifier
                    .fillMaxWidth(),
                outerPadding = 0.dp,
            ){
                //column keeping box items centered
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
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
                                    Text("Use 'Login using Google' for Gmail addresses.",
                                        style = AppTheme.textStyles.Small,
                                        color = AppTheme.colors.Error)
                            }
                        },
                        backgroundColor = AppTheme.colors.DarkerBackground
                    )
                    //password
                    CustomTextField(
                        value = password.value,
                        onValueChange = {password.value = it
                            password.value.isNotEmpty()
                        },
                        placeholderText = stringResource(R.string.password),
                        placeholderTextColor = AppTheme.colors.Gray,
                        textStyle = AppTheme.textStyles.HeadingSix,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        supportingUnit = {
                            Column {
                                pwordRules.forEach { rule: Pair<String, Boolean> ->
                                    Text(text = rule.first,
                                        color = if(rule.second){
                                            AppTheme.colors.SecondaryTwo
                                        }else{
                                            AppTheme.colors.Error
                                        },
                                        style = AppTheme.textStyles.Small
                                    )
                                }
                            }
                        },
                        backgroundColor = AppTheme.colors.DarkerBackground
                    )
                    // Terms checkbox
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CustomCheckbox(
                            checked = termsCheck.value,
                            onCheckedChange = {termsCheck.value = it},
                        )
                        Text(
                            text = termsAndPrivacy,
                            style = AppTheme.textStyles.Default,
                            color = AppTheme.colors.Gray
                        )
                    }
                    // Join
                    CustomButton(
                        onClick = {
                            userManager.register(email.value, password.value)
                        },
                        enabled = !isGmail && email.value.isNotEmpty() && isPasswordValid && termsCheck.value
                        ,
                        content = {
                            Text(stringResource(R.string.join), color = AppTheme.colors.DarkerBackground,style = AppTheme.textStyles.HeadingSix)
                        }
                    )
                }
            }
            // Google sign in button
            Button(
                    modifier = Modifier
                        .width(250.dp)
                        .height(50.dp),
            onClick = {
                val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

                val client = GoogleSignIn.getClient(context, googleSignInOptions)

                googleLauncher.launch(client.signInIntent)
            },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.Gray)
            ) {         //This below can place and image in the button
//                        Image(
//                            painter = painterResource(id = R.drawable.gmail_color),
//                            contentDescription = "Google Image",
//                            modifier = Modifier
//                                .size(48.dp)
//                        )
                //button text
                Text(
                    "G",
                    color = AppTheme.colors.DropShadow,
                    style = AppTheme.textStyles.HeadingFive,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    stringResource(R.string.useGoogle),
                    color = AppTheme.colors.DropShadow,
                    style = AppTheme.textStyles.Default,
                )
            }

            //create an account nav link
            Text(
                text = stringResource(R.string.backToLogin),
                color = AppTheme.colors.SecondaryThree,
                textAlign = TextAlign.Center,
                style = AppTheme.textStyles.DefaultUnderlined,
                modifier = Modifier.clickable { navController.navigate("signIn") }
            )
        }
    }
}

@Composable
fun PasswordRules(pWord: String): List<Pair<String, Boolean>> {
    return listOf(
        stringResource(R.string.passwordLength) to (pWord.length in 8..20),
        stringResource(R.string.noSpace) to pWord.none { it.isWhitespace() },
        stringResource(R.string.lowercase) to pWord.any { it.isLowerCase() },
        stringResource(R.string.uppercase) to pWord.any { it.isUpperCase() },
        stringResource(R.string.number) to pWord.any { it.isDigit() },
        stringResource(R.string.specialChar) to pWord.any { it in "!@#$%^&*()_+-=" }
    )
}


@Suppress("VisualLintAccessibilityTestFramework")
@Preview(showBackground = true)
@Composable
fun PreviewCreateAccount() {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    CreateAccountScreen()
}