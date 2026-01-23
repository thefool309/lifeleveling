package com.lifeleveling.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.YearMonth
import com.kizitonwose.calendar.core.lengthOfMonth
import com.lifeleveling.app.R
import com.lifeleveling.app.data.Reminders
import com.lifeleveling.app.data.occursOn
//import com.lifeleveling.app.ui.components.TestUser.calendarReminders
import com.lifeleveling.app.ui.theme.AppTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.toJavaLocalDate
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale
import kotlin.collections.toList
//import kotlin.collections.filter

///**
// * This creates the day box on the calendar along with facilitating the dots for the reminders and the in and out dates of the calendar
// * @param day from Kizitonwose Calendar (https://github.com/kizitonwose/Calendar?utm_source=chatgpt.com) helps get the date for the box and its position in the calendar (in/out date or normal date range)
// * @param reminders list of users reminders that is filtered to find if its enabled, if it's a daily, and the day/month/year for the reminder so its dot can be placed on the calendar
// * @param startYear the base calendar year used to calculate the year offset when matching reminders to calendar dates, day.date is the
// * current year so when matching the index chosen by the user (date.year - startYear) for example date.year would be the current year the
// * user is looking at on the calendar, and 2025 is the current year given by the param startYear by localDate.now().year, so this will give
// * 0 as the index being 2025 in the year selection.
// * @author sgcfsu1993 (Stephen C.)
// */
//@Composable
//fun Day(
//    day: CalendarDay,
//    reminders: List<calReminder>,
//    startYear:Int
//) {
//    val isOutDate = day.position != DayPosition.MonthDate
//    val date = day.date
//    val yearIndex = date.year - startYear
//    val monthValue = date.month.value
//    val dayValue = date.dayOfMonth
//    val toShowReminderInfo = remember {mutableStateOf(false)}
//    val hasReminder = reminders.filter { r ->
//        r.isEnabled && (
//                (r.year == yearIndex && r.month == monthValue && r.day == dayValue) || (r.isDaily && (yearIndex > r.year || (yearIndex == r.year && monthValue > r.month) || (yearIndex == r.year && monthValue == r.month && dayValue >= r.day))))
//    }
//    val colorOptions = listOf(
//        Color.Red,
//        Color.Blue,
//        Color.Green,
//        Color.Magenta,
//        Color.Yellow,
//        Color.Cyan,
//        Color.LightGray,
//        Color.White
//    )
//    val hourOptions = stringArrayResource(R.array.hour_array).toList()
//    val minutesOptions = stringArrayResource(R.array.minutes_array).toList()
//    val amOrPmOptions = listOf( stringResource(R.string.am), stringResource(R.string.pm))
//    val dayReminders = remember{mutableStateOf(hasReminder)}
//    Box(
//        modifier = Modifier
//            .border(
//                color = AppTheme.colors.Gray,
//                shape = RectangleShape,
//                width = 0.2.dp
//            )
//            .fillMaxWidth()
//            .height(70.dp),
//        contentAlignment = Alignment.TopCenter
//    ) {
//        Text(
//            text = dayValue.toString(),
//            color = if (isOutDate) AppTheme.colors.FadedGray else AppTheme.colors.Gray
//        )
//        if (hasReminder.isNotEmpty()) {
//            Row(
//                modifier = Modifier
//                    .align(Alignment.Center),
//                horizontalArrangement = Arrangement.spacedBy(4.dp)
//            ){
//                hasReminder.take(4).forEach { reminder -> // The .take(n=4) limits how many dots will be in Day cell
//                    Box(
//                        modifier = Modifier
//                            .align(Alignment.CenterVertically)
//                            .size(8.dp)
//                            .background(colorOptions[reminder.color], CircleShape)
//                            .clickable {
//                                toShowReminderInfo.value = true
//                                dayReminders.value = hasReminder
//                            }
//                    )
//                }
//            }
//        }
//    }
//    if (toShowReminderInfo.value) {
//
//        ShowCalendarReminders(
//            toShowReminderInfo,
//            dayReminders.value,
//            dayValue,
//            monthValue,
//            hourOptions,
//            minutesOptions,
//            amOrPmOptions
//        )
//    }
//}

