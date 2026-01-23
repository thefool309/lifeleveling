package com.lifeleveling.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.lifeleveling.app.R
import com.lifeleveling.app.data.Terms
import com.lifeleveling.app.ui.components.CircleButton
import com.lifeleveling.app.ui.components.HighlightCard
import com.lifeleveling.app.ui.components.SeparatorLine
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.data.FirestoreRepository.*


@Composable
fun termsAndConditionsScreen(db: FirebaseFirestore){


    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = AppTheme.colors.Background
    ){
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
            ){
                Text(
                    text = "Terms and Conditions",
                    color = AppTheme.colors.SecondaryOne,
                    style = AppTheme.textStyles.HeadingThree.copy(
                        shadow = Shadow(
                            color = AppTheme.colors.DropShadow,
                            offset = Offset(2f, 2f),
                            blurRadius = 2f,
                        )
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.weight(1f))
                CircleButton(
                    modifier = Modifier,
                    onClick = {},
                    imageVector = ImageVector.vectorResource(R.drawable.back_arrow),
                    size = 48.dp
                )
            }

            HighlightCard(
                modifier = Modifier
                    .fillMaxWidth(),
                outerPadding = 0.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

//                    Text(
//                        text = null,
//                        color = AppTheme.colors.Gray,
//                        style = AppTheme.textStyles.HeadingSix.copy(
//                            shadow = Shadow(
//                                color = AppTheme.colors.DropShadow,
//                                offset = Offset(2f, 2f),
//                                blurRadius = 2f,
//                            )
//                        ),
//                    )
                }
            }
        }
    }
}