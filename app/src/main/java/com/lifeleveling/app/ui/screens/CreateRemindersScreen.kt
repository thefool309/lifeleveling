package com.lifeleveling.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.components.*
import com.lifeleveling.app.ui.theme.AppTheme
import androidx.compose.runtime.rememberCoroutineScope
import com.lifeleveling.app.data.LocalNavController
import com.lifeleveling.app.data.LocalUserManager
import com.lifeleveling.app.data.buildReminderDraft
import java.util.Calendar


@Preview
@Composable
fun CreateReminderScreen(){
    val userManager = LocalUserManager.current
//    val userState by userManager.uiState.collectAsState()
    val navController = LocalNavController.current

    val scope = rememberCoroutineScope()
    val showCreateRemindersToolTip = remember { mutableStateOf(false) }
    var createdReminderTitle by remember { mutableStateOf("") } // Title for reminder string <-- This is needed
    var doNotRepeat by remember { mutableStateOf(false) }       // if it repeats bool       <-- This is needed
    var asDaily by remember { mutableStateOf(false) }           // does it repeat as a daily bool <-- This is needed
    var asWeekDay by remember { mutableStateOf(false) }         // does it repeat daily bool    <-- This is needed
    var indefinitelyRepeat by remember { mutableStateOf(false) } // does it repeat forever bool <-- This is needed
    var selectedReminderIndex by remember { mutableStateOf(0) } // selected icon for reminder   <-- This is needed
    val iconMenu = remember { mutableStateOf(false) }           // bool to show menu
    var selectedHour by remember { mutableStateOf(0) }          // selected hour for reminder   <-- This is needed
    val selectedHourMenu = remember { mutableStateOf(false) }   // bool to show hour menu
    val selectedMinuteMenu = remember { mutableStateOf(false) } // bool to show minute menu
    var selectedMinute by remember { mutableStateOf(0) }        // selected minute                 <-- This is needed
    var selectedAmOrPm by remember {mutableStateOf(0)}          // selected AM or PM                <-- This is needed
    val amOrPmOptionsMenu = remember {mutableStateOf(false) }   // menu for selecting am or pm
    var reminderAmountNumber by remember { mutableStateOf("") }       // how many times the reminder is set for ex 5 days , 5 weeks , 5 months, 5 years <-- This is needed
    val selectedReminderAmountMenu = remember { mutableStateOf(false) } // bool for menu
    var selectedReminderAmountHourDayWeek by remember { mutableStateOf(0) }        // the reminder is set for hours , days, week    <-- This is needed
    var repeatAmount by remember { mutableStateOf("") }                     // how many times to repeat the reminder text entered by user   <-- This is needed
    val selectedRepeatAmountMenu = remember { mutableStateOf(false) }       // bool to show menu
    var selectedRepeatAmount by remember { mutableStateOf(0) }              // menu selection for if the reminder is to repeat for days, weeks, months, years   <-- This is needed
    val iconOptions = listOf(
        R.drawable.water_drop,
        R.drawable.bed_color,
        R.drawable.shirt_color,
        R.drawable.med_bottle,
        R.drawable.shower_bath,
        R.drawable.shop_color,
        R.drawable.person_running,
        R.drawable.heart,
        R.drawable.bell,
        R.drawable.brain,
        R.drawable.document,
        R.drawable.doctor,
    )
    val iconNameOptions = listOf(
        "water_drop",    // 0
        "bed_color",     // 1
        "shirt_color",   // 2
        "med_bottle",    // 3
        "shower_bath",   // 4
        "shop_color",    // 5
        "person_running",// 6
        "heart",         // 7
        "bell",          // 8
        "brain",         // 9
        "document",      // 10
        "doctor"         // 11
    )
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

    Surface {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = AppTheme.colors.Background),
            contentAlignment = Alignment.Center
        ){
            Column(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ){
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ){
                    Text(
                        text = stringResource(R.string.add_reminders),
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
                    ShadowedIcon(
                        imageVector = ImageVector.vectorResource(R.drawable.info),
                        tint = AppTheme.colors.FadedGray,
                        modifier = Modifier
                            .size(20.dp)
                            .offset(y = 9.74.dp)
                            .clickable { showCreateRemindersToolTip.value = !showCreateRemindersToolTip.value }
                    )
                }


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
                            text = stringResource(R.string.title_colon),
                            color = AppTheme.colors.SecondaryOne,
                            style = AppTheme.textStyles.HeadingFive
                        )
                        CustomTextField(
                            value = createdReminderTitle,
                            onValueChange = { createdReminderTitle = it },
                            placeholderText = stringResource(R.string.reminder_title),
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ){
                            Text(
                                text = stringResource(R.string.icon),
                                color = AppTheme.colors.SecondaryOne,
                                style = AppTheme.textStyles.HeadingFive
                            )
                            DropDownIconGridMenu(
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
                                text = stringResource(R.string.starting_at),
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
                                    text = stringResource(R.string.hour_minute_colon),
                                    color = AppTheme.colors.SecondaryOne,
                                    style = AppTheme.textStyles.HeadingFive,
                                    modifier = Modifier

                                )
                                DropDownTextMenu(
                                    options = minutesOptions,
                                    selectedIndex = selectedMinute,
                                    onSelectedChange = {selectedMinute = it },
                                    expanded = selectedMinuteMenu,
                                    modifier = Modifier
                                        .weight(1f)
                                )
                                DropDownTextMenu(
                                    options = amOrPmOptions,
                                    selectedIndex = selectedAmOrPm,
                                    onSelectedChange = {selectedAmOrPm = it },
                                    expanded = amOrPmOptionsMenu,
                                    modifier = Modifier
                                        .weight(1f)
                                )
                            }

                        }
                        Column(
                            modifier = Modifier,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ){
                            Text(
                                text = stringResource(R.string.remind_me_every),
                                color = AppTheme.colors.SecondaryOne,
                                style = AppTheme.textStyles.HeadingFive
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ){
                                CustomTextField(
                                    value = reminderAmountNumber,
                                    onValueChange = {   newText ->
                                        reminderAmountNumber = newText
                                        if(newText.isNotEmpty()) {
                                            indefinitelyRepeat = false
                                            doNotRepeat = false
                                        }
                                    },
                                    placeholderText = "",
                                    inputFilter = { it.all { char -> char.isDigit() } },
                                    modifier = Modifier
                                        .weight(1f),

                                    )
                                DropDownTextMenu(
                                    options = hoursDaysWeeks,
                                    selectedIndex = selectedReminderAmountHourDayWeek,
                                    onSelectedChange = {selectedReminderAmountHourDayWeek = it },
                                    expanded = selectedReminderAmountMenu,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {

                                // As daily
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    CustomCheckbox(
                                        checked = asDaily,
                                        onCheckedChange = {
                                            asDaily = it
                                            if(it){
                                                doNotRepeat = false

                                                asWeekDay = false
                                            }
                                        }
                                    )
                                    Text(
                                        text = stringResource(R.string.checkbox_setdaily),
                                        style = AppTheme.textStyles.Default,
                                        color = AppTheme.colors.Gray
                                    )
                                }

                                // Do not repeat
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        CustomCheckbox(
                                            checked = asWeekDay,
                                            onCheckedChange = {
                                                asWeekDay = it
                                                if(it){
                                                    doNotRepeat = false
                                                    asDaily = false

                                                }
                                            }
                                        )
                                        Text(
                                            text = stringResource(R.string.checkbox_weekdays),
                                            style = AppTheme.textStyles.Default,
                                            color = AppTheme.colors.Gray
                                        )
                                    }
                                }
                            }
                        }
                        Column(
                            modifier = Modifier,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ){
                            Text(
                                text = stringResource(R.string.repeat_for),
                                color = AppTheme.colors.SecondaryOne,
                                style = AppTheme.textStyles.HeadingFive
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ){
                                CustomTextField(
                                    value = repeatAmount,
                                    onValueChange = {   newText ->
                                        repeatAmount = newText
                                        if(newText.isNotEmpty()) {
                                            indefinitelyRepeat = false
                                            doNotRepeat = false
                                        }
                                    },
                                    placeholderText = "",
                                    inputFilter = { it.all { char -> char.isDigit() } },
                                    modifier = Modifier
                                        .weight(1f),

                                    )
                                DropDownTextMenu(
                                    options = daysWeeksMonthsYearsList,
                                    selectedIndex = selectedRepeatAmount,
                                    onSelectedChange = {selectedRepeatAmount = it },
                                    expanded = selectedRepeatAmountMenu,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {

                                // Indefinitely repeat
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    CustomCheckbox(
                                        checked = indefinitelyRepeat,
                                        onCheckedChange = {
                                            indefinitelyRepeat = it
                                            if(it){
                                                doNotRepeat = false

                                            }
                                        }
                                    )
                                    Text(
                                        text = stringResource(R.string.repeats_indefinitely),style = AppTheme.textStyles.Default,
                                        color = AppTheme.colors.Gray
                                    )
                                }

                                // Do not repeat
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        CustomCheckbox(
                                            checked = doNotRepeat,
                                            onCheckedChange = {
                                                doNotRepeat = it
                                                if(it){
                                                    indefinitelyRepeat = false

                                                    asWeekDay = false
                                                    asDaily = false
                                                }
                                            }
                                        )
                                        Text(
                                            text = stringResource(R.string.do_not_repeat),
                                            style = AppTheme.textStyles.Default,
                                            color = AppTheme.colors.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CustomButton(
                        width = 120.dp,
                        onClick = {
                            navController.popBackStack()
                        },
                        backgroundColor = AppTheme.colors.Error75,
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            style = AppTheme.textStyles.HeadingSix,
                            color = AppTheme.colors.Background
                        )
                    }
                    Spacer(Modifier.width(20.dp))
                    CustomButton(
                        width = 120.dp,
                        onClick = {
                            // Basic validation that we can build upon if needed
                            if (createdReminderTitle.isBlank()){
                                userManager.logger.w("Reminders", "CreateReminderScreen: title is blank, not saving.")
                                return@CustomButton
                            }

                            // Build the draft with all proper calculations
                            val draft = buildReminderDraft(
                                title = createdReminderTitle.trim(),
                                selectedHour = selectedHour,
                                selectedMinute = selectedMinute,
                                selectedAmOrPm = selectedAmOrPm,
                                selectedReminderIndex = selectedReminderIndex,
                                iconNameOptions = iconNameOptions,
                                asDaily = asDaily,
                                asWeekDay = asWeekDay,
                                reminderAmountNumber = reminderAmountNumber,
                                selectedReminderAmountHourDayWeek = selectedReminderAmountHourDayWeek,
                                doNotRepeat = doNotRepeat,
                                indefinitelyRepeat = indefinitelyRepeat,
                                repeatAmount = repeatAmount,
                                selectedRepeatAmount = selectedRepeatAmount,
                            )

                            userManager.addReminder(draft)

                            navController.popBackStack()
                            // TODO: show a user-facing error dialog box
                        },
                        backgroundColor = AppTheme.colors.Success75,
                    ) {
                        Text(
                            text = stringResource(R.string.create),
                            style = AppTheme.textStyles.HeadingSix,
                            color = AppTheme.colors.Background
                        )
                    }
                }
            }
        }





















        /**
         *This is the sacred whitespace - do not remove
         */





























    }
    if(showCreateRemindersToolTip.value){
        CreateRemindersToolTip(showCreateRemindersToolTip)
    }

}

