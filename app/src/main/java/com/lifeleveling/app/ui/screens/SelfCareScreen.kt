package com.lifeleveling.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.lifeleveling.app.ui.theme.HighlightCard
import com.lifeleveling.app.ui.theme.ShadowedIcon

@Preview
@Composable
fun SelfCareScreen(
    navController: NavController? = null,
){
val context = LocalContext.current
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = AppTheme.colors.Background
    ){
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = stringResource(R.string.selfCareTips),
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


                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier

                    ) {


                        Spacer(modifier = Modifier.size(16.dp))
                        Text(
                            text = stringResource(R.string.suggestedReminders),
                            color = AppTheme.colors.Gray,
                            style = AppTheme.textStyles.HeadingSix.copy(
                                shadow = Shadow(
                                    color = AppTheme.colors.DropShadow,
                                    offset = Offset(3f, 4f),
                                    blurRadius = 6f,
                                )
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)

                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier

                            .fillMaxWidth()
                            .height(1.dp)
                            .background(AppTheme.colors.Gray)
                            .padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier) {


                        Spacer(modifier = Modifier.size(16.dp))
                        Text(
                            text = stringResource(R.string.importanceOfSelfCare),
                            color = AppTheme.colors.Gray,
                            style = AppTheme.textStyles.HeadingSix.copy(
                                shadow = Shadow(
                                    color = AppTheme.colors.DropShadow,
                                    offset = Offset(3f, 4f),
                                    blurRadius = 6f,
                                )
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)

                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier

                            .fillMaxWidth()
                            .height(1.dp)
                            .background(AppTheme.colors.Gray)
                            .padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier) {


                        Spacer(modifier = Modifier.size(16.dp))
                        Text(
                            text = stringResource(R.string.extraArticles),
                            color = AppTheme.colors.Gray,
                            style = AppTheme.textStyles.HeadingSix.copy(
                                shadow = Shadow(
                                    color = AppTheme.colors.DropShadow,
                                    offset = Offset(3f, 4f),
                                    blurRadius = 6f,
                                )
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)

                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier

                            .fillMaxWidth()
                            .height(1.dp)
                            .background(AppTheme.colors.Gray)
                            .padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier) {


                        Spacer(modifier = Modifier.size(16.dp))
                        Text(
                            text = stringResource(R.string.needAdive),
                            color = AppTheme.colors.Gray,
                            style = AppTheme.textStyles.HeadingSix.copy(
                                shadow = Shadow(
                                    color = AppTheme.colors.DropShadow,
                                    offset = Offset(3f, 4f),
                                    blurRadius = 6f,
                                )
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .clickable {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.healthline.com/wellness"))
                                    context.startActivity(intent)
                                },

                            )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                }
            }
        }
    }
}