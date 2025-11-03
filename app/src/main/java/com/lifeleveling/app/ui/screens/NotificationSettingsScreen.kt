package com.lifeleveling.app.ui.screens

import androidx.compose.foundation.clickable
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
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.theme.AppTheme
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
                Spacer(modifier = Modifier.width(52.dp))
                ShadowedIcon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.back_arrow),
                    contentDescription = "Bell icon",
                    tint = AppTheme.colors.SecondaryThree,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.CenterVertically)
                        .clickable { navController?.popBackStack() }
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
                        text = "Notification Sound:",
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
                        //checkbox for vibrate only
//                        Checkbox(
//                            checked = TODO(),
//                            onCheckedChange = TODO(),
//                            modifier = TODO(),
//                            enabled = TODO(),
//                            colors = TODO(),
//                            interactionSource = TODO()
//                        )
//                        //checkbox for no sound and dont vibrate
//                        Checkbox(
//                            checked = TODO(),
//                            onCheckedChange = TODO(),
//                            modifier = TODO(),
//                            enabled = TODO(),
//                            colors = TODO(),
//                            interactionSource = TODO()
//                        )
                    }
                    Text(
                        text = "When to Notify:",
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
//                    Checkbox(
//                        checked = TODO(),
//                        onCheckedChange = TODO(),
//                        modifier = TODO(),
//                        enabled = TODO(),
//                        colors = TODO(),
//                        interactionSource = TODO()
//                    )
//                    //checkbox for at time
//                    Checkbox(
//                        checked = TODO(),
//                        onCheckedChange = TODO(),
//                        modifier = TODO(),
//                        enabled = TODO(),
//                        colors = TODO(),
//                        interactionSource = TODO()
//                    )
//                    // checkbox for when character levels up
//                    Checkbox(
//                        checked = TODO(),
//                        onCheckedChange = TODO(),
//                        modifier = TODO(),
//                        enabled = TODO(),
//                        colors = TODO(),
//                        interactionSource = TODO()
//                    )
//                    //checkbox for when health runs out
//                    Checkbox(
//                        checked = TODO(),
//                        onCheckedChange = TODO(),
//                        modifier = TODO(),
//                        enabled = TODO(),
//                        colors = TODO(),
//                        interactionSource = TODO()
//                    )



                }
            }
        }
    }
}



