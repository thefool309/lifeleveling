package com.lifeleveling.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lifeleveling.app.R
import com.lifeleveling.app.data.LocalNavController
import com.lifeleveling.app.ui.components.CircleButton
import com.lifeleveling.app.ui.components.HighlightCard
import com.lifeleveling.app.ui.components.ScrollFadeEdges
import com.lifeleveling.app.ui.components.SeparatorLine
import com.lifeleveling.app.ui.components.ShadowedIcon
import com.lifeleveling.app.ui.theme.AppTheme

@Preview
@Composable
fun selfcareArticlesScreenFun() {
    val navController = LocalNavController.current

    val scrollState = rememberScrollState()
    val websites = listOf<WebsiteDataClass>(
        WebsiteDataClass(
            name = "Healthline Wellness Topics",
            url = "https://www.healthline.com/wellness",
            description = "Healthline Wellness Topics offers solutions and tips for a multitude of topics"
        ),

    )
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AppTheme.colors.Background)
            .padding(16.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Title
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "Self-Care Articles",
                    color = AppTheme.colors.SecondaryOne,
                    style = AppTheme.textStyles.HeadingThree.copy(
                        shadow = Shadow(
                            color = AppTheme.colors.DropShadow,
                            offset = Offset(2f, 2f),
                            blurRadius = 2f,
                        )
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.Top)
                )
                Spacer(modifier = Modifier.width(16.dp))
                CircleButton(
                    modifier = Modifier.align(Alignment.Top),
                    onClick = {navController.popBackStack()},
                    imageVector = ImageVector.vectorResource(R.drawable.back_arrow),
                    size = 48.dp
                )
            }

            // Body box
            HighlightCard(
                modifier = Modifier.fillMaxWidth(),
                outerPadding = 0.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    websites.forEachIndexed { index, (name, url, description) ->
                        Text(
                            text = name,
                            textAlign = TextAlign.Center,
                            style = AppTheme.textStyles.HeadingFive.copy(textDecoration = TextDecoration.Underline),
                            color = AppTheme.colors.BrandOne,
                            modifier = Modifier
                                .clickable {
                                   val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.healthline.com/wellness"))
                                    context.startActivity(intent)
                                },
                        )
                        Text(
                            text = description,
                            textAlign = TextAlign.Start,
                            style = AppTheme.textStyles.HeadingSix,
                            color = AppTheme.colors.Gray,
                            modifier = Modifier

                        )
                        if (index < websites.lastIndex) {
                            SeparatorLine()
                        }
                    }
                }
                ScrollFadeEdges(
                    scrollState = scrollState,
                )
            }
        }
    }
}

data class WebsiteDataClass(
    val name: String,
    val url: String,
    val description: String,
)