/**
 * Renders a single day cell inside the calendar Month View with Firestore-backed reminder dots.
 *
 * This composable is responsible for:
 * - Displaying the day number inside the calendar grid.
 * - Determining whether the day belongs to the current visible month (vs leading/trailing dates).
 * - Filtering which reminders should appear on this date using:
 *     - enabled == true
 *     - occursOn(date, systemZone)
 * - Rendering up to **4 reminder dots max** (UI constraint = 2x2 grid).
 * - Handling click events so the parent screen can navigate or react to the selected date.
 *
 * Why this exists:
 * - The calendar library provides dates as kotlinx.datetime.LocalDate.
 * - The rest of the app uses java.time.LocalDate.
 * - This component handles that conversion locally so the UI stays clean and consistent.
 *
 * Visual behavior:
 * - Out-of-month dates render in a faded color.
 * - In-month dates render normally.
 * - If reminders exist for the day, colored dots appear centered in the cell.
 * - Dot color is derived from the reminder type via reminderDotColor().
 *
 * Performance notes:
 * - Filtering is done using a sequence to avoid unnecessary intermediate lists.
 * - The list is hard-limited to 4 reminders to keep rendering predictable and cheap.
 *
 * @param day CalendarDay provided by the calendar library for this cell.
 * @param reminders Full reminder list for the currently visible month (already fetched from Firestore).
 * @param onDateClick Callback triggered when the user taps this day cell.
 * @author fdesouza1992
 */
@Composable
fun DayFirestore(
    day: CalendarDay,
    reminders: List<Reminders>,
    onDateClick: (LocalDate) -> Unit,
) {
    val isOutDate = day.position != DayPosition.MonthDate

    // Kizitonwose (your build) gives kotlinx.datetime.LocalDate here:
    val dateKx = day.date

    // Convert to java.time.LocalDate for the rest of your app:
    val date = dateKx.toJavaLocalDate()

    val zone = ZoneId.systemDefault()

    // enabled + occursOn (your Firestore rules)
    val remindersForThisDate = reminders
        .asSequence()
        .filter { it.enabled }
        .filter { it.occursOn(date, zone) }
        .take(4) // hard limit of 4 dots
        .toList()

    Box(
        modifier = Modifier
            .border(
                color = AppTheme.colors.Gray,
                shape = RectangleShape,
                width = 0.2.dp
            )
            .fillMaxWidth()
            .height(70.dp)
            .clickable { onDateClick(date) },
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            color = if (isOutDate) AppTheme.colors.FadedGray else AppTheme.colors.Gray
        )

        if (remindersForThisDate.isNotEmpty()) {
            // Arrange as 2x2 max
            val topRow = remindersForThisDate.take(2)
            val bottomRow = remindersForThisDate.drop(2).take(2)

            Column(
                modifier = Modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    topRow.forEach { reminder ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(reminderDotColor(reminder), CircleShape)
                        )
                    }
                }

                if (bottomRow.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        bottomRow.forEach { reminder ->
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(reminderDotColor(reminder), CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }
}


/**
 * This gives the days the title of M T W T F
 * @param daysOfWeek An ordered list of days used for creating the weekday headers matching teh calendars configured first day.
 * @author sgcfsu1993 (Stephen C.)
 **/
@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    val topLine = AppTheme.colors.SecondaryTwo
    val grayLine = AppTheme.colors.Gray
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Transparent)
            .drawBehind {
                val strokeWidth = 2.dp.toPx()
                // Top border
                drawLine(
                    color = topLine,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = strokeWidth
                )
                val thinStroke = 1.dp.toPx()
                // Bottom border
                drawLine(
                    color = grayLine,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = thinStroke
                )
                // Right border
                drawLine(
                    color = grayLine,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = thinStroke
                )
                // Left border
                drawLine(
                    color = grayLine,
                    start = Offset(size.width, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = thinStroke
                )
            }
    ) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                text = dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault()).toString(),
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
                color = AppTheme.colors.Gray
            )
        }
    }
}

/**
 * This allows the user to jump to a month on the calendar in month view.
 * @param toShow The bool value to show or not to show the dialog for MonthJump
 * @param startMonth the earliest month the user can select
 * @param endMonth the last month the user can select
 * @param onJumpToMonth callback from the users selected date
 * @author sgcfsu1993 (Stephen C.)
 **/
