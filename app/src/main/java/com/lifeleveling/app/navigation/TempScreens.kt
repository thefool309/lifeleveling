package com.lifeleveling.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.theme.CustomButton

@Composable
fun TempCalendarScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.Background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.calendar),
            contentDescription = null,
            tint = AppTheme.colors.BrandTwo,
            modifier = Modifier.size(80.dp)
        )
        Text(
            text = "Calendar screen",
            color = AppTheme.colors.Gray,
            style = AppTheme.textStyles.HeadingThree
        )
    }
}

@Composable
fun TempStatsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.Background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.bar_graph),
            contentDescription = null,
            tint = AppTheme.colors.BrandTwo,
            modifier = Modifier.size(80.dp)
        )
        Text(
            text = "Stats screen",
            color = AppTheme.colors.Gray,
            style = AppTheme.textStyles.HeadingThree
        )
    }
}

@Composable
fun TempHomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.Background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.person),
            contentDescription = null,
            tint = AppTheme.colors.BrandTwo,
            modifier = Modifier.size(80.dp)
        )
        Text(
            text = "Home screen",
            color = AppTheme.colors.Gray,
            style = AppTheme.textStyles.HeadingThree
        )
    }
}

@Composable
fun TempStreaksScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.Background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.award_ribbon),
            contentDescription = null,
            tint = AppTheme.colors.BrandTwo,
            modifier = Modifier.size(80.dp)
        )
        Text(
            text = "Streaks screen",
            color = AppTheme.colors.Gray,
            style = AppTheme.textStyles.HeadingThree
        )
    }
}

@Composable
fun TempSettingsScreen(
    onSignOut: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.Background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CustomButton(
            onClick = onSignOut,
            backgroundColor = AppTheme.colors.Error75
        ) {
            Text(
                "Sign Out",
                color = AppTheme.colors.DarkerBackground,
                style = AppTheme.textStyles.HeadingSix,
            )
        }
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.settings_icon),
            contentDescription = null,
            tint = AppTheme.colors.BrandTwo,
            modifier = Modifier.size(80.dp)
        )
        Text(
            text = "Settings screen",
            color = AppTheme.colors.Gray,
            style = AppTheme.textStyles.HeadingThree
        )
    }
}