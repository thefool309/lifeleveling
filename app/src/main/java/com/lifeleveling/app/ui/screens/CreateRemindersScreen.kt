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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kizitonwose.calendar.core.YearMonth
import com.kizitonwose.calendar.core.lengthOfMonth
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.components.*
import com.lifeleveling.app.ui.theme.AppTheme
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale
import com.lifeleveling.app.data.FirestoreRepository
import com.lifeleveling.app.util.ILogger
import com.lifeleveling.app.util.AndroidLogger
import androidx.compose.runtime.rememberCoroutineScope
import com.lifeleveling.app.data.Reminders
import kotlinx.coroutines.launch
import com.google.firebase.Timestamp
import java.util.Calendar


@Preview
@Composable
fun CreateReminderScreen(
    navController: NavController? = null,
    repo: FirestoreRepository = FirestoreRepository(),
    logger: ILogger = AndroidLogger(),
){

    val scope = rememberCoroutineScope()
    val showCreateRemindersToolTip = remember { mutableStateOf(false) }
    var createdReminderTitle by remember { mutableStateOf("") } // Title for reminder string <-- This is needed
    //var doNotRepeat by remember { mutableStateOf(false) }       // if it repeats bool       <-- This is needed            @Todo Stephen Commented this out as not used
    var asDaily by remember { mutableStateOf(false) }           // does it repeat as a daily bool <-- This is needed
    //var asWeekDay by remember { mutableStateOf(false) }         // does it repeat daily bool    <-- This is needed        @Todo Stephen Commented this out as not used
    var repeatReminder by remember { mutableStateOf(false) } // does it repeat forever bool <-- This is needed
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
        Reminder(0, "", R.drawable.water_drop, null, false, 0, 0, 0),
        Reminder(1, "", R.drawable.bed_color, null, false, 0, 0, 0),
        Reminder(2, "", R.drawable.shirt_color, null, false, 0, 0, 0),
        Reminder(3, "", R.drawable.med_bottle, null, false, 0, 0, 0),
        Reminder(4, "", R.drawable.shower_bath, null, false, 0, 0, 0),
        Reminder(5, "", R.drawable.shop_color, null, false, 0, 0, 0),
        Reminder(6, "", R.drawable.person_running, null, false, 0, 0, 0),
        Reminder(7, "", R.drawable.heart, null, false, 0, 0, 0),
        Reminder(8, "", R.drawable.bell, null, false, 0, 0, 0),
        Reminder(9, "", R.drawable.brain, null, false, 0, 0, 0),
        Reminder(10, "", R.drawable.document, null, false, 0, 0, 0),
        Reminder(11, "", R.drawable.doctor, null, false, 0, 0, 0)
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
    val hoursOrMins = listOf(
        stringResource(R.string.minutesShort),
        stringResource(R.string.hours),

    )
    val daysWeeksMonthsYearsList = listOf(
        stringResource(R.string.days),
        stringResource(R.string.weeks),
        stringResource(R.string.months),
        stringResource(R.string.years)
    )

    val today = LocalDate.now()                                                             // Current date
    val monthList = (1..12).map { monthNumber ->
        Month.of(monthNumber).getDisplayName(TextStyle.SHORT, Locale.getDefault())   // Months list
    }
    val startYear = today.year                                                               // Years list (current year + 5)
    val endYear = startYear + 5
    val yearList = (startYear..endYear).toList()
    var selectedDay by remember { mutableStateOf(today.dayOfMonth - 1) }             // Default selections
    var selectedMonth by remember { mutableStateOf(today.monthValue - 1) }
    var selectedYear by remember { mutableStateOf(0) }
    val actualYear = yearList[selectedYear]                                                 // Actual selected values
    val actualMonth = selectedMonth + 1
    val daysInMonth = YearMonth(actualYear, actualMonth).lengthOfMonth()    // Days in selected month/year
    val dayList = (1..daysInMonth).map { day ->
        SuffixForDays(day)
    }
    val selectedMonthMenu = remember { mutableStateOf(false) }

    val selectedDayMenu = remember { mutableStateOf(false) }
    val selectedYearMenu = remember { mutableStateOf(false) }
    var selectedColorIndex by remember { mutableStateOf(0) }
    val colorMenu = remember { mutableStateOf(false) }
    val colorOptions = listOf(
        Color.Red,
        Color.Blue,
        Color.Green,
        Color.Magenta,
        Color.Yellow,
        Color.Cyan,
        Color.LightGray,
        Color.White
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
                            DropDownReminderMenu(
                                modifier = Modifier
                                    .width(108.dp)
                                    .height(32.dp),

                                options = iconOptions,
                                selectedIndex = selectedReminderIndex,
                                onSelectedChange = { selectedReminderIndex = it },
                                expanded = iconMenu
                            )
                            Text(
                                text = stringResource(R.string.color),
                                color = AppTheme.colors.SecondaryOne,
                                style = AppTheme.textStyles.HeadingFive
                            )

                            DropDownColorMenu(
                                modifier = Modifier
                                    .width(108.dp)
                                    .height(32.dp),
                                colors = colorOptions,
                                selectedIndex = selectedColorIndex,
                                onSelectedChange = { selectedColorIndex = it },
                                expanded = colorMenu
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
                                    options = monthList,
                                    selectedIndex = selectedMonth,
                                    onSelectedChange = {selectedMonth = it },
                                    expanded = selectedMonthMenu,
                                    modifier = Modifier
                                        .weight(1f),
                                )
                                DropDownTextMenu(
                                    options = dayList,
                                    selectedIndex = selectedDay,
                                    onSelectedChange = {selectedDay = it },
                                    expanded = selectedDayMenu,
                                    modifier = Modifier
                                        .weight(1f),
                                )
                                DropDownTextMenu(
                                    options = yearList.map {it.toString()},
                                    selectedIndex = selectedYear,
                                    onSelectedChange = {selectedYear = it },
                                    expanded = selectedYearMenu,
                                    modifier = Modifier
                                        .weight(1f),
                                )
                            }
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
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                CustomCheckbox(
                                    checked = asDaily,
                                    onCheckedChange = {
                                        asDaily = it
                                    }
                                )
                                Text(
                                    text = stringResource(R.string.checkbox_setdaily), style = AppTheme.textStyles.Default,
                                    color = AppTheme.colors.Gray
                                )
                            }
                            if(asDaily) {
                                Text(
                                    text = stringResource(R.string.remind_me_every),
                                    color = AppTheme.colors.SecondaryOne,
                                    style = AppTheme.textStyles.HeadingFive
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {

                                    CustomTextField(
                                        value = reminderAmountNumber,
                                        onValueChange = { newText ->
                                            reminderAmountNumber = newText
                                            if (newText.isNotEmpty()) {
                                                repeatReminder = false
                                                //doNotRepeat = false
                                            }
                                        },
                                        placeholderText = "",
                                        inputFilter = { it.all { char -> char.isDigit() } },
                                        modifier = Modifier
                                            .weight(1f),

                                        )
                                    DropDownTextMenu(
                                        options = hoursOrMins,
                                        selectedIndex = selectedReminderAmountHourDayWeek,
                                        onSelectedChange = { selectedReminderAmountHourDayWeek = it },
                                        expanded = selectedReminderAmountMenu,
                                        modifier = Modifier.weight(1f),
                                    )


                                }
                            }

                        }
                        Column(
                            modifier = Modifier,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ){
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                CustomCheckbox(
                                    checked = repeatReminder,
                                    onCheckedChange = {
                                        repeatReminder = it
//                                        if (it) {
//                                            //doNotRepeat = false
//
//                                        }
                                    }
                                )
                                Text(
                                    text = stringResource(R.string.repeat_reminder),
                                    style = AppTheme.textStyles.Default,
                                    color = AppTheme.colors.Gray
                                )
                            }
                            if(repeatReminder) {
                                Text(
                                    text = stringResource(R.string.repeat_for), color = AppTheme.colors.SecondaryOne,
                                    style = AppTheme.textStyles.HeadingFive
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    CustomTextField(
                                        value = repeatAmount,
                                        onValueChange = { newText ->
                                            repeatAmount = newText
                                            if (newText.isNotEmpty()) {
                                                repeatReminder = false
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
                                        onSelectedChange = { selectedRepeatAmount = it },
                                        expanded = selectedRepeatAmountMenu,
                                        modifier = Modifier.weight(1f),
                                    )
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
                            navController?.popBackStack()
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
                                logger.w("Reminders", "CreateReminderScreen: title is blank, not saving.")
                                return@CustomButton
                            }

                            scope.launch {
                                try {
                                    val hourStr = hourOptions.getOrNull(selectedHour) ?: "0"
                                    val minuteStr = minutesOptions.getOrNull(selectedMinute) ?: "0"
                                    val rawHour = hourStr.toIntOrNull() ?: 0
                                    val minute = minuteStr.toIntOrNull() ?: 0

                                    // This block converts the chosen AM/PM hour into a proper 24-hour format,
                                    // handling the special cases for 12 AM and 12 PM.
                                    val hour24 = if (selectedAmOrPm == 1) {
                                        // PM
                                        if (rawHour % 12 == 0) 12 else (rawHour % 12 + 12)
                                    } else {
                                        // AM
                                        rawHour % 12
                                    }
                                    // --- Starting time: move to tomorrow if time already passed today ---
                                    val now = Calendar.getInstance()
                                    val cal = Calendar.getInstance().apply{
                                        set(Calendar.HOUR_OF_DAY, hour24)
                                        set(Calendar.MINUTE, minute)
                                        set(Calendar.SECOND, 0)
                                        set(Calendar.MILLISECOND, 0)
                                    }
                                    // If time is earlier than "now", we will move to the next day
                                    if (cal.before(now)){
                                        cal.add(Calendar.DAY_OF_YEAR,1)
                                    }
                                    val dueAt = Timestamp(cal.time)
                                    val iconName = iconNameOptions.getOrNull(selectedReminderIndex) ?: ""

                                    // --- "Remind me every" section ---
                                    //val isDaily = asDaily || asWeekDay @Todo Stephen Commented this out as not used
                                    var timesPerHour = 0
                                    var timesPerDay = 0
                                    var timesPerMonth = 0

                                    val everyCount = reminderAmountNumber.toIntOrNull() ?: 0
                                    if (everyCount > 0) {
                                        when (selectedReminderAmountHourDayWeek) {
                                            0 -> {
                                                // Example: "Remind me every 8 Hours"
                                                // We store "8" in timesPerHour.
                                                timesPerHour = everyCount
                                            }
                                            1 -> {
                                                // Example: "Remind me every 3 Days"
                                                // For now we store the number 3 in timesPerDay.
                                                timesPerDay = everyCount
                                            }
                                            2 -> {
                                                // Example: "Remind me every 2 Weeks"
                                                // For now we store the number 2 in timesPerMonth
                                                timesPerMonth = everyCount
                                            }
                                        }
                                    }

                                    // --- "Repeat for" [ amount + (Days/Weeks/Months/Years) ]
                                    //val repeatForever = indefinitelyRepeat
                                    var repeatCount = 0
                                    var repeatInterval: String? = null

//                                    if (!doNotRepeat && !repeatForever) {                    @Todo Stephen Commented this out as not used
//                                        val count = repeatAmount.toIntOrNull() ?: 0
//                                        if (count > 0) {
//                                            repeatCount = count
//                                            repeatInterval = when (selectedRepeatAmount) {
//                                                0 -> "days"
//                                                1 -> "weeks"
//                                                2 -> "months"
//                                                3 -> "years"
//                                                else -> null
//                                            }
//                                        }
//                                    }

                                    val reminder = Reminders(
                                        reminderId = "",                    // Firestore auto-generates
                                        title = createdReminderTitle.trim(),
                                        notes = "",                         // no notes field in UI yet, but here if needed
                                        dueAt = dueAt,
                                        isCompleted = false,
                                        completedAt = null,
                                        createdAt = null,
                                        lastUpdate = null,
                                        //isDaily = isDaily,               @Todo Stephen Commented this out as not used
                                        timesPerHour = timesPerHour,
                                        timesPerDay = timesPerDay,
                                        timesPerMonth = timesPerMonth,
                                        //repeatForever = repeatForever, @Todo Stephen Commented this out as not used
                                        repeatCount = repeatCount,
                                        repeatInterval = repeatInterval,
                                        colorToken = null,
                                        iconName = iconName           // fallback to empty if somehow null
                                    )

                                    val id = repo.createReminder(reminder, logger)
                                    if (id != null) {
                                        navController?.popBackStack()
                                    } else {
                                        logger.e("Reminders", "CreateReminderScreen: createReminder returned null.")
                                        // TODO: show a user-facing error dialog box
                                    }
                                } catch (e: Exception) {
                                    logger.e("Reminders", "CreateReminderScreen: failed to create reminder", e)
                                    // TODO: show a user-facing error dialog box
                                }
                            }
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

