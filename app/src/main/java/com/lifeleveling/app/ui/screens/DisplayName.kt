package com.lifeleveling.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.components.CustomButton
import com.lifeleveling.app.ui.components.CustomCheckbox
import com.lifeleveling.app.ui.components.CustomTextField
import com.lifeleveling.app.ui.components.HighlightCard
import com.lifeleveling.app.ui.theme.AppTheme

@Preview
@Composable
fun DisplayNameCreation(

){
    val displayName = rememberSaveable { mutableStateOf("") }
    val tutorial = rememberSaveable { mutableStateOf(true) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AppTheme.colors.Background)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            //logo
            Image(
                painter = painterResource(id = R.drawable.ll_life_tree),
                contentDescription = "logo",
                modifier = Modifier
                    .size(100.dp)
            )
            Text(
                "Create a Username",
                color = AppTheme.colors.BrandOne,
                style = AppTheme.textStyles.HeadingFour,
                textAlign = TextAlign.Center
            )
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
                    CustomTextField(
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
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        CustomCheckbox(
                            checked = tutorial.value,
                            onCheckedChange = {
                                tutorial.value = it
                            }
                        )
                        Text(
                            text = "Start Tutorial", style = AppTheme.textStyles.Default,
                            color = AppTheme.colors.Gray
                        )

                    }
                    Text(
                        text = "*Tutorial can be started from settings at a later time", style = AppTheme.textStyles.Small,
                        color = AppTheme.colors.Gray
                    )
                    // Join

                    CustomButton(
                        onClick = {},
                        enabled = !displayName.value.isEmpty(),
                        content = {
                            Text(
                                "Create",
                                color = AppTheme.colors.DarkerBackground,
                                style = AppTheme.textStyles.HeadingSix
                            )
                        }
                    )
                }
            }
        }
    }
}