@Composable
fun MonthJump(
    toShow: MutableState<Boolean>,
    startMonth: YearMonth,
    endMonth: YearMonth,
    onJumpToMonth: (LocalDate) -> Unit
) {
    val monthListMonth = (1..12).map { monthNumber ->
        Month(monthNumber).name.lowercase().replaceFirstChar { it.titlecase() }
    }
    val yearList = (startMonth.year..endMonth.year).toList()
    val currentYear = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
    var selectedMonth by remember { mutableStateOf(0) }
    var selectedYear by remember { mutableStateOf(yearList.indexOf(currentYear).coerceAtLeast(0)) }
    val monthExpanded = remember { mutableStateOf(false) }
    val yearExpanded = remember { mutableStateOf(false) }
    val actualYear = yearList[selectedYear]
    val actualMonth = selectedMonth + 1

    CustomDialog(
        toShow = toShow,
        dismissOnInsideClick = false,
        dismissOnOutsideClick = false,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Title
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Jump to",
                    style = AppTheme.textStyles.HeadingFour,
                    color = AppTheme.colors.SecondaryOne
                )
                // Separator
                SeparatorLine(color = AppTheme.colors.SecondaryTwo)
            }

            // Choosing a reminder
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Select Month",
                    style = AppTheme.textStyles.HeadingFive,
                    color = AppTheme.colors.SecondaryThree
                )
                DropDownTextMenu(
                    options = monthListMonth,
                    selectedIndex = selectedMonth,
                    onSelectedChange = { selectedMonth = it },
                    expanded = monthExpanded,
                    textStyle = AppTheme.textStyles.HeadingSix,
                    arrowSize = 25.dp
                )
            }

            // Repeat options
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Repeat
                Text(
                    text = "Select Year",
                    style = AppTheme.textStyles.HeadingFive,
                    color = AppTheme.colors.SecondaryThree
                )
                DropDownTextMenu(
                    options = yearList.map { it.toString() },
                    selectedIndex = selectedYear,
                    onSelectedChange = { selectedYear = it },
                    expanded = yearExpanded,
                    textStyle = AppTheme.textStyles.HeadingSix,
                    arrowSize = 25.dp
                )
            }

            // Button controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CustomButton(
                    width = 120.dp,
                    onClick = { toShow.value = false },
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
                        val date = LocalDate.of(
                            actualYear,
                            actualMonth,
                            1 // This is a default day for LocalDate.of - it looks for 3 args
                        )
                        onJumpToMonth(date)
                        toShow.value = false
                    },
                    backgroundColor = AppTheme.colors.Success75,
                ) {
                    Text(
                        text = "Jump to",
                        style = AppTheme.textStyles.HeadingSix,
                        color = AppTheme.colors.Background
                    )
                }
            }
        }
    }
}

/**
 * This allows the user to jump to a selected day while on the calendar day view
 * @param toShowDay The bool value to show or not to show the dialog for DayJump
 * @param startMonth the earliest month the user can select
 * @param endMonth the last month the user can select
 * @param onJumpToDay callback from the users selected date
 * @author sgcfsu1993 (Stephen C.)
 **/
