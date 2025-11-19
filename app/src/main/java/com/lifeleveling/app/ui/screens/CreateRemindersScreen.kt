package com.lifeleveling.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.play.integrity.internal.a
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.components.*
import com.lifeleveling.app.ui.theme.AppTheme


@Preview
@Composable
fun CreateReminderScreen(

){
    var createdReminderTitle by remember { mutableStateOf("") }
    var doNotRepeat by remember { mutableStateOf(false) }
    var indefinitelyRepeat by remember { mutableStateOf(false) }
    var selectedReminderIndex by remember { mutableStateOf(0) }
    val iconMenu = remember { mutableStateOf(false) }
    var selectedHour by remember { mutableStateOf(0) }
    val selectedHourMenu = remember { mutableStateOf(false) }
    val selectedMinuteMenu = remember { mutableStateOf(false) }
    var selectedMinute by remember { mutableStateOf(0) }
    val selectedAmOrPm by remember {mutableStateOf(0)}
    val amOrPmOptionsMenu = remember {mutableStateOf(false) }
    var reminderAmount by remember { mutableStateOf("") }
    val selectedrepeatAmountMenu = remember { mutableStateOf(false) }
    var selectedrepeatAmount by remember { mutableStateOf(0) }
    val iconOptions = listOf(
        Reminder(0, "", R.drawable.water_drop, null, false, 0, 0, 0),
        Reminder(1, "", R.drawable.bed_color, null, false, 0, 0, 0),
        Reminder(2, "", R.drawable.med_bottle, null, false, 0, 0, 0),
        Reminder(3, "", R.drawable.shower_bath, null, false, 0, 0, 0),
        Reminder(4, "", R.drawable.shop_color, null, false, 0, 0, 0),
        Reminder(5, "", R.drawable.person_running, null, false, 0, 0, 0),
        Reminder(6, "", R.drawable.heart, null, false, 0, 0, 0),
        Reminder(7, "", R.drawable.bell, null, false, 0, 0, 0),
        Reminder(8, "", R.drawable.brain, null, false, 0, 0, 0),
        Reminder(9, "", R.drawable.document, null, false, 0, 0, 0),
        Reminder(10, "", R.drawable.doctor, null, false, 0, 0, 0),
    )
    val hourOptions = listOf( "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11","12")
    val minutesOptions = listOf("00", "01".."59")
    val amOrPmOptions = listOf(
        "AM", "PM"
    )

    val repeatAmount = listOf(
        "Days",
        "Weeks",
        "Months",
        "Years",
    )

    Surface(

    ){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = AppTheme.colors.Background),
            contentAlignment = Alignment.Center
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ){
                Text(
                    text = "Add Reminders",
                    color = AppTheme.colors.SecondaryOne,
                    style = AppTheme.textStyles.HeadingThree.copy(
                        shadow = Shadow(
                            color = AppTheme.colors.DropShadow,
                            offset = Offset(3f, 4f),
                            blurRadius = 6f,
                        )
                    )
                )

                HighlightCard(
                    modifier = Modifier
                        .fillMaxWidth(),
                    outerPadding = 8.dp
                ){
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        ){
                        Text(
                            text = "Title:",
                            color = AppTheme.colors.SecondaryOne,
                            style = AppTheme.textStyles.HeadingFive
                        )
                        CustomTextField(
                            value = createdReminderTitle,
                            onValueChange = { createdReminderTitle = it },
                            placeholderText = "Reminder Title",
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ){
                            Text(
                                text = "Icon:",
                                color = AppTheme.colors.SecondaryOne,
                                style = AppTheme.textStyles.HeadingFive
                            )
                            DropDownReminderMenu(
                                modifier = Modifier
                                    .width(108.dp)
                                    .height(32.dp)
                                    .padding(horizontal = 16.dp),
                                options = iconOptions,
                                selectedIndex = selectedReminderIndex,
                                onSelectedChange = { selectedReminderIndex = it },
                                expanded = iconMenu
                            )
                        }
                        Column(
                            modifier = Modifier,
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                        ){
                            Text(
                                text = "Starting at:",
                                color = AppTheme.colors.SecondaryOne,
                                style = AppTheme.textStyles.HeadingFive
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ){
                                DropDownTextMenu(
                                    options = hourOptions,
                                    selectedIndex = selectedHour,
                                    onSelectedChange = {selectedHour = it },
                                    expanded = selectedHourMenu,
                                    modifier = Modifier
                                        .weight(1f),
                                )

                                Text(
                                    text = ":",
                                    color = AppTheme.colors.SecondaryOne,
                                    style = AppTheme.textStyles.HeadingFive,
                                    modifier = Modifier

                                )
                                DropDownTextMenu(
                                    options = minutesOptions as List<String>,
                                    selectedIndex = selectedMinute,
                                    onSelectedChange = {selectedMinute = it },
                                    expanded = selectedMinuteMenu,
                                    modifier = Modifier
                                        .weight(1f),


                                )
                                DropDownTextMenu(
                                    options = amOrPmOptions,
                                    selectedIndex = selectedAmOrPm,
                                    onSelectedChange = {selectedMinute = it },
                                    expanded = amOrPmOptionsMenu,
                                    modifier = Modifier
                                        .weight(1f),


                                )
                            }

                        }
                        Column(
                            modifier = Modifier,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ){
                            Text(
                                text = "Remind me every:",
                                color = AppTheme.colors.SecondaryOne,
                                style = AppTheme.textStyles.HeadingFive
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ){
                                CustomTextField(
                                    value = reminderAmount,
                                    onValueChange = { reminderAmount = it },
                                    placeholderText = "",
                                    inputFilter = { it.all { char -> char.isDigit() } },
                                    modifier = Modifier
                                           .weight(1f),

                                )
                                DropDownTextMenu(
                                    options = repeatAmount,
                                    selectedIndex = selectedrepeatAmount,
                                    onSelectedChange = {selectedrepeatAmount = it },
                                    expanded = selectedrepeatAmountMenu,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                        Column(

                        ){
                            Text(
                                text = "Repeat for:",
                                color = AppTheme.colors.SecondaryOne,
                                style = AppTheme.textStyles.HeadingFive
                            )
                        }
                    }
                }
            }

        }
    }
}

