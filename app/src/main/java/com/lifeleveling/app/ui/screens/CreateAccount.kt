package com.lifeleveling.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.sp
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.ui.components.CustomButton
import com.lifeleveling.app.ui.components.HighlightCard
import kotlin.text.isEmpty



@Composable
fun CreateAccountScreen(
    onJoin: () -> Unit = {println("Join pressed")},
    onGoogleLogin: () -> Unit = {println("Google login pressed")},
    onLog: () -> Unit = {println("Login account pressed")},
    email: MutableState<String>,
    password: MutableState<String>
) {

    val pwordRules = PasswordRules(password.value)
    val isPasswordValid = pwordRules.all{it.second}

    //screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AppTheme.colors.Background),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){
            //logo
            Image(
                painter = painterResource(id=R.drawable.ll_life_tree),
                contentDescription = "logo",
                modifier = Modifier
                    .size(148.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center

            ){
                Text(stringResource(R.string.createAccountTitle), color = AppTheme.colors.BrandOne,style = AppTheme.textStyles.HeadingThree, textAlign = TextAlign.Center)
            }

            //inner box holding text fields
            HighlightCard( modifier = Modifier
                .height(350.dp)
                .width(300.dp)
                .background(color = AppTheme.colors.DarkerBackground),
                outerPadding = 0.dp,
                //innerPadding = 0.dp,

            ){
                //column keeping box items centered
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly

                ) {
                    //email
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(0.9f),
                        value = email.value,
                        onValueChange = {email.value = it},
                        //label = { Text("Email", color = AppTheme.colors.Gray,style = AppTheme.textStyles.HeadingFive) },
                        placeholder = { Text(stringResource(R.string.email), color = AppTheme.colors.Gray, style = AppTheme.textStyles.HeadingFive) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        supportingText = {
                            if(email.value.isEmpty()){
                                Text(stringResource(R.string.emailNotEmpty), style = AppTheme.textStyles.Small, color = AppTheme.colors.Error)
                            }
                        }

                    )
                    //Spacer(modifier = Modifier.size(8.dp))
                    //password

                    OutlinedTextField(
                        modifier = Modifier

                            .fillMaxWidth(0.9f),
                        value = password.value,
                        onValueChange = {password.value = it
                            password.value.isNotEmpty()
                        },

                        //label = { Text("Password", color = AppTheme.colors.Gray,style = AppTheme.textStyles.HeadingFive) },
                        placeholder = { Text(stringResource(R.string.password), color = AppTheme.colors.Gray, style = AppTheme.textStyles.HeadingFive) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        supportingText = {
                            Column {
                                pwordRules.forEach {rule ->
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

                        )

                    Spacer(modifier = Modifier.size(6.dp))
                    CustomButton(
                        modifier = Modifier,
                        // .width(120.dp),
                        onClick = onJoin,
                        enabled = isPasswordValid,
                        content = {
                            Text(stringResource(R.string.join), color = AppTheme.colors.DropShadow,style = AppTheme.textStyles.HeadingSix, fontSize = 16.sp)
                        }
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            ) {

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
                        style = AppTheme.textStyles.HeadingThree,
                        fontSize = 28.sp,

                        )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        stringResource(R.string.useGoogle),
                        color = AppTheme.colors.DropShadow,
                        style = AppTheme.textStyles.HeadingSix,
                        fontSize = 16.sp,

                        )
                }

                Spacer(modifier = Modifier.height(32.dp))
                //create an account nav link
                Text(
                    text = stringResource(R.string.backToLogin),
                    color = AppTheme.colors.SecondaryThree,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    style = AppTheme.textStyles.DefaultUnderlined,
                    modifier = Modifier.clickable { onLog() }
                )
            }
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
        stringResource(R.string.specialChar) to pWord.any { it in "!@#\$%^&*()_+-=" }
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewCreateAccount() {
    var email = remember { mutableStateOf("") }
    var password = remember { mutableStateOf("") }

    CreateAccountScreen(
        email = email,
        password = password
    )
}