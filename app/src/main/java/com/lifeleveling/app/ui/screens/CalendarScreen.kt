package com.lifeleveling.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.YearMonth
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.plusMonths
import com.lifeleveling.app.R
import com.lifeleveling.app.data.LocalNavController
import com.lifeleveling.app.data.LocalUserManager
import com.lifeleveling.app.ui.components.HighlightCard
import com.lifeleveling.app.ui.components.*
import com.lifeleveling.app.ui.components.ShadowedIcon
import com.lifeleveling.app.ui.components.SlidingSwitch
import com.lifeleveling.app.ui.theme.AppTheme
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import kotlin.time.ExperimentalTime
import com.lifeleveling.app.ui.components.DailyRemindersList
import com.lifeleveling.app.data.Reminder


//@Preview
@OptIn(ExperimentalTime::class)
@Composable
fun CalendarScreen() {
    val userManager = LocalUserManager.current
    val userState by userManager.uiState.collectAsState()
    val navController = LocalNavController.current
//    val dailyReminders by userManager.dailyRemindersState.collectAsState()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AppTheme.colors.Background)
            .padding(16.dp),

        ) {
        val showCalendarToolTip = remember { mutableStateOf(false) }
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp
        val showMonths = remember { mutableStateOf(false) }
        val showDays = remember { mutableStateOf(false) }
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(24)
        val endMonth = currentMonth.plusMonths(60)
        val daysOfWeek = remember { daysOfWeek() }
        val isMonthView = remember { mutableStateOf(true) }
        val jumpedDay = remember { mutableStateOf(LocalDate.now()) }
        val jumpedMonth = remember { mutableStateOf<LocalDate?>(null) }
//        val monthReminders = remember { mutableStateOf<List<Reminder>>(emptyList()) }
        val monthReminders = remember(userState.reminders) {
            userState.reminders.filter { it.enabled }
        }

        val state = rememberCalendarState(
            startMonth = startMonth,
            endMonth = endMonth,
            firstVisibleMonth = currentMonth,
            firstDayOfWeek = daysOfWeek.first(),
            outDateStyle = OutDateStyle.EndOfGrid
        )
        LaunchedEffect(jumpedDay) {
            userManager.getRemindersForDate(jumpedDay.value)
//            val all = userState.reminders
//            monthReminders.value = all.filter { it.enabled }
        }

        LaunchedEffect(jumpedMonth.value) {
            jumpedMonth.value?.let { date ->
                val myYearMonth = YearMonth(date.year, date.month.ordinal + 1)
                state.scrollToMonth(myYearMonth)
                jumpedMonth.value = null
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = AppTheme.colors.Background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),

                    ) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.Center),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ){
                        SlidingSwitch(
                            modifier = Modifier,
                            options = listOf(stringResource(R.string.day_calendar), stringResource(R.string.month_calendar)),
                            selectedIndex = if (isMonthView.value) 0 else 1,
                            onOptionSelected = { index -> isMonthView.value = (index == 0) },
                            horizontalPadding = 12.dp,
                            verticalPadding = 8.dp,
                            backgroundColor = AppTheme.colors.DarkerBackground,
                            selectedColor = AppTheme.colors.BrandOne,
                            unselectedColor = AppTheme.colors.Gray,
                            cornerRadius = 32.dp,
                            textStyle = AppTheme.textStyles.HeadingFour,
                            insetAmount = 4.dp,
                            extraWidth = 64.dp,
                        )
                        ShadowedIcon(
                            imageVector = ImageVector.vectorResource(R.drawable.info),
                            contentDescription = null,
                            tint = AppTheme.colors.FadedGray,
                            modifier = Modifier

                                .size(28.dp)
                                .clickable { showCalendarToolTip.value = !showCalendarToolTip.value },

                            )
                    }

                }

                HighlightCard(
                    modifier = Modifier,
                    //    .wrapContentHeight() <- only makes as tall as children? maybe could use, @Todo speak to cass about this
                    height = (screenHeight / 5) * 3,
                    innerPadding = 0.dp,
                    outerPadding = 0.dp,
                ) {
                    Column(
                        modifier = Modifier
                    ) {
                        if (!isMonthView.value) {
                            HorizontalCalendar(
                                modifier = Modifier
                                    .background(color = Color.Transparent),
                                state = state,
                                dayContent = { day ->
                                    DayFirestore(
                                        day = day,
                                        reminders = monthReminders,
                                        onDateClick = { clicked ->
                                            jumpedDay.value = clicked
                                            isMonthView.value = true // your Day view is when isMonthView == true
                                        }
                                    )
                                },
                                monthHeader = {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.Transparent)
                                    ) {
                                        val visibleYearMonth = jumpedMonth.value?.let {
                                            YearMonth(it.year, it.monthValue)
                                        } ?: state.firstVisibleMonth.yearMonth
                                        val monthName =
                                            visibleYearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                                        val year = visibleYearMonth.year

                                        Text(
                                            text = "$monthName $year",
                                            style = AppTheme.textStyles.HeadingFour,
                                            color = AppTheme.colors.Gray,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp)
                                                .clickable { showMonths.value = true },
                                            textAlign = TextAlign.Center
                                        )
                                        DaysOfWeekTitle(daysOfWeek = daysOfWeek)
                                    }
                                },
                            )
                        } else {
                            val dayInfo = jumpedDay.value ?: LocalDate.now()
                            val dayName = dayInfo.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
                            val monthName = dayInfo.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                            val currentDay = LocalDate.now()
                            val isToday = dayInfo == currentDay


                            Column(

                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(all = 16.dp)
                                ) {
                                    ShadowedIcon(
                                        imageVector = ImageVector.vectorResource(R.drawable.left_arrow),
                                        contentDescription = null,
                                        tint = AppTheme.colors.BrandTwo,
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clickable { jumpedDay.value = jumpedDay.value.minusDays(1) }
                                            .align(Alignment.CenterStart),
                                    )
                                    Box(
                                        modifier = Modifier
                                            .then(
                                                when {
                                                    isToday -> Modifier.border(
                                                        width = 1.dp,
                                                        color = AppTheme.colors.SecondaryThree
                                                    )

                                                    else -> Modifier
                                                }
                                            )
                                            .padding(8.dp)
                                            .align(Alignment.Center),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ){
                                            Text(
                                                text = "$dayName",
                                                style = AppTheme.textStyles.HeadingSix,
                                                color = AppTheme.colors.BrandOne,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier
                                                    .clickable {
                                                        showDays.value = true
                                                    }

                                            )
                                            Text(
                                                text = "$monthName ${dayInfo.dayOfMonth}, ${dayInfo.year}",
                                                style = AppTheme.textStyles.HeadingSix,
                                                color = AppTheme.colors.BrandOne,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier
                                                    .clickable {
                                                        showDays.value = true
                                                    }

                                            )
                                        }

                                    }
                                    ShadowedIcon(
                                        imageVector = ImageVector.vectorResource(R.drawable.right_arrow),
                                        contentDescription = null,
                                        tint = AppTheme.colors.BrandTwo,
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clickable { jumpedDay.value = jumpedDay.value.plusDays(1) }
                                            .align(Alignment.CenterEnd),
                                    )
                                }