@Composable
fun DayJump(
    toShowDay: MutableState<Boolean>,
    startMonth: YearMonth,
    endMonth: YearMonth,
    onJumpToDay: (LocalDate) -> Unit
) {
    val monthListDay = (1..12).map { monthNumber ->
        Month(monthNumber).name.lowercase().replaceFirstChar { it.titlecase() }
    }
    val yearList = (startMonth.year..endMonth.year).toList()
    val currentYear = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
    var selectedDay by remember { mutableStateOf(0) }
    var selectedMonth by remember { mutableStateOf(0) }
    var selectedYear by remember { mutableStateOf(yearList.indexOf(currentYear).coerceAtLeast(0)) }
    val dayExpanded = remember { mutableStateOf(false) }
    val monthExpanded = remember { mutableStateOf(false) }
    val yearExpanded = remember { mutableStateOf(false) }
    val actualYear = yearList[selectedYear]
    val actualMonth = selectedMonth + 1
    val daysInMonth = YearMonth(actualYear, actualMonth).lengthOfMonth()
    val dayList = (1..daysInMonth).map { day ->
        val date = LocalDate.of(actualYear, actualMonth, day)
        val dayName = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
        "${SuffixForDays(day)} , $dayName "
    }

    CustomDialog(
        toShow = toShowDay,
        dismissOnInsideClick = false,
        dismissOnOutsideClick = false,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Title
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Jump to Day",
                    style = AppTheme.textStyles.HeadingFour,
                    color = AppTheme.colors.SecondaryOne
                )
                // Separator
                SeparatorLine(color = AppTheme.colors.SecondaryTwo)
            }
            // Choosing year
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Repeat
                Text(
                    text = "Select Year",
                    style = AppTheme.textStyles.HeadingFive,
                    color = AppTheme.colors.SecondaryThree
                )
                DropDownTextMenu(
                    options = yearList.map { it.toString() },
                    selectedIndex = selectedYear,
                    onSelectedChange = { selectedYear = it },
                    expanded = yearExpanded,
                    textStyle = AppTheme.textStyles.HeadingSix,
                    arrowSize = 25.dp
                )
            }
            // Choosing a month
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Select Month",
                    style = AppTheme.textStyles.HeadingFive,
                    color = AppTheme.colors.SecondaryThree
                )
                DropDownTextMenu(
                    options = monthListDay,
                    selectedIndex = selectedMonth,
                    onSelectedChange = { selectedMonth = it },
                    expanded = monthExpanded,
                    textStyle = AppTheme.textStyles.HeadingSix,
                    arrowSize = 25.dp
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Select Day",
                    style = AppTheme.textStyles.HeadingFive,
                    color = AppTheme.colors.SecondaryThree
                )
                DropDownTextMenu(
                    options = dayList,
                    selectedIndex = selectedDay,
                    onSelectedChange = { selectedDay = it },
                    expanded = dayExpanded,
                    textStyle = AppTheme.textStyles.HeadingSix,
                    arrowSize = 25.dp
                )
            }

            // Button controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CustomButton(
                    width = 120.dp,
                    onClick = { toShowDay.value = false },
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
                        val date = LocalDate.of(
                            actualYear,
                            actualMonth,
                            selectedDay + 1,
                        )
                        onJumpToDay(date)
                        toShowDay.value = false
                    },
                    backgroundColor = AppTheme.colors.Success75,
                ) {
                    Text(
                        text = "Jump to",
                        style = AppTheme.textStyles.HeadingSix,
                        color = AppTheme.colors.Background
                    )
                }
            }
        }
    }
}

/**
 * This adds the suffix to the day.
 * @param day takes in the day and applies the correct suffix to it (when its day date is 11 return 11th)
 * day % 10 returns the remainder so the correct suffix can be applied to it
 * @author sgcfsu1993 (Stephen C.)
 **/
fun SuffixForDays(
    day: Int
): String {
    return when {
        day in 11..13 -> "$day" + "th"
        day % 10 == 1 -> "$day" + "st"
        day % 10 == 2 -> "$day" + "nd"
        day % 10 == 3 -> "$day" + "rd"
        else -> "$day" + "th"
    }
}

/**
 * This brings up the pop-up in the My Reminders that show the information on the reminder
 * @param toShow The bool value to show or not to show the dialog
 * @param passedReminder the users reminders
 * @param hourOptions full list of hour options - [reminder.selectedHours] gives the correct value(indices) to be used
 * @param minutesOptions full list of minute options - [reminder.selectedMinutes] gives the correct value(indices) to be used
 * @param amOrPmOptions list of AM or PM - [reminder.amOrPm] gives the correct value(indices) to be used
 * @author sgcfsu1993 (Stephen C.)
 **/
