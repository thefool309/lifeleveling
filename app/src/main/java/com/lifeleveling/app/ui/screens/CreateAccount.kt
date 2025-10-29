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
import androidx.compose.material3.ExposedDropdownMenuDefaults.textFieldColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.ui.theme.CircleButton
import com.lifeleveling.app.ui.theme.CustomButton
import com.lifeleveling.app.ui.theme.HighlightCard




@Preview(showBackground = true)
@Composable
fun CreateAccountScreen(
    onJoin: () -> Unit = {println("Join pressed")},
    onGoogleLogin: () -> Unit = {println("Google login pressed")},
    onLog: () -> Unit = {println("Login account pressed")},
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val pwordRules = PasswordRules(password)
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
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)

        ){
            //logo
            Image(
                painter = painterResource(id=R.drawable.ll_life_tree),
                contentDescription = "logo",
                modifier = Modifier
                    .padding(24.dp)
                    .width(148.dp)
                    .height(148.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center

            ){
                Text("Create An Account", color = AppTheme.colors.BrandOne,style = AppTheme.textStyles.HeadingThree, textAlign = TextAlign.Center)
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
                        value = email,
                        onValueChange = {email = it},
                        label = { Text("Email", color = AppTheme.colors.Gray,style = AppTheme.textStyles.HeadingFive) },
                        placeholder = { Text("Email address", color = AppTheme.colors.Gray, style = AppTheme.textStyles.HeadingFive) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        supportingText = {
                            if(email.isEmpty()){
                                Text("Email can not be empty", style = AppTheme.textStyles.Small, color = AppTheme.colors.Error)
                            }
                        }

                    )
                    //Spacer(modifier = Modifier.size(8.dp))
                    //password

                    OutlinedTextField(
                        modifier = Modifier

                            .fillMaxWidth(0.9f),
                        value = password,
                        onValueChange = {password = it
                            password.isNotEmpty()
                        },

                        label = { Text("Password", color = AppTheme.colors.Gray,style = AppTheme.textStyles.HeadingFive) },
                        placeholder = { Text("Password", color = AppTheme.colors.Gray,style = AppTheme.textStyles.HeadingFive) },
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
                            Text("Join", color = AppTheme.colors.DropShadow,style = AppTheme.textStyles.HeadingSix, fontSize = 16.sp)
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
                        "Login using Google",
                        color = AppTheme.colors.DropShadow,
                        style = AppTheme.textStyles.HeadingSix,
                        fontSize = 16.sp,

                        )
                }

                Spacer(modifier = Modifier.height(32.dp))
                //create an account nav link
                Text(
                    text = "Already have an account? Login",
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

fun PasswordRules(pWord: String): List<Pair<String, Boolean>> {
    return listOf(
        "8–20 characters" to (pWord.length in 8..20),
        "No spaces" to pWord.none { it.isWhitespace() },
        "1 lowercase (a–z)" to pWord.any { it.isLowerCase() },
        "1 uppercase (A–Z)" to pWord.any { it.isUpperCase() },
        "1 number (0–9)" to pWord.any { it.isDigit() },
        "1 special character (!@#\$%^&*()_+-=)" to pWord.any { it in "!@#\$%^&*()_+-=" }
    )
}
