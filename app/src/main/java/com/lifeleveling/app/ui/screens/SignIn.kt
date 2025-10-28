package com.lifeleveling.app.ui.screens


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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.theme.AppTheme


@Preview(showBackground = true)
@Composable
fun SignIn(
    onLogin: () -> Unit = {println("Login pressed")},
    onGoogleLogin: () -> Unit = {println("Google Login pressed")},
    onCreateAccount: () -> Unit = {println("Create account pressed")},
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
                .fillMaxSize()
        ){
            //logo
            Image(
                painter = painterResource(id= R.drawable.ll_circle_logo_dots),
                contentDescription = "logo",
                modifier = Modifier
                    .width(300.dp)
                    .height(300.dp)
            )

            //inner box holding text fields
            Box( modifier = Modifier
                .height(350.dp)
                .width(300.dp)
                .background(color = AppTheme.colors.DarkerBackground),
                contentAlignment = Alignment.Center
            ){
                //column keeping box items centered
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
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
                            if (email.isEmpty()) {
                                Text("Email can not be empty", style = AppTheme.textStyles.Small)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    //password
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(0.9f),
                        value = password,
                        onValueChange = {password = it},
                        label = { Text("Password", color = AppTheme.colors.Gray,style = AppTheme.textStyles.HeadingFive) },
                        placeholder = { Text("Password", color = AppTheme.colors.Gray,style = AppTheme.textStyles.HeadingFive) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                       supportingText = {
                           if (password.isEmpty()) {
                               Text("Password can not be empty", style = AppTheme.textStyles.Small)
                           }
                       }
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    //login button
                    Button(
                        modifier = Modifier
                            .width(120.dp),
                            onClick = onLogin,
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.SecondaryTwo),
                        enabled = email.isNotEmpty() && password.isNotEmpty()
                        ) {
                        Text("Login", color = AppTheme.colors.DropShadow,style = AppTheme.textStyles.HeadingSix, fontSize = 16.sp)
                    }
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
                    text = "Create an Account",
                    color = AppTheme.colors.SecondaryThree,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    style = AppTheme.textStyles.Default,
                    modifier = Modifier.clickable { onCreateAccount() }
                )
            }
        }
    }
}