@Composable
fun ShowReminder(
    toShow: MutableState<Boolean>,
    passedReminder: MutableState<Reminders?>,
    hourOptions: List<String>,
    minutesOptions: List<String>,
    amOrPmOptions: List<String>,
    onDelete: (Reminders) -> Unit
) {
    val reminder = passedReminder.value ?: return
    var delete by remember { mutableStateOf(false) }
//    val hour = hourOptions[reminder.selectedHours]
//    val minutes = minutesOptions[reminder.selectedMinutes]
//    val amOrPm = amOrPmOptions[reminder.amOrPm]
    val timeLabel = formatReminderTime(reminder)
    CustomDialog(
        toShow = toShow,
        dismissOnInsideClick = false,
    ) {
        if (!delete) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Display icon and title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    ShadowedIcon(
                        modifier = Modifier.size(30.dp),
                        imageVector = ImageVector.vectorResource(id = iconResForNameCalendar(reminder.iconName)),
                        tint =  Color.Unspecified

                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = reminder.title,
                        style = AppTheme.textStyles.HeadingFour,
                        color = AppTheme.colors.SecondaryThree
                    )
                }
                Text(
//                    text = "Remind me at: $hour:$minutes $amOrPm",
                    text = "Remind me at: $timeLabel",
                    style = AppTheme.textStyles.Default,
                    color = AppTheme.colors.Gray
                )
                Text(
                    text = "Place holder for more info",
                    style = AppTheme.textStyles.Default,
                    color = AppTheme.colors.Gray
                )

                // Buttons for deleting or closing window
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CustomButton(
                        width = 120.dp,
                        onClick = { delete = true },
                        backgroundColor = AppTheme.colors.Error75,
                    ) {
                        Text(
                            text = stringResource(R.string.delete),
                            style = AppTheme.textStyles.HeadingSix,
                            color = AppTheme.colors.Background
                        )
                    }
                    Spacer(Modifier.width(20.dp))
                    CustomButton(
                        width = 120.dp,
                        onClick = { toShow.value = false },
                        backgroundColor = AppTheme.colors.Success75,
                    ) {
                        Text(
                            text = stringResource(R.string.close),
                            style = AppTheme.textStyles.HeadingSix,
                            color = AppTheme.colors.Background
                        )
                    }
                }
            }
        } else {
            // Screen for confirming delete
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Center,
                    text = (
                            buildAnnotatedString {
                                withStyle(style = AppTheme.textStyles.HeadingSix.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                                    append("Are you sure you want to delete the reminder ")
                                }
                                withStyle(style = AppTheme.textStyles.HeadingSix.toSpanStyle().copy(color = AppTheme.colors.SecondaryThree, textDecoration = TextDecoration.Underline)) {
                                    append(reminder.title)
                                }
                                withStyle(style = AppTheme.textStyles.HeadingSix.toSpanStyle().copy(color = AppTheme.colors.Gray)) {
                                    append(stringResource(R.string.streak_delete_two))
                                }
                            }
                            ),
                    style = AppTheme.textStyles.HeadingSix,
                    color = AppTheme.colors.Gray
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CustomButton(
                        width = 120.dp,
                        onClick = { delete = false },
                        backgroundColor = AppTheme.colors.Success75,
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
                            onDelete(reminder)
//                            calendarReminders.value = calendarReminders.value.filter {
//                                it != passedReminder.value
//                            }
                            toShow.value = false
                        },
                        backgroundColor = AppTheme.colors.Error75,
                    ) {
                        Text(
                            text = stringResource(R.string.delete),
                            style = AppTheme.textStyles.HeadingSix,
                            color = AppTheme.colors.Background
                        )
                    }
                }
            }
        }
    }
}

/**
 * This brings up the pop-up on the calendar to show the reminders for that day (used in Day())
 * @param toShow The bool value to show or not to show the dialog
 * @param reminders the users reminders
 * @param day the day of the month for the reminder - used in the title for the pop-up
 * @param month the month for the reminder - used in the title for the pop-up
 * @param hourOptions full list of hour options - [reminder.selectedHours] gives the correct value(indices) to be used
 * @param minutesOptions full list of minute options - [reminder.selectedMinutes] gives the correct value(indices) to be used
 * @param amOrPmOptions list of AM or PM - [reminder.amOrPm] gives the correct value(indices) to be used
 * @author sgcfsu1993 (Stephen C.)
 **/
