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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.YearMonth
import com.kizitonwose.calendar.core.lengthOfMonth
import com.lifeleveling.app.R
import com.lifeleveling.app.ui.components.TestUser.calendarReminders
import com.lifeleveling.app.ui.theme.AppTheme
import com.lifeleveling.app.ui.theme.resolveEnumColor
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import kotlin.collections.toList
import kotlin.collections.filter

@Composable
fun Day(day: CalendarDay, reminders: List<calReminder> = emptyList(), startYear:Int) {
    val isOutDate = day.position != DayPosition.MonthDate
    val date = day.date
    val yearIndex = date.year - startYear
    val monthValue = date.month.value
    val dayValue = date.dayOfMonth
    val hasReminder = reminders.any { r ->
        r.isEnabled && r.year == yearIndex && r.month == monthValue && r.day == dayValue
    }

    Box(
        modifier = Modifier
            .border(
                color = AppTheme.colors.Gray,
                shape = RectangleShape,
                width = 0.2.dp
            )
            .fillMaxWidth()
            .height(70.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = dayValue.toString(),
            color = if (isOutDate) AppTheme.colors.FadedGray else AppTheme.colors.Gray
        )
        if (hasReminder) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                   .padding(bottom = 4.dp)
                    .size(8.dp)
                    .background(AppTheme.colors.SecondaryOne, CircleShape),

            )

        }
    }
}

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

private fun DayOfWeek.getDisplayName(short: Any, default: Any) {}

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

fun SuffixForDays(day: Int): String {
    return when {
        day in 11..13 -> "$day" + "th"
        day % 10 == 1 -> "$day" + "st"
        day % 10 == 2 -> "$day" + "nd"
        day % 10 == 3 -> "$day" + "rd"
        else -> "$day" + "th"
    }
}

@Composable
fun ShowReminder(
    toShow: MutableState<Boolean>,
    passedReminder: MutableState<calReminder>,
    hourOptions: List<String>,
    minutesOptions: List<String>,
    amOrPmOptions: List<String>
) {
    val reminder = passedReminder.value
    var delete by remember { mutableStateOf(false) }
    // == below is safe access to the list - if somehow the index is messed up, it will just return null - if null is returned it uses the value in the quotations
    val hour = hourOptions.getOrNull(reminder.selectedHours) ?: "0"
    val minutes = minutesOptions.getOrNull(reminder.selectedMinutes) ?: "00"
    val amOrPm = amOrPmOptions.getOrNull(reminder.amOrPm) ?: "AM"

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
                        imageVector = ImageVector.vectorResource(reminder.icon),
                        tint =  Color.Unspecified

                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = reminder.name,
                        style = AppTheme.textStyles.HeadingFour,
                        color = AppTheme.colors.SecondaryThree
                    )
                }
                Text(
                    text = "Remind me at: $hour:$minutes $amOrPm",
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
                        onClick = {
                            delete = true
                        },
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
                                    append(reminder.name)
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
                            calendarReminders.value = calendarReminders.value.filter {
                                it != passedReminder.value
                            }
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