//                                val pair = userManager.getRemindersForDate(dayInfo)

                                // Todo add in display of daily reminders
                                DailyRemindersList(
                                    date = dayInfo,
                                    reminders = userState.reminders,
                                    completionsByReminderId = userState.reminderCompletions,
                                    isLoading = userState.isCalendarLoading,
                                    onChecked = { reminderId, reminderTitle, increment ->
                                        if (increment) {
                                            userManager.incrementReminderCompletionForDate(reminderId, reminderTitle, dayInfo)
                                        } else {
                                            userManager.decrementReminderCompletionForDate(reminderId,reminderTitle, dayInfo)
                                        }
                                    }
                                )
                                }

                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(24.dp),

                    ) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.Start)
                            .clickable {
                                navController.navigate("createReminder")
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        ShadowedIcon(
                            imageVector = ImageVector.vectorResource(R.drawable.plus),
                            contentDescription = null,
                            tint = AppTheme.colors.SecondaryThree,
                            modifier = Modifier
                                .size(24.dp)


                        )

                        Text(
                            text = stringResource(R.string.add_reminders),
                            color = AppTheme.colors.SecondaryThree,
                            style = AppTheme.textStyles.DefaultUnderlined.copy(
                                shadow = Shadow(
                                    color = AppTheme.colors.DropShadow,
                                    offset = Offset(3f, 4f),
                                    blurRadius = 6f,
                                ),
                            ),
                        )
                    }

                    Row(
                        modifier = Modifier
                            .align(Alignment.Start)
                            .clickable {
                                navController.navigate("myReminders")

                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        ShadowedIcon(
                            imageVector = ImageVector.vectorResource(R.drawable.bars_solid_full),
                            contentDescription = null,
                            tint = AppTheme.colors.SecondaryThree,
                            modifier = Modifier
                                .size(24.dp)


                        )

                        Text(
                            text = stringResource(R.string.my_reminders),
                            color = AppTheme.colors.SecondaryThree,
                            style = AppTheme.textStyles.DefaultUnderlined.copy(
                                shadow = Shadow(
                                    color = AppTheme.colors.DropShadow,
                                    offset = Offset(3f, 4f),
                                    blurRadius = 6f,
                                )
                            ),
                        )
                    }
                }
            }
        }

        if (showMonths.value) {
            MonthJump(
                toShow = showMonths,
                startMonth = startMonth,
                endMonth = endMonth,
                onJumpToMonth = { selectedMonth ->
                    jumpedMonth.value = selectedMonth
                }
            )
        }

        if (showDays.value) {
            DayJump(
                toShowDay = showDays,
                startMonth = startMonth,
                endMonth = endMonth,
                onJumpToDay = { selectedDate ->
                    jumpedDay.value = selectedDate
                }
            )
        }
        if(showCalendarToolTip.value) {
            CalendarToolTip(showCalendarToolTip)
        }
    }
}