//@Composable
//fun ShowCalendarReminders(
//    toShow: MutableState<Boolean>,
//    reminders: List<calReminder>,
//    day: Int,
//    month: Int,
//    hourOptions: List<String>,
//    minutesOptions: List<String>,
//    amOrPmOptions: List<String>
//){
//    if(reminders.isEmpty()){
//        return
//    }
//    val month = Month.of(month).getDisplayName(TextStyle.FULL, Locale.getDefault())
//    CustomDialog(
//        toShow = toShow,
//        dismissOnInsideClick = true
//    ){
//        Column(
//           verticalArrangement = Arrangement.spacedBy(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(
//                text = "Reminders for $month ${SuffixForDays(day)}",
//                style = AppTheme.textStyles.HeadingSix,
//                color = AppTheme.colors.SecondaryThree
//            )
//            HighlightCard(
//                modifier = Modifier
//
//                    .fillMaxWidth(),
//
//                outerPadding = 0.dp
//            ){
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(150.dp)
//                        .verticalScroll(rememberScrollState()),
//                    verticalArrangement = Arrangement.spacedBy(8.dp)
//
//                ) {
//                    reminders.forEach { reminder: calReminder ->
//                        val hour = hourOptions[reminder.selectedHours]
//                        val min = minutesOptions[reminder.selectedMinutes]
//                        val amPm = amOrPmOptions[reminder.amOrPm]
//                        Column(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .background(AppTheme.colors.DarkerBackground, shape = RoundedCornerShape(12.dp)),
//                            verticalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            Row(
//                                verticalAlignment = Alignment.CenterVertically,
//                                horizontalArrangement = Arrangement.spacedBy(8.dp)
//                            ) {
//                                ShadowedIcon(
//                                    modifier = Modifier.size(30.dp),
//                                    imageVector = ImageVector.vectorResource(reminder.icon),
//                                    tint = Color.Unspecified
//                                )
//                                Text(
//                                    text = reminder.name,
//                                    style = AppTheme.textStyles.Default,
//                                    color = AppTheme.colors.SecondaryThree
//                                )
//                                Text(
//                                    text = "$hour:$min $amPm",
//                                    style = AppTheme.textStyles.Default,
//                                    color = AppTheme.colors.Gray
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//            CustomButton(
//                width = 120.dp,
//                onClick = { toShow.value = false },
//                backgroundColor = AppTheme.colors.Success75,
//            ) {
//                Text(
//                    text = stringResource(R.string.close),
//                    style = AppTheme.textStyles.HeadingSix,
//                    color = AppTheme.colors.Background
//                )
//            }
//        }
//    }
//}


fun formatReminderTime(reminder: Reminders): String {
    val date = reminder.startingAt?.toDate() ?: return "--:--"
    val zoned = date.toInstant().atZone(ZoneId.systemDefault())
    return java.time.format.DateTimeFormatter.ofPattern("h:mm a").format(zoned)
}

fun iconResForNameCalendar(iconName: String?): Int {
    return when (iconName) {
        "water_drop"     -> R.drawable.water_drop
        "bed_color"      -> R.drawable.bed_color
        "shirt_color"    -> R.drawable.shirt_color
        "med_bottle"     -> R.drawable.med_bottle
        "shower_bath"    -> R.drawable.shower_bath
        "shop_color"     -> R.drawable.shop_color
        "person_running" -> R.drawable.person_running
        "heart"          -> R.drawable.heart
        "bell"           -> R.drawable.bell
        "brain"          -> R.drawable.brain
        "document"       -> R.drawable.document
        "doctor"         -> R.drawable.doctor
        else             -> R.drawable.bell
    }
}

@Composable
private fun reminderDotColor(reminder: Reminders): Color {
    val token = reminder.colorToken?.trim()?.lowercase()

    // 1) Handles named tokens (string)
    val named = when (token) {
        "red" -> Color.Red
        "blue" -> Color.Blue
        "green" -> Color.Green
        "magenta" -> Color.Magenta
        "yellow" -> Color.Yellow
        "cyan" -> Color.Cyan
        "light_gray", "lightgrey", "light gray" -> Color.LightGray
        "white" -> Color.White
        else -> null
    }
    if (named != null) return named

    // 2) Handles numeric tokens ("0", "1", ...)
    val palette = listOf(
        Color.Red,
        Color.Blue,
        Color.Green,
        Color.Magenta,
        Color.Yellow,
        Color.Cyan,
        Color.LightGray,
        Color.White
    )

    val index = token?.toIntOrNull()?.coerceIn(0, palette.lastIndex) ?: 0
    return palette[index]
}
