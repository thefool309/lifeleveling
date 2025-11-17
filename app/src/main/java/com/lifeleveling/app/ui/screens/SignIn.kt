package com.lifeleveling.app.ui.screens


import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.ui.components.CustomButton
import com.lifeleveling.app.ui.components.CustomTextField
import com.lifeleveling.app.ui.components.HighlightCard


@Composable
fun SignIn(
    onLogin: () -> Unit = {println("Login pressed")},
    onGoogleLogin: () -> Unit = {println("Google login pressed")},
    onCreateAccount: () -> Unit = {println("Create account pressed")},
    email: MutableState<String>,
    password: MutableState<String>,
) {
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
            verticalArrangement = Arrangement.Center,
        ){
            //logo
            Image(
                painter = painterResource(id= R.drawable.ll_circle_logo_dots),
                contentDescription = "logo",
                modifier = Modifier
                    .width(300.dp)
                    .aspectRatio(1f)
            )

            //inner box holding text fields
            HighlightCard(
                modifier = Modifier
                    .fillMaxWidth(),
                outerPadding = 0.dp
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
                        placeholderText = stringResource(R.string.email),
                        placeholderTextColor = AppTheme.colors.Gray,
                        textStyle = AppTheme.textStyles.HeadingSix,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        supportingUnit = {
                            if (email.value.isEmpty()) {
                                Text(stringResource(R.string.emailNotEmpty), style = AppTheme.textStyles.Small, color = AppTheme.colors.Error)
                            }
                        },
                        backgroundColor = AppTheme.colors.DarkerBackground
                    )
                    //password
                    CustomTextField(
                        value = password.value,
                        onValueChange = {password.value = it},
                        placeholderText = stringResource(R.string.password),
                        placeholderTextColor = AppTheme.colors.Gray,
                        textStyle = AppTheme.textStyles.HeadingSix,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        supportingUnit = {
                            if (password.value.isEmpty()) {
                                Text(
                                    stringResource(R.string.passwordNotEmpty),
                                    style = AppTheme.textStyles.Small,
                                    color = AppTheme.colors.Error
                                )
                            }
                        },
                        backgroundColor = AppTheme.colors.DarkerBackground
                    )
                    //login button
                    CustomButton(
                        onClick = onLogin,
                        enabled = email.value.isNotEmpty() && password.value.isNotEmpty()
                    ) {
                        Text(stringResource(R.string.login), color = AppTheme.colors.DropShadow,style = AppTheme.textStyles.HeadingSix)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier
                    .width(250.dp)
                    .height(50.dp),
                onClick = onGoogleLogin,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.LightShadow)
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
                    style = AppTheme.textStyles.HeadingFive
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    stringResource(R.string.useGoogle),
                    color = AppTheme.colors.DropShadow,
                    style = AppTheme.textStyles.Default,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Forgotten password
            Text(
                text = stringResource(R.string.forgotten_password),
                color = AppTheme.colors.Gray,
                textAlign = TextAlign.Center,
                style = AppTheme.textStyles.DefaultUnderlined,
                modifier = Modifier.clickable { /* Forgotten password logic here */ }
            )

            Spacer(modifier = Modifier.height(16.dp))

            //create an account nav link
            Text(
                text = stringResource(R.string.createAccount),
                color = AppTheme.colors.SecondaryThree,
                textAlign = TextAlign.Center,
                style = AppTheme.textStyles.DefaultUnderlined,
                modifier = Modifier.clickable { onCreateAccount() }
            )

            // Extra spacer to make up for image blank space
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewSignIn() {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    SignIn(
        email = email,
        password = password,
    )
}