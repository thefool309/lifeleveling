package com.lifeleveling.app.ui.screens

import android.media.RingtoneManager
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.ui.theme.CircleButton
import com.lifeleveling.app.ui.theme.CustomCheckbox
import com.lifeleveling.app.ui.components.HighlightCard
import com.lifeleveling.app.ui.components.ShadowedIcon

@Preview
@Composable
fun NotificationScreen(
    navController: NavController? = null,
){

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = AppTheme.colors.Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
            ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ){
                Text(
                    text = "Notifications",
                    color = AppTheme.colors.SecondaryOne,
                    style = AppTheme.textStyles.HeadingThree.copy(
                        shadow = Shadow(
                            color = AppTheme.colors.DropShadow,
                            offset = Offset(3f, 4f),
                            blurRadius = 6f,
                        )
                    ),
                )
                Spacer(modifier = Modifier.weight(1f))
                CircleButton(
                    modifier = Modifier,
                    onClick = {navController?.popBackStack()},
                    imageVector = ImageVector.vectorResource(R.drawable.back_arrow)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            HighlightCard(
                modifier = Modifier
                    .fillMaxWidth(),
                outerPadding = 0.dp


            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.notificonSound),
                        color = AppTheme.colors.Gray,
                        style = AppTheme.textStyles.HeadingSix.copy(
                            shadow = Shadow(
                                color = AppTheme.colors.DropShadow,
                                offset = Offset(3f, 4f),
                                blurRadius = 6f,
                            )
                        ),
                    )

                    Row(){
                        CustomCheckbox(
                            modifier = TODO(),
                            checked = TODO(),
                            onCheckedChange = TODO(),
                            size = TODO(),
                            cornerRadius = TODO(),
                            mainColor = TODO(),
                            checkColor = TODO()
                        )
                        //checkbox for no sound and dont vibrate
                        CustomCheckbox(
                        modifier = TODO(),
                        checked = TODO(),
                        onCheckedChange = TODO(),
                        size = TODO(),
                        cornerRadius = TODO(),
                        mainColor = TODO(),
                        checkColor = TODO()
                    )
                    }
                    Text(
                        text = stringResource(R.string.whenToNotify),
                        color = AppTheme.colors.Gray,
                        style = AppTheme.textStyles.HeadingSix.copy(
                            shadow = Shadow(
                                color = AppTheme.colors.DropShadow,
                                offset = Offset(3f, 4f),
                                blurRadius = 6f,
                            )
                        ),
                    )
                    //checkbox for reminder times
                    CustomCheckbox(
                        modifier = TODO(),
                        checked = TODO(),
                        onCheckedChange = TODO(),
                        size = TODO(),
                        cornerRadius = TODO(),
                        mainColor = TODO(),
                        checkColor = TODO()
                    )
                    //checkbox for at time
                     CustomCheckbox(
                        modifier = TODO(),
                        checked = TODO(),
                        onCheckedChange = TODO(),
                        size = TODO(),
                        cornerRadius = TODO(),
                        mainColor = TODO(),
                        checkColor = TODO()
                    )
                    // checkbox for when character levels up
                     CustomCheckbox(
                        modifier = TODO(),
                        checked = TODO(),
                        onCheckedChange = TODO(),
                        size = TODO(),
                        cornerRadius = TODO(),
                        mainColor = TODO(),
                        checkColor = TODO()
                    )
                    //checkbox for when health runs out
                     CustomCheckbox(
                        modifier = TODO(),
                        checked = TODO(),
                        onCheckedChange = TODO(),
                        size = TODO(),
                        cornerRadius = TODO(),
                        mainColor = TODO(),
                        checkColor = TODO()
                    )



                }
            }
        }
    }
}



