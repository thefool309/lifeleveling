package com.lifeleveling.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.booleanResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.components.CustomButton
import com.lifeleveling.app.ui.components.CustomCheckbox
import com.lifeleveling.app.ui.components.HighlightCard
import com.lifeleveling.app.ui.components.SeparatorLine
import com.lifeleveling.app.ui.components.ShadowedIcon
import com.lifeleveling.app.ui.components.TestUser
import com.lifeleveling.app.ui.theme.AppTheme
import kotlin.collections.forEach

@Preview
@Composable
fun MyRemindersScreen(
    navController: NavController? = null
){
    val hourOptions = stringArrayResource(R.array.hour_array).toList()
    val minutesOptions = stringArrayResource(R.array.minutes_array).toList()
    val amOrPmOptions = listOf(
        stringResource(R.string.am),
        stringResource(R.string.pm),
    )
    val hoursDaysWeeks = listOf(
        stringResource(R.string.hours),
        stringResource(R.string.days),
        stringResource(R.string.weeks)
    )
    val daysWeeksMonthsYearsList = listOf(
        stringResource(R.string.days),
        stringResource(R.string.weeks),
        stringResource(R.string.months),
        stringResource(R.string.years)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AppTheme.colors.Background)
            .padding(8.dp)

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart),
            verticalArrangement = Arrangement.spacedBy(8.dp),

            ) {
            Text(
                text = "My Reminders",
                color = AppTheme.colors.SecondaryOne,
                style = AppTheme.textStyles.HeadingThree.copy(
                    shadow = Shadow(
                        color = AppTheme.colors.DropShadow,
                        offset = Offset(3f, 4f),
                        blurRadius = 6f,
                    )
                ),
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            )

            HighlightCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                outerPadding = 0.dp
            ) {
                val isEnabled = remember {
                    mutableStateListOf<Boolean>().apply {
                        addAll(List(TestUser.calendarReminders.size) { true })
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ){
                    TestUser.calendarReminders.forEachIndexed { index ,calReminder ->
                        val hour = hourOptions[calReminder.selectedHours]
                        val minutes = minutesOptions[calReminder.selectedMinutes]
                        val ampm = amOrPmOptions[calReminder.amOrPm]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {

                            ShadowedIcon(
                                modifier = Modifier
                                    .size(32.dp),
                                imageVector = ImageVector.vectorResource(id=calReminder.icon),
                                contentDescription = null,
                                tint = Color.Unspecified

                            )
                            Spacer(Modifier.width(20.dp))
                            Text(
                                text = calReminder.name,
                                style = AppTheme.textStyles.HeadingSix,
                                color = AppTheme.colors.Gray
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "$hour:$minutes $ampm",
                                style = AppTheme.textStyles.HeadingSix,
                                color = AppTheme.colors.Gray
                            )

                            Spacer(Modifier.weight(1f))
                            Box(
                                modifier = Modifier

                            ){
                                CustomCheckbox(
                                    checked = isEnabled[index],
                                    onCheckedChange = { newValue ->
                                        isEnabled[index] = newValue
                                    }
                                )
                            }



                        }

                        if (index < TestUser.calendarReminders.lastIndex) {
                            SeparatorLine()
                        }
                    }

                }

            }
        }
    }
}

